package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;

public class DataPretreatment {
	
	//��ȡ����������֮���ʱ��
	private static Date getDateAfter(Date curTime, int milliSeconds){
		Calendar cal = Calendar.getInstance();
		try{
		cal.setTime(curTime);
		}catch(Exception e){
			System.out.println("");
		}
		cal.add(Calendar.MILLISECOND, milliSeconds);
		return cal.getTime();
	}
	/**
	 * ���ݾۺ�
	 * @param valsArrayD ��������
	 * @param method �ۺϷ���
	 * @return �ۺϺ������
	 */
	static private Double aggregateDoubleVals(Double[] valsArrayD,
			AggregateMethod method) {
		int len = valsArrayD.length;
		double []valsArray = new double[len];
		for (int i = 0; i < len; i ++)
			valsArray[i] = valsArrayD[i];
		switch (method) {
		case Aggregate_MAX:
			return StatUtils.max(valsArray);
		case Aggregate_MEAN:
			return StatUtils.mean(valsArray);
		case Aggregate_MIN:
			return StatUtils.min(valsArray);
		case Aggregate_SUM:
			return StatUtils.sum(valsArray);
		default:
			return 0.0;
		}
	}
	
	/**
	 * �ж�val�������ֵ�����ĸ����䣨0~len-1���У������ַ�����ʽ����������
	 * @param discreteNodes	�˵�ֵ
	 * @param len	�˵�����Ϊ�˱���ÿ�ε��ú���ʱ����ȡһ�����鳤�ȣ�
	 * @param val	ֵ
	 * @return
	 */
	private static String getIndexOfData(int len, double val, Double[] discreteNodes){
		if (val < discreteNodes[0])
			return discreteNodes[0]+"";
		if (val < discreteNodes[1])
			return discreteNodes[0]+"";
		for (int i = 1; i < len - 1; i ++)
			if (val >= discreteNodes[i] && val < discreteNodes[i+1])
				return discreteNodes[i]+"";
		return discreteNodes[len-1]+"";
	}
	
	//datItems����ͬ��ʱ�������ϵľۺ�
	public static DataItems aggregateData(DataItems di,int granularity,
			AggregateMethod method,boolean isDiscreteOrNonDouble){	
		
		DataItems dataOut = new DataItems();
		int len = di.getLength();
		if (di == null || di.getTime() == null || di.getData() == null || len == 0)
			return dataOut;
		
		List<Date> times = di.getTime();
		List<String> datas = di.getData();
		Date t1 = times.get(0);
		Date t2 = getDateAfter(t1, granularity * 1000);
		TreeSet<String> valsStr = new TreeSet<String>();// �ַ����ľۺϽ��
		List<Double> vals = new ArrayList<Double>(); 	// ��ֵ�ľۺϽ��
		Date t = t1;									// �ۺϺ��ʱ���
		for (int i = 0; i < len; i++){
			Date time = times.get(i);
			if (time.equals(t2) || time.after(t2)){	// ��һ��ʱ�������ڵ�ֵ�����ˣ������µ�ֵ
				if(isDiscreteOrNonDouble){
					StringBuilder sb = new StringBuilder();
					for (String valStr : valsStr)
						sb.append(valStr+" ");
					if (sb.length() > 0)
						dataOut.add1Data(t, sb.toString().trim());
					valsStr.clear();
				}else{
					Double[] valsArray = vals.toArray(new Double[0]);
					if (valsArray.length > 0){
						Double val = aggregateDoubleVals(valsArray, method);
						dataOut.add1Data(t, val.toString());
					}
					vals.clear();
				}
				t1 = t2;
				t2 = getDateAfter(t2, granularity * 1000);
				t = time;
			}			
			if (!time.before(t1)){
				if (isDiscreteOrNonDouble)	// ��ɢֵ���ַ���
					valsStr.add(datas.get(i));
				else{			// ��Ϊ����ֵ�������vals�У�����һ��ۺ�
					try{
						double data= Double.parseDouble(datas.get(i));
						vals.add(data);
					}catch(Exception e){}					
				}
			}else if (time.before(t1))	// ��������ܳ��ֵģ���Ϊǰ���ǰ���ʱ��˳��ȡ������
				JOptionPane.showMessageDialog(MainFrame.topFrame, "�ۣ���ʱ���Ⱥ�˳��ȡ���������⣡");
		}	
		// �����һ��ʱ����ڵ����ݼӽ�ȥ
		if (isDiscreteOrNonDouble && valsStr.size() > 0){
			StringBuilder sb = new StringBuilder();
			for (String valStr : valsStr)
				sb.append(valStr + " ");
			if (sb.length() > 0)
				dataOut.add1Data(t, sb.toString().trim());
			valsStr.clear();
		}else if (!isDiscreteOrNonDouble && vals.size() > 0){
			Double[] valsArray = vals.toArray(new Double[0]);
			if (valsArray.length > 0){
				Double val = aggregateDoubleVals(valsArray, method);
				dataOut.add1Data(t, val.toString());
			}					
			vals.clear();
		}
		return dataOut;
	}
	
	/**
	 * ����discreteMethod,�Ը����ݽ�����ɢ��
	 * @param discreteMethod	��ɢ������
	 * @param numDims			��ɢ���ά��
	 * @param endNodes			�Զ���˵㣬�����Զ�����ɢ��������������Ч
	 * @return
	 */
	public static DataItems toDiscreteNumbers(DataItems dataItems,DiscreteMethod discreteMethod, int numDims, String endNodes){
		DataItems newDataItems = null;
		switch (discreteMethod) {
		case ��������ֵ��Χ��ͬ:
			newDataItems = toDiscreteNumbersAccordingToMean3Sigma(dataItems,numDims);
			break;
		case ���������ݵ�����ͬ:
			newDataItems = toDiscreteNumbersAccordingToPercentile(dataItems,numDims);
			break;
		case �Զ���˵�:
			newDataItems = toDiscreteNumbersAccordingToCustomNodes(dataItems,endNodes);
			break;
		case None://������ɢ��,ֱ�ӷ���
		default:
			newDataItems = dataItems;
		}
		return newDataItems;
		
	}
	
	/**
	 * �����û�ָ���Ľڵ������ɢ��
	 * @param endNodes �û�ָ���Ľ��
	 * @return ��ɢ�����DataItems
	 */
	private static DataItems toDiscreteNumbersAccordingToCustomNodes(DataItems dataItems,String endNodes){
		DataItems newDataItems = new DataItems();
		if (endNodes == null || endNodes.length() == 0)
			return newDataItems;
		String []nodesStr = endNodes.split(",");
		int numDims = nodesStr.length;
		Double[] discreteNodes=new Double[numDims];
		for (int i = 0; i < numDims; i ++)
			discreteNodes[i] = Double.parseDouble(nodesStr[i]);
		newDataItems.setDiscreteNodes(discreteNodes);
		List<String> datas = dataItems.getData();
		int len = datas.size();
		for (int i = 0; i < len; i ++){				
			newDataItems.add1Data(dataItems.getTime().get(i),
		    getIndexOfData(numDims,Double.parseDouble(datas.get(i)),newDataItems.getDiscreteNodes()));
		}
		return newDataItems;
	}
	/**
	 * ������[mean-3*sigma��mean+3*sigma]ƽ������ΪnumDims�����䣬��ɢ���õ���dataItems��
	 * @param numDims	��ɢ���ȡֵ��
	 * @return	�Ѿ���ɢ����dataItems����
	 */
	private static DataItems toDiscreteNumbersAccordingToMean3Sigma(DataItems dataItems,int numDims){
		DataItems newDataItems=new DataItems();
		Double minVal = Double.MAX_VALUE;
		Double maxVal = Double.MIN_VALUE;
		
		List<String> datas=dataItems.getData();
		int length=datas.size();
		
		// ���ȣ��ж�ȡֵ�����������Ϊ20��ֵ���£���ֱ�ӽ�ֵ��Ϊ��ɢֵ
		boolean isDiscrete = dataItems.isDiscrete();
		
		// ֱ�ӵ���ɢֵ����
		if (!isDiscrete){
			if(!dataItems.isAllDataIsDouble()){
				throw new RuntimeException("����ֵ�����ݲ�����ɢ��");
			}
			DescriptiveStatistics statistics=new DescriptiveStatistics();
			double mean = 0.0;
			double std = 0.0;
			for(String data:datas){
				statistics.addValue(Double.parseDouble(data));
			}
			mean=statistics.getMean();
			std=statistics.getStandardDeviation();
			minVal=mean-4*std;
			maxVal=mean+4*std;
			
			Double[] discreteNodes=new Double[numDims];
			for (int i = 0; i < numDims; i ++){
				discreteNodes[i] = minVal + (maxVal - minVal) * i / numDims;
			}
			for (int i = 0; i < length; i ++){
				DataItem item = dataItems.getElementAt(i);
				Double val = null;
				val = Double.parseDouble(item.getData());
				if (val != null){
					String ind = getIndexOfData(numDims,val,discreteNodes);
					newDataItems.add1Data(item.getTime(), ind);
				}
			}
			newDataItems.setDiscreteNodes(new Double[numDims]);
			return newDataItems;
		}else{
			return dataItems;
		}
		
	}
	/**
	 * ���ݷ�λ����������ɢ��
	 * @param numDims
	 * @return
	 */
	private static DataItems toDiscreteNumbersAccordingToPercentile(DataItems dataItems,int numDims){
		
		DataItems newDataItems=new DataItems();
		List<String> datas=dataItems.getData();
		int length=datas.size();
		
		boolean isDiscrete = dataItems.isDiscrete();
		if (isDiscrete){	// ֱ�ӵ���ɢֵ����
			return dataItems;
		}else{				// ����ֵ����Ҫ������ɢ��
			double step = 1.0 / numDims * length; 
			int ind = 0;
			int ind_step = (int) ((ind + 1) * step - 1);
			Double[] discreteNodes = new Double[numDims];
			DataItems sortedItems = DataItems.sortByDoubleValue(dataItems);
			discreteNodes[0] = Double.parseDouble(datas.get(0));
			if(!dataItems.isAllDataIsDouble()){
				throw new RuntimeException("����ֵ�����ݲ�����ɢ��");
			}
			datas=sortedItems.getData();
			for (int i = 0; i < length; i ++){
				Double val = Double.parseDouble(datas.get(i));
				if (i > ind_step){
					discreteNodes[ind + 1] = val;
					ind ++;
					ind_step = (int) ((ind + 1) * step - 1);
				}				
			}

			for (int i = 0; i < length; i ++){				
				newDataItems.add1Data(sortedItems.getTime().get(i),
						getIndexOfData(numDims, Double.parseDouble(datas.get(i)),discreteNodes));
			}
			newDataItems.setDiscreteNodes(discreteNodes);
			return newDataItems;
		}
	}
	
	
}

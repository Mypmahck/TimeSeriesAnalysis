package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Array;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;












import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
//import org.hamcrest.Matcher;
//import weka.clusterers.SimpleKMeans;
//import weka.core.Instances;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
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
	
	/**
	 * ��·����Ϣת��Ϊ·��������Ϣ  ��ÿ��ʱ�����ÿ��·�������ĸ���
	 * @param dataItems
	 * @return
	 */
	public static DataItems changeDataToProb(DataItems dataItems){
		DataItems dataOut=new DataItems();
		dataOut.setIsAllDataDouble(dataItems.getIsAllDataDouble());
		int len = dataItems.getLength();
		if (dataItems == null ||  len == 0)
			return dataOut;
		List<Date> times = dataItems.getTime();
		List<Map<String, Integer>> datas=dataItems.getNonNumData();
		Iterator<Map.Entry<String, Integer>> mapIter=null;
		for(Map<String, Integer> map:datas){
			Map<String, Double> probMap=new HashMap<String, Double>();
			mapIter=map.entrySet().iterator();
			while(mapIter.hasNext()){
				Entry<String,Integer> entry=mapIter.next();
				double pathPossi=getPathProb(map,entry.getKey());
				probMap.put(entry.getKey(), pathPossi);
			}
			dataOut.getProbMap().add(probMap);
		}
		dataOut.setTime(times);
		dataOut.setProb(dataItems.getProb());
		dataOut.setVarSet(dataItems.getVarSet());
		return dataOut;
	}
	
	/**
	 * ������ʷ·����Ϣ���������·�����ֵĸ���
	 * @param map ·����hashMap ��ʷ·��
	 * @param path Ҫ������ָ��ʵ�·��
	 * @return ��·���ĸ���
	 */
	private static double getPathProb(Map<String, Integer> map,String path){
		int sum=sumMap(map);
		String[] pathNodes=path.split(","); //·���ϵĽڵ�
		double possiblity=1.0;              //����·���ĸ���
		double[] eachNodePossi=new double[pathNodes.length-1];   //·��ÿ���ڵ���ָ���  ���������P(X)
		double[] neighborNodePossi=new double[pathNodes.length-1];  //���ڽڵ���ָ���  �����ϸ���P(X,Y)
		double[] conditionalPossi=new double[pathNodes.length-1];  //�������� ��P(Y|X)
		for(int i=0;i<pathNodes.length-1;i++){
			int nodeNum=containsNodesPathNum(map,pathNodes[i]);
			eachNodePossi[i]=(nodeNum*1.0)/sum;
		}
		possiblity*=eachNodePossi[0];
		//·�����ʼ������һ��Markovģ�� ���� P(A,B,C,D,E)=P(A)*P(B|A)*P(C|B)*P(D|C)*P(E|D)
		for(int i=0;i<pathNodes.length-1;i++){
			int nodeNum=containsNodesPathNum(map,pathNodes[i]+","+pathNodes[i+1]);
			neighborNodePossi[i]=(nodeNum*1.0)/sum;
			conditionalPossi[i]=neighborNodePossi[i]/eachNodePossi[i];
			possiblity*=conditionalPossi[i];
		}
		return possiblity;
	}
	
	/**
	 * �ж�ָ��·���ڵ�������·���г��ֵĴ���
	 * @param map ·�� map
	 * @param node ָ����·���ڵ�
	 * @return �ڵ���·���г���
	 */
	private static int containsNodesPathNum(Map<String, Integer>map,String node){
		int pathsNum=0;
		String[] nodes=node.split(",");
		boolean isContain=false;
		Iterator<Map.Entry<String, Integer>> iterator=map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Integer>entry=iterator.next();
			String key=entry.getKey();  //·��
			int value=entry.getValue();
			String[] pathNodes=key.split(",");   
			
			//����·��key���Ƿ���������Ľڵ�@node
			isContain=false;
			for(int i=0;i<pathNodes.length-nodes.length+1;i++){
				for(int j=0;j<nodes.length;j++){
					if(!pathNodes[i+j].equals(nodes[j])){
						isContain=false;
						break;
					}
					isContain=true;
				}
				if(isContain){
					break;
				}
			}
			if(isContain){
				pathsNum+=value;
			}
		}
		return pathsNum;
	}
	
	/**
	 * �ж�����·��������
	 * @param map �洢·����hashMap
	 * @return ·��������
	 */
	private static int sumMap(Map<String, Integer> map){
		Iterator<Map.Entry<String, Integer>> iter=map.entrySet().iterator();
		int sum=0;
		while (iter.hasNext()) {
			sum+=iter.next().getValue();
		}
		return sum;
	}
	
	//datItems����ͬ��ʱ�������ϵľۺ�
	public static DataItems aggregateData(DataItems di,int granularity,
			AggregateMethod method,boolean isDiscreteOrNonDouble){	
		
		DataItems dataOut = new DataItems();
		dataOut.setIsAllDataDouble(di.getIsAllDataDouble());
		int len = di.getLength();
		if (di == null || di.getTime() == null || di.getData() == null || len == 0)
			return dataOut;
		
		List<Date> times = di.getTime();
		List<String> datas = di.getData();
		Date t1 = times.get(0);
		Date t2 = getDateAfter(t1, granularity * 1000);
		Map<String,Integer> valsStr = new HashMap<String, Integer>(); // �ַ����ľۺϽ��
		Set<String> varSet=new HashSet<String>();                     //�ַ����ļ���
		List<Double> vals = new ArrayList<Double>(); 	// ��ֵ�ľۺϽ��
		
//		Date t = t1;									// �ۺϺ��ʱ���
		int i=0;
		for(;!t1.after(times.get(times.size()-1));t1=t2,t2 = getDateAfter(t2, granularity * 1000))
		{
			
			while(i<times.size()&&times.get(i).before(t2))
			{
				if(i>0&&times.get(i).before(times.get(i-1)))
					JOptionPane.showMessageDialog(MainFrame.topFrame, "����δ����");
				if (isDiscreteOrNonDouble){	// ��ɢֵ���ַ���
					if(valsStr.containsKey(datas.get(i))){
						int originValue=valsStr.get(datas.get(i));
						valsStr.remove(datas.get(i));
						int newValue=originValue+1;
						valsStr.put(datas.get(i), newValue);
					}else{
						valsStr.put(datas.get(i), 1);
					}
					varSet.add(datas.get(i));
				}
				else{			// ��Ϊ����ֵ�������vals�У�����һ��ۺ�
					try{
						double data= Double.parseDouble(datas.get(i));
						vals.add(data);
					}catch(Exception e){}					
				}
				i++;
			}
			//һ��ʱ�������ڵ�ֵ�����ˣ������µ�ֵ
			if(isDiscreteOrNonDouble){
//				StringBuilder sb = new StringBuilder();
//				for (String valStr : valsStr)
//					sb.append(valStr+" ");
//				if (sb.length() > 0)
//					dataOut.add1Data(t1, sb.toString().trim());
//				else
//					dataOut.add1Data(t1, "");
				dataOut.add1Data(t1, valsStr);
				valsStr.clear();
			}else{
				Double[] valsArray = vals.toArray(new Double[0]);
				
				if (valsArray.length > 0){
					Double val = aggregateDoubleVals(valsArray, method);
					dataOut.add1Data(t1, val.toString());
				}
				else
				{
					dataOut.add1Data(t1,String.valueOf(0.0));
				}
				vals.clear();
			}
		}			
		if(varSet.size()!=0){
			dataOut.setVarSet(varSet);
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
		newDataItems.setIsAllDataDouble(dataItems.getIsAllDataDouble());
		newDataItems.setVarSet(dataItems.getVarSet());
		if (endNodes == null || endNodes.length() == 0)
			return newDataItems;
		String []nodesStr = endNodes.split(",");
		int numDims = nodesStr.length;
		Double[] discreteNodes=new Double[numDims];
		for (int i = 0; i < numDims; i ++)
			discreteNodes[i] = Double.parseDouble(nodesStr[i]);
		newDataItems.setDiscreteNodes(discreteNodes);
		int len = dataItems.getLength();
		for (int i = 0; i < len; i ++){		
			if(i==220){
				System.out.println("here");
			}
			if(dataItems.isAllDataIsDouble()){
		    	newDataItems.add1Data(dataItems.getTime().get(i),
		        getIndexOfData(numDims,Double.parseDouble(dataItems.getData().get(i)),newDataItems.getDiscreteNodes()));
			}else{
				Map<String, Integer> map=dataItems.getNonNumData().get(i);
				Map<String, Integer> discreMap=new HashMap<String, Integer>();
				Iterator<Map.Entry<String, Integer>> iter=map.entrySet().iterator();
				while(iter.hasNext()){
					Map.Entry<String, Integer>entry=iter.next();
					discreMap.put(entry.getKey(),
					(int) Double.parseDouble(getIndexOfData(numDims,entry.getValue(),newDataItems.getDiscreteNodes())));
				}
				newDataItems.add1Data(dataItems.getTime().get(i), discreMap);
			}
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
		newDataItems.setIsAllDataDouble(dataItems.getIsAllDataDouble());
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
	public static DataItems toDiscreteNumbersAccordingToWaveform(DataItems dataItems,TaskElement task)
	{
		
		//return WavCluster.waveTest(dataItems,task);
		return WavCluster.waveSelfCluster(dataItems);
	}
	
	
	public static DataItems toDiscreteNumbersAccordingToSegment(DataItems dataItems,TaskElement task)
	{
		//return WavCluster.segmentTest(dataItems, task);
		return WavCluster.segmentSelfCluster(dataItems);
	}
}
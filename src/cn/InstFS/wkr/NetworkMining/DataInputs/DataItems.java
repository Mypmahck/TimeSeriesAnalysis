package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.Week;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class DataItems {
	public List<Date> time;
	public List<String> data;
	public List<Double> prob;
	private int granularity;
	private boolean isDiscrete=false;
	
	private Double []discreteNodes;
	private Map<String, String>discreteStrings;
	public DataItems() {
		time = new ArrayList<Date>();
		data = new ArrayList<String>();	
		setProb(new ArrayList<Double>());
		setGranularity(0);
	}
	
	public RegularTimePeriod getTimePeriodOfElement(int i){
		if (getLength() < 2)
			return new Day();
		Date d1 = getElementAt(1).getTime();
		Date d2 = getElementAt(0).getTime();
		if (d1 == null || d2 == null)
			return new Day();
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(d1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(d2);
		
		Date curTime = getElementAt(i).getTime();
		if (getGranularity() == 0){
			if (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH))
				return new Month(curTime);
			if (cal1.getWeeksInWeekYear() != cal2.getWeeksInWeekYear())
				return new Week(curTime);
			if(cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR))
				return new Day(curTime);
			if (cal1.get(Calendar.HOUR_OF_DAY) != cal2.get(Calendar.HOUR_OF_DAY))
				return new Hour(curTime);
			if (cal1.get(Calendar.MINUTE) != cal2.get(Calendar.MINUTE))
				return new Minute(curTime);
			if (cal1.get(Calendar.SECOND) != cal2.get(Calendar.SECOND))
				return new Second(curTime);
			return new Day(curTime);		
		}else{
			if (getGranularity() < 60)
				return new Second(curTime);
			else if (getGranularity() < 60 * 60)
				return new Minute(curTime);
			else if (getGranularity() < 24 * 60 * 60)
				return new Hour(curTime);
			else if (getGranularity() < 7 * 24 * 60 * 60)
				return new Week(curTime);
			else if (getGranularity() < 30 * 24 * 60 * 60)
				return new Month(curTime);
			return new Day(curTime);			
		}
			
	}

	public boolean isDiscretized(){
		if(getDiscreteNodes() != null && getDiscreteNodes().length > 1)
			return true;
		else if (getDiscreteStrings() != null && getDiscreteStrings().size() > 0)
			return true;
		return false;
	}
	
	//����DataItemsά��
	public int getDiscretizedDimension(){
		if(getDiscreteNodes() != null && getDiscreteNodes().length > 1)
			return discreteNodes.length;
		else if (getDiscreteStrings() != null && getDiscreteStrings().size() > 0)
			return discreteStrings.size();
		return 0;
	}
	
	public void add1Data(DataItem di) {
		this.time.add(di.getTime());
		this.data.add(di.getData());
		this.prob.add(di.getProb());
	}
	
	public void add1Data(Date time, String data){
		this.time.add(time);
		this.data.add(data);
		this.getProb().add(0.0);
	}
	
	public int getLength(){
		return Math.min(Math.min(time.size(), data.size()), prob.size());
	}
	
	public DataItem getElementAt(int i ){
		DataItem ii = new DataItem();
		ii.setData(data.get(i));
		ii.setTime(time.get(i));
		ii.setProb(prob.get(i));
		return ii;
	}	
	public List<Date> getTime() {
		return time;
	}
	public List<String> getData() {
		return data;
	}
	public void setTime(List<Date> time) {
		this.time = time;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	public List<Double> getProb() {
		return prob;
	}
	public void setProb(List<Double> prob) {
		this.prob = prob;
	}
	/**
	 * ��ԴDataItems��գ�������items
	 * @param items
	 */
	public void setItems(DataItem []items){
		time.clear();
		data.clear();
		prob.clear();
		for (DataItem item :items){
			time.add(item.getTime());
			data.add(item.getData());
			prob.add(item.getProb());
		}
	}
	
	public Date getLastTime(){
		if (time.size() > 0)
			return time.get(time.size() - 1);
		else
			return null;
	}
	
	
	
	
	public boolean isAllDataIsDouble(){
		List<String>datas = getData();
		for(String data: datas)
			try{
				Double.parseDouble(data);
			}catch(Exception e){
				return false;
			}
		return true;
	}
	
	/**
	 * �жϸ������Ƿ�Ϊ��ɢֵ���С�
	 * <p>����ǣ�����ɢֵ�����Ӧ�ı�Ŵ���mapStr�У�������true</p>
	 * <p>���򣬷���false</p>
	 * @param mapStr	��Ϊ��
	 * @return
	 */
	public boolean isDiscrete(){
		int numDiffVals = 0;
		Map<String, String> mapStr= new HashMap<String, String>();
		List<String> datas = getData();
		for (String data:datas){
			if(!mapStr.containsKey(data)){
				mapStr.put(data, "" +numDiffVals);
				numDiffVals ++;
				if (numDiffVals > 20){
					isDiscrete=false;
					return false;
				}
			}
		}
		isDiscrete=true;
		setDiscreteStrings(mapStr);
		generateDiscreteNodes();
		return true;
	}
	
	/**
	 * ��ȡ��ɢ��Ľڵ�ֵ
	 * @return discrete Nodes in String type
	 */
	public String discreteNodes(){
		if(discreteNodes==null||discreteNodes.length==0){
			return null;
		}
		StringBuilder sb=new StringBuilder();
		int length=discreteNodes.length;
		for(int i=0;i<length;i++){
			sb.append(discreteNodes[i]);
			sb.append(",");
		}
		String nodes=sb.toString();
		return nodes.substring(0, nodes.length()-1);
	}
	
	//��itemsԭ����Ϊ��ɢ�������£�����discreteNode
	private void generateDiscreteNodes(){
		Collection<String> nodes=getDiscreteStrings().keySet();
		discreteNodes=new Double[nodes.size()];
		int index=0;
		for(String node:nodes){
			discreteNodes[index]=Double.parseDouble(node);
			index++;
		}
	}

//	/**
//	 * �ж�val�������ֵ�����ĸ����䣨0~len-1���У������ַ�����ʽ����������
//	 * @param discreteNodes	�˵�ֵ
//	 * @param len	�˵�����Ϊ�˱���ÿ�ε��ú���ʱ����ȡһ�����鳤�ȣ�
//	 * @param val	ֵ
//	 * @return
//	 */
//	private String getIndexOfData(int len, double val){
//		if (val < discreteNodes[0])
//			return getDiscreteNodes()[0]+"";
//		if (val < discreteNodes[1])
//			return getDiscreteNodes()[0]+"";
//		for (int i = 1; i < len - 1; i ++)
//			if (val >= discreteNodes[i] && val < discreteNodes[i+1])
//				return getDiscreteNodes()[i]+"";
//		return getDiscreteNodes()[len-1]+"";
//	}
//	/**
//	 * ����discreteMethod,�Ը����ݽ�����ɢ��
//	 * @param discreteMethod	��ɢ������
//	 * @param numDims			��ɢ���ά��
//	 * @param endNodes			�Զ���˵㣬�����Զ�����ɢ��������������Ч
//	 * @return
//	 */
//	public DataItems toDiscreteNumbers(DiscreteMethod discreteMethod, int numDims, String endNodes){
//		DataItems newDataItems = null;
//		switch (discreteMethod) {
//		case ��������ֵ��Χ��ͬ:
//			newDataItems = this.toDiscreteNumbersAccordingToMean3Sigma(numDims);
//			break;
//		case ���������ݵ�����ͬ:
//			newDataItems = this.toDiscreteNumbersAccordingToPercentile(numDims);
//			break;
//		case �Զ���˵�:
//			newDataItems = this
//					.toDiscreteNumbersAccordingToCustomNodes(endNodes);
//			break;
//		case None://������ɢ��,ֱ�ӷ���
//		default:
//			newDataItems = this;
//		}
//		newDataItems.setDiscreteNodes(this.getDiscreteNodes());
//		newDataItems.setDiscreteStrings(this.getDiscreteStrings());
//		return newDataItems;
//		
//	}
//	
//	/**
//	 * �����û�ָ���Ľڵ������ɢ��
//	 * @param endNodes �û�ָ���Ľ��
//	 * @return ��ɢ�����DataItems
//	 */
//	private DataItems toDiscreteNumbersAccordingToCustomNodes(String endNodes){
//		DataItems newDataItems = new DataItems();
//		if (endNodes == null || endNodes.length() == 0)
//			return newDataItems;
//		String []nodesStr = endNodes.split(",");
//		int numDims = nodesStr.length;
//		setDiscreteNodes(new Double[nodesStr.length]);
//		for (int i = 0; i < getDiscreteNodes().length; i ++)
//			getDiscreteNodes()[i] = Double.parseDouble(nodesStr[i]);
//		
//		List<String>datas = this.getData();
//		int len = this.getLength();
//		for (int i = 0; i < len; i ++){				
//			newDataItems.add1Data(this.getTime().get(i), ""+getIndexOfData(numDims, Double.parseDouble(datas.get(i))));
//		}
//		return newDataItems;
//	}
//	/**
//	 * ������[mean-3*sigma��mean+3*sigma]ƽ������ΪnumDims�����䣬��ɢ���õ���dataItems��
//	 * @param numDims	��ɢ���ȡֵ��
//	 * @return	�Ѿ���ɢ����dataItems����
//	 */
//	private DataItems toDiscreteNumbersAccordingToMean3Sigma(int numDims){
//		DataItems newDataItems=new DataItems();
//		Double minVal = Double.MAX_VALUE;
//		Double maxVal = Double.MIN_VALUE;
//		
//		int length=this.getLength();
//		List<String> datas=this.getData();
//		// ���ȣ��ж�ȡֵ�����������Ϊ20��ֵ���£���ֱ�ӽ�ֵ��Ϊ��ɢֵ
//		setDiscreteStrings(new HashMap<String, String>());
//		boolean isDiscrete = isDiscrete();
//				
//		if (isDiscrete){	// ֱ�ӵ���ɢֵ����
//			for (int i = 0; i < length; i ++){
//				DataItem item = this.getElementAt(i);
//				newDataItems.add1Data(item.getTime(), getDiscreteStrings().get(item.getData()));
//			}
//			generateDiscreteNodes();
//		}else{				// ����ֵ����Ҫ������ɢ����
//			// �Ȱ�DOUBLE ��ȡֵȡ����
//			List<Double>doubles = new ArrayList<Double>();
//			int numNonDouble = 0;
//			for(String data:datas){
//				try{
//					Double val = Double.parseDouble(data);
//					doubles.add(val);
//				}catch(Exception e){
//					numNonDouble ++;
//					if (numNonDouble > 10)
//						break;
//				}				
//			}
//			if (numNonDouble > 10)	// ˵������Ǹ��ַ������У���Ϊ����ֵ��ȡֵ�ĸ�������20��������ˣ���������ɢ������ֱ�ӷ�����������
//				return this;
//			// ������mean-3*sigma��Ϊ��Сֵ����mean+3*sigma��Ϊ���ֵ
//			int numDouble = doubles.size();
//			double mean1 = 0.0;
//			double std1 = 0.0;
//			for (int i = 0; i < numDouble; i++)
//				mean1 += doubles.get(i);
//			mean1/= numDouble;
//			for (int i = 0; i < numDouble; i ++)
//				std1 += (doubles.get(i) - mean1) * (doubles.get(i)-mean1);
//			std1/=(numDouble-1);
//			minVal=mean1-3*std1;
//			maxVal=mean1+3*std1;
//			
//			setDiscreteNodes(new Double[numDims]);
//			int numNodes = getDiscreteNodes().length;
//			for (int i = 0; i < numDims; i ++){
//				getDiscreteNodes()[i] = minVal + (maxVal - minVal) * i / numDims;
//			}
//			for (int i = 0; i < length; i ++){
//				DataItem item = this.getElementAt(i);
//				Double val = null;
//				try{
//					val = Double.parseDouble(item.getData());
//				}catch(Exception e){}
//				if (val != null){
//					String ind = getIndexOfData(numNodes, val);
//					newDataItems.add1Data(item.getTime(), ind);
//				}
//			}
//		}
//		return newDataItems;
//	}
//	/**
//	 * ���ݷ�λ����������ɢ��
//	 * @param numDims
//	 * @return
//	 */
//	private DataItems toDiscreteNumbersAccordingToPercentile(int numDims){
//		DataItems newDataItems=new DataItems();
//		
//		int length=this.getLength();
//		
//		// ���ȣ��ж�ȡֵ�����������Ϊ20��ֵ���£���ֱ�ӽ�ֵ��Ϊ��ɢֵ
//		setDiscreteStrings(new HashMap<String, String>());
//		boolean isDiscrete = isDiscrete(getDiscreteStrings());
//		if (isDiscrete){	// ֱ�ӵ���ɢֵ����
//			for (int i = 0; i < length; i ++){
//				DataItem item = this.getElementAt(i);
//				newDataItems.add1Data(item.getTime(), getDiscreteStrings().get(item.getData()));
//			}
//			generateDiscreteNodes();
//		}else{				// ����ֵ����Ҫ������ɢ��
//			int numNonDouble = 0;
//			double step = 1.0 / numDims * length; 
//			int ind = 0;
//			int ind_step = (int) ((ind + 1) * step - 1);
//			discreteNodes = new Double[numDims];
//			DataItems sortedItems = DataInputUtils.sortByDoubleValue(this);
//			List<String> datas=sortedItems.getData();
//			discreteNodes[0] = Double.parseDouble(datas.get(0));
//			// ��ȡ��λ��
//			for (int i = 0; i < length; i ++){
//				try{
//					Double val = Double.parseDouble(datas.get(i));
//					if (i > ind_step) {
//						discreteNodes[ind + 1] = val;
//						ind ++;
//						ind_step = (int) ((ind + 1) * step - 1);
//					}
//				}catch(Exception e){
//					numNonDouble ++;
//					if (numNonDouble > 10)
//						break;
//				}				
//			}
//			// �õ�������ֵ
//			datas = this.getData();
//			for (int i = 0; i < length; i ++){				
//				newDataItems.add1Data(this.getTime().get(i), ""+getIndexOfData(numDims, Double.parseDouble(datas.get(i))));
//			}
//			if (numNonDouble > 10)	// ˵������Ǹ�ȡֵ������20�����ַ�����ɢֵ���У�������
//				return newDataItems;
//		}
//		return newDataItems;
//	}


	public static DataItems sortByDoubleValue(DataItems input){
		DataItems output = new DataItems();
		int len = input.getLength();
		ItemDouble []items = new ItemDouble[len];	
		List<Date>times = input.getTime();
		List<String>vals = input.getData();
		try{
			for (int i = 0; i < len; i ++){
				items[i] = new ItemDouble();
				items[i].setTime(times.get(i));
				items[i].setData(Double.parseDouble(vals.get(i)));
			}
		}catch(Exception e){
			UtilsUI.appendOutput("�ַ���ת��ΪDouble�ͱ���");
			return output;
		}
		Arrays.sort(items);
		for (int i = 0; i < len; i ++){
			output.add1Data(items[i].getTime(), items[i].getData().toString());
		}
		return output;
	}
	
	public static DataItems sortByTimeValue(DataItems input){
		DataItems output = new DataItems();
		int len = input.getLength();
		ItemTime []items = new ItemTime[len];	
		List<Date>times = input.getTime();
		List<String>vals = input.getData();
		for (int i = 0; i < len; i ++){
			items[i] = new ItemTime();
			items[i].setTime(times.get(i));
			items[i].setData(vals.get(i));
		}
		Arrays.sort(items);
		for (int i = 0; i < len; i ++){
			output.add1Data(items[i].getTime(), items[i].getData());
		}
		return output;
	}
	
	public Double[] getDiscreteNodes() {
		return discreteNodes;
	}

	public void setDiscreteNodes(Double [] discreteNodes) {
		this.discreteNodes = discreteNodes;
	}

	public Map<String, String> getDiscreteStrings() {
		return discreteStrings;
	}

	public void setDiscreteStrings(Map<String, String> discreteStrings) {
		this.discreteStrings = discreteStrings;
	}

	public int getGranularity() {
		return granularity;
	}

	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
}

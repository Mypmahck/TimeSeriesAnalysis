package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Exception.NotFoundDicreseValueException;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class averageEntropyPM implements IMinerPM{
	private final int dimension;//��ɢ��ά��
	private TaskElement task;
	private Boolean hasPeriod; //�Ƿ�������
	private int predictPeriod;   //���ڳ���
	private List<Integer> existPeriod;
	private DataItems di; //��ǰʱ����������ݼ�
	private Date startTime;    //�����е���ʼʱ��
	private DataItems itemsInPeriod;  //һ�������ڵ�items
	private Double minEntropy = Double.MAX_VALUE;  
    private Double []entropies;   //�洢ÿ���������ڵ�ƽ���ػ�ƽ��ERP����
    private HashMap<Integer, Integer[]> predictValuesMap;
	private double threshold;  //�Ƿ�������ڵ���ֵ
	private int lastNumIndexInPeriod;//���һ�����������е�λ��
	
	public averageEntropyPM(TaskElement taskElement,int dimension){
		this.dimension=dimension;
		this.task=taskElement;
		hasPeriod = false;	
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public averageEntropyPM(TaskElement task,int dimension,Double threshold){
		this.dimension=dimension;
		this.task=task;
		hasPeriod = false;	
		predictPeriod=1;		
		this.threshold=threshold;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	/**
	 * ��ȡvalueֵ��dimension�е�ά�����
	 * @param value
	 * @return ���
	 * @throws NotFoundDicreseValueException
	 */
	private int getValueIndex(String value) throws NotFoundDicreseValueException {
		String endNodes=task.getDiscreteEndNodes();
		String[] nodes=endNodes.split(",");
		for(int i=0;i<nodes.length;i++){
			if(nodes[i].equals(value)){
				return i;
			}
		}
		throw new NotFoundDicreseValueException(value+" not exist");	
	}
	/**
	 * ���ݵ�һ�������ʼʱ��startTime����ȡd�������ʱ����һ�������ڵ���ţ���1��ʼ��
	 * @param d			ʱ��ֵ
	 * @param period	����ֵ
	 * @return ���������
	 */
	private int getTimeIndex(Date d, int period){
		double diffTime = (double)(d.getTime() - startTime.getTime()) / 1000.0;	// ������ʼ�������
		int granularity = task.getGranularity();
		return (int)(diffTime / granularity) % period + 1;
	}
	
	/**
	 * Ԥ�����ں͸������ڸ���������ɢֵ�ĸ��ʷֲ�
	 */
	public void predictPeriod(){
		if(!di.isDiscretized()){
			throw new RuntimeException("ƽ�����㷨Ҫ��������ɢ��");
		}
		int numItems=di.getLength();
		if (numItems == 0)
			return;
		List<Date> times = di.getTime();
		List<String> values=di.getData();
		startTime = times.get(0);
		
		int period=1;
		int maxPeriod = Math.min(numItems/2, 100);
		entropies = new Double[maxPeriod];
		while((period+1)< maxPeriod){
			period++;	//���ڵݼ�
			double entropy=0.0;
			double[][] data=new double[period+1][dimension];
			//��ʼ��Ϊ��
			for(int i=1;i<=period;i++){
				for(int j=0;j<dimension;j++){
					data[i][j]=0.0;
				}
			}
			for (int i = 0; i < values.size(); i++) {
				try {
					int timeIndex = getTimeIndex(times.get(i), period);
					int valIndex = getValueIndex(values.get(i));
					data[timeIndex][valIndex]+=1;
				} catch (NotFoundDicreseValueException e) {
					e.printStackTrace();
				}
			}
			for(int i=1;i<=period;i++){
				double itemNum=0.0;
				for(int j=0;j<dimension;j++){
					itemNum+=data[i][j];
				}
				for(int j=0;j<dimension;j++){
					data[i][j]=(data[i][j])/(itemNum);
				}
				for(int j=0;j<dimension;j++){
					if(data[i][j]>0){
						entropy+=(-(data[i][j]*Math.log(data[i][j])));
					}
				}
			}
			System.out.println("����:"+period+" ƽ����:"+(entropy/period)+" �أ�"+entropy);
			entropies[period - 1] = (entropy/period);
		}
		isPeriodExist(maxPeriod);
	}
	
	/**
	 * ȷ�������Ƿ���ڣ�������ڼ��������ڵķֲ�
	 * ȷ����С��
	 * @param maxPeriod ���Ե����ڸ���
	 */
	private void isPeriodExist(int maxPeriod){
		int minPeriod=0;
		Double maxEntropy=Double.MAX_VALUE;
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<threshold){
				if(entropies[i]<maxEntropy){
					minPeriod=(i+1);
					maxEntropy=entropies[i];
				}
				hasPeriod=true;
				existPeriod.add(i+1);
				Integer[] predictValues=new Integer[i+1];
				for(int j=0;j<di.getLength();j++){
					predictValues[j%(i+1)]+=(int)Double.parseDouble(di.getData().get(j));
				}
				for(int j=0;j<(i+1);j++){
					predictValues[j]/=(di.getLength()/(i+1));
				}
				predictValuesMap.put((i+1), predictValues);
			}
		}
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<minEntropy){
				minEntropy=entropies[i];
			}
		}
		if(hasPeriod){
			itemsInPeriod=new DataItems();
			Integer[] predictValues=predictValuesMap.get(minPeriod);
			for(int i=0;i<minPeriod;i++){
				itemsInPeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
			}
		}
	}
	
	@Override
	public boolean hasPeriod() {
		return hasPeriod;
	}
	
	@Override
	public void setDataItems(DataItems dataItems) {
		this.di=dataItems;
	}
	
	@Override
	public int getPredictPeriod() {
		return predictPeriod;
	}
	
	@Override
	public DataItems getItemsInPeriod() {
		return itemsInPeriod;
	}
	
	@Override
	public Double getMinEntropy() {
		return minEntropy;
	}
	
	@Override
	public Double[] getEntropies() {
		return entropies;
	}
	
	@Override
	public int getFirstPossiblePeriod() {
		if(hasPeriod()){
     		return existPeriod.get(0);
		}else{
			return -1;
		}
		
	}
	
	@Override
	public int getLastNumberIndexInperiod() {
		if(hasPeriod()){
	    	lastNumIndexInPeriod=(di.getLength()-1)%(predictPeriod);
	    	return lastNumIndexInPeriod;
		}else{
			return -1;
		}
	}
	
	public DataItems getDi(){
		return di;
	}
	
	public void setThreshold(double Threshold){
		this.threshold=Threshold;
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	public TaskElement getTask(){
		return task;
	}
	
}

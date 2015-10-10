package cn.InstFS.wkr.NetworkMining.Miner;

//import java.io.File;
//import java.io.FileWriter;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.Exception.NotFoundDicreseValueException;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;

public class DiscretePM {
	
	private final int dimension;//��ɢ��ά��
	private TaskElement task;
	private Boolean hasPeriod; //�Ƿ�������
	private int predictPeriod;   //���ڳ���
	private List<Integer> existPeriod;
	private int lastNumIndexInPeriod;//���һ�����������е�λ��
	private DataItems di; //��ǰʱ����������ݼ�
	private DataItems distributePeriod;  //һ�������ڵ�items��times����ʼʱ����item�ĵ�һ��ֵ��ʱ�� values��һ�������ڵ�ֵ
	private Date startTime;    //�����е���ʼʱ��
	private double threshold;  //�Ƿ�������ڵ���ֵ
	
	private Double minEntropy = Double.MAX_VALUE;  
    private Double []entropies;   //�洢ÿ���������ڵ�ƽ���ػ�ƽ��ERP����
    private int[] predictValues;  //����������ʱ��һ�������е�ֵ
    private HashMap<Integer, Integer[]> predictValuesMap;

	private int[][] distMatrix;
	
	
	/**
	 * ��С���㷨��õ�����
	 * @return ��һ������Ҫ�������
	 */
	public int getFirstPossiblePeriod(){
		if (existPeriod == null || existPeriod.size() == 0){
			hasPeriod=false;
			return -1;
		}
		int len = existPeriod.size();
		for (int i=0;i<len;i++){
			hasPeriod=true;
			predictPeriod=existPeriod.get(i);
			Integer[] values=predictValuesMap.get(predictPeriod);
			predictValues=new int[values.length];
			for(int j=0;j<values.length;j++){
				predictValues[j]=values[j];
			}
			lastNumIndexInPeriod=(di.getLength()-1)%(predictPeriod);
			distributePeriod=new DataItems();
			for(int j=0;j<predictPeriod;j++){
				int index=predictValues[j];
				String value=index+"";
				distributePeriod.add1Data(di.getTime().get(j), value);
			}
			return predictPeriod;
		}
		hasPeriod=false;
		return -1;
	}
	
	public int getLastNumIndexInPeriod(){
		return lastNumIndexInPeriod;
	}
	
	/**
	 * @param task
	 * @param dimension ��ɢ����ά�� ���ж��ٸ���ɢֵ
	 * @param values ��ɢֵ ���ά��ɢ��{0,1} 
	 */
	
	public DiscretePM(TaskElement task,int dimension){
		this.dimension=dimension;
		this.task=task;
		hasPeriod = false;	
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public DiscretePM(TaskElement task,int dimension,Double threshold){
		this.dimension=dimension;
		this.task=task;
		hasPeriod = false;	
		predictPeriod=1;		
		this.threshold=threshold;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}

	public int[] getPreidctValues(){
		return predictValues;
	}
	
	public int getPredictPeriod(){
		if(hasPeriod){
			return predictPeriod;
		}else{
			return -1;
		}
		
	}
	
	public boolean hasPeriod(){
		return hasPeriod;
	}
	
	public int getDimension(){
		return dimension;
	}
	
	public TaskElement getTask(){
		return task;
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
	
	public void predictBySeqSimility(){
		int numItems=di.getLength();
		if(numItems==0){
			return;
		}
		
		List<String> seqX=new ArrayList<String>();
		List<String> seqY=new ArrayList<String>();
		int[][] ErpDistMatrix;

		for(int i=0;i<numItems;i++){
			seqX.add((int)Double.parseDouble(di.getData().get(i))+"");
	    	seqY.add((int)Double.parseDouble(di.getData().get(i))+"");
		}
		setDistMatrix(seqX);
		int maxPeriod = numItems/2;
		int period=1;
		seqX.remove(numItems-1);
		seqY.remove(0);
		entropies=new Double[maxPeriod];
		while((period+1)<= maxPeriod){
			period++;	//���ڵݼ�
			seqX.remove(seqX.size()-1);
			seqY.remove(0);
			ErpDistMatrix=new int[numItems-period][numItems-period];
			int seqDistance=ERPDistance(seqX, seqY, seqX.size()-1, seqY.size()-1, ErpDistMatrix, period);
			double diff=(seqDistance*1.0)/(numItems-period);
			entropies[period-1]=diff;
			System.out.println("period "+period+"'s diff is "+diff);
		}
		isPeriodExist(maxPeriod);
	}
	
	/**
	 * ���������и�Ԫ�ؼ��ERP���� �Խ����ϵ�ERP������Ϊ�����
	 * @param items ����
	 */
	private void setDistMatrix(List<String> items) {
		int length=items.size();
		distMatrix=new int[length][length];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				if(i==j)
					distMatrix[i][j]=7000*10;
				else
					distMatrix[i][j]=Math.abs((int)Double.parseDouble(items.get(i))-
							(int)Double.parseDouble(items.get(j)));
			}
		}
	}
	
	/**
	 * �����������е�ERP����
	 * @param seqX ��һ������
	 * @param seqY �ڶ�������
	 * @param offset �ڶ���������ƽ��offset����λ�õ���
	 * @return ERP����
	 */
	private int ERPDistance(List<String> seqX,List<String>seqY,int xSize,int ySize,int [][]matrix,int offset){
		if(xSize<0&&ySize<0){
			return 0;
		}else if(xSize<0){
			int sum=0;
			for(int i=0;i<=ySize;i++){
				sum+=Integer.parseInt(seqY.get(i));
			}
			return sum;
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=Integer.parseInt(seqX.get(i));
			}
			return sum;
		}
		
		if(matrix[xSize][ySize]!=0){
			return matrix[xSize][ySize];
		}else{
			int xItem=Integer.parseInt(seqX.get(xSize));
			int yItem=Integer.parseInt(seqY.get(ySize));
			int dis1=ERPDistance(seqX,seqY,xSize-1,ySize-1,matrix,offset)+distMatrix[xSize][ySize+offset];
			int dis2=ERPDistance(seqX, seqY,xSize-1,ySize,matrix,offset)+Math.abs(xItem);
			int dis3=ERPDistance(seqX, seqY,xSize,ySize-1,matrix,offset)+Math.abs(yItem);
			int min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[xSize][ySize]=min;
			return min;
		}
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
			predictValues=new int[period+1];
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
			if((entropy/period)<threshold){//��ƽ������С������
				existPeriod.add(period);
				Integer[] predictValues=new Integer[period];
				double maxPoss=0;
				for(int i=1;i<=period;i++){
					maxPoss=0.0;
					for(int j=0;j<dimension;j++){
						if(data[i][j]>maxPoss){
							maxPoss=data[i][j];
							predictValues[i-1]=(int)Double.parseDouble(task.getDiscreteEndNodes().split(",")[j]);
						}
					}
				}
				predictValuesMap.put(period, predictValues);
				isPeriodExist(maxPeriod);
			}
		}		
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
			distributePeriod=new DataItems();
			Integer[] predictValues=predictValuesMap.get(minPeriod);
			for(int i=0;i<minPeriod;i++){
				distributePeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
			}
		}
	}
	
	public DataItems getDistributeItems(){
		if(hasPeriod){
			return this.distributePeriod;
		}else{
			return null;
		}
		
	}
	
	public Double getMinEntropy() {
		return minEntropy;
	}
	
	public Double[] getEntropies(){
		return entropies;
	}

	public void setDataItems(DataItems dataItems) {
		this.di = dataItems;
	}
	
	
}

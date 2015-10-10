package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class ARTSA implements IMinerTSA{
	private TaskElement task;
	private DataItems di;
	private DataItems outlies;           //�쳣��
	private int predictPeriod;           //Ԥ��ĳ���
	private DataItems predictItems;
	private Date endDate;                //���������ֵ������
	
	private double[] seq=null;           //�洢ƽ�Ȼ��������
	private double seqMean;              //��¼ƽ��ǰ���е�ƽ��ֵ     
	private double[] offset;             //��¼һ����ÿ��ʱ�̵�ƫ��
	private double[] params��=null;       //�洢AR���еĲ���
	private final int window=20;         //AR �Ĵ��ڳ��� 
	
	public ARTSA(TaskElement task,int predictPeriod,DataItems di){
		this.task=task;
		this.di=di;
		outlies=new DataItems();
		this.predictPeriod=predictPeriod;
		endDate=di.getLastTime();
	}
	
	@Override
	public void TimeSeriesAnalysis() {
		outlies=new DataItems();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		if(seq==null){
			transToStationarySeq();
		}
		int seqSize=di.getData().size();
		int window=20;//AR����=20  ��AR��Ϊ 2;��Xt=��1*Xt-1+��2*Xt-2+et  etΪ������  �����1�ͦ�2
		//����ҵ�ÿһ���쳣ֵ
		params��=new double[2];
		for(int i=0;i<seqSize-window;i++){
			getParams(i,params��);//��ȡparams�ղ���
			double e=0.0;//ƫ��  �����и���ʱ��AR����ֵ��ʵ��ֵ��ƫ��
			for(int j=i+2;j<i+window;j++){
				e=seq[j]-params��[0]*seq[j-1]-params��[1]*seq[j-2];
				statistics.addValue(e);
			}
			e=seq[i+window]-(params��[0]*seq[i+window-1]-params��[1]*seq[i+window-2]);
			double mean=statistics.getMean();
			double standardDeviation=statistics.getStandardDeviation();
			statistics.clear();
			if(e>(mean+3.0*standardDeviation)||mean<(mean-3.0*standardDeviation)){
				System.out.print(i+window+",");
				e=mean;
				//�����쳣��
				outlies.add1Data(di.getTime().get(i+window),di.getData().get(i+window)); 
				//�޸��쳣ֵʹ�ó������Ԥ����һ��ֵ�Ƿ��쳣
				seq[i+window]=params��[0]*seq[i+window-1]+params��[1]*seq[i+window-2]+e;  
			}
		}
		endDate=di.getLastTime();
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(endDate);
		predictItems=new DataItems();
		for(int k=0;k<predictPeriod;k++){
			getParams(seqSize-window+k, params��);
			double x1=seq[seqSize-1+k];
			double x2=seq[seqSize-2+k];
			seq[seqSize+k]=params��[0]*x1+params��[1]*x2;
			//����Ԥ��ֵ
			predictItems.getData().add((seq[seqSize+k]+seqMean+offset[(seqSize+k)%(offset.length)])+"");
			calendar.add(Calendar.SECOND, task.getGranularity());
			predictItems.getTime().add(calendar.getTime());
		}
		//��ת�����ƽ�����л�ԭ����ƽ������
		for(int k=0;k<seqSize+predictPeriod;k++){
			seq[k]=seq[k]+seqMean+offset[(k)%(offset.length)];
		}
	}
	
	/**
	 * ������dataItemsת����ƽ�����У�������ARƽ����Ҫ��
	 */
	private void transToStationarySeq(){
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		List<String> items=di.getData();
		int seqSize=items.size();
		seq=new double[seqSize+predictPeriod];
		for(int i=0;i<seqSize;i++){
			seq[i]=Double.parseDouble(items.get(i));
			statistics.addValue(seq[i]);
		}
		
		seqMean=statistics.getMean();
		int recordsOfDay=(3600*24*1000)/(task.getGranularity()*1000);  //һ���еļ�¼��

		int recordDays=seqSize/recordsOfDay;                //������
		offset=new double[recordsOfDay];           //��¼һ����ÿ��ʱ�̵�ƫ��
		for(int i=0;i<recordsOfDay;i++){
			double sum=0.0;
			for(int j=0;j<recordDays;j++){
				sum+=seq[i+j*recordsOfDay];
			}
			sum=(sum/recordDays)-seqMean;
			offset[i]=sum;  //��ʾÿ��ĵ�iʱ�̺��ܵ�meanƫ��
		}
		
		statistics.clear();
		//������ƽ�Ȼ�   ��ȥ��ƽ��ֵ��������
		System.out.println("ƽ������");
		for(int i=0;i<seqSize;i++){
			seq[i]=(seq[i]-seqMean-offset[i%recordsOfDay]);
			int snumber=(int)seq[i]*100;
			double dnumber=snumber+0.0;
			System.out.print(dnumber/100.0+",");
			statistics.addValue(seq[i]);
		}
		System.out.println("");
		
		//0��ֵ��������ֵΪ��
	 	double mean=statistics.getMean();
		for(int i=0;i<seqSize;i++){
			seq[i]-=mean;
		}
	}
	
	/**
	 * ���ÿ��AR�����Ц�1�ͦ�2����
	 * @param index �������е��±�
	 * @param params��  ������1�ͦ�2���������ظ����ú���
	 */
	private void getParams(int index,double[] params��){
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		//�ҵ� ��ά����X*XT�������
		double a11=0.0;
		double a12=0.0;
		double a21=0.0;
		double a22=0.0;
		for(int j=index+1;j<index+window-1;j++){
			a11+=(seq[j]*seq[j]);
			a12+=(seq[j]*seq[j-1]);
			a21+=(seq[j]*seq[j-1]);
		}
		for(int j=index;j<index+window-2;j++){
			a22+=(seq[j]*seq[j]);
		}
		
		double matrixNorm=a11*a22-a12*a21;//������  ����������Ϊ��ʱ��������������
		if(matrixNorm!=0){
			double temp;
			temp=a11;
			a11=(a22/matrixNorm);
			a22=(temp/matrixNorm);
			a12=-(a12/matrixNorm);
			a21=-(a21/matrixNorm);
			
			//���� XT*Y
			double b11=0.0;
			double b21=0.0;
			for(int j=index+2;j<index+window;j++){
				b11+=(seq[j]*seq[j-1]);
				b21+=(seq[j]*seq[j-2]);
			}
			double ��1=(a11*b11+a12*b21);
			double ��2=(a21*b11+a22*b21);
			params��[0]=��1;
			params��[1]=��2;
			statistics.clear();
		}else{
			return;
		}
	}
	
	public DataItems getDi(){
		return di;
	}
	
	public void setDi(DataItems di) {
		this.di = di;
	}
	
	public int getPredictPeriod() {
		return predictPeriod;
	}

	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}
	
	public TaskElement getTask(){
		return task;
	}
	
	public void setTask(TaskElement task) {
		this.task = task;
	}
	
	@Override
	public DataItems getOutlies() {
		return outlies;
	}
	
	@Override
	public DataItems getPredictItems() {
		return predictItems;
	}
}

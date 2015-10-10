package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Calendar;
import java.util.Date;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class ERPDistTSA implements IMinerTSA {
	private DataItems outlies;          //�쳣��
	private TaskElement task;
	private int predictPeriod;          //Ԥ��ĳ���
	private DataItems predictItems;
	private DataItems di;
	
	private long cycleSpan;             //���ڳ���
	private boolean hasPeriod=false;    //�Ƿ��������
	private int[] periodValues;         //�����и�ʱ�������е�ֵ
	private int lastNumIndexInPeriod;   //����ֵ�������е�λ��
	private Date endDate;               //���������ֵ������
	private double periodThreshold;     //���ڵ�������ֵ
	
	public ERPDistTSA(TaskElement task,int predictPeriod,DataItems di){
		this.task=task;
		this.di=di;
		outlies=new DataItems();
		this.predictPeriod=predictPeriod;
		endDate=di.getLastTime();
	}
	
	public void TimeSeriesAnalysis(){
		ERPDistencePM pm=new ERPDistencePM(periodThreshold);
		pm.setDataItems(di);
		pm.predictPeriod();
		pm.getFirstPossiblePeriod();
		hasPeriod=pm.hasPeriod();
		cycleSpan=pm.getPredictPeriod();
		periodValues=pm.getPreidctValues();
		lastNumIndexInPeriod=pm.getLastNumberIndexInperiod();
		endDate=di.getLastTime();
		if(!hasPeriod){
			return;
		}
		
		predictItems=new DataItems();
		Calendar start=Calendar.getInstance();
		start.setTime(endDate);//ʵ������ĩβ
		for(int span=1;span<=predictPeriod;span++){
			String item=periodValues[(span+lastNumIndexInPeriod)%(int)cycleSpan]+"";
			predictItems.getData().add(item);
			start.add(Calendar.SECOND, task.getGranularity());
			predictItems.getTime().add(start.getTime());
		}
		
		//TODO find outlies
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

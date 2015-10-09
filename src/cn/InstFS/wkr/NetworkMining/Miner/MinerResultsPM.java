package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;

public class MinerResultsPM{
	boolean hasPeriod;
	ParamsPM params;	// ���� java.util.Properties params;
	long period;
	private Double featureValue;	// ָ��ֵ
	private Double[] featureValues;	// ��ͬ���ڶ�Ӧ��ָ��ֵ����featureValues[2]��������Ϊ2ʱ��ָ��ֵ
	DataItems distibutePeriod;
	private int firstPossiblePeriod;
	
	public MinerResultsPM(){}
	
	public MinerResultsPM(boolean hasPeriod,long period){
		this.hasPeriod=hasPeriod;
		this.period=period;
	}
	public ParamsPM getParamsPM(){
		return this.params;
	}
	public void setParamsPM(ParamsPM params){
		this.params=params;
	}
	public void setPeriod(long period) {
		this.period = period;
	}
	
	public void setDistributePeriod(DataItems item){
		this.distibutePeriod=item;
	}
	
	public void setHasPeriod(boolean hasPeriod) {
		this.hasPeriod = hasPeriod;
	}
	
	public long getPeriod() {
		return period;
	}
	
	public boolean getHasPeriod(){
		return this.hasPeriod;
	}
	public DataItems getDistributePeriod(){
		return this.distibutePeriod;
	}

	public double getFeatureValue() {
		return featureValue;
	}

	public void setFeatureValue(double featureValue) {
		this.featureValue = featureValue;
	}

	public Double[] getFeatureValues() {
		return featureValues;
	}

	public void setFeatureValues(Double[] featureValues) {
		this.featureValues = featureValues;
	}

	public int getFirstPossiblePeriod() {
		return firstPossiblePeriod;
	}

	public void setFirstPossiblePeriod(int firstPossiblePeriod) {
		this.firstPossiblePeriod = firstPossiblePeriod;
	}
}
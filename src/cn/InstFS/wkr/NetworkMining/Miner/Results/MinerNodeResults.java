package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;

public class MinerNodeResults implements Serializable{
	private MinerResultsStatistics retStatistics;
	private MinerResultsPM retPM;
	private MinerResultsOM retOM;
	private MinerResultsSM retSM;
	private MinerResultsPartialCycle retPartialCycle;
	public MinerNodeResults(){
		retStatistics=new MinerResultsStatistics();
		retPM=new MinerResultsPM();
		retOM=new MinerResultsOM();
		retSM=new MinerResultsSM();
		retPartialCycle = new MinerResultsPartialCycle();
	}

	public MinerResultsStatistics getRetStatistics() {
		return retStatistics;
	}

	public void setRetStatistics(MinerResultsStatistics retStatistics) {
		this.retStatistics = retStatistics;
	}

	public MinerResultsPM getRetPM() {
		return retPM;
	}

	public void setRetPM(MinerResultsPM retPM) {
		this.retPM = retPM;
	}

	public MinerResultsOM getRetOM() {
		return retOM;
	}

	public void setRetOM(MinerResultsOM retOM) {
		this.retOM = retOM;
	}

	public MinerResultsSM getRetSM() {
		return retSM;
	}

	public void setRetSM(MinerResultsSM retSM) {
		this.retSM = retSM;
	}

	public MinerResultsPartialCycle getRetPartialCycle() {
		return retPartialCycle;
	}

	public void setRetPartialCycle(MinerResultsPartialCycle retPartialCycle) {
		this.retPartialCycle = retPartialCycle;
	}
	
}
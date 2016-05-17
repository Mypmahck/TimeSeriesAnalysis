package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;


public class MinerResults {
	private INetworkMiner miner;
	
	private Date dateProcess;
	private boolean isAbnormal;	// �Ƿ��쳣
	DataItems di;
	private MinerResultsPM retPM;
	private MinerResultsOM retOM;
	private MinerResultsSM retSM; 
	private MinerResultsFP retFP;
	private MinerResultsFM retFM;
	private MinerResultsStatistics retStatistics;
	private MinerResultsPath retPath;
	
	
	public MinerResults(INetworkMiner miner) {
		setMiner(miner);
		//ʵ��������ȡ����
		retPM = new MinerResultsPM();
		retOM = new MinerResultsOM();
		retSM = new MinerResultsSM();
		retFP = new MinerResultsFP();
		retFM = new MinerResultsFM();
		retStatistics=new MinerResultsStatistics();
	}
	
	public MinerResultsFP getRetFP() {
		return retFP;
	}

	public void setRetFP(MinerResultsFP retFP) {
		this.retFP = retFP;
	}

	public DataItems getInputData(){
		return di;
	}
	public void setInputData(DataItems di){
		this.di = di;
	}
	public Date getDateProcess() {
		return dateProcess;
	}
	public void setDateProcess(Date dateProcess) {
		this.dateProcess = dateProcess;
	}
	public MinerResultsPM getRetPM() {
		return retPM;
	}
	public void setRetPM(MinerResultsPM retPM) {
		this.retPM = retPM;
	}
	public boolean isAbnormal() {
		return isAbnormal;
	}
	public void setAbnormal(boolean isAbnormal) {
		this.isAbnormal = isAbnormal;
	}
	
	public MinerResultsSM getRetSM(String name) {
		return retSM;
	}
	
	public MinerResultsSM getRetSM(){
		return retSM;
	}
	
	public MinerResultsPath getRetPath() {
		return retPath;
	}

	public void setRetPath(MinerResultsPath retPath) {
		this.retPath = retPath;
	}

	public void setRetSM(MinerResultsSM retSM) {
		this.retSM = retSM;
	}
	public INetworkMiner getMiner() {
		return miner;
	}
	public void setMiner(INetworkMiner miner) {
		this.miner = miner;
	}

	public MinerResultsOM getRetOM() {
		return retOM;
	}

	public void setRetOM(MinerResultsOM retOM) {
		this.retOM = retOM;
	}

	public MinerResultsFM getRetFM() {
		return retFM;
	}

	public void setRetFM(MinerResultsFM retFM) {
		this.retFM = retFM;
	}

	public MinerResultsStatistics getRetStatistics() {
		return retStatistics;
	}

	public void setRetStatistics(MinerResultsStatistics retStatistics) {
		this.retStatistics = retStatistics;
	}
	
	
}


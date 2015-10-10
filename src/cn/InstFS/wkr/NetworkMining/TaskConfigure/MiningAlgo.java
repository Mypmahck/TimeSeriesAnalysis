package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningAlgo {
	MiningAlgo_averageEntropyPM("ƽ�������ڼ���㷨"),
	MiningAlgo_ERPDistencePM("ERP�������ڼ���㷨"),
	MiningAlgo_ARTSA("ARģ�����з���"),
	MiningAlgo_ERPDistTSA("ERPģ�����з���"),
	MiningAlgo_NULL("��");
	
	private String value;
	MiningAlgo(String value) {
		this.value=value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static MiningAlgo fromString(String str){
		if (str.equals(MiningAlgo_averageEntropyPM.toString()))
			return MiningAlgo_averageEntropyPM;
		else if (str.equals(MiningAlgo_ERPDistencePM.toString()))
			return MiningAlgo_ERPDistencePM;
		else if(str.equals(MiningAlgo_ARTSA.toString())){
			return MiningAlgo_ARTSA;
		}else if(str.equals(MiningAlgo_ARTSA.toString())){
			return MiningAlgo_ERPDistTSA;
		}else{
			return MiningAlgo_NULL;
		}
			
	}
	
}

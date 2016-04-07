package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningMethod {
	MiningMethods_FrequenceItemMining("Ƶ����ھ�"),
	MiningMethods_SequenceMining("����ģʽ�ھ�"),
	MiningMethods_PeriodicityMining("����ģʽ����"),
	MiningMethods_PathProbilityMining("·�����ʷ���"),
	MiningMethods_TsAnalysis("ʱ�����з���");
	
	private String value;
	MiningMethod(String value) {
		this.value = value;
	}	
	@Override
	public String toString() {
		return value;
	}
	
	public static MiningMethod fromString(String str){
		if (str.equals(MiningMethods_TsAnalysis.toString()))
			return MiningMethods_TsAnalysis;
		else if (str.equals(MiningMethods_PeriodicityMining.toString()))
			return MiningMethods_PeriodicityMining;
		else if(str.equals(MiningMethods_FrequenceItemMining.toString())){
			return MiningMethods_FrequenceItemMining;
		}else if(str.equals(MiningMethods_SequenceMining.toString())){
			return MiningMethods_SequenceMining;
		}else if(str.equals(MiningMethods_PathProbilityMining.toString())){
			return MiningMethods_PathProbilityMining;
		}else{
			return MiningMethods_TsAnalysis;
		}
	}
}

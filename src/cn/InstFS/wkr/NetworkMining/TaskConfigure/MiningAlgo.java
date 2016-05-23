package cn.InstFS.wkr.NetworkMining.TaskConfigure;

import javax.lang.model.element.Element;

public enum MiningAlgo {
	MiningAlgo_averageEntropyPM("ƽ�������ڼ���㷨"),
	MiningAlgo_ERPDistencePM("ERP�������ڼ���㷨"),
	MiningAlgo_ARTSA("ARģ�����з���"),
	MiningAlgo_ERPDistTSA("ERPģ�����з���"),
	MiningAlgo_FastFourier("FFT�쳣���"),
	MiningAlgo_GaussDetection("��˹���������쳣���"),
	MiningAlgo_TEOTSA("TEO�߶��쳣���"),
	MiningAlgo_NeuralNetworkTSA("������Ԥ��"),
	MiningAlgo_ARIMATSA("ARIMAģ��Ԥ��"),
	MiningAlgo_LineProtocolASS("�߶λ���Ԫ���й���"),
	MiningAlgo_SimilarityProtocolASS("DTW���ƶȶ�Ԫ���й���"),
	MiningAlgo_RtreeProtocolASS("Rtree���ƶȶ�Ԫ���й���"),
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
		}else if(str.equals(MiningAlgo_FastFourier.toString())){
			return MiningAlgo_FastFourier;
		}else if(str.equals(MiningAlgo_GaussDetection.toString())){
			return MiningAlgo_GaussDetection;
		}else if(str.equals(MiningAlgo_NeuralNetworkTSA.toString())){
			return MiningAlgo_NeuralNetworkTSA;
		}else if(str.equals(MiningAlgo_TEOTSA.toString())){
			return MiningAlgo_TEOTSA;
		}else if(str.equals(MiningAlgo_ARIMATSA.toString())){
			return MiningAlgo_ARIMATSA;
		}else{
			return MiningAlgo_NULL;
		}
	}
}
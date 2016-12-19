package cn.InstFS.wkr.NetworkMining.TaskConfigure;

import javax.lang.model.element.Element;

public enum MiningAlgo {
	// 周期
	MiningAlgo_averageEntropyPM("平均熵周期检测算法"),
	MiningAlgo_ERPDistencePM("ERP距离周期检测算法"),
	//
	MiningAlgo_ARTSA("AR模型序列分析"),
	MiningAlgo_ERPDistTSA("ERP模型序列分析"),
	// 异常
	MiningAlgo_FastFourier("FFT异常检测"),
	MiningAlgo_GaussDetection("高斯滑动窗口异常检测"), // 对应AnormalyDetection算法
	MiningAlgo_Muitidimensional("基于混合高斯的异常检测"),
//	MiningAlgo_TEOTSA("TEO线段异常检测"),
	MiningAlgo_NodeOutlierDetection("高斯异常检测"), // 对应GaussianOutlierDetection
	// 预测
	MiningAlgo_NeuralNetworkTSA("神经网络预测"),
	MiningAlgo_ARIMATSA("ARIMA模型预测"),

	// 多业务关联
	MiningAlgo_LineProtocolASS("线段化多元序列关联"),

	// 多元序列相似度挖掘
	MiningAlgo_SimilarityProtocolASS("DTW相似度多元序列关联"),
	MiningAlgo_RtreeProtocolASS("Rtree相似度多元序列关联"),
	MiningAlgo_NULL("无");
	
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
		}else if(str.equals(MiningAlgo_Muitidimensional.toString())){
			return MiningAlgo_Muitidimensional;
		}else if(str.equals(MiningAlgo_NeuralNetworkTSA.toString())){
			return MiningAlgo_NeuralNetworkTSA;
		}else if(str.equals(MiningAlgo_ARIMATSA.toString())){
			return MiningAlgo_ARIMATSA;
		}else{
			return MiningAlgo_NULL;
		}
	}
}
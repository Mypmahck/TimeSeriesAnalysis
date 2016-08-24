package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import javax.print.attribute.HashAttributeSet;

import lineAssociation.BottomUpLinear;
import lineAssociation.Linear;
import oracle.net.aso.d;
import WaveletUtil.VectorDistance;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.GMMParameter;
import cn.InstFS.wkr.NetworkMining.DataInputs.Pattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.ResultItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.SerializationHelper;

/**
   *@author LYH
   *基于高斯混合分布的异常检测
 **/
public class MultidimensionalOutlineDetection implements IMinerOM{
	private DataItems dataItems = new DataItems();
	private DataItems outlines = new DataItems();
	private List<Pattern> patterns = new ArrayList<Pattern>();   //线段模式
	private int densityK = 2; //序列线段化时，找极值点的参数
	private double patternThreshold = 0.1; //线段化参数
	private int k = 4; //混合高斯中高斯个数
	private int dataDimen = 3;
	private int outK = 20; //异常度矩阵聚类簇数
	ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> outlinSet = new ArrayList<ArrayList<Double>>(); //异常度矩阵
	GMMParameter gmmParameter = new GMMParameter(); //高斯混合模型参数:pMiu,pPi,pSigma
	
	public MultidimensionalOutlineDetection(DataItems di){
		this.dataItems = di;
	}
	@Override
	public void TimeSeriesAnalysis(){
		PointSegment segment=new PointSegment(dataItems, densityK,patternThreshold); //线段化模式
		patterns=segment.getTEOPattern(); 
		OutlineDetection();
	}
	@Override
	public DataItems getOutlies(){
		return outlines;
	}
	
	/**异常检测过程**/
	public DataItems OutlineDetection(){
		//获得观测值dataSet	
		for(int i=0;i<patterns.size();i++){
			ArrayList<Double> data = new ArrayList<Double>();
			data.add(patterns.get(i).getSpan());
			data.add(patterns.get(i).getAverage());
			data.add(patterns.get(i).getSlope());
			dataSet.add(data);
		}
		//混合高斯建模
		//gmmParameter = GMMmode();
		gmmParameter = EMGMM(dataSet, k, dataDimen, "EMGMMCluster");
		//计算没点的高斯距离并归一化
		ArrayList<Double> distance = computeDistance(dataSet, gmmParameter); //最小高斯距离(1范数距离)
		ArrayList<Double> distance1 = computeDistance1(dataSet, gmmParameter); //带权重的最小高斯距离(1范数距离)
		ArrayList<Double> distance2 = computeDistance2(dataSet, gmmParameter); //最小高斯距离(2范数距离)
		distance = normalization(distance);
		distance1 = normalization(distance1);
		distance2 = normalization(distance2);
		//获得异常度矩阵
		for(int i=0;i<distance.size();i++){
			ArrayList<Double> data = new ArrayList<Double>();
			data.add(distance.get(i));
			data.add(distance1.get(i));
			data.add(distance2.get(i));
			outlinSet.add(data);
		}
		outlines = genOutline(outlinSet); //使用异常度矩阵进行异常检测
		//outlines = genOutline1(distance); //不使用异常度矩阵，仅使用gmm模型进行异常检测
		return outlines;
	}

	/**
	 * EMGMM建模
	 * @param instances  数据
	 * @param clusternum 簇数
	 * @param fileName 保存数据的文件名
	 * @return GMMParameter
	 */
	public GMMParameter EMGMM(ArrayList<ArrayList<Double>>instances,int clusternum,int dataDimen,String fileName){
		fileName = "./result/"+fileName;		
		changesample2arff(instances,fileName+".arff");
		double[][][] res = new double[clusternum][dataDimen][3];
		
		EM em = new EM();
		try{
			ArffLoader arffloader	=	new	ArffLoader();
			arffloader.setFile(new File(fileName+".arff"));
			Instances dataset	=	arffloader.getDataSet();
			
			em.setMaxIterations(100);
			em.setNumClusters(clusternum);
			em.setMinStdDev(1e-3);
			em.setDisplayModelInOldFormat(false);
			em.buildClusterer(dataset);
			System.out.println("混合高斯建模结果显示:"+em.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//设置gmm参数
		res = em.getClusterModelsNumericAtts();
		ArrayList<ArrayList<Double>> miu = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> sigma = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> pie = new ArrayList<Double>();
		for(int i=0;i<clusternum;i++){
			ArrayList<Double> miu1 = new ArrayList<Double>();
			ArrayList<Double> sigma1 = new ArrayList<Double>();
			for(int j=0;j<dataDimen;j++){
				miu1.add(res[i][j][0]);
				sigma1.add(res[i][j][1]);
			}
			miu.add(miu1);
			sigma.add(sigma1);
			pie.add(em.getClusterPriors()[i]);
		}
		
		
		GMMParameter gmm = new GMMParameter();
		gmm.setpMiu(miu);		
		gmm.setpSigma(sigma);
		gmm.setpPi(pie);
		return gmm;
	}
	/**
	 *@Title genOutline
	 *@Description 根据异常度矩阵获取异常线段(点)
	 *@return DataItems
	 */
	public DataItems genOutline(ArrayList<ArrayList<Double>> outlinSet){
		DataItems outline = new DataItems();
		//对异常度矩阵聚类 kmeans
		SimpleKMeans kMeans = Kmeans(outlinSet, outK, "kmeansCluster", true);
		Map<Integer, ArrayList<Double>> culterMap = new HashMap<Integer, ArrayList<Double>>();//分类结果
		int labels[]= new int[dataSet.size()];
		try{
			labels=kMeans.getAssignments();
			for(int i=0;i<outK;i++){
				ArrayList<Double> culter = new ArrayList<Double>();
				for(int j=0;j<labels.length;j++){
					if(labels[j]==i){
						culter.add(comVectorLen(outlinSet.get(j)));
					}
				}
				culterMap.put(i, culter);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		//找出culterMap中均值最大的一类
		int maxIndex = getMaxCulter(culterMap);
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for(int i=0;i<labels.length;i++){
			if(labels[i] == maxIndex){
				indexList.add(i);
			}
		}
		//找出异常模式(异常点)
		for(int i=0;i<indexList.size();i++){
			DataItem dataItem = new DataItem();
			List<Date> time = dataItems.getTime();
			List<String> data = dataItems.getData();
			int start = patterns.get(indexList.get(i)).getStart();
			int end = patterns.get(indexList.get(i)).getEnd();
			for(int j=start;j<=end;j++){				
				outline.add1Data(time.get(j),data.get(j));
			}
			System.out.println("线段"+indexList.get(i)+"的时间跨度为:"+patterns.get(indexList.get(i)).getSpan());
		}
		return outline;
	}
	/**
	 *@Title genOutline
	 *@Description 根据gmm聚类结果获取异常线段(点)
	 *@return DataItems
	 */
	public DataItems genOutline1(List<Double> dis){
		DataItems outline = new DataItems();
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for(int i=0;i<dis.size();i++){
			if(dis.get(i)>0.8){
				indexList.add(i);
			}
		}
		
		//找出异常模式(异常点)
		for(int i=0;i<indexList.size();i++){
			DataItem dataItem = new DataItem();
			List<Date> time = dataItems.getTime();
			List<String> data = dataItems.getData();
			int start = patterns.get(indexList.get(i)).getStart();
			int end = patterns.get(indexList.get(i)).getEnd();
			for(int j=start;j<=end;j++){				
				outline.add1Data(time.get(j),data.get(j));
			}
			System.out.println("线段"+indexList.get(i)+"的时间跨度为:"+patterns.get(indexList.get(i)).getSpan());
		}
		return outline;
	}
	/**
	 *@Title computeDistance
	 *@Description 计算每个数据点到混合高斯模型的最小距离  |x-pMiu|/pSigma
	 *@return ArrayList<Double>
	 *@throws **/
	public ArrayList<Double> computeDistance(ArrayList<ArrayList<Double>> dataSet,GMMParameter parameter){		
		ArrayList<Double> distance = new ArrayList<Double>();
		for(int i=0;i<dataSet.size();i++){
			ArrayList<Double> eveDis = new ArrayList<Double>();
			for(int j=0;j<k;j++){				
				double d = comVectorDis(dataSet.get(i),  parameter.getpMiu().get(j));
				double sigma = comVectorLen(parameter.getpSigma().get(j));
				d = d/sigma;
 				eveDis.add(d);
			}
			double dmin = getMinDis(eveDis);			
			distance.add(dmin);
		}
		return distance;
	}

	/**
	 *@Title computeDistance1
	 *@Description 计算每个数据点到混合高斯模型的最小距离  带权重的pPi*|x-pMiu|/pSigma
	 *@return ArrayList<Double>
	 *@throws **/
	public ArrayList<Double> computeDistance1(ArrayList<ArrayList<Double>> dataSet,GMMParameter parameter){		
		ArrayList<Double> distance = new ArrayList<Double>();
		for(int i=0;i<dataSet.size();i++){
			double d = 0;
			for(int j=0;j<k;j++){				
				double dis = comVectorDis(dataSet.get(i),  parameter.getpMiu().get(j));
				double sigma = comVectorLen(parameter.getpSigma().get(j));
				d = d+(parameter.getpPi().get(j))*dis/sigma; 				
			}			
			distance.add(d);
		}
		return distance;
	}
	
	/**
	 *@Title computeDistance2
	 *@Description 计算每个数据点到混合高斯模型的最小距离  |x-pMiu|/pSigma
	 *@return ArrayList<Double>
	 *@throws **/
	public ArrayList<Double> computeDistance2(ArrayList<ArrayList<Double>> dataSet,GMMParameter parameter){		
		ArrayList<Double> distance = new ArrayList<Double>();
		for(int i=0;i<dataSet.size();i++){
			ArrayList<Double> eveDis = new ArrayList<Double>();
			for(int j=0;j<k;j++){				
				double d = comVectorDis1(dataSet.get(i),  parameter.getpMiu().get(j));
				double sigma = comVectorLen1(parameter.getpSigma().get(j));
				d = d/sigma;
 				eveDis.add(d);
			}
			double dmin = getMinDis(eveDis);			
			distance.add(dmin);
		}
		return distance;
	}
	/**
	 *@Title computeDistance3
	 *@Description 计算每个数据点到混合高斯模型的最小距离  带权重的pPi*|x-pMiu|/pSigma
	 *@return ArrayList<Double>
	 *@throws **/
	public ArrayList<Double> computeDistance3(ArrayList<ArrayList<Double>> dataSet,GMMParameter parameter){		
		ArrayList<Double> distance = new ArrayList<Double>();
		for(int i=0;i<dataSet.size();i++){
			double d = 0;
			for(int j=0;j<k;j++){				
				double dis = comVectorDis1(dataSet.get(i),  parameter.getpMiu().get(j));
				double sigma = comVectorLen1(parameter.getpSigma().get(j));
				d = d+(parameter.getpPi().get(j))*dis/sigma; 				
			}			
			distance.add(d);
		}
		return distance;
	}
	
	/**
	 *@Title 
	 *@Description 数据归一化 x* = (x-xmin)/(xmax-xmin)
	 *@return ArrayList<Double>
	 *@throws **/
	public ArrayList<Double> normalization(ArrayList<Double> a){
		ArrayList<Double> a1 = new ArrayList<Double>();
		double xmin = 100000000;
		double xmax = -100000000;
		for(int i=0;i<a.size();i++){
			xmax = a.get(i)>xmax ? a.get(i) : xmax;
			xmin = a.get(i)<xmin ? a.get(i) : xmin;
		}
		for(int i=0;i<a.size();i++){
			double x = (a.get(i)-xmin)/(xmax-xmin);
			a1.add(x);
		}
		return a1;
	}
	
	/**
	 * Kmeans聚类
	 * @param instances  数据
	 * @param clusternum 簇数
	 * @param fileName 保存数据的文件名
	 * @param preserveOrder
	 * @return Kmeans对象
	 */
	public static SimpleKMeans Kmeans(ArrayList<ArrayList<Double>>instances,int clusternum,String fileName,boolean preserveOrder)
	{
		fileName = "./result/"+ fileName;
		changesample2arff(instances,fileName+".arff");
		//System.out.println(instances.size());
		SimpleKMeans  kMeans= new SimpleKMeans(); 
		try
		{
			ArffLoader arffloader	=	new	ArffLoader();
			arffloader.setFile(new File(fileName+".arff"));
			Instances dataset	=	arffloader.getDataSet();
			
			kMeans.setDistanceFunction(new VectorDistance() );
			kMeans.setNumClusters(clusternum);
			kMeans.setMaxIterations(1000);
			kMeans.setPreserveInstancesOrder(preserveOrder);
			kMeans.buildClusterer(dataset);
			kMeans.clusterInstance(dataset.get(0));
			
			SerializationHelper.write(fileName+".model", kMeans);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
//		changesample2arff(instances,fileName+".arff");
		return kMeans;
	}
	/**
	 * 将训练集转换成arff文件
	 * @param instances
	 * @param path
	 */
	public static void changesample2arff(ArrayList<ArrayList<Double>> instances,String path)
	{
		try
		{
			OutputStreamWriter ow = new OutputStreamWriter(
					new FileOutputStream(path), "UTF-8");
	
			BufferedWriter bw = new BufferedWriter(ow);
	
			bw.write("@relation "+path);
			bw.newLine();
			if(instances.size()>0)
			{
				for(int i =0;i<instances.get(0).size();i++)
				{
					bw.write("@attribute " + i + " numeric");
					bw.newLine();
				}
			}
			bw.write("@DATA");
			bw.newLine();
			for(int i=0;i<instances.size();i++)
			{
				StringBuilder sb=new StringBuilder();
//				sb.append("{");
				List<Double> instance = instances.get(i);
				for(int j=0;j<instance.size();j++)
				{
					sb.append(instance.get(j)+",");
				}
				sb.deleteCharAt(sb.length()-1);
//				sb.append("}");
				bw.write(sb.toString());
				bw.newLine();
			}
			
			bw.flush();
			bw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 *@Title getMaxCulter
	 *@Description 得到map中value的均值最大的index
	 *@return int
	 *@throws **/
	public int getMaxCulter(Map<Integer, ArrayList<Double>> map){
		Map<Integer,Double> centerArg = new HashMap<Integer, Double>();
		
		//计算每个类的均值
		for(Map.Entry<Integer, ArrayList<Double>> entry : map.entrySet()){
			ArrayList<Double> value = entry.getValue();
			double d = 0;
			for(int i=0;i<value.size();i++){
				d = d+value.get(i);
			}
			d = d/value.size();
			centerArg.put(entry.getKey(), d);
		}
		
		// 获取最大值的index
		double max = 0;
		int maxindex = 0;
		for(Map.Entry<Integer,Double> entry : centerArg.entrySet()){
			if(entry.getValue()>max){
				max = entry.getValue();
				maxindex = entry.getKey();
			}
		}
		return maxindex;
	}
	
	
	/*****************************************以下为计算工具***********************************************/
	/**
	 *@Title getMinDis
	 *@Description 得到数据点到混合高斯模型的最小距离
	 *@return double
	 *@throws **/
	public double getMinDis(ArrayList<Double> a){
		double dmin = 1000000;
		for(int i=0;i<a.size();i++){
			if(a.get(i)<dmin){
				dmin = a.get(i);
			}
		}
		return dmin;
	}
	
	/**
	 *@Title getMaxDis
	 *@Description 得到数据点到混合高斯模型的最小距离
	 *@return double
	 *@throws **/
	public double getMaxDis(ArrayList<Double> a){
		double dmax = -1000000;
		for(int i=0;i<a.size();i++){
			if(a.get(i)>dmax){
				dmax = a.get(i);
			}
		}
		return dmax;
	}
	
	/**
	 *@Title comVectorDis
	 *@Description 计算两个向量的距离 |x-pMiu| (1范数)
	 *@return double
	 *@throws **/
	public double comVectorDis(ArrayList<Double> a1,ArrayList<Double> a2){
		double d = 0;
		for(int i=0;i<a1.size();i++){
			double d1 = Math.abs(a1.get(i) - a2.get(i));
			d = d+d1;
		}
		return d;
	}
	/**
	 *@Title comVectorDis1
	 *@Description 计算两个向量的距离 ||x-pMiu|| 2-范数
	 *@return double
	 *@throws **/
	public double comVectorDis1(ArrayList<Double> a1,ArrayList<Double> a2){
		double d = 0;
		for(int i=0;i<a1.size();i++){
			double d1 = a1.get(i) - a2.get(i);
			d = d+d1*d1;
		}
		d = Math.sqrt(d);
		return d;
	}
	/**
	 *@Title comVectorLen
	 *@Description 计算向量的长度(1范数)
	 *@return double
	 *@throws **/
	public double comVectorLen(ArrayList<Double> a){
		double d = 0;
		for(int i=0;i<a.size();i++){
			d = d+a.get(i);
		}
		return d;
	}
	/**
	 *@Title comVectorLen
	 *@Description 计算向量的长度(2范数)
	 *@return double
	 *@throws **/
	public double comVectorLen1(ArrayList<Double> a){
		double d = 0;
		for(int i=0;i<a.size();i++){
			d = d+(a.get(i))*(a.get(i));
		}
		d = Math.sqrt(d);
		return d;
	}
}

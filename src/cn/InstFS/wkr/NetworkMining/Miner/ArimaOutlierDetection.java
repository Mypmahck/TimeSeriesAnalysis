package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections.map.HashedMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;


/** 
 * @author xuzhaobang
 *
 */
public class ArimaOutlierDetection {
	/**
	 * ��װԤ��ģ�ͣ���ȡscore�ж��쳣
	 * 
	 * ���г��Ȳ�����40
	 * 
	 * ����arima����������dataItems����Ԥ��
	 * �����dataItems��Ҫ����Ԥ����Ϊ����ʱ��ֲ���ʱ������
	 * ��������ʱ����з�װ������Ӧ�ⲿ��
	 * 
	 * @param dataItems ѵ������
	 * @return DataItems Ԥ����
	 */
	public static DataItems outlierDetection(DataItems dataItems){
		if(dataItems==null){
			return null;
		}
		int size = dataItems.getData().size();
		if(size<40){
			return null;
		}
		
		//���������
        DataItems outlierDataItems = new DataItems();
        List<Date> fTime = new ArrayList<Date>();
        List<String> fData = new ArrayList<String>();
		
		for(int i = 40;i<size;i++){
			double[] values = new double[i];
			DataItems subDataItems = cutDataItems(dataItems, size-i);
			for(int j=0;j<i;j++){
				values[j] = Double.parseDouble(subDataItems.getData().get(j));
			}
			
			//����ʱ�����У���������ʱ���ǩ
			TsPeriod start = new TsPeriod(TsFrequency.Monthly, 2012, 0);
	        TsData tsData = new TsData(start, values, true);
	        
	        //arima
	        CheckLast xTerror = new CheckLast(RegArimaSpecification.RG4.build());
	        xTerror.setBackCount(1);
	        xTerror.check(tsData);
	        double score = xTerror.getScore(0);
	        System.out.println(score);
	        if(Math.abs(score)>=4){
	        	fData.add(dataItems.getData().get(i-1));
	        	fTime.add(dataItems.getTime().get(i-1));
	        }
		}
        
		outlierDataItems.setData(fData);
		outlierDataItems.setTime(fTime);
        
		return outlierDataItems;
	}
	
	/**
	 * �и�ʱ�����У��Ա�������е�ÿһ������Ԥ�Ⲣ����score���ж��쳣
	 * 
	 * @param dataItems
	 * @param backCount
	 * @return
	 */
	private static DataItems cutDataItems(DataItems dataItems, int backCount) {
		if(dataItems==null){
			return null;
		}
		
		List<String> data = dataItems.getData();
		List<Date> time = dataItems.getTime();
		
		int size = data.size();
		if(size<=backCount){
			return null;
		}
		
		List<Date> tTime = new ArrayList<Date>();
        List<String> tData = new ArrayList<String>();
		
		for (int i=0;i<size-backCount;i++){
			tTime.add(time.get(i));
			tData.add(data.get(i));
		}
		
		DataItems tDataItems = new DataItems();
		tDataItems.setData(tData);
		tDataItems.setTime(tTime);
		return tDataItems;
	}
	
	
	
	
	
	
	public static void main(String[] args){
		
		//��������·��
		String textPath = "D:\\Java\\workspace\\TestPro\\conf\\test.csv";
		
		//��ʼ����������
		DataItems dataItems = getDataItems(textPath);
		//��ȡ�쳣���
		DataItems fDataItems = outlierDetection(dataItems);
		//����쳣���
		printResult(fDataItems);
	}

	
	
	
	
	


	/**
	 * ���Գ���
	 * ������Ƚ�Ԥ����
	 * @param trainDataItems
	 * @param fDataItems
	 */
	private static void printResult(DataItems fDataItems) {
		
		List<String> fData = fDataItems.getData();
		List<Date> fTime = fDataItems.getTime();
		int fSize = fData.size();
		
		for(int i=0;i<fSize;i++){
			System.out.println(fTime.get(i)+"\t"+fData.get(i));
		}
		
	}

	/**
	 * 
	 * ���Գ���
	 * �������ɲ��Գ�����Ҫ��dataItems
	 * @param textPath �������ݵ�·��
	 * @return
	 */
	private static DataItems getDataItems(String textPath) {
		DataItems dataItems = new DataItems();
		
		//��ʼ����������
		TreeMap<String,Double> treeMap = new TreeMap<String,Double>();
		String line=null;
		try {
			FileInputStream is=new FileInputStream(new File(textPath));
			BufferedReader reader=new BufferedReader(new InputStreamReader(is));
			reader.readLine();//��������
			while((line=reader.readLine())!=null){
				String[] values=line.split(",");
				String key = values[0].substring(0, values[0].lastIndexOf("/"));
				double value = Double.parseDouble(values[1]);
				if(treeMap.containsKey(key)){
					treeMap.put(key, treeMap.get(key)+value);
				}else{
					treeMap.put(key, value);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Date> time = new ArrayList<Date>();
        List<String> data = new ArrayList<String>();
		
		long stime = 694195200000L;
		int size = treeMap.size();
		for(String key : treeMap.keySet()){
			Date date = new Date(stime);
			time.add(date);
			data.add(String.valueOf(treeMap.get(key)));
			stime+=86400000L;
		}
		dataItems.setData(data);
		dataItems.setTime(time);
		
		return dataItems;
	}
}

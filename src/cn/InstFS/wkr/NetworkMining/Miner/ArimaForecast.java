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
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

public class ArimaForecast {
	
	/**
	 * ����arima����������dataItems����Ԥ��
	 * �����dataItems��Ҫ����Ԥ����Ϊ����ʱ��ֲ���ʱ������
	 * ��������ʱ����з�װ������Ӧ�ⲿ��
	 * 
	 * @param dataItems ѵ������
	 * @param backCount ��ҪԤ���δ�������
	 * @return DataItems Ԥ����
	 */
	public static DataItems forecast(DataItems dataItems,int backCount){
		if(dataItems==null||backCount==0){
			return null;
		}
		int size = dataItems.getData().size();
		double[] values = new double[size+backCount];
		for(int i=0;i<size;i++){
			values[i] = Double.parseDouble(dataItems.getData().get(i));
		}
		//��Ԥ�����ݲ�����Ϊ0
		for(int i=0;i<backCount;i++){
			values[size+i]=0;
		}
		//����ʱ�����У���������ʱ���ǩ
//		Day day = Day.toDay();
//		TsPeriod start = new TsPeriod(TsFrequency.Undefined,day);
//		TsPeriod start = TsPeriod.year(2014);
		TsPeriod start = new TsPeriod(TsFrequency.Monthly,2012,0);
        TsData tsData = new TsData(start, values, true);
        
        //arima
        CheckLast xTerror = new CheckLast(RegArimaSpecification.RG4.build());
        xTerror.setBackCount(backCount);
        xTerror.check(tsData);
        double[] forecastData = xTerror.getForecastsValues();
        
        //����ʱ������
        Date date1 = dataItems.getTime().get(size-1);
		Calendar cur1 = Calendar.getInstance();
		cur1.setTime(date1);
		Long curLong1 = cur1.getTimeInMillis();
		
		Date date2 = dataItems.getTime().get(size-2);
		Calendar cur2 = Calendar.getInstance();
		cur2.setTime(date2);
		Long curLong2 = cur2.getTimeInMillis();
        long particle = curLong1-curLong2;
        
        //���������
        DataItems forecastDataItems = new DataItems();
        List<Date> fTime = new ArrayList<Date>();
        List<String> fData = new ArrayList<String>();
        
        long nowLong = curLong1;
        for(int i=0;i<backCount;i++){
        	nowLong += particle;
        	Calendar newCur = Calendar.getInstance();
    		newCur.setTimeInMillis(nowLong);
    		Date newDate = newCur.getTime();
    		fTime.add(newDate);
    		fData.add(String.valueOf(forecastData[i]));
        }
        forecastDataItems.setData(fData);
        forecastDataItems.setTime(fTime);
        
		return forecastDataItems;
	}
	
	
	
	
	
	
	public static void main(String[] args){
		
		//��������·��
		String textPath = "D:\\Java\\workspace\\TestPro\\conf\\test.csv";
		
		//��ʼ����������
		DataItems dataItems = getDataItems(textPath);
		//Ԥ��������
		int backCount = 5;
		//�ָ�õ�ѵ�����ݣ����backCount���������ڼ���Ԥ����
		DataItems trainDataItems = cutDataItems(dataItems,backCount);
		//��ȡԤ����
		DataItems fDataItems = forecast(trainDataItems, backCount);
		//������Ƚ�Ԥ����
		printResult(trainDataItems,fDataItems);
	}

	
	
	
	
	
	
	/**
	 * ���Գ���
	 * �����������λ��������Խ���Ƚ�
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

	/**
	 * ���Գ���
	 * ������Ƚ�Ԥ����
	 * @param trainDataItems
	 * @param fDataItems
	 */
	private static void printResult(DataItems trainDataItems,DataItems fDataItems) {
		
		List<String> tData = trainDataItems.getData();
		List<Date> tTime = trainDataItems.getTime();
		
		List<String> fData = fDataItems.getData();
		List<Date> fTime = fDataItems.getTime();
		
		int tSize = tData.size();
		int fSize = fData.size();
		
		System.out.println("ԭʼֵ	Ԥ��ֵ");
		System.out.println("--------------------------");
		for(int i=0;i<fSize;i++){
			System.out.println(tData.get(tSize-fSize+i)+"\t"+fData.get(i));
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
//				System.out.println("line: "+line);
//				System.out.println("values[0]: "+values[0]);
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

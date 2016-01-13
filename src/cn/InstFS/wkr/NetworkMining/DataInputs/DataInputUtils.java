package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.StatUtils;
import org.netbeans.swing.plaf.util.UIUtils;

import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;
/**
 * Ԥ�����࣬������
 * 1����ȡ����
 * 2��Ԥ��������������ģʽ�ھ�ġ���ɢ��������
 */
public class DataInputUtils {
	TaskElement task;
	OracleUtils conn;
	DataItems data;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	public DataInputUtils(TaskElement task) {
		this.task = task;
		if(task.getDataSource().equals("DataBase")){
			conn = new OracleUtils();
			if (!conn.tryConnect())
				UtilsUI.showErrMsg("���ݿ��޷����ӣ�");
		}	
	}

	public DataItems readInputAfter(Date date){
		Date dEnd;
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1, 0, 0, 0);
		
		if (task.getDateEnd() == null || task.getDateEnd().equals(cal.getTime()))
			dEnd = UtilsSimulation.instance.getCurTime();
		else
			dEnd = task.getDateEnd();
		if (dEnd.after(UtilsSimulation.instance.getCurTime()))
			dEnd = UtilsSimulation.instance.getCurTime();
		return readInputBetween(date, dEnd);
	}
	public DataItems readInputBefore(Date date){
		Date dStart;
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1, 0, 0, 0);
		
		if (task.getDateStart().equals(cal.getTime()))	// ���û������������������ʱ�䣬�����������������
			dStart = UtilsSimulation.instance.getStartTime();
		else	// �������������������ʼʱ�䣬���������
			dStart = task.getDateStart();
		
		return readInputBetween(dStart, date);
	}
	public DataItems readInputBetween(Date date1, Date date2){
		String filter = "�¼�����ʱ��>'" + sdf.format(date1) + "' and " +
			"�¼�����ʱ��<='" + sdf.format(date2) + "'";
		return readInput(filter, true, true);
	}
	public DataItems readInput(){
		return readInput(true,true);
	}
	/**
	 * �����������ö�ȡ���ݣ������������������ʱ�����ȣ������ݽ��оۺ�
	 * @param doAggregate	�Ƿ�ۺ�
	 * @param doDiscretize	�Ƿ���ɢ��
	 * @return
	 */
	public DataItems readInput(boolean doAggregate, boolean doDiscretize){
		if (UtilsSimulation.instance.isUseSimulatedData())//ʹ�� 
		{
			TextUtils txt = new TextUtils();
			txt.setTextPath(task.getSourcePath());
			DataItems dataItems = txt.readInput();
			boolean isNonDouble = !dataItems.isAllDataIsDouble();//��ɢ�Ļ��double�͵�
			if (doAggregate)
			    dataItems=DataPretreatment.aggregateData(dataItems, task.getGranularity(),
			    		task.getAggregateMethod(), isNonDouble);
			if (doDiscretize)
				dataItems=DataPretreatment.toDiscreteNumbers(dataItems,task.getDiscreteMethod(),task.getDiscreteDimension(),
						task.getDiscreteEndNodes());
			String endNodes=dataItems.discreteNodes();
			task.setDiscreteEndNodes(endNodes);
			return dataItems;
		}else{
			Calendar cal = Calendar.getInstance();
			cal.set(1, 0, 1, 0, 0, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date dStart;
			Date dEnd;
			if(task.getDateStart() == null || task.getDateStart().equals(cal.getTime()))
				dStart = UtilsSimulation.instance.getStartTime();
			else
				dStart = task.getDateStart();
			
			if (task.getDateEnd() == null || task.getDateEnd().equals(cal.getTime()))
				dEnd = UtilsSimulation.instance.getCurTime();
			else
				dEnd = task.getDateEnd();
			if (dEnd.after(UtilsSimulation.instance.getCurTime()))
				dEnd = UtilsSimulation.instance.getCurTime();
			return readInputBetween(dStart, dEnd);
		}
	}

	private Date parseTime(String timeStr){
		int difLen = sdf.toPattern().length() - timeStr.length();
		StringBuilder sb = new StringBuilder();
		sb.append(timeStr);
		for (int i = 0; i < difLen; i ++)
			sb.append("0");
		try {
			return sdf.parse(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;		
	}
	/**
	 * 1.���չ���������ȡ����<br>
	 * 2.���������������趨����ɢ��������������ɢ��<br>
	 * 3.���������������ʱ�����ȣ������ݽ��оۺ�
	 * @param filterCondition	��������
	 * @param doAggregate		�Ƿ�ʱ������ݽ��оۺ�
	 * @param doDiscretize		�Ƿ�����ɢ��
	 * @return
	 */
	private DataItems readInput(String filterCondition, boolean doAggregate, boolean doDiscretize){
		String sqlStr = "SELECT �¼�����ʱ��," + task.getMiningObject() + " "
		+ "FROM " + conn.DB_TABLE + " WHERE ";
		if(task.getFilterCondition().length() > 0)
			sqlStr += task.getFilterCondition() + " AND ";
		if (filterCondition != null && filterCondition.length() > 0)
			sqlStr += filterCondition + " AND ";
		sqlStr += "1=1 ORDER BY �¼�����ʱ�� asc";	// ��ʱ���Ⱥ�˳���ȡ����
		conn.closeConn();
		ResultSet rs = conn.sqlQuery(sqlStr);
		if (rs == null){
			return null;
		}
		ResultSetMetaData meta = null;
		int numRecords = 0;
		try {
			meta = rs.getMetaData();
			int numCols = meta.getColumnCount();
			data = new DataItems();
			while(rs.next()){
				numRecords ++;
				StringBuilder sb = new StringBuilder();
				for (int i = 2; i <= numCols; i ++)
					if (rs.getString(i) != null)
						sb.append(rs.getString(i).trim() + ",");
				if (sb.length() > 0){
					Date d = parseTime(rs.getString(1).trim());
					if (d != null)
						data.add1Data(d, sb.substring(0, sb.length() - 1));    
					else
						System.out.println("");
				}
					
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("��" + numRecords + "����¼��");
		System.out.println("��ȡ���:" + data.getLength() + "����¼��");  
		
		boolean isNonDouble = !data.isAllDataIsDouble();
		// �Ƚ���ʱ�������ϵľۺ�
		if (doAggregate)
			data =DataPretreatment.aggregateData(data, task.getGranularity(),
					task.getAggregateMethod(), isNonDouble);
		// �ٽ�����ɢ����ֻ����ֵ�Ͳ��ܹ���ɢ��������Ӧ�ûᱨ����
		if (doDiscretize)
			data = DataPretreatment.toDiscreteNumbers(data, task.getDiscreteMethod(),
					task.getDiscreteDimension(), task.getDiscreteEndNodes());
		data.setGranularity(task.getGranularity());	// �������ݵ�һЩ������������
		String endNodes=data.discreteNodes();
		task.setDiscreteEndNodes(endNodes);
		return data;
	}
	
	public String printFormatData(DataItems data){
		String ret = "";
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);
		int numRows = data.getLength();
		try {
			for (int row = 0; row < numRows; row++) {
				DataItem s = data.getElementAt(row);
				bw.write(s.toString());
				bw.write("\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			ret = sw.toString();
			if (bw != null)
				try{bw.close();}catch(Exception ee){}
				if (sw != null)
					try{sw.close();}catch(Exception ee){}
		}
		return ret;
	}


	public static DataItems sort(DataItems dataInput){
		DataItem datin[]=new DataItem[dataInput.getLength()];
		for( int i = 0; i < dataInput.getLength(); i ++){
			datin[i] = dataInput.getElementAt(i);
		}
		Arrays.sort(datin);
		DataItems di = new DataItems();
		di.setItems(datin);
		return di;
	}
	/**
	 * ��DataItems���ݽ�������
	 * @param input Ҫ�����DataItems
	 * @return ������DataItems
	 */
	
	//��Ƶ�����������
	public static String sortFP(String FP, int DataSetsSize){
		String FPs[]=FP.split("\n");
		Item it[] = new Item[FPs.length];
		int i = 0;
		for(String FPs1:FPs){
			String FPs1s[]=FPs1.split(":");
			it[i] = new Item(FPs1s[1], FPs1s[0]);
			i++;
		}
		Arrays.sort(it, new MyCompratorFP());
		FP="";
		for(Item it1:it){
			FP+=it1.getData()+"\t\t"+it1.getTime()+"\t"+String.format("%.2f",Double.parseDouble(it1.getTime())/DataSetsSize)+"\n";
		}
		return FP;		
	}
	
	//��ȡ��������֮���ʱ��
	public static Date getDateAfter(Date curTime, int miniSecondsAfter){
		Calendar cal = Calendar.getInstance();
		try{
		cal.setTime(curTime);
		}catch(Exception e){
			System.out.println("");
		}
		cal.add(Calendar.MILLISECOND, miniSecondsAfter);
		return cal.getTime();
	}
	
	//����ʱ�䣬sizeWindow��stepWindow�����ݷֳ�����ٵ���Itemset2Fileд���ļ�
	public static String[] movingdivide(DataItems datainput, TaskElement task , boolean FP ) throws IOException{
		int sizeWindow = (int)((ParamsSM)task.getMiningParams()).getSizeWindow() * 1000;//seconds
		int stepWindow = (int)((ParamsSM)task.getMiningParams()).getStepWindow() * 1000;//seconds

		int len = datainput.getLength();
		List<Date> time = datainput.getTime();
		List<String> data = datainput.getData();	
		
		List<String> DataSets = new ArrayList<String>();
		
		Date win_start_time = time.get(0);
		Date win_end_time = getDateAfter(win_start_time, sizeWindow);
		Date win_start_next = getDateAfter(win_start_time, stepWindow);
		int ind_next = -1;
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		do{
			DataItem item = datainput.getElementAt(i);
			i++;
			Date date = item.getTime();
			String val = item.getData();
			if (!date.before(win_start_time) && !date.after(win_end_time)){
				if (sb.length() != 0 )
					sb.append(" ");
				sb.append(val + " -1");
				if (!date.before(win_start_next) && ind_next == -1)
					ind_next = i - 1;
			}else{
				sb.append(" -2");
				DataSets.add(sb.toString());
				sb = new StringBuilder();
				
				if (ind_next == -1){
					if (!date.before(getDateAfter(win_end_time, stepWindow))){
						win_start_time = date;	
						if (sb.length() != 0 )
							sb.append(" ");
						sb.append(val + " -1");
					}else{
						win_start_time = win_start_next; // getDateAfter(win_start_time, stepWindow);
						if (sb.length() != 0 )
							sb.append(" ");
						sb.append(val + " -1");
					}					
				}else{
					i = ind_next;
					ind_next = -1;
					win_start_time = win_start_next;					
				}
				win_end_time = getDateAfter(win_start_time, sizeWindow);
				win_start_next = getDateAfter(win_start_time, stepWindow);
				
			}		
			
		}while (i < len);
		sb.append(" -2");
		DataSets.add(sb.toString());
		return DataSets.toArray(new String[0]);
	}

	public static DataItems string2DataItems(String str) {// �����������ַ���ת��DataItems����ʽ

		DataItems ans = new DataItems();
		if (str == null || str.length() == 0)
			return ans;

		String[] temp = str.split("\n");
		List<String> dataList = new ArrayList<String>();
		List<Double> probList = new ArrayList<Double>();
		List<Date> timeList = new ArrayList<Date>();
		for (String temp1 : temp) {
			String[] temp2 = temp1.split("\t\t");
			dataList.add(temp2[0]);
			Double prob = 0.0;
			try {
				prob = Double.parseDouble(temp2[1]);
			} catch (Exception ee) {
				System.out.println("");
			}
			probList.add(prob);
			timeList.add(null);
		}
		ans.setData(dataList);
		ans.setProb(probList);
		ans.setTime(timeList);
		return ans;
	}
}

class MyCompratorFP implements Comparator<Item> {

	@Override
	public int compare(Item t1, Item t2) {
		if(Integer.parseInt(t1.getTime())>Integer.parseInt(t2.getTime()))
			return -1;
		else if(Integer.parseInt(t1.getData())<Integer.parseInt(t2.getData()))
			return 1;
		else return 0;
	}
}
class Item{
	private String time;
	private String data;
	public Item(String time, String data){
		this.time = time;
		this.data = data;
	}
	public void setTime(String time){
		this.time = time;
	}
	public void setData(String data){
		this.data = data;
	}
	public String getTime(){
		return time;
	}
	public String getData(){
		return data;
	}
}

class ItemDouble implements Comparable<ItemDouble>{
	private Date time;
	private Double data;
	@Override
	public int compareTo(ItemDouble o) {
		return this.getData().compareTo(o.getData());
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Double getData() {
		return data;
	}
	public void setData(Double data) {
		this.data = data;
	}
	
}

class ItemTime implements Comparable<ItemTime>{
	private Date time;
	private String data;
	private Map<String, Integer> NonNumData;
	@Override
	public int compareTo(ItemTime o) {
		return this.getTimeInMill().compareTo(o.getTimeInMill());
	}
	
	public Date getTime(){
		return time;
	}
	
	public void setTime(Date time){
		this.time=time;
	}
	
	public String getData(){
		return data;
	}
	
	public Map<String, Integer> getNonNumData() {
		return NonNumData;
	}

	public void setNonNumData(Map<String, Integer> nonNumData) {
		NonNumData = nonNumData;
	}

	public void setData(String data){
		this.data=data;
	}
	
	public Long getTimeInMill(){
		return time.getTime();
	}
}
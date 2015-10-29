package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;


/**
 * �ڵ�ԵĶ�ȡ��
 * @author wsc
 */
public class nodePairReader implements IReader {
	TaskElement task;
	OracleUtils conn;
	DataItems data;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String[] ipPair;
	boolean textSource;
	public nodePairReader(TaskElement task,String[] ipPair) {
		this.task=task;
		this.ipPair=ipPair;
		if(task.getDataSource().equals("DataBase")){
			textSource=false;
			conn=new OracleUtils();
			if(!conn.tryConnect()){
				throw new RuntimeException("���ݿ��޷�����");
			}
		}else{
			textSource=true;
		}
	}
	
	public nodePairReader(){}
	
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
		
		// ���û������������������ʱ�䣬�����������������
		if (task.getDateStart()==null||task.getDateStart().equals(cal.getTime()))	
			dStart = UtilsSimulation.instance.getStartTime();
		else	// �������������������ʼʱ�䣬���������
			dStart = task.getDateStart();
		
		return readInputBetween(dStart, date);
	}
	
	
	/**
	 * ��ȡ����ʱ���֮�������
	 * @param date1 ��ʼʱ��
	 * @param date2 ����ʱ��
	 * @return
	 */
	public DataItems readInputBetween(Date date1, Date date2){
		if(textSource){
			Calendar calendar=Calendar.getInstance();
			calendar.set(2014, 9, 1, 0, 0, 0);
			long startTime=(date1.getTime()-calendar.getTimeInMillis())/(1000*1000);
			long endTime=(date2.getTime()-calendar.getTimeInMillis())/(1000*1000);
			
			String[] conditions=new String[2];
			conditions[0]=("Time(S)>="+startTime);
			conditions[1]=("Time(S)<="+endTime);
			return readInputByText(conditions);
		}else{
			String filter = "�¼�����ʱ��>'" + sdf.format(date1) + "' and " +
					"�¼�����ʱ��<='" + sdf.format(date2) + "'";
			return readInputBySql(filter);
		}
	}
	
	@Override
	public DataItems readInputBySql() {
		StringBuilder sqlsb=new StringBuilder();
		sqlsb.append("SELECT Time,").append(task.getMiningObject()).append(" from ")
		     .append(conn.DB_TABLE).append(" where ");
		if(task.getFilterCondition().length()>0){
			sqlsb.append(task.getFilterCondition()).append(" and ");
		}
		sqlsb.append("SIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ").
		append("DIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ");
		sqlsb.append("1=1 order by �¼�����ʱ�� asc");
		if(!conn.isOpen()){
			conn.openConn();
		}
		ResultSet rs = conn.sqlQuery(sqlsb.toString());
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
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("��" + numRecords + "����¼��");
		System.out.println("��ȡ���:" + data.getLength() + "����¼��");
		conn.closeConn();
		return data;
	}
	
	@Override
	public DataItems readInputBySql(String condition) {
		StringBuilder sqlsb=new StringBuilder();
		sqlsb.append("SELECT �¼�����ʱ��,").append(task.getMiningObject()).append(" from ")
		     .append(conn.DB_TABLE).append(" where ");
		sqlsb.append(condition).append(" and ");
		if(task.getFilterCondition().length()>0){
			sqlsb.append(task.getFilterCondition()).append(" and ");
		}
		sqlsb.append("SIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ").
		append("DIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ");
		sqlsb.append("1=1 order by �¼�����ʱ�� asc");
		if(!conn.isOpen()){
			conn.openConn();
		}
		ResultSet rs = conn.sqlQuery(sqlsb.toString());
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
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("��" + numRecords + "����¼��");
		System.out.println("��ȡ���:" + data.getLength() + "����¼��");
		conn.closeConn();
		return data;
	}
	
	@Override
	public DataItems readInputByText() {
		DataItems dataItems=new DataItems();
		String minierObject=task.getMiningObject();
		File sourceFile=new File(task.getSourcePath());
		if(sourceFile.isFile()){
			readFile(sourceFile.getAbsolutePath(), minierObject, dataItems);
		}else{
			File[] files=sourceFile.listFiles();
			for(File file:files){
				readFile(file.getAbsolutePath(), minierObject, dataItems);
			}
		}
		return dataItems;
		
	}
	
	
	
	@Override
	public DataItems readInputByText(String[] conditions) {
		DataItems dataItems=new DataItems();
		String minierObject=task.getMiningObject();
		File sourceFile=new File(task.getSourcePath());
		if(sourceFile.isFile()){
			readFile(sourceFile.getAbsolutePath(), minierObject, dataItems,conditions);
		}else{
			File[] files=sourceFile.listFiles();
			for(File file:files){
				readFile(file.getAbsolutePath(), minierObject, dataItems,conditions);
			}
		}
		return dataItems;
	}
	
	private Date parseTime(String timeStr){
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		cal.add(Integer.parseInt(timeStr), Calendar.SECOND);
		return cal.getTime();
	}
	
	/**
	 * �ҵ��ַ���������ĳ���ַ�����λ��
	 * @param name �ַ���
	 * @param names �ַ�������
	 * @return �ַ����������е�λ��  -1����û�ҵ�
	 */
	private int NameToIndex(String name,String[] names){
		int length=names.length;
		for(int i=0;i<length;i++){
			if(names[i].equals(name)||names[i]==name){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * ��ȡ�ļ�
	 * @param filePath �ļ�·��
	 * @param minierObject Ҫ��ȡ������
	 * @param dataItems ����������
	 */
	private void readFile(String filePath,String minierObject,DataItems dataItems){
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int minerObjectIndex=NameToIndex(minierObject, columns);
		if(minerObjectIndex==-1){
			throw new RuntimeException("δ�ҵ��ھ����");
		}
		int TimeColIndex=NameToIndex("Time(S)", columns);
		int SIPColIndex=NameToIndex("srcIP", columns);
		int DIPColIndex=NameToIndex("dstIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP SIP �������ļ���δ�ҵ�");
		}
		String line=null;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			if((columns[SIPColIndex].equals(ipPair[0])||columns[SIPColIndex].equals(ipPair[1]))&&
				(columns[DIPColIndex].equals(ipPair[0])||columns[DIPColIndex].equals(ipPair[1]))){
				dataItems.add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
			}
		}
	}
	
	private void readFile(String filePath,String minierObject,DataItems dataItems,String[] conditions){
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int minerObjectIndex=NameToIndex(minierObject, columns);
		if(minerObjectIndex==-1){
			throw new RuntimeException("δ�ҵ��ھ����");
		}
		int TimeColIndex=NameToIndex("Time(S)", columns);
		int SIPColIndex=NameToIndex("srcIP", columns);
		int DIPColIndex=NameToIndex("dstIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP SIP �������ļ���δ�ҵ�");
		}
		
		//�������� conditions
		for(int i=0;i<conditions.length;i++){
			String condition=conditions[i];
			String compareOper="";
			Pattern pattern=Pattern.compile("[><=]+");
			Matcher matcher=pattern.matcher(condition);
			if(matcher.find()){
				compareOper=matcher.group(1);
			}
			String[] conditionColumns=condition.split("[><=]+");
			int conditionIndex=NameToIndex(conditionColumns[0], columns);
			if(conditionIndex==-1){
				throw new RuntimeException("�����������ô���");
			}
			switch (compareOper) {
			case ">":
				condition=conditionIndex+","+">"+","+conditionColumns[1];
				break;
			case "<":
				condition=conditionIndex+","+"<"+","+conditionColumns[1];
				break;
			case ">=":
				condition=conditionIndex+","+">="+","+conditionColumns[1];
				break;
			case "<=":
				condition=conditionIndex+","+"<="+","+conditionColumns[1];
				break;
			case "==":
				condition=conditionIndex+","+"=="+","+conditionColumns[1];
				break;
			case "!=":
				condition=conditionIndex+","+"!="+","+conditionColumns[1];
				break;
			default:
				throw new RuntimeException("��ѯ�����޷�ȷ��");
			}
			conditions[i]=condition;
		}
		String line=null;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			if((columns[SIPColIndex]==ipPair[0]||columns[SIPColIndex]==ipPair[1])&&
				(columns[DIPColIndex]==ipPair[0]||columns[DIPColIndex]==ipPair[1])){
				//�������
				boolean fixCondition=true;
				for(int i=0;i<conditions.length;i++){
					if(!fixCondition){
						break;
					}
					String[] conditionColumn=conditions[i].split(",");
					String compareOper=conditionColumn[1];
					int conditionIndex=Integer.parseInt(conditionColumn[0]);
					switch (compareOper) {
					case "<":
						if(!(Double.parseDouble(columns[conditionIndex])<
								Double.parseDouble(conditionColumn[2]))){
							fixCondition=false;
						}
						break;
					case "<=":
						if(!(Double.parseDouble(columns[conditionIndex])<=
								Double.parseDouble(conditionColumn[2]))){
							fixCondition=false;
						}
						break;
					case ">":
						if(!(Double.parseDouble(columns[conditionIndex])>
								Double.parseDouble(conditionColumn[2]))){
							fixCondition=false;
						}
						break;
					case ">=":
						if(!(Double.parseDouble(columns[conditionIndex])>=
								Double.parseDouble(conditionColumn[2]))){
							fixCondition=false;
						}
						break;
					case "==":
						if(!(columns[conditionIndex]==conditionColumn[2])){
							fixCondition=false;
						}
						break;
					case "!=":
						if(!(columns[conditionIndex]!=conditionColumn[2])){
							fixCondition=false;
						}
						break;
					default:
						break;
					}
				}
				if(fixCondition){
					dataItems.add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
				}
			}
		}
	}
	
	public TaskElement getTask() {
		return task;
	}

	public void setTask(TaskElement task) {
		this.task = task;
	}

	public String[] getIpPair() {
		return ipPair;
	}

	public void setIpPair(String[] ipPair) {
		this.ipPair = ipPair;
	}

	public boolean isTextSource() {
		return textSource;
	}
	

	public void setTextSource(boolean textSource) {
		this.textSource = textSource;
	}

	
	
	
	public static void main(String[] args){
		Calendar cal=Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		Date startDate=cal.getTime();
		cal.add(100, Calendar.DAY_OF_YEAR);
		Date endDate=cal.getTime();
		
		nodePairReader reader=new nodePairReader();
		reader.readInputBetween(startDate, endDate);
		
		
	}
}

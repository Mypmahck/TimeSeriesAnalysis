package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class PortTreatyInputs implements IReader {
	TaskElement task;
	OracleUtils conn;
	DataItems data;
	String whichPort = "";
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	// private string node;
	public PortTreatyInputs(TaskElement task) {
		this.task = task;
		if (task.getDataSource().equals("DataBase")) {
			conn = new OracleUtils();
			if (!conn.tryConnect())
				System.out.println("数据库无法连接！");
			// UtilsUI.showErrMsg("锟斤拷菘锟斤拷薹锟斤拷锟斤拷樱锟�);
		}

	}
	public void setWhichPort(String port)
	{
		whichPort = port ;
	}
	public String getWhichPort()
	{
		return whichPort;
	}
	@Override  //没有参数，则对所有的端口进行统计分析
	public DataItems readInputByText() {

		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line = null;
		try {
			FileInputStream is = new FileInputStream(new File(task.getSourcePath()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			reader.readLine();
			while((line = reader.readLine()) != null)
			{
				String[] values = line.split(","); 
				String srcIp = values[1];
				String destIp = values[2];
				lastYear.add(Calendar.HOUR_OF_DAY, 1);
				if(srcIp.compareTo(whichPort) == 0 || destIp.compareTo(whichPort) == 0)
					dataItems.add1Data(lastYear.getTime(), values[5]); 
				
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
	}

	@Override
	public DataItems readInputByText(String[] conditions) {

		String whichPort = conditions[0];   //得到需要预测的iP
		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line = null;
		try {
			FileInputStream is = new FileInputStream(new File(task.getSourcePath()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			reader.readLine();
			while((line = reader.readLine()) != null)
			{
				String[] values = line.split(","); 
				String srcIP = values[1];
				String destIp = values[2];
				if(srcIP.compareTo(whichPort) == 0 || srcIP.compareTo(whichPort) == 0)
				{
					lastYear.add(Calendar.HOUR_OF_DAY, 1);
					dataItems.add1Data(lastYear.getTime(), values[5]);
				}
				
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
		
	}

	@Override
	public DataItems readInputBySql() {
		String sqlStr = "";   // 鏍规嵁浼犲弬鏉′欢杩涜璇诲彇鏁版嵁搴撲腑鐨勬暟鎹�
		// String whichNode = condition;

		conn.closeConn();
		ResultSet rs = conn.sqlQuery(sqlStr); // 需要根据数据库的表格式给出读取语句
		if (rs == null) {
			System.out.println("no data satisfied data ,please check...");
			return null;
		}
		ResultSetMetaData meta = null;
		int numRecords = 0;
		int condition_num = 0;
		try {

			meta = rs.getMetaData();
			int numCols = meta.getColumnCount();
			data = new DataItems();
			while (rs.next()) {
				numRecords++;

				String time = rs.getString(1).trim();
				Date d = parseTime(time); // 将数值时间转化为标准时间格式

				String srcIP = rs.getString(2); // source IP
				String desIP = rs.getString(3); // destination IP
				String traffic = rs.getString(4); // traffic
				if (srcIP.compareTo(whichPort) == 0 || desIP.compareTo(whichPort) == 0) // 只读取单个节点的数据
				{
					if (d != null) {
						condition_num++;
						data.add1Data(d, traffic);
					} else
						System.out.println("时间为空，无法分析");
				}

			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("该表总记录数：" + numRecords + " 满足条件的记录数有："
				+ condition_num);
		// System.out.println("锟斤拷取锟斤拷锟�" + data.getLength() + "锟斤拷锟斤拷录锟斤拷");

		return data;
	}

	@Override
	public DataItems readInputBySql(String condition) {

		String sqlStr = condition; // sql语句需要根据实际情况的表进行书写
		String whichPort = condition;

		StringBuilder sqlsb=new StringBuilder();
		sqlsb.append("SELECT *,").append(task.getMiningObject()).append(" from ")
		     .append(conn.DB_TABLE).append(" where ");
		if(task.getFilterCondition().length()>0){
			sqlsb.append(task.getFilterCondition());
			whichPort = task.getFilterCondition();
		}
		
		if(!conn.isOpen()){
			conn.openConn();
		}
		conn.closeConn();
		ResultSet rs = conn.sqlQuery(sqlStr);
		if (rs == null) {
			System.out.println("no data satisfied data ,please check...");
			return null;
		}
		ResultSetMetaData meta = null;
		int numRecords = 0;
		int condition_num = 0;
		try {

			meta = rs.getMetaData();
			int numCols = meta.getColumnCount();
			data = new DataItems();
			while (rs.next()) {
				numRecords++;

				String time = rs.getString(1).trim();
				Date d = parseTime(time); // 璇诲彇鏃堕棿锛屾槸涓�釜鏁存暟锛岄渶瑕佽浆鍖栦负date

				String srcIP = rs.getString(2); // source IP
				String desIP = rs.getString(3); // destination IP
				String traffic = rs.getString(4); // traffic

				if (srcIP.compareTo(whichPort) == 0 || desIP.compareTo(whichPort) == 0) // 只读取单个节点的数据
				{
					if (d != null) {
						condition_num++;
						data.add1Data(d, traffic);
					} else
						System.out.println("时间为空，无法分析");
				}

			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("该表总记录数：" + numRecords + " 满足条件的记录数有："
				+ condition_num);
		// System.out.println("锟斤拷取锟斤拷锟�" + data.getLength() + "锟斤拷锟斤拷录锟斤拷");

		return data;
	}
	private Date parseTime(String timeStr) {
		int difLen = sdf.toPattern().length() - timeStr.length();
		StringBuilder sb = new StringBuilder();
		sb.append(timeStr);
		for (int i = 0; i < difLen; i++)
			sb.append("0");
		try {
			return sdf.parse(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Map<String, DataItems> readAllRoute() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

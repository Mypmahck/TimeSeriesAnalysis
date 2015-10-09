package cn.InstFS.wkr.NetworkMining.PcapStatistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.OracleUtils;

public enum IPStreamPool {
	instance;
	
	private Map<String,IPStream>streams = new HashMap<String,IPStream>();
	private Map<String,Long>aliveStreamTime = new HashMap<String,Long>();
	private Map<String, IPStream> aliveStream=new HashMap<String, IPStream>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String logFile = "./configs/temp.txt";
	private static String DB_TABLE = "�¼�2";
	
	private FileWriter fw;
	private OracleUtils oracle;
	private static boolean writeToFile = true;	// д���ļ�(true)��д�����ݿ�
	static {
		File f = new File(logFile);
		if (f.exists())
			f.delete();
	}
	
	public Map<String,IPStream> getAllStreams(){
		return aliveStream; //�������е�stream
	}
	/**
	 * ����ipͨ���¼�����ѯ�Ƿ����Ѿ���ͳ�Ƶ�������<br>
	 * ���У��򷵻�<br>
	 * ���ޣ��򴴽�������
	 * @param stream
	 * @return
	 */
	public IPStream createOrGetIPStream(IPStream stream){
		return createOrGetIPStream(stream.getSrcIP(), stream.getDstIP(),
				stream.getProtoType(),stream.getHops(),stream.getTraffic(),stream.getTimeStart());
	}
	public IPStream createOrGetIPStream(String srcIP, String dstIP, 
			String protoType,long hops,long traffic,long timeStamp){
		String key = getKeyFromTcpElems(srcIP,dstIP,protoType);
		if (aliveStream.containsKey(key)){
//			long d = aliveStreamTime.get(key);
//			key += "_" + d;
//			return streams.get(key);
			return aliveStream.get(key);
		}else{
			IPStream stream = new IPStream();
			stream.setKeyElement(srcIP, dstIP,protoType,hops,traffic,timeStamp);
			stream.setTimeEnd(timeStamp);
			stream.setTimeStart(timeStamp);
			aliveStream.put(key, stream);
//			aliveStreamTime.put(key, timeStamp);
//			key += "_" + timeStamp;
//			streams.put(key, stream);
			return stream;
		}
	}
	
	public IPStream getIPStream(String srcIP, String dstIP, 
			int srcPort, int dstPort, String protoType){
		String key = getKeyFromTcpElems(srcIP,dstIP,protoType);
//		if (!aliveStreamTime.containsKey(key))
//			return null;
//		long d = aliveStreamTime.get(key);
//		key += "_" + d;
//		return streams.get(key);
		
		if(aliveStream.containsKey(key)){
			return aliveStream.get(key);
		}else{
			return null;
		}
	}
	/**
	 * ��������������������Ϣ
	 * @ע�� �ڸ���֮ǰ��<br>
	 * �����ȼ���°���ʱ���Ƿ������һ����̫��ʱ�䣨��2���ӣ���ע�⿴�����������û�б�ע�͵��������̫������������������������µ�������<br>
	 * �������TCP��FIN��־���ж����Ƿ����
	 * @param stream	�����µ�������
	 * @param ipEvent	һ��ͨ��
	 */
	public IPStream updateTraffic(IPStream stream, IPStream ipEvent, boolean isTcpFIN){
		if (isTcpFIN){
			endStream(stream);
		}
		if (ipEvent.getTimeStart()-stream.getTimeEnd()>6*60*1000){//����6����
			endStream(stream);
			IPStream stream_new = createOrGetIPStream(ipEvent);
			return stream_new;
		}else if(ipEvent.getTimeStart()-stream.getTimeEnd()>0){//����ipEvent��Stream�ǲ���ͬһ��stream
			long traffic=stream.getTraffic() + ipEvent.getTraffic();
			stream.setTraffic(traffic);
			stream.setTimeEnd(ipEvent.getTimeStart());
		}
		
		return stream;
	}
	public void endStream(IPStream stream){
		String key = getKeyFromTcp5Elems(stream);
		write1Stream(stream);
		aliveStream.remove(key);
	}
	private String getKeyFromTcpElems(String srcIP, String dstIP, 
			String protoType){
		StringBuilder sb = new StringBuilder();
		sb.append(srcIP).append("-")
			.append(dstIP).append("\t")
			.append(protoType);
		return sb.toString();
	}
	private String getKeyFromTcp5Elems(IPStream stream){
		return getKeyFromTcpElems(stream.getSrcIP(), stream.getDstIP(),
			stream.getProtoType());
	}
	private void write1Stream(IPStream stream){
		if (writeToFile)
			write1Stream2File(stream);
		else
			write1Stream2Oracle(stream);
	}
	private void write1Stream2Oracle(IPStream stream){
		if (oracle == null){
			oracle = new OracleUtils();
			oracle.setDB_TABLE(DB_TABLE);
		}		
		oracle.openConn();
		String sql = "INSERT INTO " + oracle.DB_TABLE +
				" (�¼�����ʱ��,�¼�����ʱ��,��������㼶,���ͽڵ���,���սڵ���,����,Э������) values"+
				" ('" + sdf.format(new Date(stream.getTimeStart())) +"',"+
				"'" + sdf.format(new Date(stream.getTimeEnd()))+ "'," +
				"'" + "200" + "'," +	// Э���
				"'" + stream.getSrcIP() + "'," +
				"'" + stream.getDstIP() + "'," +
				stream.getTraffic() + "," +
				"'" + stream.getProtoType() + "'" +
				")";
		
		oracle.sqlUpdate(sql);
	}
	private void write1Stream2File(IPStream stream){		
		try {
			if (fw == null)
				fw = new FileWriter(new File(logFile), true);
			Date d = new Date(stream.getTimeStart());
			Date d2 = new Date(stream.getTimeEnd());
			StringBuilder sb = new StringBuilder();
			sb.append(sdf.format(d))
			.append(" - ")
			.append(sdf.format(d2)).append("\t");
			sb.append(getKeyFromTcp5Elems(stream));
			sb.append("\t").append(stream.getTraffic());
			sb.append("\t").append(stream.getHops()).append("\r\n");

			fw.write(sb.toString());
		} catch (IOException e) {
			if (fw != null)
				try{fw.close();fw = null;}catch(Exception ee){}
		}finally{			
		}
	}
	
	public void endAllStreams(){
		for (IPStream stream : aliveStream.values())
			write1Stream(stream);
		if (fw != null)
			try {
				fw.close();
				fw = null;
			} catch (Exception ee) {
			}
		if (oracle != null){
			oracle.closeConn();
			oracle = null;
		}
		aliveStreamTime.clear();
		streams.clear();
	}
	
	
	
	
}

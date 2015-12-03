package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.List;



public interface IReader {	
	//��ȡ�ı��ļ��е�����
	public DataItems readInputByText();
	//��ȡ���ݿ��е�����
	public DataItems readInputBySql();
	
	/**
	 * ��ȡ���ݿ��з���Ҫ�������  
	 * @param condition Ϊsql�����ʽ�����ݹ������� �� "sip=='10.0.1.1' and dip=='10.0.1.2'"
	 * @return ����Ҫ�������
	 */
	public DataItems readInputBySql(String condition);
	
	/**
	 * ��ȡ�ı��ļ��еķ���Ҫ�������
	 * @param condistions ��������ʽ�����ݹ������� 
	 * ��  conditions[0]Ϊsip=='10.0.1.1'    conditions[1]Ϊdip=='10.0.1.2'
	 * @return
	 */
	public DataItems readInputByText(String[] condistions);
}

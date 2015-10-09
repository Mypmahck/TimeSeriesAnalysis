package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum DiscreteMethod {
	 None("��"),
	��������ֵ��Χ��ͬ("��������ֵ��Χ��ͬ"),
	���������ݵ�����ͬ("���������ݵ�����ͬ"),
	�Զ���˵�("�Զ���˵�");
	
	private String val;
	private DiscreteMethod(String str){
		this.val = str;
	}
	
	@Override
	public String toString() {
		return val;
	}
	public static DiscreteMethod fromString(String str){
		try{
			return valueOf(str);
		}catch(Exception e){
			return None;
		}
		
	}
}

package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum AggregateMethod {
	Aggregate_MAX("���ֵ"),
	Aggregate_MIN("��Сֵ"),
	Aggregate_SUM("���"),
	Aggregate_MEAN("ƽ��"),
	Aggregate_NONE("��");
	
	final String value;
	AggregateMethod(String val){
		this.value = val;
	}
	
	
	@Override
	public String toString() {
		return value;
	}
	public static AggregateMethod fromString(String str){
		try{
			AggregateMethod ret =  AggregateMethod.valueOf(str);
			return ret;
		}catch(Exception e){
			return Aggregate_SUM;
		}
	}
}

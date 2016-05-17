package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsFP;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;

import com.l2fprod.common.propertysheet.Property;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;

public class PropertyUI_TaskElement implements IObjectDescriptor<TaskElement> {

	private TaskElement core;
	
	PropertyUI_MiningParamsSM sm = null;
	PropertyUI_MiningParamsTSA tsa = null;
	PropertyUI_MiningParamsPM pm = null;
	PropertyUI_MiningParamsFP fp=null;
	PropertyUI_MiningParamsOM om=null;
	PropertyUI_MiningParamsFM fm=null;
	HashMap<String, String> displayNames = new HashMap<String,String>();
	
	String [] names = new String[]{"taskName", "comments", "miningObject", 
			"discreteMethod", "discreteEndNodes","discreteDimension","miningAlgo","taskRange","range",
			"aggregateMethod", "granularity",
			"filterCondition", "miningMethod", "miningParams", 
			"dateStart", "dateEnd","dataSource","sourcePath","sqlStr"};
	String []CnNames = new String[]{"��������", "˵��", "�ھ����",
			"��ɢ������", "��ɢ�����õĶ˵�","��ɢ���ά��","�ھ��㷨","����Χ","ѡ��ڵ�ֵ",
			"���ݾۺϷ���","ʱ������(s)",
			"��������", "�ھ򷽷�", "�ھ����",
			"��ʼʱ��","����ʱ��","������Դ","�ı�·��","���ݿ�"};
	
	
	public PropertyUI_TaskElement() {
		this(new TaskElement());		
	}
	
	public PropertyUI_TaskElement(TaskElement core) {
		if (core != null)
			this.core = core;
		else
			this.core = new TaskElement();
		
		displayNames.clear();
		for(int i = 0; i < names.length; i ++)
			displayNames.put(names[i], CnNames[i]);
	}
	@Override
	public String getDisplayName() {
		return "��������";
	}
	
	private String getDisplayNameOfStr(String str){
		if(displayNames.containsKey(str))
			return displayNames.get(str);
		else
			return str;
	}

	@Override
	public List<EnhancedPropertyDescriptor> getProperties() {

		List<EnhancedPropertyDescriptor>props = new ArrayList<EnhancedPropertyDescriptor>();
		props.add(getPropDesc("taskName", 0));
		props.add(getPropDesc("comments", 0));
		props.add(getPropDesc("miningObject", 0));
		
		props.add(getPropDesc("discreteMethod", 0, "�����Ҫ���ȶ����ݽ�����ɢ������ѡ�����<br>" +
				"1.������ɢ��<br>2.ʹ�ø�������ֵ��Χ��ͬ<br>3.ʹ�ø��������ݵ�����ͬ<br>4.�Զ���˵�"));
//		switch (core.getDiscreteMethod()) {
//		case ��������ֵ��Χ��ͬ:
//		case ���������ݵ�����ͬ:
		props.add(getPropDesc("discreteDimension", 0));
//		case �Զ���˵�:
		props.add(getPropDesc("discreteEndNodes", 0));
//					"����ɢ������Ϊ'�Զ��巽��'�������������Ч�������������Ч��"));
//		default:
//		}
		props.add(getPropDesc("miningAlgo",0));
		props.add(getPropDesc("taskRange",0));
		props.add(getPropDesc("range",0));
		props.add(getPropDesc("granularity", 0, "���õ��������£�<br>" +
				"�֣�60<br>" +
				"ʱ��3600<br>" +
				"�죺86400"));		
		props.add(getPropDesc("aggregateMethod", 0, 
				"����ɢֵ���ַ���ֵ������������Ч��ֱ�ӽ����ֵ���������ո����<br>" +
				"������ֵ����Ϊ��͡�ƽ�������ֵ����Сֵ��"));
		
		props.add(getPropDesc("filterCondition", 0, 
				"��SQL����е�WHERE �Ӿ䣬�硰���� > 10��"));
		props.add(getPropDesc("miningMethod", 0));
		props.add(getPropDesc("miningParams", 0));
		props.add(getPropDesc("dateStart", 0));
		props.add(getPropDesc("dateEnd", 0));
		props.add(getPropDesc("dataSource",0));
		props.add(getPropDesc("sourcePath",0));
//		props.add(getPropDesc("pathSource",0));
//		props.add(getPropDesc("sqlStr",0));
		return props;
	}
	@Override
	public TaskElement getCore() {
		return core;
	}
	public void setCore(TaskElement task){
		this.core = task;
	}
	private EnhancedPropertyDescriptor getPropDesc(String propStr, int id){
		return getPropDesc(propStr, id, "");
	}
	private EnhancedPropertyDescriptor getPropDesc(String propStr, int id, String descStr) {
		if (propStr == null || propStr.length() == 0)
			return null;
        try {
        	String propStrUpper = propStr.substring(0,1).toUpperCase() + propStr.substring(1);
            PropertyDescriptor desc = new PropertyDescriptor(propStr, this.getClass(), 
            		"get" + propStrUpper, "set" + propStrUpper);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, id);
            desc.setDisplayName(getDisplayNameOfStr(propStr));
            desc.setShortDescription(descStr);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
	public String getTaskName() {
		return core.getTaskName();
	}
	public MiningAlgo getMiningAlgo()
	{
		return core.getMiningAlgo();
	}
	public void setMiningAlgo(MiningAlgo miningAlgo)
	{
		core.setMiningAlgo(miningAlgo);
	}
	public void setTaskRange(TaskRange taskRange)
	{
		core.setTaskRange(taskRange);
	}
	public TaskRange getTaskRange()
	{
		return core.getTaskRange();
	}
	public void setTaskName(String taskName) {
		core.setTaskName(taskName);
	}
	public void setRange(String taskValue)
	{
		core.setRange(taskValue);
	}
	public String getRange()
	{
		return core.getRange();
	}
	public String getComments() {
		return core.getComments();
	}

	public void setComments(String comments) {
		core.setComments(comments);
	}

	public String getMiningObject() {
		return core.getMiningObject();
	}

	public void setMiningObject(String miningObject) {
		core.setMiningObject(miningObject);
	}

	public AggregateMethod getAggregateMethod() {
		return core.getAggregateMethod();
	}
	public void setAggregateMethod(AggregateMethod aggregateMethod) {
		core.setAggregateMethod(aggregateMethod);
	}

	public String getFilterCondition() {
		return core.getFilterCondition();
	}

	public void setFilterCondition(String filterCondition) {
		core.setFilterCondition(filterCondition);
	}

	public IObjectDescriptor getMiningParams() {
		if (core.getMiningMethod().equals(
				MiningMethod.MiningMethods_OutliesMining)) {
//			if (tsa == null)
				om = new PropertyUI_MiningParamsOM(core.getMiningParams());
			return om;
		}
		else if (core.getMiningMethod().equals(MiningMethod.MiningMethods_SequenceMining)){
//			if (sm == null)
				sm = new PropertyUI_MiningParamsSM(core.getMiningParams());
			return sm;
		}
		else if (core.getMiningMethod().equals(MiningMethod.MiningMethods_PeriodicityMining)){
			pm = new PropertyUI_MiningParamsPM(core.getMiningParams());
			return pm;

		}
		else if (core.getMiningMethod().equals(MiningMethod.MiningMethods_FrequenceItemMining))
		{
			fp = new PropertyUI_MiningParamsFP(core.getMiningParams());
			return fp;
		}
		else if(core.getMiningMethod().equals(MiningMethod.MiningMethods_PredictionMining))
		{
			fm=new PropertyUI_MiningParamsFM(core.getMiningParams());
			return fm;
		}

		else
			return null;
	}

	public void setMiningParams(IObjectDescriptor miningParams) {
		core.setMiningParams((IParamsNetworkMining)miningParams.getCore());
	}

	public MiningMethod getMiningMethod() {
		return core.getMiningMethod();
	}
	public void setMiningMethod(MiningMethod miningMethod) {
		core.setMiningMethod(miningMethod);
	}
	public void setDateStart(Date dateStart){
		core.setDateStart(dateStart);
	}
	public Date getDateStart(){
		return core.getDateStart();
	}
	public void setDateEnd(Date dateEnd){
		core.setDateEnd(dateEnd);
	}
	public Date getDateEnd(){
		return core.getDateEnd();
	}
	public void setSqlStr(String sqlStr){
		core.setSqlStr(sqlStr);
	}
	public String getSqlStr(){
		return core.getSqlStr();
	}

	public int getGranularity() {
		return core.getGranularity();
	}
	public void setGranularity(int granularity) {
		core.setGranularity(granularity);
	}
	public DiscreteMethod getDiscreteMethod() {
		return core.getDiscreteMethod();
	}
	public void setDiscreteMethod(DiscreteMethod discreteMethod) {
		core.setDiscreteMethod(discreteMethod);
	}
	public String getDiscreteEndNodes() {
		return core.getDiscreteEndNodes();
	}
	public void setDiscreteEndNodes(String discreteEndNodes) {
		core.setDiscreteEndNodes(discreteEndNodes);
	}
	public int getDiscreteDimension() {
		return core.getDiscreteDimension();
	}
	public void setDiscreteDimension(int discreteDimension) {
		core.setDiscreteDimension(discreteDimension);
	}
	public String getPathSource()
	{
		return core.getPathSource();
	}
	public void setPathSource(String pathSource)
	{
		core.setPathSource(pathSource);
	}
	public String getDataSource()
	{
		return core.getDataSource();
	}
	public void setDataSource(String dataSource)
	{
		core.setDataSource(dataSource);
	}
	public void setSourcePath(String sourcePath)
	{
		core.setSourcePath(sourcePath);
	}
	public String getSourcePath()
	{
		return core.getSourcePath();
	}


}

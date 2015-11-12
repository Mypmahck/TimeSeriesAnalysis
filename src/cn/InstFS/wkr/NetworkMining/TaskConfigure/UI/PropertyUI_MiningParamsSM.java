package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;

public class PropertyUI_MiningParamsSM implements IObjectDescriptor<ParamsSM>{
	ParamsSM core;
	
	String [] names = new String[]{"sizeWindow", "stepWindow", "minSeqLen", "minSupport"};
	String []CnNames = new String[]{"ʱ�䴰��(s)", "����(s)", "��С���г���", "��С֧�ֶ�(0-1)",
			"��������", "�ھ򷽷�", "�ھ����"};
	HashMap<String, String> displayNames = new HashMap<String,String>();
	
	
	public PropertyUI_MiningParamsSM(IParamsNetworkMining core) {
		this.core = (ParamsSM) core;
		
		displayNames.clear();
		for(int i = 0; i < names.length; i ++)
			displayNames.put(names[i], CnNames[i]);		
	}
	@Override
	public String getDisplayName() {
		return "�ھ����";
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

		props.add(getPropDesc("sizeWindow", 0, "���ڴ�С"));
		props.add(getPropDesc("stepWindow", 0, "���ڲ���������<br>����һ������ȣ���ǰ������ʱ�����ƶ��˶���"));
		props.add(getPropDesc("minSeqLen", 0, "�ھ������������г���"));
		props.add(getPropDesc("minSupport", 0, "��С֧�ֶ�����<br>ֵԽС�����ھ��ٶ�Խ�����ھ�����Խ�ḻ<br>ֵԽ���ھ��ٶ�Խ�죬�������Խ���"));

		return props;
	}

	@Override
	public ParamsSM getCore() {
		return core;
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
	
	
	public double getMinSupport() {
		return core.getMinSupport();
	}
	public void setMinSupport(double minSupport) {
		core.setMinSupport(minSupport);
	}
	public double getSizeWindow() {
		return core.getSizeWindow();
	}
	public void setSizeWindow(int sizeWindow) {
		core.setSizeWindow(sizeWindow);
	}
	public double getStepWindow() {
		return core.getStepWindow();
	}
	public void setStepWindow(int stepWindow) {
		core.setStepWindow(stepWindow);
	}
	public int getMinSeqLen() {
		return core.getMinSeqLen();
	}
	public void setMinSeqLen(int minSeqLen) {
		core.setMinSeqLen(minSeqLen);
	}
	
}

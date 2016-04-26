package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPP;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;

public class PropertyUI_MiningParamsPP implements IObjectDescriptor<ParamsPP>{
    ParamsPP core;



    String [] names = new String[]{"dimension", "periodThreshold", "endNodes", "discreteMethod"};
    String []CnNames = new String[]{"��ɢֵ����", "�������", "����˵㣨���Ÿ�����", "��ɢ������"};
    HashMap<String, String> displayNames = new HashMap<String,String>();

    public PropertyUI_MiningParamsPP(IParamsNetworkMining core) {
        displayNames.clear();
        for(int i = 0; i < names.length; i ++)
            displayNames.put(names[i], CnNames[i]);
        if (core.getClass().equals(ParamsPP.class))
            this.core = (ParamsPP) core;
        else
            this.core = new ParamsPP();
    }
    private String getDisplayNameOfStr(String str){
        if(displayNames.containsKey(str))
            return displayNames.get(str);
        else
            return str;
    }
    @Override
    public String getDisplayName() {
        return "�ھ����";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        List<EnhancedPropertyDescriptor> props = new ArrayList<EnhancedPropertyDescriptor>();
        props.add(getPropDesc("periodThreshold", 0, "�����Լ������"));
//		props.add(getPropDesc("dimension", 0, "������ֵΪ��ɢ�����ֵ��Ч<br>���򣬽�����ֵ��ɢ��Ϊ�������"));
//		props.add(getPropDesc("discreteMethod", 0, "��ɢ������:<br>1.ʹ�ø�������ֵ��Χ��ͬ<br>2.ʹ�ø��������ݵ�����ͬ<br>3.�Զ���˵�"));
//		props.add(getPropDesc("endNodes", 0, "��ɢ��ʱ��ʹ�õ���ֵ����˵㣬���Ÿ���<br>ע�⣺���ڲ����Զ�����ɢ������ʱ��Ч��"));
        return props;
    }

    @Override
    public ParamsPP getCore() {
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


    public Double getPeriodThreshold(){
        return core.getPeriodThreshold();
    }
//	public int getDimension() {
//		return core.getDimension();
//	}

    public void setPeriodThreshold(Double periodThreshold){
        core.setPeriodThreshold(periodThreshold);
    }
//	public void setDimension(int dimension) {
//		core.setDimension(dimension);
//	}

//	public DiscreteMethod getDiscreteMethod() {
//		return core.getDiscreteMethod();
//	}
//	public void setDiscreteMethod(DiscreteMethod discreteMethod) {
//		core.setDiscreteMethod(discreteMethod);
//	}

//	public String getEndNodes() {
//		return core.getEndNodes();
//	}
//	public void setEndNodes(String endNodes) {
//		core.setEndNodes(endNodes);
//	}
}

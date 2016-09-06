package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * @author LYH
 * 显示异常点和异常度*/
public class ChartPanelShowAbl extends JPanel{
	JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);
    public static int timeGranunity = 3600;
    private ChartPanelShowAbl() {
        // 创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 15));
        // 设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 10));
        // 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 10));
        // 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);

        setLayout(new BorderLayout());

    }
    ChartPanelShowAbl(String title, String timeAxisLabel, String valueAxisLabel,
                      XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/){
        this();
    }


    public void setTitle(String title){
        chart.setTitle(title);
    }
    public void setAxisXLabel(String x){
        chart.getXYPlot().getDomainAxis().setLabel(x);
    }
    public void setAxisYLabel(String y){
        chart.getXYPlot().getRangeAxis().setLabel(y);
    }
    public JFreeChart getChart(){
        return chart;
    }

    public void displayDataItems(DataItems items){
        if (items == null)
            return;
//        TimeSeriesCollection tsc = new TimeSeriesCollection();
//        TimeSeries ts = new TimeSeries("序列值");

        XYSeriesCollection tsc = new XYSeriesCollection();
        XYSeries ts = new XYSeries("序列值");


        int len = items.getLength();
        for (int i = 0; i < len; i ++){
            DataItem item = items.getElementAt(i);
            Date date = item.getTime();
            double val = Double.parseDouble(item.getData());
            //ts.addOrUpdate(items.getTimePeriodOfElement(i), val);
            ts.add(i,val);
        }
        tsc.addSeries(ts);
        chart.getXYPlot().setDataset(tsc);
    }
    
    //获取原始数据集
    public static XYDataset createNormalDataset(DataItems normal)
    {
        //获取正常数据的长度、
        int length=normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries("正常点");
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //为数据集添加数据
        for (int i = 0; i <length; i++) {
            DataItem temp=new DataItem();
            temp=normal.getElementAt(i);
            //xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData())); // 对应的横轴
            xyseries.add(i,Double.parseDouble(temp.getData()));
        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    
    //获取异常度数据集
    public static XYDataset createAbnormalDataset(DataItems abnor)
    {  // 统计异常点的长度
        int length=abnor.getLength();
        XYSeries xyseries = new XYSeries("异常度");
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        //添加数据值
        for (int i = 0; i < length; i++) {

            DataItem temp=new DataItem();
            temp=abnor.getElementAt(i);
            xyseries.add(i,Double.parseDouble(temp.getData()));
            
        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    
    //获取异常线段数据集
    public static List<XYDataset> createAbLineDataset(DataItems nor,List<DataItems> outSet){  
    	List<XYDataset> xyDatasetList = new ArrayList<XYDataset>();
    	for(int i=0;i<outSet.size();i++){
    		XYSeries xyseries = new XYSeries("");
            XYSeriesCollection xyseriescollection = new XYSeriesCollection();
            if(nor.getData().size()>0){
            	Date date1 = nor.getTime().get(0);
            	
    	        for (int j = 0; j < outSet.get(i).getLength(); j++) {
    	
    	            DataItem temp=new DataItem();
    	            temp=outSet.get(i).getElementAt(j);
    	   		 	Date date2 = temp.getTime();
    	   		 	long diff = date2.getTime()-date1.getTime();
    	   		 	long hour = diff/(1000*60*60);
    	   		 	long index = hour*3600/timeGranunity;
    	            xyseries.add(index,Double.parseDouble(temp.getData()));      	            
    	        }
    	        xyseriescollection.addSeries(xyseries);
    	    }
            xyDatasetList.add(xyseriescollection);           
    	}
    	return xyDatasetList;
    }
    public static JFreeChart createChart(DataItems oriItems,DataItems outdegree,List<DataItems> outSet,String yName)
    {

        //设置异常点提示红点大小
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-4D, -4D, 6D, 6D);

        XYDataset xydataset = createNormalDataset(oriItems);//原始值
        XYDataset xydataset1 = createAbnormalDataset(outdegree);//异常度
        List<XYDataset> xyDatasetlist = createAbLineDataset(oriItems,outSet);//异常点
        
        JFreeChart jfreechart = ChartFactory.createScatterPlot("异常度检测", "序列编号", yName, null);
        jfreechart.removeLegend();
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setDomainPannable(true);
        xyplot.setOrientation(PlotOrientation.VERTICAL);
        //设置原始坐标轴
        double max = getMaxPiont(oriItems);
        NumberAxis numberAxis0 = new NumberAxis();
        xyplot.setRangeAxis(0,numberAxis0);
        numberAxis0.setLowerMargin(10);
        numberAxis0.setLowerBound(0-max/10);
        numberAxis0.setUpperBound(max*1.01);
        numberAxis0.setLabelFont(new Font("微软雅黑",Font.BOLD,12));

        //设置异常度坐标轴 
        NumberAxis numberaxis1 = new NumberAxis("异常度");
        xyplot.setRangeAxis(1, numberaxis1);
        xyplot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        numberaxis1.setAutoTickUnitSelection(true);//数据轴的数据标签是否自动确定        
        numberaxis1.setTickUnit(new NumberTickUnit(1D));  //y轴单位间隔为1
        numberaxis1.setRange(0,10);
        numberaxis1.setUpperMargin(1);
        numberaxis1.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        NumberAxis xAxis=(NumberAxis)xyplot.getDomainAxis();
        xAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        xyplot.setDataset(0, xydataset);
        xyplot.setDataset(1, xydataset1);        
        //设置同一个横轴显示两组数据。
        xyplot.mapDatasetToDomainAxis(0, 0);
        xyplot.mapDatasetToRangeAxis(1, 1);
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setLowerMargin(1);
        numberaxis.setAutoRangeIncludesZero(false);
        
        //设置原始数据显示方式
        XYLineAndShapeRenderer xylineandshaperenderer0 = new XYLineAndShapeRenderer(); //绑定xydataset,原始数据
        xyplot.setDataset(0, xydataset);
        xyplot.setRenderer(0, xylineandshaperenderer0);
        xylineandshaperenderer0.setSeriesShapesVisible(0,false);
        xylineandshaperenderer0.setSeriesLinesVisible(0, true);
        xylineandshaperenderer0.setSeriesShape(0, double1);
        xylineandshaperenderer0.setSeriesPaint(0, Color.black);
        xylineandshaperenderer0.setSeriesFillPaint(0, Color.black);
        xylineandshaperenderer0.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
        xylineandshaperenderer0.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        
        //设置异常度显示方法
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();//绑定xydataset1,异常度显示        
        xyplot.setRenderer(1, xylineandshaperenderer1);
        xylineandshaperenderer1.setSeriesShapesVisible(0,false);
        xylineandshaperenderer1.setSeriesLinesVisible(0, true);
        xylineandshaperenderer1.setSeriesShape(0, double1);
        xylineandshaperenderer1.setSeriesPaint(0, new Color(65,105,225));
        xylineandshaperenderer1.setSeriesFillPaint(0, new Color(65,105,225));
        xylineandshaperenderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
        
        //设置异常线段显示方式
        XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();//绑定xydatasetlist,异常点显示
        Shape itemShape = ShapeUtilities.createDiamond((float) 0);
        xylineandshaperenderer2.setBaseShapesVisible(false);
        xylineandshaperenderer2.setBaseLinesVisible(true);
        xylineandshaperenderer2.setSeriesShape(0, itemShape);
        xylineandshaperenderer2.setSeriesPaint(0, new Color(255,0,0,255));
        xylineandshaperenderer2.setSeriesFillPaint(0, new Color(255,0,0,255));
        xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(2F));//设置线条粗细
        
        xylineandshaperenderer2.setSeriesShapesVisible(0, true);
        xylineandshaperenderer2.setBaseItemLabelsVisible(false);
        for(int i=0;i<outSet.size();i++){
        	xyplot.setDataset(i+2,xyDatasetlist.get(i));
        	xyplot.setRenderer(i+2, xylineandshaperenderer2);
        }       
        return jfreechart;
    }
    private static double getMaxPiont(DataItems items){
    	double max = 0;
    	for(int i=0;i<items.getLength();i++){
    		double data = Double.parseDouble(items.getData().get(i));
    		if(data>max){
    			max = data;
    		}
    	}
    	return max;
    }
}

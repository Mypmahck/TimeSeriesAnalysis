package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;



import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.UIs.TSATest;

/**
 * @author LYH
 * 显示异常线段和异常度*/

public class ChartPanelShowAbl extends JPanel{
    JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);
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
    //对异常点进行初始化
    public static XYDataset createAbnormalDataset(DataItems abnor)
    {  // 统计异常点的长度
        int length=abnor.getLength();
        XYSeries xyseries = new XYSeries("异常点");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();



        //添加数据值

        for (int i = 0; i < length; i++) {

            DataItem temp=new DataItem();
            temp=abnor.getElementAt(i);
            //xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData()));
            xyseries.add(i,Double.parseDouble(temp.getData()));

        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    public static JFreeChart createChart(DataItems nor,DataItems abnor,List<DataItems> outsetItems)
    {
//        XYDataset xydataset = createNormalDataset(nor);

        //设置异常点提示红点大小
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-4D, -4D, 6D, 6D);
//        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
//        xylineandshaperenderer.setBaseShapesVisible(false);
//        xylineandshaperenderer.setBaseLinesVisible(false);
//        xylineandshaperenderer.setSeriesShape(0, double1);
//        xylineandshaperenderer.setSeriesPaint(0, Color.black);
//        xylineandshaperenderer.setSeriesFillPaint(0, Color.yellow);
//        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.gray);
//        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(0.5F));
        XYDataset xydataset = createNormalDataset(nor);
        XYDataset xydataset1 = createAbnormalDataset(abnor);
        //JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("异常度检测", "时间", "值", xydataset1);
        JFreeChart jfreechart = ChartFactory.createScatterPlot("异常度检测", "时间", "值", xydataset);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setDomainPannable(true);
        xyplot.setOrientation(PlotOrientation.VERTICAL);
//        NumberAxis numberaxis = new NumberAxis("Domain Axis 2");
//        numberaxis.setAutoRangeIncludesZero(false);
//        xyplot.setDomainAxis(1, numberaxis);
        NumberAxis numberaxis1 = new NumberAxis("异常度");
        xyplot.setRangeAxis(1, numberaxis1);

        xyplot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
//        XYDataset xydataset1 = createDataset("abnormal value", 1D, new Minute(), 170);
        xyplot.setDataset(1, xydataset1);
//        xyplot.mapDatasetToDomainAxis(1, 1);
        //设置同一个横轴显示两组数据。
        xyplot.mapDatasetToRangeAxis(1, 1);
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
        xyplot.setDataset(0, xydataset);
        xyplot.setRenderer(0, xylineandshaperenderer1);
        xylineandshaperenderer1.setSeriesShapesVisible(0,false);
        xylineandshaperenderer1.setSeriesLinesVisible(0, true);
        xylineandshaperenderer1.setSeriesShape(0, double1);
        xylineandshaperenderer1.setSeriesPaint(0, Color.black);

        xylineandshaperenderer1.setSeriesFillPaint(0, Color.black);
        xylineandshaperenderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));

//        xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.gray);
//        xylineandshaperenderer1.setUseFillPaint(true);
        xylineandshaperenderer1.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        //设置异常度显示方法
        XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();
        //设置自动显示值。
        xylineandshaperenderer1.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());

        xyplot.setRenderer(1, xylineandshaperenderer2);
        xylineandshaperenderer2.setSeriesShapesVisible(0,false);
        xylineandshaperenderer2.setSeriesLinesVisible(0, true);
        xylineandshaperenderer2.setSeriesShape(0, double1);
        xylineandshaperenderer2.setSeriesPaint(0, Color.red);
        xylineandshaperenderer2.setSeriesFillPaint(0, Color.red);
        xylineandshaperenderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));

//        xylineandshaperenderer1.setBaseItemLabelsVisible(true);
        return jfreechart;
    }
}

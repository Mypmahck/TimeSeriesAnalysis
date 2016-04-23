package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

/**
 * Created by hidebumi on 2016/3/30.
 */
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
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
public class ChartPanelShowPre extends JPanel{
    JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);
    private ChartPanelShowPre() {
        // ����������ʽ
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        // ���ñ�������
        standardChartTheme.setExtraLargeFont(new Font("����", Font.BOLD, 15));
        // ����ͼ��������
        standardChartTheme.setRegularFont(new Font("����", Font.PLAIN, 10));
        // �������������
        standardChartTheme.setLargeFont(new Font("����", Font.PLAIN, 10));
        // Ӧ��������ʽ
        ChartFactory.setChartTheme(standardChartTheme);

        setLayout(new BorderLayout());

    }
    ChartPanelShowPre(String title, String timeAxisLabel, String valueAxisLabel,
                     XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/){
        this();
//        chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset);
//        ChartPanel p = new ChartPanel(chart);
//        add(p, BorderLayout.CENTER);
//
//
//        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
//
//        renderer.setBaseShapesVisible(false);
////		renderer.setBaseShape(itemShape);	// ���񲻹��ã�������setSeriesShape
//        renderer.setBaseLinesVisible(true);
////		renderer.setBasePaint(new Color(0));	// ���񲻹��ã�������setSeriesPaint
//
//        itemShape = ShapeUtilities.createDiamond((float) 3);
//        renderer.setSeriesShape(0, itemShape);
//        renderer.setSeriesPaint(0, new Color(255,0,0));
//
//        renderer.setSeriesShape(1, itemShape);
//        renderer.setSeriesPaint(1, new Color(0,255,0));
//
//        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
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
        TimeSeriesCollection tsc = new TimeSeriesCollection();

        TimeSeries ts = new TimeSeries("����ֵ");

        int len = items.getLength();
        for (int i = 0; i < len; i ++){
            DataItem item = items.getElementAt(i);
            Date date = item.getTime();
            double val = Double.parseDouble(item.getData());
            ts.addOrUpdate(items.getTimePeriodOfElement(i), val);
        }
        tsc.addSeries(ts);
        chart.getXYPlot().setDataset(tsc);
    }
    public static XYDataset createNormalDataset(DataItems normal)
    {
        //��ȡ�������ݵĳ��ȡ�
        int length=normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries("normal");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //Ϊ���ݼ��������

        for (int i = 0; i <length; i++) {
            DataItem temp=new DataItem();
            temp=normal.getElementAt(i);
            xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData())); // ��Ӧ�ĺ���

        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    //���쳣����г�ʼ��
    public static XYDataset createAbnormalDataset(DataItems abnor)
    {  // ͳ���쳣��ĳ���
        int length=abnor.getLength();
        XYSeries xyseries = new XYSeries("abnormal");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();



        //�������ֵ

        for (int i = 0; i < length; i++) {

            DataItem temp=new DataItem();
            temp=abnor.getElementAt(i);
            xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData()));
            xyseries.add((double)temp.getTime().getTime(),Double.parseDouble(temp.getData()));

        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    public static JFreeChart createChart(DataItems nor,DataItems abnor)
    {

        XYDataset xydataset = createNormalDataset(nor);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(" Ԥ��", "time", "value", xydataset);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        //�����쳣����ʾ����С
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-4D, -4D, 6D, 6D);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        //���ò��ɿ����㡣
        xylineandshaperenderer.setBaseShapesVisible(false);
        xylineandshaperenderer.setSeriesShape(0, double1);
        xylineandshaperenderer.setSeriesPaint(0, Color.black);
        xylineandshaperenderer.setSeriesFillPaint(0, Color.yellow);
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.gray);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(0.5F));
        XYDataset xydataset1 = createAbnormalDataset(abnor);
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
        xyplot.setDataset(1, xydataset1);
        xyplot.setRenderer(1, xylineandshaperenderer1);
        //���ò��ɼ����㡣
        xylineandshaperenderer1.setBaseShapesVisible(false);
        //���ÿ��Կ����ߡ�
        xylineandshaperenderer1.setSeriesLinesVisible(0, true);
        xylineandshaperenderer1.setSeriesShape(0, double1);
        //�����ߺ͵����ɫ��
        xylineandshaperenderer1.setSeriesPaint(0, Color.red);
        xylineandshaperenderer1.setSeriesFillPaint(0, Color.red);
        xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.gray);
        xylineandshaperenderer1.setUseFillPaint(true);
        //�������ݵ�ɼ�
        //xylineandshaperenderer1.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        //xylineandshaperenderer1.setBaseItemLabelsVisible(true);
        return jfreechart;
    }
}


package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerPM;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import ec.nbdemetra.ws.WorkspaceItem.InnerComparator;

import java.awt.GridLayout;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;

public class PanelShowResultsTSA extends JPanel implements IPanelShowResults{
	private Date now;	// ָʾ����ʱ�䣬���п��ܲ�������������ʱ��
	private INetworkMiner miner;
	private Timer timer;
	JDesktopPane desktopPane;
	
	ChartPanelShowTs chart1;
	ChartPanelShowTs chart2;
	
	
	public PanelShowResultsTSA(TaskElement task){
		this();		
		InitMiner(task);
	}
	private void InitMiner(TaskElement task){
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
	}
	/**
	 * Create the panel.
	 */
	private PanelShowResultsTSA() {
		InitChartScheme();
		setLayout(new GridLayout(0, 1, 0, 0));

		chart1 = new ChartPanelShowTs("ԭʼֵ", "ʱ��", "ֵ", null);
		chart2 = new ChartPanelShowTs("Ԥ��ֵ", "ʱ��", "", null);

		add(chart1);
		add(chart2);
	}

	private void InitChartScheme(){
		StandardChartTheme sct = new StandardChartTheme("");
		sct.setExtraLargeFont(new Font("����", 0, 20));
		sct.setLargeFont(new Font("����", 0, 15));		
		sct.setRegularFont(new Font("����", 0, 12));
		sct.setSmallFont(new Font("����", 0, 8));
		ChartFactory.setChartTheme(sct);
	}
	
	@Override
	public boolean start() {
		return miner.start();
	}

	@Override
	public boolean stop() {
		return miner.stop();
	}


	@Override
	public void setData(DataItems data) {
		
	}

	@Override
	public TaskElement getTask() {
		return miner.getTask();
	}
	@Override
	public INetworkMiner getMiner() {
		return miner;
	}
	@Override
	public void displayMinerResults() {
		if (this.isVisible()){
//			displayOrigTimeSeries();
		}
		displayMinerResults(miner.getResults());
		
	}
	private void displayOrigTimeSeries(){
		DataInputUtils diu = new DataInputUtils(miner.getTask());
		DataItems di;
		if (now == null)
			now = new Date(-1000, 1,1);
		di = diu.readInputAfter(now);
		
		if(di != null){
			List<Date> timeStr = di.getTime();
			List<String> dataStr = di.getData();
			
			int numData = di.getData().size();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			XYDataset dataset = chart1.getChart().getXYPlot().getDataset();
			if (dataset == null){
				dataset = new TimeSeriesCollection();
				chart1.getChart().getXYPlot().setDataset(dataset);
			}			
			TimeSeriesCollection tsc = (TimeSeriesCollection)dataset;
			TimeSeries ts = null;
			if (tsc.getSeriesCount() == 0){
				ts = new TimeSeries(miner.getTask().getMiningObject());
				tsc.addSeries(ts);
			}else
				ts = tsc.getSeries(0);
			int numNewData = 0;
			for (int i = 0; i < numData; i ++){
				Date time = null;
				time = timeStr.get(i);
				if (now != null && time.after(now)){
					numNewData ++;
					ts.addOrUpdate(new Second(time), Double.parseDouble(dataStr.get(i)));
				}			
			}
			if (ts.getItemCount() > 0){
				now = ts.getTimePeriod(ts.getItemCount() - 1).getEnd();
			}
			System.out.println("����ʾ" +ts.getItemCount() +  "�����ݵ㣡(����" + numNewData +"��)��" );
//			chart1.getXYPlot().setDataset(tsc);
		}
	}
	@Override
	public void displayMinerResults(MinerResults rslts) {
		if (rslts == null || rslts.getRetTSA() == null||
				!rslts.getMiner().getClass().equals(NetworkMinerTSA.class))
			return;
		DataItems originItems=rslts.getInputData();
		DataItems outliesItems = rslts.getRetTSA().getOutlies();
		DataItems predictItems = rslts.getRetTSA().getPredictItems();
		chart1.displayDataItems(originItems);
		if(outliesItems==null||outliesItems.getLength()==0){
			chart2.displayDataItems(predictItems);
		}else{
			chart2.displayDataItems(outliesItems);
		}
		
	}
}

class ShowTSA_TimerTask extends TimerTask{
	@Override
	public void run() {
		
	}	
}
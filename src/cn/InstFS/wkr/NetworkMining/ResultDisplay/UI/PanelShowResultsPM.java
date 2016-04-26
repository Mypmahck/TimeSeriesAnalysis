package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResultsPM;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JDesktopPane;
import javax.swing.JCheckBox;

public class PanelShowResultsPM extends JPanel implements IPanelShowResults{
	private INetworkMiner miner;
	DecimalFormat formatter = new DecimalFormat("0.00");
//	JDesktopPane desktopPane;
	
	SubPanelShowMinerResultsTs subPanel = new SubPanelShowMinerResultsTs();
	
	ChartPanelShowScatterPlot chartDistribute;
	JLabel lblIsPeriod;
	JLabel lblPeriodValue;
	JLabel lblFirstPossiblePeriod;
//	JLabel lblPeriodFeature;
	private JCheckBox chckShowFeatureVal;
	
	
	public PanelShowResultsPM(TaskElement task){
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
	private PanelShowResultsPM() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		
		subPanel = new SubPanelShowMinerResultsTs();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(subPanel, gbc_panel_1);
		
		JPanel panel = new JPanel(new GridLayout(0, 3));
		lblIsPeriod = new JLabel("�Ƿ�����:");
		lblPeriodValue = new JLabel("����ֵ:");
		lblFirstPossiblePeriod = new JLabel("��С�Ŀ�������:");
//		lblPeriodFeature = new JLabel("����ֵ:");
		panel.add(lblIsPeriod);
		panel.add(lblPeriodValue);
		panel.add(lblFirstPossiblePeriod);
//		panel.add(lblPeriodFeature);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		
		chckShowFeatureVal = new JCheckBox("��ʾ����ֵ");
		panel.add(chckShowFeatureVal);
		chckShowFeatureVal.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshFeatureVal();
			}
		});
		
		chartDistribute = new ChartPanelShowScatterPlot("�����ڷֲ�", "�����ڵĵ�", "ֵ", null);
		GridBagConstraints gbc_chartDistribute = new GridBagConstraints();
		gbc_chartDistribute.fill = GridBagConstraints.BOTH;
		gbc_chartDistribute.gridx = 0;
		gbc_chartDistribute.gridy = 2;
		add(chartDistribute, gbc_chartDistribute);
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
	public void displayMinerResults(MinerResults rets) {
		if (rets == null)
			return;
		subPanel.displayMinerResults(rets);
		if (rets.getMiner() == null||
				!rets.getMiner().getClass().equals(NetworkMinerPM.class))
			return;
		
		if (this.isVisible()){
			MinerResultsPM retPM = null;
			if (rets != null)
				retPM = rets.getRetPM();
			if (retPM == null)
				return;
			if(retPM.getHasPeriod()){
				lblIsPeriod.setText("�Ƿ����ڣ���");
				lblPeriodValue.setText("����ֵ��"+retPM.getPeriod() + 
						"(����ֵ:" + formatter.format(retPM.getFeatureValue()) + ")");
				lblFirstPossiblePeriod.setText("��С�Ŀ�������:" + retPM.getFirstPossiblePeriod() +
						"(����ֵ:" + formatter.format(retPM.getFeatureValues()[retPM.getFirstPossiblePeriod() - 1]) + ")");
//				lblPeriodFeature.setText("����ֵ��" + formatter.format(retPM.getFeatureValue()) + 
//						"�����ޣ�" + formatter.format(((ParamsPM)rets.getMiner().getTask().getMiningParams()).getPeriodThreshold()) + "��");
				DataItems items = retPM.getDistributePeriod();
			}else{
				lblIsPeriod.setText("�Ƿ����ڣ���");
				lblPeriodValue.setText("����ֵ����");
				lblFirstPossiblePeriod.setText("��С�Ŀ�������:");
//				lblPeriodFeature.setText("����ֵ��");
				chartDistribute.displayDataItems(new DataItems(), new DataItems(),new DataItems(),"����","���ֵ","��Сֵ");
			}
			refreshFeatureVal();
			
		}		
	}
	private void refreshFeatureVal(){
		MinerResults rets = miner.getResults();
		if (rets == null)
			return;
		if (chckShowFeatureVal.isSelected())
			chartDistribute.displayDataItems(rets.getRetPM().getFeatureValues(), "����������ֵ");
		else
			chartDistribute.displayDataItems(rets.getRetPM().getDistributePeriod(),rets.getRetPM().getMaxDistributePeriod(),rets.getRetPM().getMinDistributePeriod(), "����","���ֵ","��Сֵ");
	}
	@Override
	public void displayMinerResults() {
		MinerResults rets = miner.getResults();
		displayMinerResults(rets);		
	}
	
}
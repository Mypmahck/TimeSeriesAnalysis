package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.DialogConfigTask;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.PanelConfigTask;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.PanelListAllTasks;
import cn.InstFS.wkr.NetworkMining.UIs.SimulationUIs.PanelControlSimulationTime;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;

import com.l2fprod.common.swing.LookAndFeelTweaks;
import javax.swing.JToolBar;

public class MainFrame extends JFrame {
	public static MainFrame topFrame;
	public static int numTxtOutput = 10;
	public static int ii;
	// �Ӵ���Frame
	DialogConfigTask dialogConfigTask;
	
	private PanelListAllTasks panelListAllEvents;
	private PanelShowAllResults panelShowResults;
	
	
	private JPanel contentPane;
	private JTextArea txtOutput;
	private JCheckBox chckAutoShowResults;	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {		
					UtilsUI.InitFactories();
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//					UIManager.setLookAndFeel("com.l2fprod.common.swing.plaf.metal.MetalLookAndFeelAddons");
//					LookAndFeelTweaks.tweak();
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		MainFrame.topFrame = this;
		setTitle("��������ھ�������");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
				
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		toolBar.setSize(100, 20);
		
		JMenu mnFile = new JMenu("�ļ�(F)");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);
		
		JMenuItem menuExit = new JMenuItem("�˳�(X)");
		menuExit.setMnemonic(KeyEvent.VK_X);
		menuExit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}
		});
		mnFile.add(menuExit);
		
		JMenu mnTools = new JMenu("����(T)");
		mnTools.setMnemonic(KeyEvent.VK_T);
		menuBar.add(mnTools);
		
		JMenuItem menuConfigEvent = new JMenuItem("��������(P)");
		menuConfigEvent.setMnemonic(KeyEvent.VK_P);
		menuConfigEvent.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				openFrameConfigTask();
			}
		});
		
		JMenuItem menuStartMiningAll = new JMenuItem("ȫ����ʼ�ھ�(S)");
		menuStartMiningAll.setMnemonic(KeyEvent.VK_S);
		menuStartMiningAll.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkMinerFactory.getInstance().startAllMiners();
			}
		});
		mnTools.add(menuStartMiningAll);
		
		JMenuItem menuStopMiningAll = new JMenuItem("ȫ��ֹͣ�ھ�(E)");
		menuStopMiningAll.setMnemonic(KeyEvent.VK_E);
		menuStopMiningAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkMinerFactory.getInstance().stopAllMiners();
			}
		});
		mnTools.add(menuStopMiningAll);
		
		JSeparator separator = new JSeparator();
		mnTools.add(separator);
		mnTools.add(menuConfigEvent);
		
		JMenu mnView = new JMenu("��ͼ(V)");
		mnView.setMnemonic(KeyEvent.VK_V);
		menuBar.add(mnView);
		
		chckAutoShowResults = new JCheckBox("�Զ���ʾ���");
		chckAutoShowResults.setSelected(UtilsUI.autoChangeResultsPanel);
		chckAutoShowResults.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				UtilsUI.autoChangeResultsPanel = chckAutoShowResults.isSelected();
			}
		});
		mnView.add(chckAutoShowResults);
		
		toolBar.add(new PanelControlSimulationTime());
		
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(200);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		panelShowResults = new PanelShowAllResults();
		splitPane.setRightComponent(panelShowResults);
				
		panelListAllEvents = new PanelListAllTasks(panelShowResults);
		splitPane.setLeftComponent(panelListAllEvents);
		
		JScrollPane panelStatus = new JScrollPane();
		contentPane.add(panelStatus, BorderLayout.SOUTH);
		txtOutput = new JTextArea();
		txtOutput.setRows(3);
		txtOutput.setLineWrap(true);
		txtOutput.setEnabled(false);
		panelStatus.setViewportView(txtOutput);
		initUIs();
	}
	private void initUIs(){
		chckAutoShowResults.setSelected(UtilsUI.autoChangeResultsPanel);
		
	}
	private void exitProgram(){
		System.exit(0);
	}
	private void openFrameConfigTask(){
		if (dialogConfigTask != null && dialogConfigTask.isDisplayable())
			dialogConfigTask.setVisible(true);
		else{
			dialogConfigTask = new DialogConfigTask(this);
			dialogConfigTask.setVisible(true);
		}
	}
	public void appendOutput(String str){
		if (!str.startsWith("\r\n") && txtOutput.getText().length() != 0)
			str = "\r\n" + str;
		txtOutput.append(str);
		int numLine = txtOutput.getLineCount();
		if (numLine > numTxtOutput)
		{
			int numLineDel = numLine - numTxtOutput;
			synchronized (txtOutput) {
				for (int i = numLineDel - 1; i > -1; i--) {
					int offStart;
					int offEnd;
					try {
						offStart = txtOutput.getLineStartOffset(i);
						offEnd = txtOutput.getLineEndOffset(i);
						txtOutput.replaceRange("", offStart, offEnd);
					} catch (BadLocationException e) {
						break;
					}
				}
			}
			
		}
	}
//	public void refreshAllTasks(){
//		panelListAllEvents.refreshAllTasks();
//	}
	public TaskElement getSelectedTask(){
		return panelListAllEvents.getSelectedTask();
	}
}

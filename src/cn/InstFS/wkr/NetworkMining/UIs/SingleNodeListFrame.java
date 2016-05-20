package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import oracle.net.aso.a;

import org.apache.ibatis.annotations.Update;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import cn.InstFS.wkr.NetworkMining.Miner.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class SingleNodeListFrame extends JFrame {


	protected ArrayList<MiningMethod> miningMethods = new ArrayList<MiningMethod>();
    protected ArrayList<String>     miningObjects = new ArrayList<String>();
    ArrayList<ArrayList<JPopupMenu>> popupMenus=new ArrayList<ArrayList<JPopupMenu>>();
    ArrayList<JPopupMenu> currentPopupMenus=new ArrayList<JPopupMenu>();
    ArrayList<JButton> buttons= new ArrayList<JButton>();
    ArrayList<PanelShowAllResults> panelShowList = new ArrayList<PanelShowAllResults>();
    JTabbedPane tabbedPane;
    protected int miniMethodIndex=0;
    protected int miningObjectIndex=0;
    Map<Integer,MouseListener> popupListeners= new   HashMap<Integer,MouseListener>(); //�����˵�������
	int ipIndex=0;
	int protocolIndex=0;
	JTable listTable = new JTable();
	 JScrollPane scrollPane;
	 JComboBox<String> sortTypeComboBox;
	 JComboBox<String> miningObjectComboBox;
	JPanel selectPanel = new JPanel();
	HashMap<TaskCombination, MinerNodeResults> resultMap;
	HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>> resultMaps;
	ArrayList<Map.Entry<TaskCombination, MinerNodeResults> >resultList;
	String sortMethod ="�����ڹ���";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NetworkMinerFactory.getInstance();
					SingleNodeOrNodePairMinerFactory freMinerFactory=SingleNodeOrNodePairMinerFactory.getInstance();
					freMinerFactory.dataPath="C:\\data\\out\\traffic";
					freMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
					freMinerFactory.setTaskRange(TaskRange.SingleNodeRange);
					freMinerFactory.detect();
					HashMap<TaskCombination, MinerNodeResults> resultMap = NetworkMinerFactory.getInstance().startAllNodeMiners();
					HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>> tmpresultMaps = new HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>>();
					tmpresultMaps.put(MiningObject.MiningObject_Times,resultMap);
					System.out.println("size "+resultMap.size());
					JFrame.setDefaultLookAndFeelDecorated(true); 
					SingleNodeListFrame frame = new SingleNodeListFrame(tmpresultMaps);
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
	public SingleNodeListFrame(HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>> resultMaps) {
		this.resultMaps=resultMaps;
		this.resultMap=resultMaps.get(MiningObject.MiningObject_Times);
		loadModel();
		initModel();
		initialize();
	}
	public void loadModel() {
		// TODO Auto-generated method stub
//		MiningMethod method = MiningMethod.MiningMethods_PeriodicityMining;
//		miningMethods.add(method);
		resultList=new ArrayList<Map.Entry<TaskCombination, MinerNodeResults>>(resultMap.entrySet());
	}
	private void sort()
	{
		System.out.println("����"+sortMethod);
		switch(sortMethod)
		{
		case "�����ڹ���":
			sortByPeriodicity();
			System.out.println("����"+sortMethod);
			break;
		case "���쳣����":
			sortByOutlies();
			System.out.println("����"+sortMethod);
			break;
		case "��Ƶ������":
			sortBySequence();
			System.out.println("����"+sortMethod);
			break;
		}
	}
	private void sortByPeriodicity()
	{
		System.out.println("why"+sortMethod);
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						System.out.println(o1.getValue().getRetPM().getConfidence());
						System.out.println(o2.getValue().getRetPM().getConfidence());
						return (o1.getValue().getRetPM().getConfidence()-o2.getValue().getRetPM().getConfidence())>0?-1:1;
					}
				});
	}
	private void sortBySequence()
	{
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						System.out.println(o1.getValue().getRetPM().getConfidence());
						System.out.println(o2.getValue().getRetPM().getConfidence());
						return (o1.getValue().getRetSM().getAccuracyRatio()-o2.getValue().getRetSM().getAccuracyRatio())>0?-1:1;
					}
				});
	}
	private void sortByOutlies()
	{
		System.out.println("why"+sortMethod);
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						System.out.println(o1.getValue().getRetPM().getConfidence());
						System.out.println(o2.getValue().getRetPM().getConfidence());
						return (o1.getValue().getRetOM().getConfidence()-o2.getValue().getRetOM().getConfidence())>0?-1:1;
					}
				});
	}
	private void update()
	{
		System.out.println("����");
		if(listTable!=null)
		scrollPane.remove(listTable);
		resultList=new ArrayList<Map.Entry<TaskCombination, MinerNodeResults>>(resultMap.entrySet());
		sort();
		createTable();
	}
	private void createTable()
	{
		String data[][]=new String[resultList.size()][8];
		for(int i=0;i<resultList.size();i++)
		{
			TaskCombination taskCom =resultList.get(i).getKey();
			MinerNodeResults results = resultList.get(i).getValue();
			data[i][0]="ip "+taskCom.getRange()+"protocol "+taskCom.getProtocol();
			data[i][1]=String.valueOf(results.getRetStatistics().getMean());
			data[i][2]=String.valueOf(results.getRetStatistics().getStd());
			data[i][3]=String.valueOf(results.getRetStatistics().getSampleENtropy());
			data[i][4]=String.valueOf(results.getRetStatistics().getComplex());
			data[i][5]=String.valueOf(results.getRetPM().getPeriod());
			data[i][6]=String.valueOf(results.getRetOM().isHasOutlies()==true?"��":"��");
			data[i][7]=String.valueOf(results.getRetSM().isHasFreItems()==true?"��":"��");
			
			
		}
	    String colNames[]={"ʱ��������","ƽ��ֵ","��׼��","������","���Ӷ�","����","�Ƿ����쳣","�Ƿ����Ƶ����"};
	   
	 
	    DefaultTableModel model=new DefaultTableModel(data,colNames){
	        public   boolean   isCellEditable(int   row,   int   column)   
	         {   
	         return   false;   
	         };
	     
	      };    
	    listTable = new JTable(model);
	    listTable.addMouseListener(new MouseAdapter(){
	       
	    public void mouseClicked(MouseEvent e) {
	        
	          if(e.getClickCount()==2){//������Σ�������˫���¼�
	        	System.out.println("kkkkk");
	           tableChanged();        
	          }
	       }
	      });
//		    listTable.addMouseListener(new MouseAdapter(){
//		    public void mouseClicked(final MouseEvent e) {
//		    	  if (SwingUtilities.isRightMouseButton(e)) {
//		    	 int row = listTable.getSelectedRow(); // ��õ�ǰѡ�е��к�
//		         System.out.println(row);
//		         listTable.getComponent(row);
//		         
//		    	  }
//		    	} 
//		     });
	    final JPopupMenu popupMenu = new JPopupMenu();
	    JMenuItem menu = new JMenuItem("��ʾ��ϸ���");
	    popupMenu.add(menu);
	    menu.addActionListener(new ActionListener()
		{
	    	
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tableChanged();
//								
			}
		});
	    listTable.addMouseListener(new MouseAdapter() {
	    	 
            public void mouseReleased(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    if (evt.isPopupTrigger()) {
                        //  ȡ���Ҽ����������
                        int row = evt.getY() / listTable.getRowHeight();
                        listTable.setRowSelectionInterval(row,row);
                        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                }
            }
        });  
		   
	}
	public void initModel()
	{
		selectPanel.setLayout(new FlowLayout());
		JLabel objectLabel=new JLabel("ѡ���ھ����");
		selectPanel.add(objectLabel);
		miningObjectComboBox = new JComboBox<String>();
		miningObjectComboBox.addItem("����");
		miningObjectComboBox.addItem("ͨ�Ŵ���");
		miningObjectComboBox.setSelectedIndex(1);
//		miningObjectComboBox.addItem("��������ʧ");
		miningObjectComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            {
                switch (event.getStateChange())
                {
                case ItemEvent.SELECTED: 
                    System.out.println("ѡ��" + event.getItem());
                    if(event.getItem().equals("����"))
                    {
                    	resultMap=resultMaps.get(MiningObject.MiningObject_Traffic);
                    	
                    }
                    else if(event.getItem().equals("ͨ�Ŵ���"))
                    {
                    	resultMap=resultMaps.get(MiningObject.MiningObject_Times);
                    }
                    else if(event.getItem().equals("��������ʧ"))
                    {
                    	//resultMap=resultMaps.get(MiningObject.MiningObject_Traffic);
                    }
                    update();
                    break;
                case ItemEvent.DESELECTED:
                    System.out.println("ȡ��ѡ��" + event.getItem());
                    break;
                }
            }
        });
		selectPanel.add(miningObjectComboBox);
		
		JLabel sortLabel= new JLabel("ѡ������ʽ");
		sortTypeComboBox = new JComboBox<String>();
		sortTypeComboBox.addItem("�����ڹ���");
		sortTypeComboBox.addItem("���쳣����");
		sortTypeComboBox.addItem("��Ƶ������");
		sortTypeComboBox.setSelectedIndex(0);
		sortTypeComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            { System.out.println("ѡ��");
                switch (event.getStateChange())
                {
                case ItemEvent.SELECTED: 
                    System.out.println("ѡ��" + event.getItem());
                    sortMethod=event.getItem().toString();
                    System.out.println(sortMethod);
                    update();
                    break;
                case ItemEvent.DESELECTED:
                    System.out.println("ȡ��ѡ��" + event.getItem());
                    break;
                }
            }
        });
		selectPanel.add(sortLabel);
		selectPanel.add(sortTypeComboBox);
	    scrollPane = new JScrollPane();
	   
	    createTable();	  
	    scrollPane.setViewportView(listTable);
	}
	
	public void tableChanged()
	 {
	  int row=listTable.getSelectedRow();    
	  //�õ������еĵ�һ���е�ֵ����Ϊ�����¼����ݵĲ���
	  SingleNodeFrame SingleNodeFrame=new SingleNodeFrame(resultList.get(row).getKey());
	  SingleNodeFrame.setVisible(true);
	  System.out.println("selectrow:"+row);
	 }
	
	void initialize() {
//		setDefaultCloseOperation(JFrame.);
		setTitle("������Թ���");
		setBounds(100, 100, 1500, 900);
		try { 
//			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
           
			    UIManager.setLookAndFeel( new  SubstanceBusinessBlackSteelLookAndFeel());
	            JFrame.setDefaultLookAndFeelDecorated(true);  
               
	            SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());  
	            //���ý�����Ⱦ   
	            SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());  
	            //���ñ���  
	            SubstanceLookAndFeel.setCurrentTitlePainter( new MatteHeaderPainter());     
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }
		getContentPane().add(selectPanel,BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	 void addPopup(Component component, final JPopupMenu popup) {
			MouseListener popupListener ;
			component.addMouseListener(popupListener=new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					showMenu(e);
				}
				public void mouseReleased(MouseEvent e) {
					showMenu(e);
				}
				private void showMenu(MouseEvent e) {
					popup.show(e.getComponent(), 0, e.getComponent().getHeight());
					
				}
			});

		}
}

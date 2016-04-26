package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import  cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.PropertiesPanelFactory_wkr;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import java.text.ParseException;

/**
 * Created by Administrator on 2016/1/14.
 */
public class PanelCreateTask extends TaskElement {
    private JPanel TaskNew;
    final TaskElement NewTask = new TaskElement();
    String digObject = new String();
    String businessStyle = new String();
    String taskNameValue = new String();
    JPanel configPanel = new JPanel();
    JSplitPane contentpanel = new JSplitPane();
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    String nodeip = new String();
    String toNodeip = new String();
    JPanel sourceIpPanel = new JPanel();
    String ip[] = new String[100];
    JList<String> singleList = new JList<String>();
    JLabel singleTitle = new JLabel();
    JPanel toIpPanel = new JPanel();
    String toip[] = new String[100];
    JList<String> TosingleList = new JList<String>();
    JLabel TosingleTitle = new JLabel();
    JTextField TaskNameText = new JTextField(40);
    JButton saveButton = new JButton("����");

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PanelCreateTask TaskPanle = new PanelCreateTask(null);
                    TaskPanle.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    PropertySheetPanel sheet;
    TaskElement task;
    PropertyUI_TaskElement taskUI;
    PanelConfigTask taskNew;
//	String oldTaskName;

    private JTextArea txtArea;
    private Object obj;

    private PropertyChangeListener listener;
//    public PanelFirstDisplayTask(TaskElement task) {
//        this.task = task;
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//
//        taskUI = new PropertyUI_TaskElement(new TaskElement());
////		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(MiningMethod.class);
////		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(DiscreteMethod.class);
////		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(AggregateMethod.class);
//
//        PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(MiningMethod.class);
//        PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(DiscreteMethod.class);
//        PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(AggregateMethod.class);
//
//        CustomPropertyEditorRegistry.INSTANCE.getRegistry().unregisterEditor(Date.class);
//        CustomPropertyEditorRegistry.INSTANCE.getRegistry().registerEditor(Date.class, JDatePropertyEditor.class);
//
////		((JCalendarDatePropertyEditor)CustomPropertyEditorRegistry.INSTANCE.getRegistry().getEditor(Date.class)).setDateFormatString("yyyy-MM-dd HH:mm:ss");
//
////		sheet.setEditorFactory(CustomPropertyEditorRegistry.INSTANCE.getRegistry());
////		sheet.setRendererFactory(CustomPropertyRendererFactory.INSTANCE.getRegistry());
//
//        sheet = PropertiesPanelFactory_wkr.INSTANCE.createPanel(taskUI);
//        sheet.setSorting(false);
//        sheet.setSortingCategories(true);
//        add(sheet);
//
//
////
//        JScrollPane scrollPane = new JScrollPane();
//        add(scrollPane);
////
//        txtArea = new JTextArea();
//        txtArea.setRows(3);
//        scrollPane.setViewportView(txtArea);
//    }
    public PanelCreateTask(JFrame frame) throws IOException {

        setModal(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        final TaskElement NewTask = new TaskElement();
        taskUI = new PropertyUI_TaskElement(new TaskElement());
//		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(MiningMethod.class);
//		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(DiscreteMethod.class);
//		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(AggregateMethod.class);

        PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(MiningMethod.class);
        PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(DiscreteMethod.class);
        PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(AggregateMethod.class);

        CustomPropertyEditorRegistry.INSTANCE.getRegistry().unregisterEditor(Date.class);
        CustomPropertyEditorRegistry.INSTANCE.getRegistry().registerEditor(Date.class, JDatePropertyEditor.class);

//		((JCalendarDatePropertyEditor)CustomPropertyEditorRegistry.INSTANCE.getRegistry().getEditor(Date.class)).setDateFormatString("yyyy-MM-dd HH:mm:ss");

//		sheet.setEditorFactory(CustomPropertyEditorRegistry.INSTANCE.getRegistry());
//		sheet.setRendererFactory(CustomPropertyRendererFactory.INSTANCE.getRegistry());

        sheet = PropertiesPanelFactory_wkr.INSTANCE.createPanel(taskUI);
        sheet.setSorting(false);
        sheet.setSortingCategories(true);
        add(sheet);


//
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane);
//
        txtArea = new JTextArea();
        txtArea.setRows(3);
        scrollPane.setViewportView(txtArea);
       JPanel panel = new JPanel();
       add(panel);
       panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnSave = new JButton("����");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        panel.add(btnSave);

//        // super(frame);
//        setModal(false);
//        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        setBounds(100, 100, 800, 600);
//        TaskNew = new JPanel();
//        TaskNew.setBorder(new EmptyBorder(5, 5, 5, 5));
//        TaskNew.setLayout(new BorderLayout(0, 0));
//        setContentPane(TaskNew);
//
//        setTitle("�¼����ñ�");
//
//        configPanel.setLayout(new BorderLayout());
//        contentpanel.setDividerLocation(0);
//        configPanel.add(contentpanel, BorderLayout.CENTER);
//        contentpanel.setLeftComponent(leftPanel);
//        contentpanel.setRightComponent(rightPanel);
//        String filename = "ipdata.txt";
//        BufferedReader data = new BufferedReader(new FileReader(filename));
//        int ipNumber = 0;
//        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
//        String ipString = data.readLine();
//        while (ipString != null) {
//            toip[ipNumber] = ipString;
//            ip[ipNumber] = ipString;
//            ipString = data.readLine();
//            ipNumber++;
//
//        }
//        singleTitle.setText("<html><font face=\"����\";style=font:20pt>" + " Դip" + "</font>");
//        sourceIpPanel.add(Box.createVerticalStrut(10));
//        singleList.setListData(ip);
//        sourceIpPanel.add(singleList);
//        sourceIpPanel.add(singleTitle);
//        JScrollPane jsp = new JScrollPane(singleList);
//        jsp.setPreferredSize(new Dimension(80, 500));
//        sourceIpPanel.add(jsp);
//        TosingleTitle.setText("<html><font face=\"����\";style=font:20pt>" + " Ŀ��ip" + "</font>");
//        toIpPanel.add(Box.createVerticalStrut(10));
//        TosingleList.setListData(toip);
//        toIpPanel.add(TosingleTitle);
//        JScrollPane tojsp = new JScrollPane(TosingleList);
//        tojsp.setPreferredSize(new Dimension(80, 500));
//        toIpPanel.add(tojsp);
//
//        configPanel.setVisible(true);
//        // TaskNew.add(new PanelNewConfig(), BorderLayout.CENTER);
//
//
//       // leftPanel.add(sourceIpPanel);
//       // leftPanel.add(toIpPanel);
//
//        //********************************�����ұߵ����ÿ�********************//
//        rightPanel.setLayout(new BorderLayout(0, 0));
//
//
//        JLabel TaskNameTitle = new JLabel();
//        TaskNameTitle.setText("<html><font face=\"����\";style=font:15pt>" + "��������" + "</font>");
//        JPanel textPanel = new JPanel();
//        //textPanel.setLayout(new GridLayout(10,2));
//        textPanel.add(TaskNameTitle);
//        textPanel.add(TaskNameText);
//        rightPanel.add(textPanel, BorderLayout.CENTER);
//        String miningObject[] = new String[]{"�ھ����", "����", "ͨ�Ŵ���"};
//        final JComboBox<String> minObChooser = new JComboBox<String>(miningObject);
//        final String busiStyle[] = new String[]{"�ھ�ҵ��", "�쳣", "Ԥ��", "����","Ƶ��ģʽ","��Ԫ����"};
//        final JComboBox<String> busiStyleChooser = new JComboBox<String>(busiStyle);
//        JPanel bottomPanel = new JPanel();
//        bottomPanel.add(minObChooser);
//        bottomPanel.add(busiStyleChooser);
//
//
//        bottomPanel.add(saveButton);
//        rightPanel.add(bottomPanel, BorderLayout.SOUTH);
//        TaskNew.add(configPanel);
//        TaskNew.setVisible(true);
//        /**ΪԴĿ��ip������ü�����**/
//        class iplistener implements ListSelectionListener {
//            public void valueChanged(ListSelectionEvent e) {
//                Object selected = singleList.getModel().getElementAt(singleList.getSelectedIndex());
//                System.out.println(selected);
//                nodeip = (String) selected;
//            }
//        }
//
//
//        singleList.addListSelectionListener(new iplistener());
//        /**ΪĿ��ip������ü�����**/
//        class Toiplistener implements ListSelectionListener {
//            public void valueChanged(ListSelectionEvent e) {
//                Object selected = TosingleList.getModel().getElementAt(TosingleList.getSelectedIndex());
//                System.out.println(selected);
//                toNodeip = (String) selected;
//            }
//        }
//
//        TosingleList.addListSelectionListener(new Toiplistener());
//        /**Ϊ�ھ�������ü�����**/
//        class miningObjectListener implements ItemListener {
//
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    System.out.println(e.getItem().toString());
//                    digObject = e.getItem().toString();
//                }
//            }
//
//        }
//        minObChooser.addItemListener(new miningObjectListener());
//        /**Ϊ�ھ�ҵ�����ü�����**/
//        class busiStyleListener implements ItemListener {
//
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    System.out.println(e.getItem().toString());
//                    businessStyle = e.getItem().toString();
//                }
//            }
//
//        }
//        busiStyleChooser.addItemListener(new busiStyleListener());
//
//        /**
//         * ***Ϊ���水ť���ü�����******
//         */
//        class SavebuttonListener implements ActionListener {
//            public void actionPerformed(ActionEvent e) {
//
//                NewTask.setTaskName(TaskNameText.getText());
//                NewTask.setMiningObject(digObject);
//                if(businessStyle.equals("Ƶ��ģʽ"))
//                {
//                    NewTask.setMiningMethod(MiningMethod.MiningMethods_SequenceMining);
//                }
//                if (businessStyle.equals("����")) {
//                    {
//                        NewTask.setMiningMethod(MiningMethod.MiningMethods_PeriodicityMining);
//
//                    }
//
//                }
//                if (businessStyle.equals("Ԥ��")) {
//                    // System.out.println(businessStyle);
//                    NewTask.setMiningMethod(MiningMethod.MiningMethods_TsAnalysis);
//                }
//                if (businessStyle.equals("�쳣")) {
//                    NewTask.setMiningMethod(MiningMethod.MiningMethods_TsAnalysis);
//
//                }
//                if(businessStyle.equals("��Ԫ����"))
//                    NewTask.setMiningMethod(MiningMethod.MiningMethods_FrequenceItemMining);
//                System.out.println(NewTask.getTaskName()+" "+NewTask.getMiningObject().toString()+" "+NewTask.getMiningMethod().toString());
//                add1Task(NewTask, true);
//                TaskNameText.setText("");
//                minObChooser.setSelectedItem("�ھ����");
//                busiStyleChooser.setSelectedItem("�ھ�ҵ��");
//
//
//            }
//
//
//        }
//        saveButton.addActionListener(new SavebuttonListener());
    }
}


package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

/**
 * @author Arbor
 * @date 2016/5/30
 */
public class PathDetailFrame extends JFrame{
    TaskCombination taskCombination;
    PanelShowAllResults   panelShow = new PanelShowAllResults();
    ArrayList<JButton> buttons= new ArrayList<JButton>();
    public PathDetailFrame(TaskCombination taskCombination)
    {
        this.taskCombination=taskCombination;

        initModel();
        initialize();
    }

    public void initModel() {

        List<TaskElement> taskList = taskCombination.getTasks();

        for(int i=0;i<taskList.size();i++)
        {
            final TaskElement task = taskList.get(i);
            panelShow.onTaskAdded(task);
            JButton button = new JButton(taskList.get(i).getMiningMethod().toString());
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    panelShow.displayTask(task);
                }
            });
            buttons.add(button);
        }
    }
    private void initialize() {
        setBounds(100, 100, 1500, 900);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);



        this.getContentPane().setVisible(true);
        getContentPane().setLayout(new BorderLayout(0, 0));
////
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(2);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);

        splitPane.setLeftComponent(leftPanel);


        for (int i=0;i<buttons.size();i++)
        {
            JButton button = buttons.get(i);

            button.setBounds(38, 51+i*100, 134, 27);
            leftPanel.add(button);
        }
        splitPane.setRightComponent(panelShow);
        getContentPane().add(splitPane);
        if(buttons.size()>0)
            buttons.get(0).doClick();
    }
}

package net.sourceforge.squirrel_sql.client.action;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;


public class Demo  extends JPanel {  
  
    private JTabbedPane jTabbedpane = new JTabbedPane();// 存放选项卡的组件  
    private JTable table;
    private JTable table_1;
    private JTable table_2;
  
    public Demo() {  
        layoutComponents();  
    }  
  
    private void layoutComponents() {  
        int i = 0;  
        setLayout(null);
        // 第一个标签下的JPanel  
        JPanel jpanelFirst = new JPanel();
        jTabbedpane.setBounds(10, 162, 657, 265);
        // jTabbedpane.addTab(tabNames[i++],icon,creatComponent(),"first");//加入第一个页面  
        jTabbedpane.addTab( "选项1", null, jpanelFirst, "first");// 加入第一个页面  
        jpanelFirst.setLayout(null);
        
        JButton btnNewButton = new JButton("New button");
        btnNewButton.setBounds(10, 10, 93, 23);
        jpanelFirst.add(btnNewButton);
        
        JButton btnNewButton_1 = new JButton("New button");
        btnNewButton_1.setBounds(113, 10, 93, 23);
        jpanelFirst.add(btnNewButton_1);
        
        table = new JTable();
        table.setBounds(10, 65, 618, 99);
        jpanelFirst.add(table);
  
        // 第二个标签下的JPanel  
        JPanel jpanelSecond = new JPanel();  
        jTabbedpane.addTab( "选项2", null, jpanelSecond, "second");
        jpanelSecond.setLayout(null);
        
        JButton btnNewButton_2 = new JButton("New button");
        btnNewButton_2.setBounds(10, 10, 93, 23);
        jpanelSecond.add(btnNewButton_2);
        
        JButton btnNewButton_3 = new JButton("New button");
        btnNewButton_3.setBounds(113, 10, 93, 23);
        jpanelSecond.add(btnNewButton_3);
        
        table_1 = new JTable();
        table_1.setBounds(10, 43, 622, 80);
        jpanelSecond.add(table_1);
        
        table_2 = new JTable();
        table_2.setBounds(10, 146, 622, 80);
        jpanelSecond.add(table_2);
        JPanel jpanelthird = new JPanel();  
        jTabbedpane.addTab( "选项3", null, jpanelthird, "second");
        add(jTabbedpane);  
  
    }
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        SwingUtilities.invokeLater(new Runnable() {  
  
            public void run() {  
                //JFrame.setDefaultLookAndFeelDecorated(true);// 将组建外观设置为Java外观  
                JFrame frame = new JFrame();  
                frame.getContentPane().setLayout(null);  
                frame.setContentPane(new JTabbedPaneDemo());  
                frame.setSize(400, 400);  
                frame.setVisible(true);  
                // new TabComponentsDemo().runTest();  
            }  
        });  
    }  
}

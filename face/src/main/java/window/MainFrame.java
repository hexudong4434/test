package window;

import Tools.Image.ImageTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Title:IntelliJ IDEA
 * Auther: He Xudong
 * Date: 2020/1/8 0008
 * Time: 下午 16:10
 * Description: No Description
 */
public class MainFrame extends JFrame implements ActionListener {

    JButton btn_login;
    JButton btn_regist;
    JButton btn_exit;
    public MainFrame(){
        setTitle("人脸识别安全系统");
        setLayout(null);
        setBounds(550,300,800,500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon("F:\\AI酒店\\SDK的test图片\\facin.jpg").getImage());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(null,"你确定要退出系统？",
                        "确认",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
                if(result==JOptionPane.OK_OPTION){
                    System.exit(0);
                }
            }
        });
        //主题图片
        JLabel labImg = new JLabel();
        Image fileImg = new ImageIcon("F:\\AI酒店\\SDK的test图片\\facin.jpg").getImage();
        Image titleImg = ImageTools.imageScale(fileImg, 0.6, 0.2);
        labImg.setIcon(new ImageIcon(titleImg));
        labImg.setBounds(75,60,titleImg.getWidth(null),titleImg.getHeight(null));
        add(labImg);

        //添加登录
        btn_login = new JButton("人员登录");
        btn_login.addActionListener(this);
        btn_login.setBounds(260,220,150,40);
        add(btn_login);
        //添加注册
        btn_regist = new JButton("人员注册");
        btn_regist.addActionListener(this);
        btn_regist.setBounds(260,280,150,40);
        add(btn_regist);
        //添加退出
        btn_exit = new JButton("退出系统");
        btn_exit.addActionListener(this);
        btn_exit.setBounds(260,340,150,40);
        add(btn_exit);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==btn_login){
            //进入登录
            LoginFrm loginFrm = new LoginFrm();
            loginFrm.setVisible(true);
//            this.setVisible(false);
        }
        if(e.getSource()==btn_regist){
            //进入注册
            RegistFrm  registFrm = null;
                registFrm = new RegistFrm();
            registFrm.setVisible(true);
        }
        if(e.getSource()==btn_exit){
            //退出
            int result = JOptionPane.showConfirmDialog(null, "确认退出?",
                    "确认",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if(result == JOptionPane.OK_OPTION){
                System.exit(0);
            }
        }
    }
    public static void main(String[] args) {
        MainFrame main= new MainFrame();
        main.setVisible(true);
    }
}

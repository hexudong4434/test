/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window;

import Tools.Image.ImageTools;
import Tools.face.DetectFace;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.Rect;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import entity.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Administrator
 */
public class LoginFrm extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    private WebcamPanel webcamPanel;
    private Webcam webcam;
    private  Image image;
    private List<User> userList;    //从系统读入用户信息
    private float [] similar;  //相似度序列
    static private SqlSession sqlSession=null;  //数据库对话

    static {    //初始化MyBatis
        String resources="sqlMappingConfig.xml";
        InputStream inputStream= null;
        try {
            inputStream = Resources.getResourceAsStream(resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession=sqlSessionFactory.openSession();
    }

    public LoginFrm() {
        initComponents();
        DetectFace.faceEngine_init();   //打开识别引擎
        setLocationRelativeTo(null);    //设置中心位置
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //显示摄像头
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.QVGA.getSize());      //设置为显示窗口同样大小
        webcamPanel = new WebcamPanel(webcam);      //加入并摄像
        //加入到jPanel3中
        jPanel3.setLayout(new BorderLayout());

        jPanel3.add(webcamPanel,BorderLayout.NORTH);
        webcamPanel.setFPSDisplayed(true);  //显示FPS
        webcamPanel.setMirrored(true);      //镜像显示
        pack();


        //析构窗口操作
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                webcam.close();     //必须关闭，否则下次打开会发生异常
                DetectFace.exitEngine();    //关闭引擎
                dispose();      //关闭窗口
            }

            @Override
            public void windowOpened(WindowEvent e) {
                //读取数据库用户
                userList = getUserList();
                if(userList.isEmpty()){
                    JOptionPane.showMessageDialog(null,"请保证系统至少录入一个用户信息再登录！");
                    //发送退出命令
                    webcam.close();     //必须关闭，否则下次打开会发生异常
                    DetectFace.exitEngine();    //关闭引擎
                    dispatchEvent(new WindowEvent(e.getWindow(),WindowEvent.WINDOW_CLOSING) );     //关闭窗口
                    return;
                }
                //设置定时器 3s 启动抓取图片
                Timer timer = new Timer();
                timer.schedule(new MyTimerTask() ,3000);    //延迟3s 启动
            }
        });


    }

    //创建一个计时器任务  内部类
    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            if(!webcamPanel.isStarting()) webcamPanel.start();
            byte[] bt = WebcamUtils.getImageBytes(webcam,ImageUtils.FORMAT_JPG);
            try {
                image = ImageIO.read(new ByteArrayInputStream(bt));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //抓取图片
            Rect rect = DetectFace.getFaceLocation(image);
            Image catchImg = ImageTools.cutImg(image,rect);
            ImageIcon icon = new ImageIcon(catchImg);
            icon.setImage(icon.getImage().getScaledInstance(jLabel3.getWidth(),jLabel3.getHeight(),Image.SCALE_DEFAULT));   //适应宽度
            jLabel3.setIcon(icon);

            //进行相似比对，得到目标
            User targetUser = new User();
            int live =  detectUser(image,targetUser);
            System.out.println("活体："+live);
            if(live>0){
                String imgPath = targetUser.getImg_path()+"\\"+targetUser.getImg_name();    //通过用户提供的路径读取文件系统中的图片文件
                ImageIcon tarIcon = new ImageIcon(imgPath);
                tarIcon.setImage(tarIcon.getImage().getScaledInstance(jLabel2.getWidth(),jLabel2.getHeight(),Image.SCALE_DEFAULT));
                jLabel2.setIcon(tarIcon);       //显示图片
                webcamPanel.pause();    //摄像头暂停

                //弹出目标用户信息
                JOptionPane.showMessageDialog(null,targetUser.getUser_name()+",欢迎回来");
            }
            else{
                webcamPanel.pause();    //摄像头暂停
                JOptionPane.showMessageDialog(null,"查无此人！");
                Timer timer = new Timer();
                timer.schedule(new MyTimerTask() ,3000);    //延迟3s 启动
            }
        }
    }
    //通过引擎判断目标，并且进行活体检测，返回结果
    private int detectUser(Image face,User targetUser){
        FaceFeature faceFeatue = DetectFace.detectFace(face);   //获取特征值
        similar = new float[userList.size()];

            //获取相似度序列
        for(int i= 0; i < userList.size(); i++){
             String featureStr =  userList.get(i).getFace_feature();
             FaceFeature sourceFaceFeature = new FaceFeature();
             byte[] data = getByteArray(featureStr);
             sourceFaceFeature.setFeatureData(data);    //设置特征数据
         similar[i] = DetectFace.compareFace(faceFeatue, sourceFaceFeature);
        }

        //得到最大相似度下标
        float maxS = -1; int index=-1;
        for(int i=0;i < similar.length;i ++){
            if(maxS < similar[i]){
                maxS = similar[i];
                index = i;
            }
        }
        if(maxS >= 0.55) {      //设置识别的阈值 ，
            targetUser.setUser_id(userList.get(index).getUser_id());
            targetUser.setUser_name(userList.get(index).getUser_name());
            targetUser.setGender(userList.get(index).getGender());
            targetUser.setPhone(userList.get(index).getPhone());
            targetUser.setFace_feature(userList.get(index).getFace_feature());
            targetUser.setImg_name(userList.get(index).getImg_name());
            targetUser.setImg_path(userList.get(index).getImg_path());
        }
        else targetUser.setUser_id(-1);

        //检测是否为活体
        return  DetectFace.getLiveness();        //如果为活体，则返回 大于1
    }


    //读取数据库，获得用户集合
    private List<User> getUserList(){
        return sqlSession.selectList("findAll");
    }

    //将特征字符串，转化为byte数组
    private byte[] getByteArray(String featureStr){
        String[] array = featureStr.split("\\.");
        byte [] bt = new byte[1032];
        System.out.println(array.length);
        for(int i=0;i<1032;i++){
            bt[i] = Byte.parseByte(array[i]) ;
        }
        return bt;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("登录");
        setBackground(new java.awt.Color(0, 255, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));

        jLabel4.setText("提醒：请将人脸正对摄像头,提高识别准确度。");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("jLabel2");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 255)));

        jLabel2.setText("jLabel2");
        jLabel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 51, 255)));

        jLabel5.setText("<-捕捉人脸");
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText(" 目标人脸->");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 246, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(29, 29, 29)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;     //目标人脸
    private javax.swing.JLabel jLabel3; //抓取人脸
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}

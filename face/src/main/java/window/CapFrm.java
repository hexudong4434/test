package window;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class CapFrm extends JFrame implements ActionListener {

    public boolean isSave=false;  //是否保存
    public Image image=null;    //保存的图片
    public JButton btn_cap=null;  //拍照
    public JButton btn_save=null;    //重拍
    public WebcamPanel wpanel;     //显示面板
    public Webcam webcam;

    public CapFrm(){


        setLayout(new BorderLayout());
        setTitle("拍照");
        setLocationRelativeTo(null);    //居中
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //获取摄像头
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        //显示面板
        wpanel = new WebcamPanel(webcam);
        wpanel.setMirrored(true);
        wpanel.setFPSDisplayed(true);
        wpanel.setImageSizeDisplayed(true);

        add(wpanel,BorderLayout.CENTER);
        //按钮
        btn_cap= new JButton("拍照");
        btn_save= new JButton("保存");
        btn_save.setEnabled(false);

        //监听按钮
        btn_cap.addActionListener(this);
        btn_save.addActionListener(this);

        JPanel btn_panel = new JPanel(new BorderLayout());
        btn_panel.add(btn_cap,BorderLayout.CENTER);
        btn_panel.add(btn_save,BorderLayout.SOUTH);
        add(btn_panel,BorderLayout.SOUTH);
        pack();
    }

    public Image getImage(){
        return image;
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==btn_cap) {
            btn_save.setEnabled(true);
            byte[] bt = WebcamUtils.getImageBytes(webcam, ImageUtils.FORMAT_JPG);
            ByteArrayInputStream in = new ByteArrayInputStream(bt);
            try {
                image = ImageIO.read(in);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "保存图像失败");
//                dispose();
                dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING) );
                return;
            }
            if (btn_cap.getText().equals("拍照")) {
                    wpanel.pause();
                    btn_cap.setText("重拍");
                } else {
                    btn_save.setEnabled(false);
                    int result = JOptionPane.showConfirmDialog(null, "确定要重拍？");
                    if (result != JOptionPane.OK_OPTION)
                        return;
                    wpanel.resume();
                    btn_cap.setText("拍照");
                }

        }
            if(e.getSource()==btn_save){
            int result = JOptionPane.showConfirmDialog(null,"确认保存？");
            if(result==JOptionPane.NO_OPTION) {
                isSave = false;
                webcam.close();
                dispose();
//                dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING) );
                return;
            }
            if(result==JOptionPane.CANCEL_OPTION)return;   //取消此次操作

                isSave=true;

            //关闭窗口和摄像头
            webcam.close();
//            dispose();
            dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING) );
        }
    }

}

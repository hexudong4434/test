package window;

import Tools.face.DetectFace;
import sun.awt.image.ToolkitImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Test {
    public static void main(String[] args) {
        DetectFace.faceEngine_init();
        Image image = new ImageIcon("F:\\AI酒店\\SDK的test图片\\系统人脸图片\\face_3.jpg").getImage();
        BufferedImage bufferedImage = ((ToolkitImage) image).getBufferedImage();
        DetectFace.comparetFace(bufferedImage);
    }
}

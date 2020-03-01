package Tools.Image;

import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2019/12/3 0003
 * Time: 下午 19:41
 * Description: No Description
 */
public class MatToBufferImage {
    public static Image toBufferImage(Mat mat){
        //类型
        int type= mat.channels()==1?BufferedImage.TYPE_BYTE_GRAY:BufferedImage.TYPE_3BYTE_BGR;
        //字节大小
        int bufferSize =mat.cols() * mat.rows() * mat.channels();
        //获取图片内容
        byte[] buffer = new byte[bufferSize];
        mat.get(0,0,buffer);

        BufferedImage image =new BufferedImage(mat.cols(),mat.rows(),type);

        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
}

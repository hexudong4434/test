package Tools.Image;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static javax.swing.text.StyleConstants.Size;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2019/12/4 0004
 * Time: 下午 12:43
 * Description: No Description
 */
public class MatTools {
    public static Mat resize(Mat img, double times) {
        Size size =new Size(img.width()*times,img.height()*times);
        //建立目标大小的图片
        Mat targetImg=new Mat(size,img.type());
       Imgproc.resize(img,targetImg,size);
       return targetImg;
    }
}

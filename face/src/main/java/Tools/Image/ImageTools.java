package Tools.Image;

import com.arcsoft.face.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Title:IntelliJ IDEA
 * Auther: He Xudong
 * Date: 2020/1/10 0010
 * Time: 下午 13:01
 * Description: No Description
 */
public class ImageTools {
    /**
     * description: 将Image实例按照给定的宽，高比例来进行缩放
     * create time:  下午 14:40
     * @Param: image
     * @Param: wScale
     * @Param: hScale
     * @return java.awt.Image
     */
    public static Image imageScale(Image image,double wScale,double hScale){

        if(wScale<0||hScale<0){
            wScale=1.0;
            hScale=1.0;
        }
        BufferedImage targetImg = new BufferedImage((int)(wScale*image.getWidth(null)),
                (int)(hScale*image.getHeight(null)),BufferedImage.TYPE_INT_ARGB);
        Graphics2D gra = (Graphics2D)targetImg.getGraphics();
        gra.scale(wScale,hScale);
        gra.drawImage(image,0,0,null);
        return targetImg;
    }

    /**
     *  按照给定图像长和宽的像素个数进行缩放
     *
     */
    public static Image imageScale(Image image,int w,int h){
        if(w<=0||h<=0){
            return image;
        }
        BufferedImage targetImg = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D gra = (Graphics2D)targetImg.getGraphics();
        double wScale = w/image.getWidth(null);
        double hScale = h/image.getHeight(null);

        gra.scale(wScale,hScale);
        gra.drawImage(image,0,0,null);
        return targetImg;
    }
    /***
     * 按照给定的  Rect  裁定图片
     */
    public static Image cutImg(Image img,Rect rect){
        if(rect == null)return img;
        int x1,y1,x2,y2;
        x1 = rect.getLeft();
        y1 = rect.getTop();
        x2 = rect.getRight();
        y2 = rect.getBottom();

        BufferedImage targetImg = ((BufferedImage) img).getSubimage(x1,y1,x2-x1,y2-y1);
        return targetImg;
    }
}

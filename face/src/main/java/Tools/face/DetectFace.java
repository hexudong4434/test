package Tools.face;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.enums.ImageFormat;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import sun.awt.image.ToolkitImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getGrayData;
import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2019/12/3 0003
 * Time: 下午 21:08
 * Description: No Description
 */

public class DetectFace {
    /*
    * 引擎初始化
    * */
    private static FaceEngine faceEngine;
    private static  ImageInfo imageInfo;
    private static List<FaceInfo> faceInfoList;
    public static FaceEngine faceEngine_init() {
        String appId = "9o7vKafPa8hyX3TNHSWQ9DeBgR9v5PpJ3JVdzGmzcgg2";
        String sdkKey = "E7qRfekG7xyqXQzcmscm2K5mkkjzSMV1v8bMZWYUq9e7";

        faceEngine = new FaceEngine("F:\\AI酒店\\Arcsoft_ArcFace_SDK\\libs\\WIN64");
        //激活引擎
        int activeCode = faceEngine.activeOnline(appId, sdkKey);

        if (activeCode != ErrorInfo.MOK.getValue() && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");

            return null;
        }

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);

        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);

        //初始化引擎
        int initCode = faceEngine.init(engineConfiguration);

        if (initCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }

        return faceEngine;
    }

    /**
    * 返回检测到的第一个人脸位置
    * */
    public static Rect getFaceLocation(Image img){
        faceInfoList = getFaceInfo(img);
        if(faceInfoList==null) return null;
        else return faceInfoList.get(0).getRect();
    }

    /**
     * 返回人脸信息集合
     * */
    public static List<FaceInfo> getFaceInfo(Image img){
        imageInfo =getImageInfoByImage(img);

        faceInfoList = new ArrayList<FaceInfo>();
        int detectCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
        if(faceInfoList.isEmpty()) return null;
        else return faceInfoList;
    }

    /**
     * 获取图像信息实例
     * */
    public static ImageInfo getImageInfoByImage(Image img){
        if(img instanceof ToolkitImage) img=((ToolkitImage)img).getBufferedImage();
            return  ImageFactory.bufferedImage2ImageInfo((BufferedImage) img);
    }

    /**
     * 获取人脸特征值
     * */
    public static FaceFeature detectFace(Image img){
        faceInfoList = getFaceInfo(img);
        if(faceInfoList==null) return null;
        imageInfo = getImageInfoByImage(img);
        //特征提取
        FaceFeature faceFeature = new FaceFeature();
        int extractCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
        return  faceFeature;
    }

    /**
     * 将两张人脸特征值进行比对 ，返回一个相似度
     * */
    public static float compareFace(FaceFeature targetFeature,FaceFeature sourceFeature){
        FaceSimilar faceSimilar = new FaceSimilar();
        int processCode = faceEngine.compareFaceFeature(targetFeature,sourceFeature,faceSimilar);
        return faceSimilar.getScore();
    }
    /**
     * 活体检测 ，返回识别到的活体数目,错误返回    -1
     * */
    public static int getLiveness(){
        //活体检测

        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setSupportLiveness(true);

        int processCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList, configuration);


        List<LivenessInfo> livenessInfoList = new ArrayList<LivenessInfo>();
        int livenessCode = faceEngine.getLiveness(livenessInfoList);
        if(livenessInfoList.isEmpty()) {
            System.out.println("活体：检测失败");
            return -1;
        }
        else
            return livenessInfoList.get(0).getLiveness();
    }

    //测试代码
    public static void comparetFace(Image image1){
        //摄像头检测1
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo((BufferedImage) image1);
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        int detectCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
        if(faceInfoList.isEmpty()){
            System.out.println("检测无人脸：失败");
            return;
        }
        System.out.println(faceInfoList);

        //特征提取
        FaceFeature faceFeature = new FaceFeature();
        int extractCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

        //人脸检测2
        ImageInfo imageInfo2 = getRGBData(new File("F:\\AI酒店\\SDK的test图片\\img4.jpg"));
        List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
        int detectCode2 = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList2);
//        System.out.println(faceInfoList);

        System.out.println("高:"+faceInfoList.get(0).getRect().getTop());
        System.out.println("右:"+faceInfoList.get(0).getRect().getRight());
        System.out.println("下:"+faceInfoList.get(0).getRect().getBottom());
        System.out.println("左:"+faceInfoList.get(0).getRect().getLeft());

        //特征提取2
        FaceFeature faceFeature2 = new FaceFeature();

        int extractCode2 = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList2.get(0), faceFeature2);
        byte [] bt= new byte[1032];
        bt = faceFeature2.getFeatureData();
        for (byte a: bt) {
            System.out.print(a+"  ");
        }
        System.out.print("\n");
        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

        //特征比对
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
        FaceSimilar faceSimilar = new FaceSimilar();
        int compareCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        System.out.println("相似度：" + faceSimilar.getScore());


        //人脸属性检测
        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setSupportAge(true);
        configuration.setSupportFace3dAngle(true);
        configuration.setSupportGender(true);
        configuration.setSupportLiveness(true);
        int processCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList, configuration);


        //性别检测
        List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
        int genderCode = faceEngine.getGender(genderInfoList);

        if(genderInfoList.isEmpty())
            System.out.println("性别：检测失败");
        else
        System.out.println("性别：" + genderInfoList.get(0).getGender());

        //年龄检测
        List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
        int ageCode = faceEngine.getAge(ageInfoList);
        if(ageInfoList.isEmpty())
            System.out.println("年龄：检测失败");
        else
        System.out.println("年龄：" + ageInfoList.get(0).getAge());

        //3D信息检测
        List<Face3DAngle> face3DAngleList = new ArrayList<Face3DAngle>();
        int face3dCode = faceEngine.getFace3DAngle(face3DAngleList);
        if(face3DAngleList.isEmpty())
            System.out.println("3D角度：检测失败");
        else
        System.out.println("3D角度：" + face3DAngleList.get(0).getPitch() + "," + face3DAngleList.get(0).getRoll() + "," + face3DAngleList.get(0).getYaw());

        //活体检测
        List<LivenessInfo> livenessInfoList = new ArrayList<LivenessInfo>();
        int livenessCode = faceEngine.getLiveness(livenessInfoList);
        if(livenessInfoList.isEmpty())
            System.out.println("活体：检测失败");
        else
        System.out.println("活体：" + livenessInfoList.get(0).getLiveness());


        //IR属性处理
        ImageInfo imageInfoGray = getGrayData(new File("F:\\AI酒店\\SDK的test图片\\img5.jpg"));
        List<FaceInfo> faceInfoListGray = new ArrayList<FaceInfo>();
        int detectCodeGray = faceEngine.detectFaces(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), ImageFormat.CP_PAF_GRAY, faceInfoListGray);

        FunctionConfiguration configuration2 = new FunctionConfiguration();
        configuration2.setSupportIRLiveness(true);
        int processCode2 = faceEngine.processIr(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), ImageFormat.CP_PAF_GRAY, faceInfoListGray, configuration2);
        //IR活体检测
        List<IrLivenessInfo> irLivenessInfo = new ArrayList<IrLivenessInfo>();
        int livenessIr = faceEngine.getLivenessIr(irLivenessInfo);
        System.out.println("IR活体：" + irLivenessInfo.get(0).getLiveness());


        //设置活体检测参数
        int paramCode = faceEngine.setLivenessParam(0.8f, 0.8f);
    }

    /*
    * 关闭引擎
    * */
    public static void exitEngine(){
        //获取激活文件信息
        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        int activeFileCode = faceEngine.getActiveFileInfo(activeFileInfo);

        //引擎卸载
        int unInitCode = faceEngine.unInit();
    }
}

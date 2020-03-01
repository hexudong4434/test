package entity;

/**
 * @Title:IntelliJ IDEA
 * Auther: He Xudong
 * Date: 2020/1/21 0021
 * Time: 下午 23:51
 * Description: No Description
 */
public class User {
    /**
     *
     *   `user_id` int(11) NOT NULL AUTO_INCREMENT,
     *   `user_name` varchar(20) NOT NULL,
     *   `gender` int(1) DEFAULT NULL,
     *   `phone` char(11) DEFAULT NULL,
     *   `face_feature` varchar(5000) NOT NULL,
     *   `img_id` int(8) NOT NULL,
     */

    private  int user_id;
    private String user_name;


    private String gender;
    private  String phone;
    private String face_feature;
    private String img_path;
    private String img_name;


    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }



    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFace_feature() {
        return face_feature;
    }

    public void setFace_feature(String face_feature) {
        this.face_feature = face_feature;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

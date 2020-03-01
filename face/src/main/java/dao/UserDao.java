package dao;

import Tools.database.DbUtils;
import entity.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;
import java.util.List;

import static java.sql.DriverManager.getConnection;

/**
 * @Title:IntelliJ IDEA
 * Auther: He Xudong
 * Date: 2020/1/21 0021
 * Time: 下午 23:59
 * Description: No Description
 */
public class UserDao {

    private QueryRunner qr;
    public UserDao(){
        qr = new QueryRunner(DbUtils.getDataSource());
    }

    //获取所有用户
    public List<User> findAll() throws SQLException {
        String  sql = "select * from user";
        List<User> list;
         list = qr.query(sql,new BeanListHandler<User>(User.class));
         return list;
    }

    //获取注册用户的个数
    public long getUserCount() throws SQLException {
        String sql = "select count(*) from user";
        long count =(Long) qr.query(sql, new ScalarHandler());
        return count;
    }

    //添加用户
    public void addUser(User user) throws SQLException {

        //添加基本信息
        String  sql = "insert into user(user_name,gender,phone,face_feature,img_name,img_path) values(?,?,?,?,?,?)";
        System.out.println(user.getFace_feature());
        int re=  qr.update(sql,user.getUser_name(),user.getGender(),user.getPhone(),
                user.getFace_feature(),user.getImg_name(),user.getImg_path());
    }



    //以下是jdbc的测试信息
    public String getLength() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String sql = "select count(*) from user";
        Connection conn = getConnection("jdbc:mysql://127.0.0.1:3306/ai_hotel","root","123456");
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            System.out.println(rs.getString(1));
        }
        return rs.getString(1);
    }
}
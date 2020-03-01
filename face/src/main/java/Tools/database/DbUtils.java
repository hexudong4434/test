package Tools.database;


import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;


/**
 * @Title:IntelliJ IDEA
 * Auther: He Xudong
 * Date: 2020/1/21 0021
 * Time: 下午 23:34
 * Description: No Description
 */
public class DbUtils {
    private static DataSource ds;
    static{
        ds =new ComboPooledDataSource("mysql");
    }

    public static DataSource getDataSource(){
        return ds;
    }
}

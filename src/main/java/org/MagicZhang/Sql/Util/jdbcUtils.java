package org.MagicZhang.Sql.Util;

import java.sql.*;

import org.MagicZhang.dbinfo;

public class jdbcUtils {
    private static String driver=null;
    private static String url=null;
    private static String username=null;
    private static String password=null;
    static{
        driver=dbinfo.driver;
        url=dbinfo.url;
        username=dbinfo.username;
        password=dbinfo.password;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(url,username,password);
    }
    public static void release(Connection conn, Statement st, ResultSet rs)
    {
        if(rs!=null){
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(st!=null){
            try {
                st.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

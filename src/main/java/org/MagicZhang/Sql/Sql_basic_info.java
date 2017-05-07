package org.MagicZhang.Sql;

/**
 * Created by sonof on 2017/5/7.
 */
import org.MagicZhang.Modle.basic_info;
import org.MagicZhang.Modle.user;
import org.MagicZhang.Sql.Util.jdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sql_basic_info {
    public basic_info _basic_info;
    public int id;
    public Sql_basic_info(int id){
        this.id=id;
        _basic_info=querybasic_infobyid(id);
    }
    public static basic_info querybasic_infobyid(int id){
        Connection conn=null;
        Statement st = null;
        ResultSet rs=null;
        basic_info _basicinfo=null;
        try {
            conn= jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql = "select * from basic_info where id="+id;
            rs=st.executeQuery(sql);
            while (rs.next()){
                _basicinfo=new basic_info(rs.getInt(1),rs.getString(2),
                        rs.getString(3),rs.getString(4)
                ,rs.getString(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            jdbcUtils.release(conn,st,rs);
        }
        return _basicinfo;
    }
}

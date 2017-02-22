package org.MagicZhang.Sql;
import org.MagicZhang.Modle.user;
import org.MagicZhang.Sql.Util.jdbcUtils;
import java.sql.*;

public class sql_user{
    public static user query_userbyphonenumber(String phone_number){
        Connection conn=null;
        Statement st = null;
        ResultSet rs=null;
        user _user=null;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql = "select * from users where phone_number='"+phone_number+"'";
            rs=st.executeQuery(sql);
            while (rs.next()){
                _user=new user(rs.getString(1),rs.getByte(2)
                        ,rs.getString(3),rs.getString(4)
                        ,rs.getInt(5),rs.getInt(6)
                        ,rs.getByte(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            jdbcUtils.release(conn,st,rs);
        }
        return _user;
    }
    public static boolean insert_user(user _user){
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean issuccess=false;
        try{
            conn = jdbcUtils.getConnection();
            String sql = "insert into users(phone_number," +
                    "user_type,last_updatetime,last_updatelocation,help_number" +
                    ",request_number,isonline)values(?,?,?,?,?,?,?)";
            st = conn.prepareStatement(sql);
            st.setString(1, _user.phone_number());
            st.setByte(2, _user.user_type());
            st.setString(3, _user.last_updatetime());
            st.setString(4, _user.last_updatelocation());
            st.setInt(5, _user.help_number());
            st.setInt(6, _user.request_number());
            st.setByte(7, _user.isonline());
            st.executeUpdate();
            issuccess=true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public static boolean update_userbylocation(String user_id,String last_updatetime
    ,String last_updatelocation){
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update users set last_updatetime='"+last_updatetime
            +"',last_updatelocation='"+last_updatelocation+"'where user_id='"+user_id+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public static boolean delete_userbyphonenumber(String phone_number){
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="delete from users where phone_number='"+phone_number+"'";
            st=conn.createStatement();
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
}
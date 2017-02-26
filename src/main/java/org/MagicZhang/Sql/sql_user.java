package org.MagicZhang.Sql;
import org.MagicZhang.Modle.user;
import org.MagicZhang.Sql.Util.jdbcUtils;
import java.sql.*;

public class sql_user{
    private String phone_number;
    public user _user;// as a buffer,but no effect for concurrence
    public sql_user(String phone_number){
        this.phone_number=phone_number;
        _user=query_byphonenumber(phone_number);
    }
    public static user query_byphonenumber(String phone_number){
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
    public synchronized boolean update_location(String last_updatetime
    ,String last_updatelocation){
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update users set last_updatetime='"+last_updatetime
            +"',last_updatelocation='"+last_updatelocation+"' where phone_number='"
                    +phone_number+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _user.last_updatetime_$eq(last_updatetime);
                _user.last_updatelocation_$eq(last_updatelocation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public synchronized boolean update_helpnumber(int offset){
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            int tmp_helpnumber=_user.help_number()+offset;
            String sql="update users set helpnumber="+tmp_helpnumber
                    +" where phone_number='"+phone_number+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _user.update_helpnumber(offset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public synchronized boolean update_requestnumber(int offset){
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            int tmp_requestnumber=_user.request_number()+offset;
            String sql="update users set requestnumber="+tmp_requestnumber
                    +"where phone_number='"+phone_number+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _user.update_requestnumber(offset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public synchronized boolean update_isonline(Byte isonline){
        if(isonline==_user.isonline())
            return true;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update users set isonline="+isonline
                    +" where phone_number='"+phone_number+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _user.isonline_$eq(isonline);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public static boolean delete_byphonenumber(String phone_number){
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
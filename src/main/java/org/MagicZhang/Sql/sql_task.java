package org.MagicZhang.Sql;

import org.MagicZhang.Modle.task;
import org.MagicZhang.Sql.Util.jdbcUtils;
import java.sql.*;

/**
 * Created by sonof on 2017/2/21.
 */
public class sql_task {
    public static task query_bytaskid(String task_id){
        Connection conn=null;
        Statement st = null;
        ResultSet rs=null;
        task _task=null;
        try {
            conn= jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql = "select * from tasks where task_id='"+task_id+"'";
            rs=st.executeQuery(sql);
            while (rs.next()){
                _task=new task(rs.getString(1),rs.getString(2)
                        ,rs.getString(3),rs.getString(4)
                        ,rs.getString(5),rs.getString(6)
                        ,rs.getString(7),rs.getString(8));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            jdbcUtils.release(conn,st,rs);
        }
        return _task;
    }
    public static boolean insert_task(task _task){
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean issuccess=false;
        try{
            conn = jdbcUtils.getConnection();
            String sql = "insert into tasks(task_id," +
                    "request_phone_number,request_time,request_location,request_info" +
                    ",volunteer_phone_number,ack_time,ack_location)values(?,?,?,?,?,?,?,?)";
            st = conn.prepareStatement(sql);
            st.setString(1, _task.task_id());
            st.setString(2, _task.request_phone_number());
            st.setString(3, _task.request_time());
            st.setString(4, _task.request_location());
            st.setString(5, _task.request_info());
            st.setString(6, _task.volunteer_phone_number());
            st.setString(7, _task.ack_time());
            st.setString(8, _task.ack_location());
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
    public static boolean update_ack(String task_id,String volunteer_phone_number,
                                           String acktime,String acklocation){
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set volunteer_phone_number='"+volunteer_phone_number
                    +"',acktime='"+acktime+"',acklocation='"+acklocation+
                    "' where task_id='"+task_id+"'";
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
    public static boolean delete_bytaskid(String task_id){
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="delete from tasks where task_id='"+task_id+"'";
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

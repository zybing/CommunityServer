package org.MagicZhang.Sql;

import org.MagicZhang.Modle.task;
import org.MagicZhang.Sql.Util.jdbcUtils;
import java.sql.*;

/**
 * Created by sonof on 2017/2/21.
 * 用于将数据库的task表数据保存到该类中，同时提供内存中备份
 * 对于task中的更新操作，会同时修改数据库并对成员变量_task进行修改
 * 这样进行查询操作时不需要重新读取数据库（但是task有多个修改点，必要时需要进行
 * 信息同步）
 *  */
public class Sql_task {
    public task _task;
    private static final int publishtime_interval=1000;
    private static final int publishnum_threshold=30;
    private long publishtime;
    private int publishdetectnum;
    private long ordertime;
    private static final int ordertime_threshold=90000;
    public Sql_task(String task_id){
        _task=query_bytaskid(task_id);
        publishtime=System.currentTimeMillis()-publishtime_interval;
        publishdetectnum=0;
        if(_task!=null){
            ordertime=_task.ordertime();
        }
    }
    //2代表超时，1代表继续判断，publishtime来源于类的初始化
    public byte publishtimeout(long time){
        if(publishdetectnum>publishnum_threshold)
            return (byte)2;
        if((time-publishtime)>publishtime_interval){
            if(++publishdetectnum>publishnum_threshold)
                return (byte)2;
            else
            {
                publishtime=time;
                return (byte)1;
            }
        }
        return (byte)0;
    }
    public void setOrdertime(long time){
        ordertime=time;
        update_ordertime(time);
    }
    public boolean order_timeout(long time){
        if(time-ordertime>ordertime_threshold){
            return true;
        }
        else{
            return false;
        }
    }
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
                        ,rs.getString(7),rs.getString(8)
                        ,rs.getByte(9),rs.getLong(10),
                        rs.getString(11));
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
                    ",volunteer_phone_number,ack_time,ack_location,status)" +
                    "values(?,?,?,?,?,?,?,?,?)";
            st = conn.prepareStatement(sql);
            st.setString(1, _task.task_id());
            st.setString(2, _task.request_phone_number());
            st.setString(3, _task.request_time());
            st.setString(4, _task.request_location());
            st.setString(5, _task.request_info());
            st.setString(6, _task.volunteer_phone_number());
            st.setString(7, _task.ack_time());
            st.setString(8, _task.ack_location());
            st.setByte(9, _task.status());
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
    public boolean update_ack(String volunteer_phone_number,
                                           String acktime,String acklocation){
        if(_task==null)
            return false;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set volunteer_phone_number='"+volunteer_phone_number
                    +"',acktime='"+acktime+"',acklocation='"+acklocation+
                    "' where task_id='"+_task.task_id()+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _task.volunteer_phone_number_$eq(volunteer_phone_number);
                _task.ack_time_$eq(acktime);
                _task.ack_location_$eq(acklocation);
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
    public boolean update_status(byte status){
        if(_task==null)
            return false;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set status='"+status+
                    "' where task_id='"+_task.task_id()+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _task.status_$eq(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public boolean update_ordertime(Long ordertime){
        if(_task==null)
            return false;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set ordertime='"+ordertime+
                    "' where task_id='"+_task.task_id()+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _task.ordertime_$eq(ordertime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public boolean update_volunteerphonenumber(String volunteerphonenumber){
        if(_task==null)
            return false;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set volunteer_phone_number='"+volunteerphonenumber+
                    "' where task_id='"+_task.task_id()+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _task.volunteer_phone_number_$eq(volunteerphonenumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public boolean update_acktime(String acktime){
        if(_task==null)
            return false;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set ack_time='"+acktime+
                    "' where task_id='"+_task.task_id()+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _task.ack_time_$eq(acktime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    public boolean update_acklocation(String acklocation){
        if(_task==null)
            return false;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set ack_location='"+acklocation+
                    "' where task_id='"+_task.task_id()+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _task.ack_location_$eq(acklocation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
    //必要时必须主动更新信息，因为task可以同时被requester和helper同时拥有，或造成消息不
    //对称，但是完全意义上，被其他线程更改task数据库内容，都要及时做出更新是不可能的。
    //所以要在对信息真实性有要求的地方进行更新。
    //ack之后的任务没有必要同步，信息已经全面，而结束不需要完全同步，
    // 所以只有音频文件的位置信息需要同步
    public boolean update_task(){
        if(_task!=null)
        {
            _task=query_bytaskid(_task.task_id());
            return true;
        }
        else{
            _task=query_bytaskid(_task.task_id());
            if(_task!=null)
                return true;
        }
        return false;
    }
    public boolean update_fileurl(String fileurl){
        if(_task==null)
            return false;
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        boolean issuccess=false;
        try {
            conn=jdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="update tasks set fileurl='"+fileurl+
                    "' where task_id='"+_task.task_id()+"'";
            int num=st.executeUpdate(sql);
            if(num>0){
                issuccess=true;
                _task.ack_time_$eq(fileurl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            jdbcUtils.release(conn, st, rs);
        }
        return issuccess;
    }
}

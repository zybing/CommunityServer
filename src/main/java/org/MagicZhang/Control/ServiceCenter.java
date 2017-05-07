package org.MagicZhang.Control;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.MagicZhang.Filter;
import org.MagicZhang.Log;
import org.MagicZhang.Logic.Logic;
import org.MagicZhang.Modle.task;
import org.MagicZhang.ServerInfo;
/**
 * Created by sonof on 2017/2/21.
 */
public class ServiceCenter extends Thread{
    public int PORT = ServerInfo.PORT;
    public int THREAD_NUM = ServerInfo.THREAD_NUM;
    public ConcurrentHashMap<String,ServiceServer> online_users=new
            ConcurrentHashMap<String,ServiceServer>();
    private ThreadId _threadid;
    private static ServiceCenter myself;
    public ExecutorService read_pool;
    public ExecutorService writer_pool;
    public ExecutorService tasks_pool;
    private ServiceCenter(){
        _threadid=new ThreadId(ServerInfo.THREADINITID,ServerInfo.THREADMAXID);
        read_pool = Executors.newFixedThreadPool(THREAD_NUM);
        writer_pool = Executors.newFixedThreadPool(THREAD_NUM);
        tasks_pool = Executors.newFixedThreadPool(THREAD_NUM);
    }
    //单例设计模式
    public static ServiceCenter getinstance(){
        if(myself==null){
            myself=new ServiceCenter();
        }
        return myself;
    }
    //添加在线用户
    public void addonline_users(String phone_number,ServiceServer st){
        ServiceServer tmp=online_users.put(phone_number,st);
        Log.log("add users "+phone_number+" "+st);
        Log.log("login num:"+online_users.size());
        st._sql_user.update_isonline((byte)1);
        if(tmp!=null){
            if(!tmp.isfinish)
            {
                Log.log("try finish "+phone_number+" "+tmp);
                tmp.finish();
            }
            else{
                Log.log(phone_number+" "+
                        tmp+"has been finished");
            }
        }
        else{
            Log.log("this phoner_number user " +
                    "is first been added "+phone_number);
        }
    }
    //移除在线用户
    public void removeoffline_users(String phone_number,ServiceServer old_thread){
        ServiceServer st=online_users.get(phone_number);
        if(st!=null){
            if(old_thread==st)
            {
                st._sql_user.update_isonline((byte)0);
                if(!st.isfinish)
                    st.finish();
                online_users.remove(phone_number);
                Log.log("remove users "+phone_number+" "+st);
                Log.log("login num:"+online_users.size());
            }
            else{
                Log.log("user "+phone_number+" "+old_thread+" has been removed");
                Log.log("login num:"+online_users.size());
            }
        }
        else{
            Log.log("no user is been removed");
            Log.log("login num:"+online_users.size());
        }
    }
    //下发通知的函数
    public boolean sendnotification(byte[] result,String location,task _task
    ,String requester_phonenumber){
        boolean send=false;
        String[] locations=location.split(",");
        Iterator<Map.Entry<String,ServiceServer>> iter=online_users.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,ServiceServer> entry= iter.next();
            String location2=entry.getValue()._sql_user._user.last_updatelocation();
            String[] location2s=location2.split(",");
            ServiceServer tmp=entry.getValue();
            if(tmp._sql_user._user.user_type()== Logic.volunteer){
                if(requester_phonenumber.equals(tmp._sql_user._user
                        .phone_number().substring(0,tmp._sql_user._user
                                .phone_number().length()-1))){
                    continue;
                }
                if(Filter.cal_distance(Double.parseDouble(locations[1]),Double.parseDouble(locations[0])
                        ,Double.parseDouble(location2s[1]),Double.parseDouble(location2s[0]))
                        <Filter.distance){
                    try {
                        tmp.addmessage(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    send=true;
                }
            }
        }
        return send;
    }
    public void run(){
        try(ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();
                    connection.setSoTimeout(ServerInfo.OUTTIME);
                    new ServiceServer(connection,_threadid.getnextid());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            Log.log("Couldn't start login server");
        }
    }
}

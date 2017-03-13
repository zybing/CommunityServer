package org.MagicZhang.Control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.MagicZhang.Log;
import org.MagicZhang.ServerInfo;
/**
 * Created by sonof on 2017/2/21.
 */
public class ServiceCenter extends Thread{
    public int PORT = ServerInfo.PORT;
    public int THREAD_NUM = ServerInfo.THREAD_NUM;
    public HashMap<String,ServiceServer> online_users=new
            HashMap<String,ServiceServer>();
    private ThreadId _threadid;
    private static ServiceCenter myself;
    public ExecutorService read_pool;
    public ExecutorService writer_pool;
    private ServiceCenter(){
        _threadid=new ThreadId(ServerInfo.THREADINITID);
        read_pool = Executors.newFixedThreadPool(THREAD_NUM);
        writer_pool = Executors.newFixedThreadPool(THREAD_NUM);
    }
    public static ServiceCenter getinstance(){
        if(myself==null){
            myself=new ServiceCenter();
        }
        return myself;
    }
    public synchronized void addonline_users(String phone_number,ServiceServer st){
        ServiceServer tmp=online_users.put(phone_number,st);
        Log.log("add users "+phone_number+" "+st);
        Log.log("online num:"+online_users.size());
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
    public synchronized void removeoffline_users(String phone_number,ServiceServer old_thread){
        ServiceServer st=online_users.get(phone_number);
        if(st!=null){
            if(old_thread==st)
            {
                st._sql_user.update_isonline((byte)0);
                if(!st.isfinish)
                    st.finish();
                online_users.remove(phone_number);
                Log.log("remove users "+phone_number+" "+st);
                Log.log("online num:"+online_users.size());
            }
            else{
                Log.log("user "+phone_number+" "+old_thread+" has been removed");
                Log.log("online num:"+online_users.size());
            }
        }
        else{
            Log.log("no user is been removed");
            Log.log("online num:"+online_users.size());
        }
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

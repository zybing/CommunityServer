package org.MagicZhang.Control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.MagicZhang.serverinfo;
/**
 * Created by sonof on 2017/2/21.
 */
public class servicecenter extends Thread{
    public int PORT =serverinfo.PORT;
    public int THREAD_NUM =serverinfo.THREAD_NUM;
    public HashMap<String,serviceserver> online_users=new
            HashMap<String,serviceserver>();
    private ThreadId _threadid;
    private static servicecenter myself;
    public servicecenter(){
        _threadid=new ThreadId(10000);
    }
    public static servicecenter getinstance(){
        if(myself==null){
            myself=new servicecenter();
        }
        return myself;
    }
    public synchronized void addonline_users(String phone_number,serviceserver st){
        serviceserver tmp=online_users.put(phone_number,st);
        System.out.println(new Date()+":add users "+phone_number+" "+st);
        System.out.println(new Date()+":online num:"+online_users.size());
        st._sql_user.update_isonline((byte)1);
        if(tmp!=null){
            if(!tmp.isfinish)
            {
                System.out.println(new Date()+":try finish "+tmp);
                tmp.finish();
            }
            else{
                System.out.println(new Date()+":"+tmp+"has been finished");
            }
        }
        else{
            System.out.println(new Date()+":this phoner_number user is first been added");
        }
    }
    public synchronized void removeoffline_users(String phone_number,serviceserver old_thread){
        serviceserver st=online_users.get(phone_number);
        if(st!=null){
            if(old_thread==st)
            {
                st._sql_user.update_isonline((byte)0);
                if(!st.isfinish)
                    st.finish();
                online_users.remove(phone_number);
                System.out.println(new Date()+":remove users "+phone_number+" "+st);
                System.out.println(new Date()+":online num:"+online_users.size());
            }
            else{
                System.out.println(new Date()+":user "+phone_number+" "+old_thread+"has been removed");
                System.out.println(new Date()+":online num:"+online_users.size());
            }
        }
        else{
            System.out.println(new Date()+":no user is been removed");
            System.out.println(new Date()+":online num:"+online_users.size());
        }
    }

    public void run(){
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUM);
        try(ServerSocket server = new ServerSocket(PORT)) {
            //_garbagefactory.start();
            while (true) {
                try {
                    Socket connection = server.accept();
                    connection.setSoTimeout(serverinfo.OUTTIME);
                    Callable<Void> task = new serviceserver(connection,_threadid.getnextid());
                    pool.submit(task);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.err.println(new Date()+":Couldn't start server");
        }
    }

}

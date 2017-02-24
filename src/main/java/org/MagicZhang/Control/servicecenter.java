package org.MagicZhang.Control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    public HashMap<String,servicethread> online_users=new
            HashMap<String,servicethread>();
    private static servicecenter myself;
    public servicecenter(){
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUM);
        try(ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();
                    connection.setSoTimeout(serverinfo.OUTTIME);
                    Callable<Void> task = new servicethread(connection);
                    pool.submit(task);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.err.println("Couldn't start server");
        }
    }
    public static servicecenter getinstance(){
        if(myself==null){
            myself=new servicecenter();
        }
        return myself;
    }
    public synchronized void addonline_users(String phone_number,servicethread st){
        servicethread tmp=online_users.put(phone_number,st);
        st._sql_user.update_isonline((byte)1);
        if(tmp!=null){
            tmp.finish();
        }
    }
    public synchronized void removeoffline_users(String phone_number){
        servicethread st=online_users.get(phone_number);
        if(st!=null){
            st._sql_user.update_isonline((byte)0);
            if(!st.isfinish)
                st.finish();
            online_users.remove(phone_number);
        }
    }
    @Override
    public void run(){
        while(true){
            try {
                Iterator iter = online_users.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String)entry.getKey();
                    servicethread val = (servicethread)entry.getValue();
                    if(val.isfinish){
                        online_users.remove(key);
                    }
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

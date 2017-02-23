package org.MagicZhang.Control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.MagicZhang.Sql.sql_user;
import org.MagicZhang.serverinfo;
/**
 * Created by sonof on 2017/2/21.
 */
public class servicecenter extends Thread{
    public int PORT =serverinfo.PORT;
    public int THREAD_NUM =serverinfo.THREAD_NUM;
    public HashMap<String,sql_user> online_users=new
            HashMap<String,sql_user>();
    public servicecenter(){
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUM);
        try(ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();
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
    public synchronized void addonline_users(String phone_number,sql_user online_user){
        online_users.put(phone_number,online_user);
    }
    public synchronized void removeoffline_users(String phone_number){
        online_users.remove(phone_number);
    }
    @Override
    public void run(){

    }
}

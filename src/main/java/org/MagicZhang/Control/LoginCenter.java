package org.MagicZhang.Control;

import org.MagicZhang.Log;
import org.MagicZhang.ServerInfo;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sonof on 2017/4/19.
 */
public class LoginCenter extends Thread{
    private ExecutorService login_pool;
    public LoginCenter(){
        login_pool= Executors.newCachedThreadPool();
    }
    public void run(){
        try(ServerSocket server = new ServerSocket(ServerInfo.LOGINPORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();
                    connection.setSoTimeout(ServerInfo.LOGINOUTTIME);
                    login_pool.submit(new LoginServer(connection));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            Log.log("Couldn't start Login server");
        }
    }
}

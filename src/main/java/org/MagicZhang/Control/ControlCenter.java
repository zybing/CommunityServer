package org.MagicZhang.Control;

import org.MagicZhang.Log;
import org.MagicZhang.ServerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sonof on 2017/6/4.
 */
public class ControlCenter extends Thread {
    private ExecutorService control_pool;
    public ControlCenter(){
        control_pool= Executors.newCachedThreadPool();
    }
    public void run(){
        try(ServerSocket server = new ServerSocket(ServerInfo.CONTROLPORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();
                    connection.setSoTimeout(ServerInfo.CONTROLTIME);
                    ControlServer ltmp=new ControlServer(connection);
                    Log.log("create connection Control "+
                            connection.getInetAddress().getHostAddress()+":"+
                            connection.getPort()+" "+ltmp);
                    control_pool.submit(ltmp);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            Log.log("Couldn't start Control server");
        }
    }
}

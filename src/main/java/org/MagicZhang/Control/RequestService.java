package org.MagicZhang.Control;

import org.MagicZhang.ServerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sonof on 2017/2/27.
 */
public class RequestService extends Thread {
    @Override
    public void run(){
        ExecutorService pool = Executors.newCachedThreadPool();
        try(ServerSocket server = new ServerSocket(ServerInfo.REQUESTPORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();
                    connection.setSoTimeout(ServerInfo.REQUESTOUTTIME);
                    Callable<Void> task = new RequestCallback(connection);
                    pool.submit(task);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.err.println(new Date()+":Couldn't start request server");
        }
    }
}

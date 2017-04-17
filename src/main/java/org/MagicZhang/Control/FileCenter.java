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
 * Created by sonof on 2017/4/7.
 */
public class FileCenter extends Thread{
    private ExecutorService file_pool;
    public FileCenter(){
        file_pool= Executors.newCachedThreadPool();
        File _file=new File(ServerInfo.root);
        if(!_file.exists())
            _file.mkdir();
    }
    public void run(){
        try(ServerSocket server = new ServerSocket(ServerInfo.FILEPORT)) {
            while (true) {
                try {
                    Socket connection = server.accept();
                    connection.setSoTimeout(ServerInfo.FILEOUTTIME);
                    file_pool.submit(new FileServer(connection));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            Log.log("Couldn't start File server");
        }
    }
}

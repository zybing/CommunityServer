package org.MagicZhang.Control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.MagicZhang.serverinfo;
/**
 * Created by sonof on 2017/2/21.
 */
public class servicecenter {
    public int PORT =serverinfo.PORT;
    public int THREAD_NUM =serverinfo.THREAD_NUM;
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
}

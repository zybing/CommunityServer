package org.MagicZhang.Control;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Created by sonof on 2017/2/21.
 */
public class servicethread implements Callable<Void> {
    private String phone_number;
    private Socket connection;
    public servicethread(Socket connection) {
        this.connection = connection;
    }
    @Override
    public Void call() throws Exception {
        while (true){
            if(true)
                break;
        }
        return null;
    }
}

package org.MagicZhang.Control;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by sonof on 2017/2/27.
 */
public class RequestCallback implements Callable<Void> {
    private Socket connection;
    public RequestCallback(Socket connection){
        this.connection=connection;
    }
    @Override
    public Void call() {

        return null;
    }
}

package org.MagicZhang.Control;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sonof on 2017/2/27.
 */
public class requestservice extends Thread {
    @Override
    public void run(){
        ExecutorService pool = Executors.newCachedThreadPool();
    }
}

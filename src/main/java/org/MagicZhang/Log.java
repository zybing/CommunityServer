package org.MagicZhang;

import java.util.Date;

/**
 * Created by sonof on 2017/3/13.
 */
public class Log {
    public static final boolean OUTPUT=true;
    public static void log(String info){
        if(OUTPUT){
            System.out.println(new Date()+":"+info);
        }
    }
}

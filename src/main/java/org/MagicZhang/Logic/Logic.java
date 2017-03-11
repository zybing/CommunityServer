package org.MagicZhang.Logic;

import org.MagicZhang.Modle.user;
import org.MagicZhang.Sql.Sql_user;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.MagicZhang.Control.*;

/**
 * Created by sonof on 2017/2/22.
 */
public class Logic {
    //operator type
    public final static int login=1;
    public final static int heartbeat=2;
    public final static int request=3;
    //user_type
    public final static int requester=1;
    public final static int volunteer=2;
    public final static int req_vol=3;
    public final static int unregister=4;
    public static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
    public static final String login(String phone_number,
                                   user _user,ServiceServer thread){
        String result=null;
        if(phone_number!=null){
            if(_user==null){
                result=Logic.login+" "+unregister+" "+0+" "
                        +0+"\r\n";//unregister
                Sql_user.insert_user(new user(phone_number,(byte)4,
                        "0000-00-00 00:00:01","0,0"
                    ,0,0,(byte)0));
                System.out.println(new Date()+":add unregisster user "+phone_number);
                    thread.finish();
            }
            else{
                result=Logic.login+" "+_user.user_type()+" "+_user.help_number()+" "
                        +_user.request_number()+"\r\n";
                if(_user.user_type()!=4){
                    ServiceCenter myself= ServiceCenter.getinstance();
                    myself.addonline_users(phone_number,thread);
                }
                else{
                    thread.finish();
                }
            }
        }
        else{
            System.out.println(new Date()+":phone_number is null");
        }
        return result;
    }
    public static final String hearbeat(String location, Sql_user _sql_user){
        String result=null;
        if(location!=null){
            if(_sql_user._user!=null){
                result="2 "+"yes\r\n";
                _sql_user.update_location(sdf.format(new Date()),location);
            }
        }
        else{
            System.out.println(new Date()+":location is null");
        }
        return result;
    }
    public static final String request(){
        return null;
    }
}

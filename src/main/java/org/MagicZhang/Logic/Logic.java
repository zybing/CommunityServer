package org.MagicZhang.Logic;

import org.MagicZhang.Modle.user;
import org.MagicZhang.Sql.sql_user;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

import org.MagicZhang.Control.*;

/**
 * Created by sonof on 2017/2/22.
 */
public class Logic {
    //operator type
    public final static int login=1;
    public final static int heartbeat=2;
    //user_type
    public final static int requester=1;
    public final static int volunteer=2;
    public final static int req_vol=3;
    public final static int unregister=4;
    public static final void login(String phone_number,BufferedWriter out,
                                   user _user,servicethread thread){
        if(_user==null){
            try {
                out.write(Logic.login+" "+unregister+" "+_user.help_number()+" "
                        +_user.request_number()+"\r\n");//unregister
                out.flush();
                sql_user.insert_user(new user(phone_number,(byte)4,
                        "0000-00-00 00:00:01","0,0"
                ,0,0,(byte)0));
                System.out.println(new Date()+":add unregisster user"+phone_number+"0 0");
                thread.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                out.write(Logic.login+" "+_user.user_type()+" "+_user.help_number()+" "
                        +_user.request_number()+"\r\n");
                out.flush();
                if(_user.user_type()!=4){
                    servicecenter myself=servicecenter.getinstance();
                    myself.addonline_users(phone_number,thread);
                }
                else{
                    thread.finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static final void hearbeat(String latitude,String longitude){

    }
}

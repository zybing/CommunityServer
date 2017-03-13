package org.MagicZhang.Logic;

import org.MagicZhang.Control.Util.Converter;
import org.MagicZhang.Log;
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
    public static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //未注册的用户不给予回复，客户端连接成功后为4，否则为0
    public static final byte[] login(String phone_number,
                                   user _user,ServiceServer thread){
        byte[] result=null;
        if(phone_number!=null){
            if(_user==null){
                Sql_user.insert_user(new user(phone_number,(byte)4,
                        "0000-00-00 00:00:01","0,0"
                    ,0,0,(byte)0));
                Log.log("add unregisster user "+phone_number);
                thread.finish();
            }
            else{
                if(_user.user_type()!=4){
                    result=new byte[13];
                    byte[] login=Converter.getBytes(Logic.login);
                    byte[] help_number=Converter.getBytes(_user.help_number());
                    byte[] request_number=Converter.getBytes(_user.request_number());
                    System.arraycopy(login,0,result,0,4);
                    result[4]=_user.user_type();
                    System.arraycopy(help_number,0,result,5,4);
                    System.arraycopy(request_number,0,result,9,4);
                    ServiceCenter myself= ServiceCenter.getinstance();
                    myself.addonline_users(phone_number,thread);
                }
                else{
                    thread.finish();
                }
            }
        }
        else{
            Log.log("phone_number is null");
        }
        return result;
    }
    public static final byte[] hearbeat(String location, Sql_user _sql_user){
        byte[] result=null;
        if(location!=null){
            if(_sql_user._user!=null){
                result=Converter.getBytes(2);
                _sql_user.update_location(sdf.format(new Date()),location);
            }
        }
        else{
            Log.log("location is null");
        }
        return result;
    }
    public static final byte[] request(String phone_number,String location,
                                       String taskinfo){

        return null;
    }
}

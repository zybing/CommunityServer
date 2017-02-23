package org.MagicZhang.Logic;

import org.MagicZhang.Modle.user;
import org.MagicZhang.Sql.sql_user;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by sonof on 2017/2/22.
 */
public class Logic {
    public final static int login=1;
    public final static int requester=1;
    public final static int volunteer=2;
    public final static int req_vol=3;
    public final static int unregister=4;
    public static final void login(String phone_number,BufferedWriter out){
        user _user= sql_user.query_byphonenumber(phone_number);
        if(_user==null){
            try {
                out.write(unregister+"\r\n");//unregister
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                out.write(_user.user_type()+" "+_user.help_number()+" "
                        +_user.request_number()+"\r\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

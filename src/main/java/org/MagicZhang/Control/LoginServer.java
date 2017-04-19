package org.MagicZhang.Control;

import org.MagicZhang.Control.Util.Converter;
import org.MagicZhang.Control.Util.SocketReader;
import org.MagicZhang.Log;
import org.MagicZhang.Logic.Logic;
import org.MagicZhang.Modle.user;
import org.MagicZhang.ServerInfo;
import org.MagicZhang.Sql.Sql_user;

import java.io.*;
import java.net.Socket;

/**
 * Created by sonof on 2017/4/19.
 */
public class LoginServer extends Thread{
    private Socket connection;
    public LoginServer(Socket connection){
        this.connection=connection;
    }
    public void run(){
        DataInputStream in=null;
        DataOutputStream out=null;
        try {
            in = new DataInputStream(new BufferedInputStream(
                    connection.getInputStream()));
            out= new DataOutputStream(new BufferedOutputStream
                    (connection.getOutputStream()));
            int type= SocketReader.readInt(in);
            if(type== Logic.login){
                int tmp=SocketReader.readInt(in);
                String phone_number=SocketReader.readString(in,tmp);
                Sql_user sql_user=new Sql_user(phone_number);
                if(phone_number!=null){
                    if(sql_user._user==null){
                        Sql_user.insert_user(new user(phone_number,"user_name",(byte)4,
                                "0000-00-00 00:00:01","0,0"
                                ,0,0,(byte)0,(byte)0,
                                (byte)0,"0",0));
                        out.write(Converter.getBytes(Logic.login));
                        out.write(Converter.getBytes(Logic.loginfailed));
                        Log.log("add unregisster user "+phone_number);
                        Log.log("login failed"+phone_number+this);
                    }
                    else{
                        if(sql_user._user.user_type()<Logic.unregister){
                            byte[] ip=Converter.getBytes(ServerInfo.HOSTNAME);
                            byte[] iplen=Converter.getBytes(ip.length);
                            byte[] port=Converter.getBytes(ServerInfo.PORT);
                            byte[] portlen=Converter.getBytes(port.length);
                            out.write(Converter.getBytes(Logic.login));
                            out.write(Converter.getBytes(Logic.loginsuccess));
                            out.write(iplen);
                            out.write(ip);
                            out.write(portlen);
                            out.write(port);
                            out.flush();
                            Log.log("login success"+phone_number+this);
                        }
                        else{
                            out.write(Converter.getBytes(Logic.login));
                            out.write(Converter.getBytes(Logic.loginfailed));
                            Log.log("login failed"+phone_number+this);
                        }
                    }
                }
                else{
                    Log.log("phone_number is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(connection!=null)
                    connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//finally关闭资源的前提是try中没有创建依赖于该资源的线程。

    }
}

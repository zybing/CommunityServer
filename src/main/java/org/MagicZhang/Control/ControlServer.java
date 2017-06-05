package org.MagicZhang.Control;

import org.MagicZhang.Control.Util.Converter;
import org.MagicZhang.Control.Util.SocketReader;
import org.MagicZhang.Filter;
import org.MagicZhang.Log;
import org.MagicZhang.Logic.Logic;
import org.MagicZhang.ServerInfo;

import java.io.*;
import java.net.Socket;

/**
 * Created by sonof on 2017/6/4.
 */
public class ControlServer extends Thread{
    private Socket connection;
    public ControlServer(Socket connection){
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
            if(type== Logic.control_filter){
                if(in.readByte()==Logic.control_true){
                    ServerInfo.allow_samephonenumber=true;
                }else{
                    ServerInfo.allow_samephonenumber=false;
                }

                if(in.readByte()==Logic.control_true){
                    ServerInfo.same_registerunit=true;
                }else{
                    ServerInfo.same_registerunit=false;
                }
                Filter.distance=SocketReader.readDouble(in);
                out.write(Logic.control_filter);
                out.flush();
            }
        } catch (IOException e) {
            Log.log("control server exception "+this);
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

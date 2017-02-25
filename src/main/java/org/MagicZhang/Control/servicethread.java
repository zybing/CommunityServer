package org.MagicZhang.Control;

import org.MagicZhang.Logic.Logic;
import org.MagicZhang.Modle.user;
import org.MagicZhang.Sql.sql_user;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Created by sonof on 2017/2/21.
 */
public class servicethread implements Callable<Void> {
    private String phone_number;
    private Socket connection;
    private BufferedWriter out;
    private BufferedReader in;
    public sql_user _sql_user;
    public boolean isfinish=false;
    public servicethread(Socket connection) {
        this.connection = connection;

    }
    @Override
    public Void call(){
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream(),"UTF-8"));
            in=new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            while(true){
                String value=in.readLine();
                if(value==null)
                {
                    System.out.println("server:socketthread finished "+phone_number);
                    break;
                }
                System.out.println(value);
                execute_cs(value,out);
            }
        } catch (IOException e) {
            System.out.println("server:socketthread finished "+phone_number);
        }
        finally {
            try {
                isfinish=true;
                if(connection!=null)
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void execute_cs(String value,BufferedWriter out){
        if(value==null)
            return;
        String[] infos=value.split("\\s+");
        int info1=Integer.parseInt(infos[0]);
        if(info1== Logic.login){
            String phone_number=infos[1];
            if(_sql_user==null)
                _sql_user= new sql_user(phone_number);
            if(this.phone_number==null)
                this.phone_number=phone_number;
            Logic.login(phone_number,out,_sql_user._user,this);
        }
    }
    public void finish(){
        if(in!=null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(out!=null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

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
                execute_cs(value,out);
            }
        } catch (IOException e) {
            //ignore
        }
        finally {
            try {
                connection.close();
            } catch (IOException e) {
                // ignore;
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
            Logic.login(phone_number,out);
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

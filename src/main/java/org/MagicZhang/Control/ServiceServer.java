package org.MagicZhang.Control;

import org.MagicZhang.Logic.Logic;
import org.MagicZhang.ServerInfo;
import org.MagicZhang.Sql.Sql_user;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by sonof on 2017/2/21.
 */
public class ServiceServer {
    private String phone_number;
    private Socket connection;
    public Sql_user _sql_user;
    public boolean isfinish=false;
    private int threadid;
    private ReadThread _readthread;
    private WriterThread _writerthread;
    public ArrayBlockingQueue<String> messages;
    public ServiceServer(Socket connection, int _threadid) {
        this.connection = connection;
        threadid=_threadid;
        messages=new ArrayBlockingQueue<String>(ServerInfo.MESSAGELEG);
        try {
            _readthread=new ReadThread();
            ServiceCenter.getinstance().read_pool.submit(_readthread);
            _writerthread=new WriterThread();
            ServiceCenter.getinstance().writer_pool.submit(_writerthread);
        } catch (IOException e) {
            System.out.println(new Date()+":server:socketthread create failed "+" "+this);
        }
    }

    public String execute_cs(String value){
        if(value==null)
            return null;
        String[] infos=value.split("\\s+");
        int info1=Integer.parseInt(infos[0]);
        if(info1== Logic.login){
            String phone_number=infos[1];
            if(_sql_user==null)
                _sql_user= new Sql_user(phone_number);
            if(this.phone_number==null)
                this.phone_number=phone_number;
            return Logic.login(phone_number,_sql_user._user,this);
        }
        else if(info1==Logic.heartbeat){
            String location=infos[1];
            return Logic.hearbeat(location,_sql_user);
        }
        else if(info1==Logic.request){
            String _phonenumber=infos[1];
            String _location=infos[2];
            String _requestinfo=infos[3];
        }
        return null;
    }
    public void finish(){
        if(connection!=null)
        {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class ReadThread implements Callable<Void>{
        public BufferedReader in;
        public ReadThread() throws IOException {
            in=new BufferedReader(new InputStreamReader(
                        connection.getInputStream(),"UTF-8"));
        }
        @Override
        public Void call() {
            try {
                while(true){
                    String value=in.readLine();
                    if(value==null)
                    {
                        System.out.println(new Date()+":server:socketthread finished "+
                                phone_number+" "+ServiceServer.this);
                        break;
                    }
                    System.out.println(new Date()+":"+value+" "+ServiceServer.this);
                    String result=execute_cs(value);
                    System.out.println(new Date()+":server:get execute result "+ phone_number+" "+
                            ServiceServer.this);
                    if(result!=null)
                    {
                        try {
                            if(messages.offer(result,ServerInfo.THRESHOLD, TimeUnit.MILLISECONDS)){
                                System.out.println(new Date()+":server:msg is added queue "+ phone_number+" "+
                                        ServiceServer.this);
                            }
                            else{
                                ServiceServer.this.finish();
                            }
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                    }
                    Thread.yield();
                }
            } catch (IOException e) {
                System.out.println(new Date()+":server:socketreadthread finished "
                        + phone_number+" "+ServiceServer.this);
            }
            finally {
                try {
                    if(connection!=null)
                        connection.close();
                    isfinish=true;
                    ServiceCenter.getinstance().removeoffline_users(phone_number,ServiceServer.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    public class WriterThread implements Callable<Void>{
        public BufferedWriter out;
        public WriterThread() throws IOException {
            out = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream(),"UTF-8"));
        }
        @Override
        public Void call(){
            try {
                while(true){
                    String value=messages.poll();
                    if(value!=null){
                        out.write(value);
                        out.flush();
                    }
                    if(connection.isClosed())
                        break;
                    Thread.yield();
                }
            } catch (IOException e) {

            }
            finally {
                System.out.println(new Date()+":server:socketwriterthread finished "
                        + phone_number+" "+ServiceServer.this);
            }
            return null;
        }
    }
}

package org.MagicZhang.Control;

import org.MagicZhang.Control.Util.SocketReader;
import org.MagicZhang.Log;
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
    public volatile boolean isfinish=false;
    private volatile boolean readfinish=false;
    private volatile boolean writefinish=false;
    private int threadid;
    private ReadThread _readthread;
    private WriterThread _writerthread;
    public ArrayBlockingQueue<byte[]> messages;
    public ServiceServer(Socket connection, int _threadid) {
        this.connection = connection;
        threadid=_threadid;
        messages=new ArrayBlockingQueue(ServerInfo.MESSAGELEG);
        try {
            _readthread=new ReadThread();
            ServiceCenter.getinstance().read_pool.submit(_readthread);
            _writerthread=new WriterThread();
            ServiceCenter.getinstance().writer_pool.submit(_writerthread);
        } catch (IOException e) {
            Log.log("server:socketthread create failed "+" "+this);
        }
    }

    public byte[] execute_cs(int type,DataInputStream in) throws IOException {
        if(type== Logic.login){
            String phone_number=SocketReader.readString(in,11);
            if(_sql_user==null)
                _sql_user= new Sql_user(phone_number);
            if(this.phone_number==null)
                this.phone_number=phone_number;
            return Logic.login(phone_number,_sql_user._user,this);
        }
        else if(type==Logic.heartbeat){
            int len=SocketReader.readInt(in);
            String location=SocketReader.readString(in,len);
            return Logic.hearbeat(location,_sql_user);
        }
        else if(type==Logic.request){
//            String _phonenumber=infos[1];
//            String _location=infos[2];
//            String _requestinfo=infos[3];
        }
        return null;
    }
    public void finish(){
        try {
            if(readfinish&&writefinish)
                isfinish=true;
            if(connection!=null)
            {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class ReadThread implements Callable<Void>{
        public DataInputStream in;
        public ReadThread() throws IOException {
            in=new DataInputStream(new BufferedInputStream(connection.getInputStream()));
        }
        @Override
        public Void call() {
            try {
                while(true){
                    int type= SocketReader.readInt(in);
                    byte[] result=execute_cs(type,in);
                    if(result!=null)
                    {
                        try {
                            if(messages.offer(result,ServerInfo.THRESHOLD, TimeUnit.MILLISECONDS)){
                                Log.log("server:msg is added queue "+ phone_number+" "+
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
                Log.log("server:socketreadthread finished "
                        + phone_number+" "+ServiceServer.this);
            }
            finally {
                readfinish=true;
                finish();
                if(isfinish)
                    ServiceCenter.getinstance().removeoffline_users(phone_number,
                        ServiceServer.this);
            }
            return null;
        }
    }
    public class WriterThread implements Callable<Void>{
        public DataOutputStream out;
        public WriterThread() throws IOException {
            out = new DataOutputStream(new BufferedOutputStream(connection.
                    getOutputStream()));
        }
        @Override
        public Void call(){
            try {
                while(true){
                    byte[] value=messages.poll();
                    if(value!=null){
                        out.write(value);
                        out.flush();
                    }
                    if(connection.isClosed())
                        break;
                    Thread.yield();
                }
            } catch (IOException e) {
                Log.log("server:socketwriterthread finished "
                        + phone_number+" "+ServiceServer.this);
            }
            finally {
                writefinish=true;
                finish();
                if(isfinish)
                    ServiceCenter.getinstance().removeoffline_users(phone_number,
                            ServiceServer.this);
            }
            return null;
        }
    }
}

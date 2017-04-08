package org.MagicZhang.Control;

import org.MagicZhang.Control.Util.SocketReader;
import org.MagicZhang.Log;
import org.MagicZhang.Logic.Logic;
import org.MagicZhang.Logic.Status;
import org.MagicZhang.ServerInfo;
import org.MagicZhang.Sql.Sql_task;
import org.MagicZhang.Sql.Sql_user;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sonof on 2017/2/21.
 */
public class ServiceServer {
    private String phone_number;
    private Socket connection;
    public Sql_user _sql_user;//user的内存映射是唯一的
    public Sql_task currenttask;
    public volatile Long logintime;
    public volatile boolean isfinish=false;
    private volatile boolean readfinish=false;
    private volatile boolean writefinish=false;
    private volatile boolean islogged=false;
    private int threadid;
    public Lock tasklock =new ReentrantLock();
    public static Lock acktasklock=new ReentrantLock();
//    public Lock userlock=new ReentrantLock();
    private ReadThread _readthread;
    private WriterThread _writerthread;
    private TaskExecute taskExecute;
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
            taskExecute=new TaskExecute();
            ServiceCenter.getinstance().tasks_pool.submit(taskExecute);
        } catch (IOException e) {
            Log.log("server:socketthread create failed "+" "+this);
        }
    }

    public byte[] execute_cs(int type,DataInputStream in) throws IOException {
        if(type== Logic.login){
            int tmp=SocketReader.readInt(in);
            String phone_number=SocketReader.readString(in,tmp);
            if(_sql_user==null)
                _sql_user= new Sql_user(phone_number);
            if(this.phone_number==null)
                this.phone_number=phone_number;
            if(currenttask==null)
                currenttask=new Sql_task(_sql_user._user.current_taskid());
            return Logic.login(phone_number,_sql_user._user,currenttask._task,
                    this);
        }
        else if(type==Logic.heartbeat){
            int len=SocketReader.readInt(in);
            String location=SocketReader.readString(in,len);
            return Logic.hearbeat(location,_sql_user);
        }
        else if(type==Logic.updateinfo){
            updateonlinetime();
            return Logic.updateinfo(phone_number,_sql_user._user,currenttask._task,
                    this,Logic.updateinfo);
        }
        else if(type==Logic.request){
            int tmp=0;
            tmp=SocketReader.readInt(in);
            String info=SocketReader.readString(in,tmp);
            tmp=SocketReader.readInt(in);
            String location=SocketReader.readString(in,tmp);
            String taskid=this.gentaskid();
            String fileurl="";
            Long identity=0L;
            if(info.equals("2")){
                fileurl="";
                identity=SocketReader.readLong(in);
            }
            return Logic.request(location,info,this,taskid,fileurl,identity);
        }
        else if(type==Logic.ack){//主动发的才需要进行身份识别，被动收的没办法
            int tmp=0;
            tmp=SocketReader.readInt(in);
            String taskid=SocketReader.readString(in,tmp);
            tmp=SocketReader.readInt(in);
            String request_phonenumber=SocketReader.readString(in,tmp);
            tmp=SocketReader.readInt(in);
            String location=SocketReader.readString(in,tmp);
            ServiceServer requester=ServiceCenter.getinstance().online_users.get(request_phonenumber);
            if(requester!=null){
                boolean result=requester.acktask(this,taskid,location);
                if(result){
                    try {
                        requester.addmessage(Logic.ack_requester(this));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return Logic.ack_helper(result);
            }
            else{
                boolean result=acktask(taskid,this,location);
                return Logic.ack_helper(result);
            }
        }
        else if(type==Logic.requester_finish){
            return Logic.requester_finish(this);
        }
        else if(type==Logic.helper_finish){
            return Logic.helper_finish(this);
        }
        else if(type==Logic.requestaudio){
            int tmp=0;
            tmp=SocketReader.readInt(in);
            String taskid=SocketReader.readString(in,tmp);

        }
        return null;
    }
    public void finish(){
        try {
            if(readfinish&&writefinish)
            {
                isfinish=true;
                islogged=false;
                updateonlinetime();
            }
            if(connection!=null)
            {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateonlinetime(){
        if(_sql_user._user.isonline()==1){
            int onlinetime= (int) (System.currentTimeMillis()/
                    60000-logintime);
            _sql_user.update_onlinetime(onlinetime);
            logintime=System.currentTimeMillis()/60000;
        }
    }
    public void login(){
        isfinish=false;
        islogged=true;
        logintime=System.currentTimeMillis()/60000;
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
                            addmessage(result);
                            if(type==Logic.login)
                                login();
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
            catch(Exception e){
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
                    if(islogged){
                        byte[] value=messages.poll();
                        if(value!=null){
                            out.write(value);
                            out.flush();
                        }
                        if(connection.isClosed())
                        {
                            Log.log("writer break");
                            break;
                        }
                    }
                    Thread.yield();
                }
            } catch (IOException e) {
                Log.log("server:socketwriterthread finished "
                        + phone_number+" "+ServiceServer.this);
            }
            catch(Exception e){
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
    public class TaskExecute implements  Callable<Void>{
        @Override
        public Void call(){
            try {
                while(true){
                    if(islogged){
                        if(_sql_user!=null&&_sql_user._user!=null)
                        {
                            if(_sql_user._user.user_type()==Logic.requester){
                                requester_execute();
                            }
                            else if(_sql_user._user.user_type()==Logic.volunteer){

                            }
                        }
                    }
                    if(connection.isClosed())
                    {
                        Log.log("taskexecute break");
                        break;
                    }
                    Thread.sleep(ServerInfo.tasktime);
                }
            }
            catch (Exception e){

            }
            finally {

            }
            return null;
        }
        public void requester_execute(){
            if(currenttask!=null&&currenttask._task!=null){
                if(currenttask._task.status()== Status.unpublish){
                    tasklock.lock();
                    try {
                        if(currenttask._task.status()== Status.unpublish){
                            byte rr = currenttask.publishtimeout(System.currentTimeMillis());
                            if (rr == (byte) 1) {
                                if(currenttask._task.request_info().equals("2")&&
                                        currenttask._task.fileurl().length()==0){
                                    Log.log("file haven't been upload");
                                    return;
                                }
                                byte[] result = Logic.notification(currenttask._task);
                                boolean tmp = ServiceCenter.getinstance().sendnotification(result,
                                        currenttask._task.request_location(), currenttask._task);
                                if (tmp) {
                                    currenttask.update_status(Status.publish);
                                    currenttask.setOrdertime(System.currentTimeMillis());
                                    byte[] data=Logic.order();
                                    addmessage(data);
                                    Log.log("task is published success"+phone_number
                                            +" "+this);
                                } else {
                                    Log.log("no user is filtered");
                                }
                            } else if (rr == (byte) 2) {
                                if (currenttask._task.status() == Status.unpublish)
                                {
                                    currenttask.update_status(Status.system_finish1);
                                    _sql_user.update_taskid("0");
                                    Log.log("task is end because sys_finish1");
                                    addmessage(Logic.sys_finish1());
                                }
                            }
                        }
                    }
                    catch (Exception e){

                    }
                    finally {
                        tasklock.unlock();
                    }
                }
                else if(currenttask._task.status()== Status.publish){
                    if(currenttask.order_timeout(System.currentTimeMillis())){
                        tasklock.lock();
                        try{
                            if(currenttask._task.status()== Status.publish)
                            {
                                currenttask.update_status(Status.system_finish2);
                                _sql_user.update_taskid("0");
                                addmessage(Logic.sys_finish2());
                            }
                        }
                        catch (Exception e){

                        }
                        finally {
                            tasklock.unlock();
                        }
                    }
                }
            }
        }
    }
    public void addmessage(byte[] data) throws InterruptedException {
        if(messages.offer(data,ServerInfo.THRESHOLD, TimeUnit.MILLISECONDS)){
            Log.log("msg has been add "+phone_number+" "+this);
        }
        else{
            ServiceServer.this.finish();
        }
    }
    public String gentaskid(){
        return ""+System.currentTimeMillis()+threadid;
    }
    public synchronized boolean acktask(ServiceServer helper,String taskid,
                                        String location){
        if(currenttask!=null&&currenttask._task!=null&&
                currenttask._task.task_id().equals(taskid)){
            if(currenttask._task.status()==Status.publish){
                currenttask.update_volunteerphonenumber(helper._sql_user._user.
                        phone_number());
                currenttask.update_acktime(Logic.sdf.format(new Date()));
                currenttask.update_acklocation(location);
                currenttask.update_status(Status.order);
                _sql_user.update_requeststatus(Status.finish_ui);
                helper._sql_user.update_helpnumber(1);
                helper._sql_user.update_helperstatus(Status.detailinfo_ui);
                helper._sql_user.update_taskid(taskid);
                helper.currenttask=new Sql_task(taskid);
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }
    public static boolean acktask(String taskid,ServiceServer helper,String location){
        Sql_task _task=new Sql_task(taskid);
        if(_task._task!=null){
            acktasklock.lock();
            try{
                if(_task._task.status()==Status.publish){
                    _task.update_volunteerphonenumber(helper._sql_user._user.phone_number());
                    _task.update_acktime(Logic.sdf.format(new Date()));
                    _task.update_acklocation(location);
                    _task.update_status(Status.order);
                    Sql_user _user=new Sql_user(_task._task.request_phone_number());
                    _user.update_requeststatus(Status.finish_ui);
                    helper._sql_user.update_helpnumber(1);
                    helper._sql_user.update_helperstatus(Status.detailinfo_ui);
                    helper._sql_user.update_taskid(taskid);
                    helper.currenttask=new Sql_task(taskid);
                    return true;
                }
                else{
                    return false;
                }
            }
            catch (Exception e){
                return false;
            }
            finally {
                acktasklock.unlock();
            }
        }else{
            return false;
        }
    }
    public void updatecurrenttaskinfo(){
        tasklock.lock();
        try{
            currenttask.update_task();
        }
        catch(Exception e){

        }
        finally {
            tasklock.unlock();
        }
    }
}

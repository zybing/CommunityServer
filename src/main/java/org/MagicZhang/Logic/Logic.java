package org.MagicZhang.Logic;

import org.MagicZhang.Control.Util.Converter;
import org.MagicZhang.Log;
import org.MagicZhang.Modle.task;
import org.MagicZhang.Modle.user;
import org.MagicZhang.ServerInfo;
import org.MagicZhang.Sql.Sql_task;
import org.MagicZhang.Sql.Sql_user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    public final static int updateinfo=3;
    public final static int request=4;
    public final static int order=5;
    public final static int notification=6;
    public final static int catchaudio=7;
    public final static int ack=8;
    public final static int sys_finish1=9;
    public final static int sys_finish2=10;
    public final static int requester_finish=11;
    public final static int helper_finish=12;
    public final static int offline=13;
    //user_type
    public final static int requester=1;
    public final static int volunteer=2;
    public final static int req_vol=3;
    public final static int unregister=4;
    //确认抢单的类型
    public final static byte ack_success=1;
    public final static byte ack_failed=0;
    //文件协议类型
    public final static int upfile=0;
    public final static int downloadfile=1;

    //登陆是否成功
    public final static int loginsuccess=0;
    public final static int loginfailed=1;
    //
    public static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //未注册的用户不给予回复，客户端连接成功后为4，否则为0
    public static final byte[] login(String phone_number,
                                   user _user,task _task,ServiceServer thread){
        byte[] result=null;
        if(phone_number!=null){
            if(_user==null){
                Sql_user.insert_user(new user(phone_number,"user_name",(byte)4,
                        "0000-00-00 00:00:01","0,0"
                    ,0,0,(byte)0,(byte)0,
                        (byte)0,"0",0));
                Log.log("add unregisster user "+phone_number);
                thread.finish();
            }
            else{
               result=updateinfo(phone_number,_user,_task,thread,Logic.login);
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
                result=Converter.getBytes(Logic.heartbeat);
                _sql_user.update_location(sdf.format(new Date()),location);
            }
        }
        else{
            Log.log("location is null");
        }
        return result;
    }
    public static final byte[] updateinfo(String phone_number,
                                          user _user,task _task,ServiceServer thread,int type){
        byte[] result=null;
        if(_user.user_type()!=Logic.unregister){
            int num=13;
            byte[] _type=Converter.getBytes(type);
            byte[] help_number=Converter.getBytes(_user.help_number());
            byte[] request_number=Converter.getBytes(_user.request_number());
            byte[] username=Converter.getBytes(_user.user_name());
            byte[] usernamelen=Converter.getBytes(username.length);
            num+=usernamelen.length;
            num+=username.length;
            byte[] online_time=Converter.getBytes(_user.online_time());
            num+=online_time.length;
            byte status_requester=_user.status_requester();
            num+=1;
            byte status_helper=_user.status_helper();
            num+=1;
            byte[] taskidlen;
            byte[] taskid;
            byte[] request_phone_numberlen;
            byte[] request_phone_number;
            byte[] request_usernamelen;
            byte[] request_username;
            byte[] volunteer_phone_numberlen;
            byte[] volunteer_phone_number;
            byte[] volunteer_usernamelen;
            byte[] volunteer_username;
            byte[] request_timelen;
            byte[] request_time;
            byte[] request_locationlen;
            byte[] request_location;
            byte[] task_infolen;
            byte[] task_info;
            byte[] task_ordertime;
            if(_task==null)
            {
                int index=0;
                taskidlen=Converter.getBytes("0".length());
                num+=taskidlen.length;
                taskid=Converter.getBytes("0");
                num+=taskid.length;
                result=new byte[num];
                System.arraycopy(_type,0,result,0,4);
                result[4]=_user.user_type();
                System.arraycopy(help_number,0,result,5,4);
                System.arraycopy(request_number,0,result,9,4);
                System.arraycopy(usernamelen,0,result,13,4);
                System.arraycopy(username,0,result,17,username.length);
                index=17+username.length;
                System.arraycopy(online_time,0,result,index,4);
                index+=4;
                result[index++]=status_requester;
                result[index++]=status_helper;
                System.arraycopy(taskidlen,0,result,index,taskidlen.length);
                index+=taskidlen.length;
                System.arraycopy(taskid,0,result,index,taskid.length);
            }
            else
            {
                int index=0;
                taskid=Converter.getBytes(_task.task_id());
                taskidlen=Converter.getBytes(taskid.length);
                num+=taskidlen.length;
                num+=taskid.length;
                String requesterphonenumber=_task.request_phone_number();
                if(requesterphonenumber.length()>1){
                    requesterphonenumber=requesterphonenumber.
                            substring(0,requesterphonenumber.length()-1);
                }
                request_phone_number=Converter.getBytes(requesterphonenumber);
                request_phone_numberlen=Converter.getBytes(request_phone_number
                        .length);
                num+=request_phone_numberlen.length;
                num+=request_phone_number.length;
                user _tmpusr=Sql_user.query_byphonenumber(_task.request_phone_number());
                request_username=Converter.getBytes(_tmpusr.user_name());
                request_usernamelen=Converter.getBytes(request_username.length);
                num+=request_usernamelen.length;
                num+=request_username.length;
                String volunteerphonenumber=_task.volunteer_phone_number();
                if(volunteerphonenumber.length()>1){
                    volunteerphonenumber=volunteerphonenumber.
                            substring(0,volunteerphonenumber.length()-1);
                }
                volunteer_phone_number=Converter.getBytes(volunteerphonenumber);
                volunteer_phone_numberlen=Converter.getBytes(volunteer_phone_number
                        .length);
                num+=volunteer_phone_numberlen.length;
                num+=volunteer_phone_number.length;
                _tmpusr=Sql_user.query_byphonenumber(_task.volunteer_phone_number());
                if(_tmpusr!=null)
                {
                    volunteer_username=Converter.getBytes(_tmpusr.user_name());
                    volunteer_usernamelen=Converter.getBytes(volunteer_username.length);
                    num+=volunteer_usernamelen.length;
                    num+=volunteer_username.length;
                }
                else{
                    volunteer_username=Converter.getBytes("");
                    volunteer_usernamelen=Converter.getBytes(0);
                    num+=volunteer_usernamelen.length;
                    num+=volunteer_username.length;
                }
                request_time=Converter.getBytes(_task.request_time());
                request_timelen=Converter.getBytes(request_time.length);
                num+=request_timelen.length;
                num+=request_time.length;
                request_location=Converter.getBytes(_task.request_location());
                request_locationlen=Converter.getBytes(request_location.length);
                num+=request_locationlen.length;
                num+=request_location.length;
                task_info=Converter.getBytes(_task.request_info());
                task_infolen=Converter.getBytes(task_info.length);
                num+=task_infolen.length;
                num+=task_info.length;
                num++;
                task_ordertime=Converter.getBytes(_task.ordertime());
                num+=8;
                result=new byte[num];
                System.arraycopy(_type,0,result,0,4);
                result[4]=_user.user_type();
                System.arraycopy(help_number,0,result,5,4);
                System.arraycopy(request_number,0,result,9,4);
                System.arraycopy(usernamelen,0,result,13,4);
                System.arraycopy(username,0,result,17,username.length);
                index=17+username.length;
                System.arraycopy(online_time,0,result,index,4);
                index+=4;
                result[index++]=status_requester;
                result[index++]=status_helper;
                System.arraycopy(taskidlen,0,result,index,taskidlen.length);
                index+=taskidlen.length;
                System.arraycopy(taskid,0,result,index,taskid.length);
                index+=taskid.length;
                System.arraycopy(request_phone_numberlen,0,result,index,
                        request_phone_numberlen.length);
                index+=request_phone_numberlen.length;
                System.arraycopy(request_phone_number,0,result,index,
                        request_phone_number.length);
                index+=request_phone_number.length;
                System.arraycopy(request_usernamelen,0,result,index,
                        request_usernamelen.length);
                index+=request_usernamelen.length;
                System.arraycopy(request_username,0,result,index,
                        request_username.length);
                index+=request_username.length;
                System.arraycopy(volunteer_phone_numberlen,0,result,index,
                        volunteer_phone_numberlen.length);
                index+=volunteer_phone_numberlen.length;
                System.arraycopy(volunteer_phone_number,0,result,index,
                        volunteer_phone_number.length);
                index+=volunteer_phone_number.length;
                System.arraycopy(volunteer_usernamelen,0,result,index,
                        volunteer_usernamelen.length);
                index+=volunteer_usernamelen.length;
                System.arraycopy(volunteer_username,0,result,index,
                        volunteer_username.length);
                index+=volunteer_username.length;
                System.arraycopy(request_timelen,0,result,index,
                        request_timelen.length);
                index+=request_timelen.length;
                System.arraycopy(request_time,0,result,index,request_time.length);
                index+=request_time.length;
                System.arraycopy(request_locationlen,0,result,index,
                        request_locationlen.length);
                index+=request_locationlen.length;
                System.arraycopy(request_location,0,result,index,
                        request_location.length);
                index+=request_location.length;
                System.arraycopy(task_infolen,0,result,index,
                        task_infolen.length);
                index+=task_infolen.length;
                System.arraycopy(task_info,0,result,index,
                        task_info.length);
                index+=task_info.length;
                result[index]=_task.status();
                index+=1;
                System.arraycopy(task_ordertime,0,result,index,
                        task_ordertime.length);
            }
            if(type==Logic.login)
            {
                Log.log(phone_number+" login success begin add to online user list "
                        +thread);
                ServiceCenter myself= ServiceCenter.getinstance();
                myself.addonline_users(phone_number,thread);
            }
        }
        else{
            Log.log(phone_number+" is a unregister user "+thread);
            thread.finish();
        }
        return result;
    }
    public static final byte[] notification(task _task){
        byte[] result=null;
        int num=4;
        byte[] _type=Converter.getBytes(Logic.notification);
        byte[] taskidlen;
        byte[] taskid;
        byte[] request_phone_numberlen;
        byte[] request_phone_number;
        byte[] request_usernamelen;
        byte[] request_username;
        byte[] volunteer_phone_numberlen;
        byte[] volunteer_phone_number;
        byte[] volunteer_usernamelen;
        byte[] volunteer_username;
        byte[] request_timelen;
        byte[] request_time;
        byte[] request_locationlen;
        byte[] request_location;
        byte[] task_infolen;
        byte[] task_info;
        byte[] task_ordertime;
        if(_task!=null)
        {
            int index=0;
            taskid=Converter.getBytes(_task.task_id());
            taskidlen=Converter.getBytes(taskid.length);
            num+=taskidlen.length;
            num+=taskid.length;
            String requesterphonenumber=_task.request_phone_number();
            if(requesterphonenumber.length()>1){
                requesterphonenumber=requesterphonenumber.
                        substring(0,requesterphonenumber.length()-1);
            }
            request_phone_number=Converter.getBytes(requesterphonenumber);
            request_phone_numberlen=Converter.getBytes(request_phone_number
                        .length);
            num+=request_phone_numberlen.length;
            num+=request_phone_number.length;
            user _tmpusr=Sql_user.query_byphonenumber(_task.request_phone_number());
            request_username=Converter.getBytes(_tmpusr.user_name());
            request_usernamelen=Converter.getBytes(request_username.length);
            num+=request_usernamelen.length;
            num+=request_username.length;
            String volunteerphonenumber=_task.volunteer_phone_number();
            if(volunteerphonenumber.length()>1){
                volunteerphonenumber=volunteerphonenumber.
                        substring(0,volunteerphonenumber.length()-1);
            }
            volunteer_phone_number=Converter.getBytes(volunteerphonenumber);
            volunteer_phone_numberlen=Converter.getBytes(volunteer_phone_number
                        .length);
            num+=volunteer_phone_numberlen.length;
            num+=volunteer_phone_number.length;
            _tmpusr=Sql_user.query_byphonenumber(_task.volunteer_phone_number());
            if(_tmpusr!=null){
                volunteer_username=Converter.getBytes(_tmpusr.user_name());
                volunteer_usernamelen=Converter.getBytes(volunteer_username.length);
                num+=volunteer_usernamelen.length;
                num+=volunteer_username.length;
            }
            else{
                volunteer_username=Converter.getBytes("");
                volunteer_usernamelen=Converter.getBytes(volunteer_username.length);
                num+=volunteer_usernamelen.length;
                num+=volunteer_username.length;
            }
            request_time=Converter.getBytes(_task.request_time());
            request_timelen=Converter.getBytes(request_time.length);
            num+=request_timelen.length;
            num+=request_time.length;
            request_location=Converter.getBytes(_task.request_location());
            request_locationlen=Converter.getBytes(request_location.length);
            num+=request_locationlen.length;
            num+=request_location.length;
            task_info=Converter.getBytes(_task.request_info());
            task_infolen=Converter.getBytes(task_info.length);
            num+=task_infolen.length;
            num+=task_info.length;
            num++;
            task_ordertime=Converter.getBytes(_task.ordertime());
            num+=8;
            result=new byte[num];
            System.arraycopy(_type,0,result,0,4);
            index+=4;
            System.arraycopy(taskidlen,0,result,index,taskidlen.length);
            index+=taskidlen.length;
            System.arraycopy(taskid,0,result,index,taskid.length);
            index+=taskid.length;
            System.arraycopy(request_phone_numberlen,0,result,index,
                        request_phone_numberlen.length);
            index+=request_phone_numberlen.length;
            System.arraycopy(request_phone_number,0,result,index,
                        request_phone_number.length);
            index+=request_phone_number.length;
            System.arraycopy(request_usernamelen,0,result,index,
                        request_usernamelen.length);
            index+=request_usernamelen.length;
            System.arraycopy(request_username,0,result,index,
                        request_username.length);
            index+=request_username.length;
            System.arraycopy(volunteer_phone_numberlen,0,result,index,
                        volunteer_phone_numberlen.length);
            index+=volunteer_phone_numberlen.length;
            System.arraycopy(volunteer_phone_number,0,result,index,
                        volunteer_phone_number.length);
            index+=volunteer_phone_number.length;
            System.arraycopy(volunteer_usernamelen,0,result,index,
                        volunteer_usernamelen.length);
            index+=volunteer_usernamelen.length;
            System.arraycopy(volunteer_username,0,result,index,
                        volunteer_username.length);
            index+=volunteer_username.length;
            System.arraycopy(request_timelen,0,result,index,
                        request_timelen.length);
            index+=request_timelen.length;
            System.arraycopy(request_time,0,result,index,request_time.length);
            index+=request_time.length;
            System.arraycopy(request_locationlen,0,result,index,
                        request_locationlen.length);
            index+=request_locationlen.length;
            System.arraycopy(request_location,0,result,index,
                        request_location.length);
            index+=request_location.length;
            System.arraycopy(task_infolen,0,result,index,
                        task_infolen.length);
            index+=task_infolen.length;
            System.arraycopy(task_info,0,result,index,
                        task_info.length);
            index+=task_info.length;
            result[index]=_task.status();
            index+=1;
            System.arraycopy(task_ordertime,0,result,index,
                    task_ordertime.length);
        }
        return result;
    }
    public static final byte[] request(String location,
                                       String taskinfo,ServiceServer thread,String taskid,
                                       String fileurl,long identity){
        byte[] result=null;
        if(thread._sql_user._user.user_type()==Logic.requester)
        {
            if(thread.currenttask==null||thread.currenttask._task==null||thread.currenttask.
                _task.status()>=Status.requester_finish){
                String request_phone_number=thread._sql_user._user.phone_number();
                String request_time=sdf.format(new Date());
                String volunteer_phone_number="";
                String ack_time="0000-00-00 00:00:01";
                String ack_location="0,0";
                byte status=0;
                task _task=new task(taskid,request_phone_number,request_time
                ,location,taskinfo,volunteer_phone_number,ack_time,ack_location
                ,status,0L,fileurl);
                Sql_task.insert_task(_task);
                int num=4;
                byte[] _type=Converter.getBytes(Logic.request);
                byte[] task_id=Converter.getBytes(_task.task_id());
                byte[] taskidlen=Converter.getBytes(task_id.length);
                num+=taskidlen.length;
                num+=task_id.length;
                byte[] requesttime=Converter.getBytes(request_time);
                byte[] requesttimelen=Converter.getBytes(requesttime.length);
                num+=requesttimelen.length;
                num+=requesttime.length;
                byte[] identityb=Converter.getBytes(identity);
                num+=8;
                byte[] fileip=null;
                byte[] fileiplen=null;
                byte[] fileport=null;
                if(identity>0){
                    fileip=Converter.getBytes(ServerInfo.FILEHOSTNAME);
                    fileiplen=Converter.getBytes(fileip.length);
                    num+=fileiplen.length;
                    num+=fileip.length;
                    fileport=Converter.getBytes(ServerInfo.FILEPORT);
                    num+=fileport.length;
                }
                result=new byte[num];
                System.arraycopy(_type,0,result,0,4);
                System.arraycopy(taskidlen,0,result,4,taskidlen.length);
                int index=4+taskidlen.length;
                System.arraycopy(task_id,0,result,index,task_id.length);
                index+=taskid.length();
                System.arraycopy(requesttimelen,0,result,index,requesttimelen.length);
                index+=requesttimelen.length;
                System.arraycopy(requesttime,0,result,index,requesttime.length);
                index+=requesttime.length;
                System.arraycopy(identityb,0,result,index,identityb.length);
                if(identity>0){
                    index+=identityb.length;
                    System.arraycopy(fileiplen,0,result,index,fileiplen.length);
                    index+=fileiplen.length;
                    System.arraycopy(fileip,0,result,index,fileip.length);
                    index+=fileip.length;
                    System.arraycopy(fileport,0,result,index,fileport.length);
                }
                thread.tasklock.lock();
                try{
                    thread.currenttask=new Sql_task(taskid);
                }
                catch (Exception e)
                {

                }
                finally {
                    thread.tasklock.unlock();
                }
                thread._sql_user.update_requestnumber(1);
                thread._sql_user.update_requeststatus(Status.waiting_ui);
                thread._sql_user.update_taskid(thread.currenttask._task.task_id());
            }
        }
        return result;
    }
    public static final byte[] order(){
        byte[] result=null;
        result=Converter.getBytes(Logic.order);
        return result;
    }
    public static final byte[] ack_helper(boolean value){
        byte[] result=new byte[5];
        if(value){
            byte[] _type=Converter.getBytes(Logic.ack);
            System.arraycopy(_type,0,result,0,4);
            result[4]=Logic.ack_success;
        }
        else{
            byte[] _type=Converter.getBytes(Logic.ack);
            System.arraycopy(_type,0,result,0,4);
            result[4]=Logic.ack_failed;
        }
        return result;
    }
    public static final byte[] ack_requester(ServiceServer helper){
        byte[] result=null;
        byte[] type;
        byte[] volunteer_phonenumberlen;
        byte[] volunteer_phonenumber;
        byte[] volunteer_usernamelen;
        byte[] volunteer_username;
        int num=4;
        type=Converter.getBytes(Logic.ack);
        String volunteerphonenumber=helper._sql_user._user.phone_number();
        if(volunteerphonenumber.length()>1){
            volunteerphonenumber=volunteerphonenumber.
                    substring(0,volunteerphonenumber.length()-1);
        }
        volunteer_phonenumber=Converter.getBytes(volunteerphonenumber);
        volunteer_phonenumberlen=Converter.getBytes(volunteer_phonenumber.length);
        num+=volunteer_phonenumberlen.length;
        num+=volunteer_phonenumber.length;
        volunteer_username=Converter.getBytes(helper._sql_user._user.user_name());
        volunteer_usernamelen=Converter.getBytes(volunteer_username.length);
        num+=volunteer_usernamelen.length;
        num+=volunteer_username.length;
        result=new byte[num];
        int index=0;
        System.arraycopy(type,0,result,index,4);
        index+=4;
        System.arraycopy(volunteer_phonenumberlen,0,result,index,volunteer_phonenumberlen.length);
        index+=volunteer_phonenumberlen.length;
        System.arraycopy(volunteer_phonenumber,0,result,index,volunteer_phonenumber.length);
        index+=volunteer_phonenumber.length;
        System.arraycopy(volunteer_usernamelen,0,result,index,volunteer_usernamelen.length);
        index+=volunteer_usernamelen.length;
        System.arraycopy(volunteer_username,0,result,index,volunteer_username.length);
        return result;
    }
    public static final byte[] sys_finish1(){
        byte[] result=null;
        result=Converter.getBytes(Logic.sys_finish1);
        return result;
    }
    public static final byte[] sys_finish2(){
        byte[] result=null;
        result=Converter.getBytes(Logic.sys_finish2);
        return result;
    }
    public static final byte[] offline(){
        byte[] result=null;
        result=Converter.getBytes(Logic.offline);
        return result;
    }

    public static final byte[] requester_finish(ServiceServer requester){
        byte[] result=null;
        if(requester.currenttask._task==null)
            requester.updatecurrenttaskinfo();
        if(requester.currenttask._task.status()<Status.requester_finish){
            String vtmp=requester.currenttask._task.volunteer_phone_number();
            ServiceServer vserver=ServiceCenter.getinstance().online_users.
                    get(vtmp);
            if(vserver!=null)
            {
                vserver._sql_user.update_helperstatus(Status.help_ui);
                vserver._sql_user.update_taskid("0");
                vserver.updatecurrenttaskinfo();
            }
            else{
                Sql_user vuser=new Sql_user(vtmp);
                vuser.update_taskid("0");
                vuser.update_helperstatus(Status.help_ui);
            }
            requester._sql_user.update_taskid("0");
            requester._sql_user.update_requeststatus(Status.request_ui);
            requester.currenttask.update_status(Status.requester_finish);
        }
        result=Converter.getBytes(Logic.requester_finish);
        return result;
    }
    public static final byte[] helper_finish(ServiceServer helper){
        byte[] result=null;
        if(helper.currenttask._task==null)
            helper.updatecurrenttaskinfo();
        if(helper.currenttask._task.status()<Status.requester_finish){
            String rtmp=helper.currenttask._task.request_phone_number();
            ServiceServer rserver=ServiceCenter.getinstance().online_users.
                    get(rtmp);
            if(rserver!=null)
            {
                rserver._sql_user.update_requeststatus(Status.request_ui);
                rserver._sql_user.update_taskid("0");
                rserver.updatecurrenttaskinfo();
            }
            else{
                Sql_user ruser=new Sql_user(rtmp);
                ruser.update_taskid("0");
                ruser.update_helperstatus(Status.request_ui);
            }
            helper._sql_user.update_taskid("0");
            helper._sql_user.update_helperstatus(Status.help_ui);
            helper.currenttask.update_status(Status.helper_finish);
        }
        result=Converter.getBytes(Logic.helper_finish);
        return result;
    }
    public static final byte[] catchaudio(){

        byte[] result=null;
        int num=0;
        byte[] type=Converter.getBytes(Logic.catchaudio);
        num+=type.length;
        byte[] fileip=Converter.getBytes(ServerInfo.FILEHOSTNAME);
        byte[] fileiplen=Converter.getBytes(fileip.length);
        num+=fileiplen.length;
        num+=fileip.length;
        byte[] fileport=Converter.getBytes(ServerInfo.FILEPORT);
        num+=fileport.length;
        result=new byte[num];
        int index=0;
        System.arraycopy(type,0,result,index,type.length);
        index+=type.length;
        System.arraycopy(fileiplen,0,result,index,fileiplen.length);
        index+=fileiplen.length;
        System.arraycopy(fileip,0,result,index,fileip.length);
        index+=fileip.length;
        System.arraycopy(fileport,0,result,index,fileport.length);
        return result;
    }
}

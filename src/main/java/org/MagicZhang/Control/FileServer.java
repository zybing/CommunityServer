package org.MagicZhang.Control;

import org.MagicZhang.Control.Util.Converter;
import org.MagicZhang.Control.Util.SocketReader;
import org.MagicZhang.Log;
import org.MagicZhang.Logic.Logic;
import org.MagicZhang.Logic.Status;
import org.MagicZhang.ServerInfo;
import org.MagicZhang.Sql.Sql_task;
import org.MagicZhang.Sql.Sql_user;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sonof on 2017/4/7.
 */
public class FileServer extends Thread {
    private Socket connection;
    public FileServer(Socket connection){
        this.connection=connection;
    }
    public void run(){
        DataInputStream in=null;
        DataOutputStream out=null;
        int type=-1;
        Sql_task sql_task=null;
        try {
            in = new DataInputStream(new BufferedInputStream(
                    connection.getInputStream()));
            out= new DataOutputStream(new BufferedOutputStream
                    (connection.getOutputStream()));
            type=SocketReader.readInt(in);
            if(type== Logic.upfile){
                long ctime=System.currentTimeMillis();
                int tmp=0;
                tmp=SocketReader.readInt(in);
                String taskid=SocketReader.readString(in,tmp);
                tmp=SocketReader.readInt(in);
                String extendname=SocketReader.readString(in,tmp);
                sql_task =new Sql_task(taskid);
                String requester_number=sql_task._task.request_phone_number();
                ServiceServer rserver=ServiceCenter.getinstance().online_users.get(requester_number);
                //
                File _dir=new File(ServerInfo.root+sql_task._task.
                        request_phone_number());
                if(!_dir.exists())
                    _dir.mkdir();
                //
                File _file=new File(ServerInfo.root+sql_task._task.
                        request_phone_number()+ServerInfo.separator+taskid+"."+extendname);
                FileOutputStream _filestream=new FileOutputStream(_file);
                byte[] content=new byte[1024];
                long ll= SocketReader.readLong(in);
                Log.log("uploadfile length"+ll+"b "+taskid+" "+this);
                int length=0;
                int count=0;
                Log.log("upload 0% "+taskid+" "+this);
                while((length = in.read(content, 0, content.length)) != -1) {
                    if((System.currentTimeMillis()-ctime)>Sql_task.fileup_time)
                    {
                        _filestream.flush();
                        _filestream.close();
                        sql_task.update_filestatus(Status.uploadfailed);
                        //更新requester和helper的task信息
                        ServiceServer _tmp=ServiceCenter.getinstance().
                                online_users.get(sql_task._task.request_phone_number());
                        if(_tmp!=null){
                            _tmp.updatecurrenttaskinfo();
                        }
                        _tmp=ServiceCenter.getinstance().
                                online_users.get(sql_task._task.volunteer_phone_number());
                        if(_tmp!=null){
                            _tmp.updatecurrenttaskinfo();
                        }
                        Log.log("upload file failed for time out "+this);
                        return;
                    }
                    count+=length;
                    _filestream.write(content, 0, length);
                    if(count==(int)ll)
                        break;
                }
                if(count<(int)ll){
                    _filestream.flush();
                    _filestream.close();
                    sql_task.update_filestatus(Status.uploadfailed);
                    Log.log("upload file failed "+this);
                    //更新requester和helper的task信息
                    ServiceServer _tmp=ServiceCenter.getinstance().
                            online_users.get(sql_task._task.request_phone_number());
                    if(_tmp!=null){
                        _tmp.updatecurrenttaskinfo();
                    }
                    _tmp=ServiceCenter.getinstance().
                            online_users.get(sql_task._task.volunteer_phone_number());
                    if(_tmp!=null){
                        _tmp.updatecurrenttaskinfo();
                    }
                    return;
                }
                Log.log("upload 100% "+taskid+" "+this);
                _filestream.flush();
                _filestream.close();
                out.write(Converter.getBytes(Logic.upfile));
                out.flush();
                sql_task.update_fileurl(_file.getPath().replace(ServerInfo.separator,'/'));
                sql_task.update_filestatus(Status.uploadsuccess);
                if(rserver!=null){
                    rserver._sql_user.update_requeststatus(Status.waiting_ui);
                }else{
                    Sql_user _sql_user=new Sql_user(requester_number);
                    _sql_user.update_requeststatus(Status.waiting_ui);
                }
                //更新requester和helper的task信息
                ServiceServer _tmp=ServiceCenter.getinstance().
                        online_users.get(sql_task._task.request_phone_number());
                if(_tmp!=null){
                    _tmp.updatecurrenttaskinfo();
                }
                _tmp=ServiceCenter.getinstance().
                        online_users.get(sql_task._task.volunteer_phone_number());
                if(_tmp!=null){
                    _tmp.updatecurrenttaskinfo();
                }
                Log.log("upload file success "+taskid+" "+this);
            }
            else if(type==Logic.downloadfile){
                int tmp=0;
                tmp=SocketReader.readInt(in);
                String taskid=SocketReader.readString(in,tmp);
                sql_task =new Sql_task(taskid);
                File _file=new File(sql_task._task.fileurl().replace('/',ServerInfo.separator));
                if(_file.exists())
                    Log.log("find download file success "+taskid+" "+this);
                else
                    Log.log("find download file failed "+taskid+" "+this);
                out.write(Converter.getBytes(_file.length()));
                int index=sql_task._task.fileurl().lastIndexOf('/');
                String filename=sql_task._task.fileurl().substring(index);
                byte[] file_name=Converter.getBytes(filename);
                byte[] file_namelen=Converter.getBytes(file_name.length);
                out.write(file_namelen);
                out.write(file_name);
                out.flush();
                FileInputStream _filestream=new FileInputStream(_file);
                byte[] content=new byte[1024];
                int length=0;
                Log.log("download 0% "+taskid+" "+this);
                while((length = _filestream.read(content, 0, content.length)) != -1) {
                    out.write(content, 0, length);
                    out.flush();
                }
                Log.log("download 100% "+taskid+" "+this);
                Log.log("download file success "+taskid+" "+this);
            }
        } catch (IOException e) {
            Log.log("file server exception "+this);
            if(type==Logic.upfile){
                Log.log("upload file exception "+this);
                sql_task.update_filestatus(Status.uploadfailed);
                //更新requester和helper的task信息
                ServiceServer _tmp=ServiceCenter.getinstance().
                        online_users.get(sql_task._task.request_phone_number());
                if(_tmp!=null){
                    _tmp.updatecurrenttaskinfo();
                }
                _tmp=ServiceCenter.getinstance().
                        online_users.get(sql_task._task.volunteer_phone_number());
                if(_tmp!=null){
                    _tmp.updatecurrenttaskinfo();
                }
            }
            e.printStackTrace();
        }
        finally {
            try {
                if(connection!=null)
                    connection.close();
                if(in!=null)
                    in.close();
                if(out!=null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//finally关闭资源的前提是try中没有创建依赖于该资源的线程。

    }
}

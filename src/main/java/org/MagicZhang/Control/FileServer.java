package org.MagicZhang.Control;

import org.MagicZhang.Control.Util.Converter;
import org.MagicZhang.Control.Util.SocketReader;
import org.MagicZhang.Log;
import org.MagicZhang.Logic.Logic;
import org.MagicZhang.ServerInfo;
import org.MagicZhang.Sql.Sql_task;

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
        try {
            in = new DataInputStream(new BufferedInputStream(
                    connection.getInputStream()));
            out= new DataOutputStream(new BufferedOutputStream
                    (connection.getOutputStream()));
            int type=SocketReader.readInt(in);
            if(type== Logic.upfile){
                int tmp=0;
                tmp=SocketReader.readInt(in);
                String taskid=SocketReader.readString(in,tmp);
                tmp=SocketReader.readInt(in);
                String extendname=SocketReader.readString(in,tmp);
                Sql_task sql_task =new Sql_task(taskid);
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
                int length=0;
                int count=0;
                while((length = in.read(content, 0, content.length)) != -1) {
                    count+=length;
                    _filestream.write(content, 0, length);
                    if(count==(int)ll)
                        break;
                }
                _filestream.flush();
                _filestream.close();
                out.write(Converter.getBytes(Logic.upfile));
                out.flush();
                sql_task.update_fileurl(_file.getPath().replace(ServerInfo.separator,'/'));
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
                Sql_task sql_task =new Sql_task(taskid);
                File _file=new File(sql_task._task.fileurl().replace('/',ServerInfo.separator));
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
                while((length = _filestream.read(content, 0, content.length)) != -1) {
                    out.write(content, 0, length);
                    out.flush();
                }
                Log.log("download file success "+taskid+" "+this);
            }
        } catch (IOException e) {
            Log.log("file server exception "+this);
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

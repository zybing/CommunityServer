package org.MagicZhang;

/**
 * Created by sonof on 2017/2/21.
 */
public class ServerInfo {
    //当前服务器的唯一id
    public static final String HOSTID="00001";

    //有多个可请求的服务器，后面做成链表，或者每个服务器用不同的配置
    public static final int PORT=8493;//请求端口号
    public static final String HOSTNAME="120.55.188.107";//请求主机号1
//    public static final String HOSTNAME="localhost";//请求主机号1
    public static final int THREAD_NUM=200;//线程池的最大用户数

    public static final int OUTTIME=15000;//socket超时连接时间
    public static final int MESSAGELEG=30;//由writer下午的阻塞队列的最大长度
    public static final int THRESHOLD=10000;//添加队列消息时的最长等待时间
    public static final int THREADINITID=10000;//线程id的变化范围，最小值
    public static final int THREADMAXID=99999;//线程id的变化范围，最大值
    public static final String root="/home/mhcl/server/";//服务器存储音频的位置
//    public static final String root="D:\\community\\server\\";//服务器存储音频的位置
    //认为只有一个文件服务器
    public static final int FILEPORT=9024;//文件服务器端口号
    public static final String FILEHOSTNAME="120.55.188.107";//文件服务器ip
//    public static final String FILEHOSTNAME="localhost";//文件服务器ip
    public static final int FILEOUTTIME=10000;//上传文件的socket的超时连接时间

    public static char separator='/';//文件系统分隔符，不同系统不同
//    public static char separator='\\';//文件系统分隔符，不同系统不同
    public static int tasktime=500;//处理任务的线程的每次处理的间隔时间，毫秒
    public static int readtime=500;//读的线程的每次处理的间隔时间，毫秒
    public static int writetime=500;//写的线程的每次处理的间隔时间，毫秒

    //只有一个登陆服务器
    public static int LOGINPORT=6666;//端口号
    public static int LOGINOUTTIME=10000;//登录的超时连接时间

    //控制服务器
    public static int CONTROLPORT=7777;
    public static int CONTROLTIME=8000;

    public static boolean allow_samephonenumber=false;
    public static boolean same_registerunit=true;
}

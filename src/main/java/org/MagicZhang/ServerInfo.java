package org.MagicZhang;

/**
 * Created by sonof on 2017/2/21.
 */
public class ServerInfo {
    public static final int PORT=6666;//请求端口号
    public static final int THREAD_NUM=500;//线程池的最大用户数
    public static final int OUTTIME=15000;//socket超时连接时间
    public static final int MESSAGELEG=50;//由writer下午的阻塞队列的最大长度
    public static final int THRESHOLD=10000;//添加队列消息时的最长等待时间
    public static final int THREADINITID=10000;//线程id的变化范围，最小值
    public static final int THREADMAXID=99999;//线程id的变化范围，最大值
    public static final String root="/home/mhcl/server/";//服务器存储音频的位置

    public static final int FILEPORT=9999;//文件端口号
    public static final int FILEOUTTIME=10000;//上传文件的socket的超时连接时间

    public static char separator='/';//文件系统分隔符，不同系统不同
    public static int tasktime=500;//处理任务的线程的每次处理的间隔时间，毫秒
    public static int readtime=500;//读的线程的每次处理的间隔时间，毫秒
    public static int writetime=500;//写的线程的每次处理的间隔时间，毫秒
}

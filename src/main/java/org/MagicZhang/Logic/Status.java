package org.MagicZhang.Logic;

/**
 * Created by sonof on 2017/3/24.
 */
public class Status {
    //task_status
    public final static byte unpublish=0;
    public final static byte publish=1;
    public final static byte order=2;
    public final static byte requester_finish=3;
    public final static byte helper_finish=4;
    public final static byte system_finish1=5;
    public final static byte system_finish2=6;
    //requester_status
    public final static byte request_ui=0;
    public final static byte waiting_ui=1;
    public final static byte finish_ui=2;
    //helper_status
    public final static byte help_ui=0;
    public final static byte detailinfo_ui=1;
    //file_status
    public final static byte nfile=0;
    public final static byte uploadfile=1;
    public final static byte uploadsuccess=2;
    public final static byte uploadfailed=3;
}

package org.MagicZhang.Control;

/**
 * Created by sonof on 2017/2/27.
 * 还是会有重复问题，以后在解决吧
 */
public class ThreadId {
    private int initalid;
    private int maxid;
    private int id;
    public ThreadId(int _initialid,int _maxid){
        this.initalid=_initialid;
        this.maxid=_maxid;
        id=initalid;
    }
    //if only callback in ServiceCenter it needn't synchronized
    public int getnextid(){
        id++;
        if(id>maxid)
            id=initalid;
        return id;
    }
}

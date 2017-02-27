package org.MagicZhang.Control;

/**
 * Created by sonof on 2017/2/27.
 */
public class ThreadId {
    private int initalid;
    public ThreadId(int _initialid){
        this.initalid=_initialid;
    }
    //if only callback in servicecenter it needn't synchronized
    public int getnextid(){
        return initalid++;
    }
}

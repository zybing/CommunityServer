package org.MagicZhang;

import org.MagicZhang.Control.ThreadId;
import org.MagicZhang.Control.servicecenter;

/**
 * Created by sonof on 2017/2/23.
 */
public class Program {

    public static void main(String[] args){
        servicecenter _servicecenter=servicecenter.getinstance();
        _servicecenter.start();
    }
}

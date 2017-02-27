package org.MagicZhang;

import org.MagicZhang.Control.ServiceCenter;

/**
 * Created by sonof on 2017/2/23.
 */
public class Program {

    //here we use phone_number as login token,maybe leading to big error
    public static void main(String[] args){
        ServiceCenter _servicecenter= ServiceCenter.getinstance();
        _servicecenter.start();
    }
}

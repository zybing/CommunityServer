package org.MagicZhang;

import org.MagicZhang.Control.ControlCenter;
import org.MagicZhang.Control.FileCenter;
import org.MagicZhang.Control.LoginCenter;
import org.MagicZhang.Control.ServiceCenter;


/**
 * Created by sonof on 2017/2/23.
 */
public class Program {

    //here we use phone_number as login token,maybe leading to big error
    public static void main(String[] args){
        ServiceCenter _servicecenter= ServiceCenter.getinstance();
        _servicecenter.start();
        FileCenter _filecenter=new FileCenter();
        _filecenter.start();
        LoginCenter _logincenter=new LoginCenter();
        _logincenter.start();
        ControlCenter _controlcenter=new ControlCenter();
        _controlcenter.start();
    }
}

package org.MagicZhang.Control.Util;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by sonof on 2017/3/13.
 */
public class SocketReader {
    public static int readInt(DataInputStream in) throws IOException {
        byte[] data=new byte[4];
        int count=0;
        int len;
        while ((len=in.read(data,count,data.length-count))!=-1){
            count+=len;
            if(count==4)
                break;
        }
        if(len==-1)
            throw new IOException();
        else
            return Converter.getInt(data);
    }
    public static String readString(DataInputStream in,int length) throws IOException {
        byte[] data=new byte[length];
        int count=0;
        int len;
        while ((len=in.read(data,count,data.length-count))!=-1){
            count+=len;
            if(count==length)
                break;
        }
        if(len==-1)
            throw new IOException();
        else
            return Converter.getString(data);
    }
    public static long readLong(DataInputStream in) throws IOException {
        byte[] data=new byte[8];
        int count=0;
        int len;
        while ((len=in.read(data,count,data.length-count))!=-1){
            count+=len;
            if(count==8)
                break;
        }
        if(len==-1)
            throw new IOException();
        else
            return Converter.getLong(data);
    }
    public static Double readDouble(DataInputStream in) throws IOException {
        byte[] data=new byte[8];
        int count=0;
        int len;
        while ((len=in.read(data,count,data.length-count))!=-1){
            count+=len;
            if(count==8)
                break;
        }
        if(len==-1)
            throw new IOException();
        else
            return Converter.getDouble(data);
    }
}

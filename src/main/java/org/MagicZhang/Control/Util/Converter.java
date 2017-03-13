package org.MagicZhang.Control.Util;

/**
 * Created by sonof on 2017/3/12.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author MagicZhang
 *低位排序,java本身是高位排序
 */
@SuppressWarnings("ALL")
public class Converter
{
    public static final String DEFAULTCHARSET="UTF-8";
    public static char ReadChar(byte[] source, int from)
    {
        byte[] bs=new byte[2];
        System.arraycopy(source, from, bs, 0, 2);
        return getChar(bs);
    }

    public static String ReadString(byte[] source, int from, int length)
    {
        byte[] bs = new byte[length];
        System.arraycopy(source, from,bs,0,length);
        return getString(bs).trim();
    }

    public static String GetTimeString(long time, SimpleDateFormat sdf)
    {
        return sdf.format(new Date(time));
    }

    public static byte[] LongTime2BCD(long _time)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(_time);

        byte[] GPSTime= new byte[6];
        GPSTime[0] = (byte) (cal.get(Calendar.YEAR) - 2000);
        GPSTime[1] = (byte) (cal.get(Calendar.MONTH) + 1);
        GPSTime[2] = (byte) cal.get(Calendar.DAY_OF_MONTH);
        GPSTime[3] = (byte) cal.get(Calendar.HOUR_OF_DAY);
        GPSTime[4] = (byte) cal.get(Calendar.MINUTE);
        GPSTime[5] = (byte) cal.get(Calendar.SECOND);
        return GPSTime;
    }

    public static byte[] GetDString(String s)
    {
        int length=s.length();
        byte[] dstring=new byte[length+1];
        dstring[0]=(byte)length;
        System.arraycopy(getBytes(s), 0, dstring, 1, length);
        return dstring;
    }

    public static void WriteDString(String s, byte[] bs, int start)
    {
        byte[] dstring=GetDString(s);
        System.arraycopy(dstring, 0, bs, start, dstring.length);
    }

    public static byte[] FromDString2BS(byte[] dstring, int start)
    {
        int length=dstring[start];
        byte[] bs=new byte[length];
        System.arraycopy(dstring, start+1, bs, 0, length);
        return bs;
    }

    public static String FromDString2String(byte[] dstring, int start)
    {
        return getString(FromDString2BS(dstring,start));
    }
    public static byte[] PlusBytes(byte[] first, byte[] second) {

        byte[] newByte = new byte[first.length + second.length];

        System.arraycopy(first, 0, newByte, 0, first.length);

        System.arraycopy(second, 0, newByte, first.length, second.length);

        return newByte;

    }

    public static byte[] getBytes(short data)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >>>8);
        return bytes;
    }

    public static byte[] getBytes(char data)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >>> 8);
        return bytes;
    }

    public static byte[] getBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >>> 8);
        bytes[2] = (byte) ((data & 0xff0000) >>> 16);
        bytes[3] = (byte) ((data & 0xff000000) >>> 24);
        return bytes;
    }

    public static byte[] getBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >>> 8) & 0xff);
        bytes[2] = (byte) ((data >>> 16) & 0xff);
        bytes[3] = (byte) ((data >>> 24) & 0xff);
        bytes[4] = (byte) ((data >>> 32) & 0xff);
        bytes[5] = (byte) ((data >>> 40) & 0xff);
        bytes[6] = (byte) ((data >>> 48) & 0xff);
        bytes[7] = (byte) ((data >>> 56) & 0xff);
        return bytes;
    }

    public static byte[] getBytes(float data)
    {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data)
    {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName)
    {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static byte[] getBytes(String data)
    {
        return getBytes(data, DEFAULTCHARSET);
    }

    public static short getShort(byte[] bytes)
    {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static char getChar(byte[] bytes)
    {
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static int getInt(byte[] bytes)
    {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    public static long getLong(byte[] bytes)
    {
        return(0xffL & (long)bytes[0]) | (0xff00L & ((long)bytes[1] << 8)) | (0xff0000L & ((long)bytes[2] << 16)) | (0xff000000L & ((long)bytes[3] << 24))
                | (0xff00000000L & ((long)bytes[4] << 32)) | (0xff0000000000L & ((long)bytes[5] << 40)) | (0xff000000000000L & ((long)bytes[6] << 48)) | (0xff00000000000000L & ((long)bytes[7] << 56));
    }

    public static float getFloat(byte[] bytes)
    {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static double getDouble(byte[] bytes)
    {
        long l = getLong(bytes);
        return Double.longBitsToDouble(l);
    }

    public static String getString(byte[] bytes, String charsetName)
    {
        return new String(bytes, Charset.forName(charsetName));
    }

    public static String getString(byte[] bytes)
    {
        Charset charset = Charset.forName(DEFAULTCHARSET);
        return new String(bytes,charset).trim();
    }
    public static String byteArrayToHex(byte[] byteArray)
    {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };

        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray =new char[byteArray.length * 2];

        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray)
        {
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    public static String fileMD5(String inputFile) throws IOException
    {
        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;

        try
        {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest =MessageDigest.getInstance("MD5");

            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream,messageDigest);

            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer =new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0);

            // 获取最终的MessageDigest
            messageDigest= digestInputStream.getMessageDigest();

            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();

            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray);
        }
        catch (NoSuchAlgorithmException e)
        {
            return null;
        }
        finally
        {
            try
            {
                digestInputStream.close();

            }
            catch (Exception e)
            {

            }

            try
            {
                fileInputStream.close();
            }
            catch (Exception e)
            {

            }
        }
    }

}

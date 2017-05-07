package org.MagicZhang;

/**
 * Created by sonof on 2017/3/27.
 * 设置志愿者的筛选半径，以及包括计算给定两点的距离
 */
public class Filter {
    public static final double distance=200000000;
    //计算两点经纬度
    public static double cal_distance(double lon1,double lat1,
                                      double lon2,double lat2){
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (lon1 - lon2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }
}

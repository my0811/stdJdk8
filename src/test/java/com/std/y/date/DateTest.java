package com.std.y.date;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {

    @Test

    public void dateTest() throws ParseException, UnsupportedEncodingException {
        SimpleDateFormat format1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:sss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy");

        String d1 = "1990-08-11 23:11:11:156";
        String d2 = "1998-08-11";
        String d3="2018";

        System.out.println("d1 "+format1.parse(d1).getTime());
        System.out.println("d2 "+format2.parse(d2).getTime());
        System.out.println("d3 "+ Math.abs(format3.parse(d3).getTime()));
        System.out.println("now " +System.currentTimeMillis());
        System.out.println("dateTime"+new Date().getTime());
        String length="yang-zhong-kui";
        System.out.println("length "+length.getBytes("utf-8").length);
        Date nowDate=new Date(1543635343833L);
        System.out.println("nowDate "+format1.format(nowDate));

        System.out.println("int: "+Long.MAX_VALUE);



    }
}

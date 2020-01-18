package com.std.y.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class YJdk7Date {
    public static void main(String[] args) throws ParseException {

        /*
         * date 中大多数的方方都是过时了，不建议使用，比较恶心也
         * */
        Date d = new Date();
        // 返回的1900与当前年份的差值
        System.out.println(d.getYear());
        /*
         * dateCalada
         * */
        Calendar calendar = Calendar.getInstance();
        // toString打印
        System.out.println(calendar);

        // 根据常量获取日期的年月日信息，但是注意要看常量的意思，int类型，不是枚举很蛋疼
        System.out.println("通过常量字段获取对应的年份:" + calendar.get(Calendar.YEAR));

        // 设置字段值，更改时间
        calendar.add(Calendar.YEAR, 1);
        System.out.println("根据字段常量值修改年份: " + calendar.get(Calendar.YEAR));

        // 设置年月日
        calendar.set(1999, 1, 1);
        System.out.println("设置年份:" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH));

        // date对象转换成long类型，格林威治时间毫秒
        System.out.println("date to gl:" + d.getTime());

        // long 类型转换成date,从1970位开始计算差债，如果为负数，则转换成1970年
        System.out.println("long to date" + new Date(1));

        // calendar 与long毫秒转换
        System.out.println("calendar to long:" + calendar.getTimeInMillis());

        // calendar set long
        calendar.setTimeInMillis(31231231231223123L);
        System.out.println("calendar set long:" + calendar.getTimeInMillis());

        // calendar 与date对象组合使用
        calendar.setTime(new Date());
        System.out.println("与date组合使用:" + calendar.get(Calendar.YEAR));

        // format使用
        System.out.println("calendar dayOfWeek: " + dayOfWeek("2020-01-17 00:00:00"));

        //
    }


    public static int dayOfWeek(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        calendar.setTime(format.parse(date));
        // 国外周日是第一天
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

}

package com.std.y.date;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

/**
 * jdk 8日期使用
 * java.time下的类都是线程安全的，并且每次操作返回的对象都是不可变的，类似于String 和bigDecimal
 *
 *
 * LocalDate  日期计算处理  eg:2000-01-01
 * LocalTime  只处理时间  23:59:59.124
 * LocalDateTime 处理时间和日期 2000-01-01 23:59:59.124
 * Instant 时间戳，是用当前时间-1970-01-01 00：00：00
 * System.currentTimeMillis()返回的是格林威治时间，UTC时间，从1970到现在经过的毫秒数
 *
 *
 * 时区转换方法，请用withZoneSameInstant
 * */
public class YJdk8Date {
    public static void main(String[] args) {

        System.out.println("old java date now: " + new Date());

        // 日期、时间、日期时间常用处理
        System.out.println();
        common();

        // 时区相关处理
        System.out.println();
        zone();

        // 例如生日这种只有月日的判断
        System.out.println();
        mothDay();

        // 时钟clock
        System.out.println();
        clock();

        // 处理年月
        yearMonth();

        // 处理日期之间间隔天数
        between();

        // 时间戳使用
        instant();

        // 日期格式化
        formatter();

        // 时区转换使用withZoneSameInstant（），注意里面的坑，看此方法的注释
        withZoneSameInstant();
    }

    private static void common() {
        /*
         * LocalDate日期,存储的是到天的日期,比如入职日期,
         * 默认就是一个yyyy-MM-dd的时间个是，
         * */
        LocalDate date = LocalDate.now();
        System.out.println("common() localDate now: " + date);

        // 获取年月日
        String ymd = "year: " + date.getYear() + "moth: " + date.getMonth().getValue() + "dayOfWeek: " + date.getDayOfWeek();
        System.out.println("common() " + ymd);

        // 增加天,月，年...
        date.plusDays(1);
        System.out.println("common() date: " + date + "plusDays: " + date + " date.plusDays(1): " + date.plusDays(1));

        /*
         * 没有日期，只有时间
         * */
        LocalTime time = LocalTime.now();
        System.out.println("common() localTime now: " + time);

        // 时间加减
        System.out.println("common() localTime plus 1 hour: " + time.plusHours(1));
        System.out.println("common() localTime plus 2 hour: " + time.plus(2, ChronoUnit.HOURS));
        // UnsupportedTemporalTypeException: Unsupported unit: Days
        //System.out.println("localTime plus 2 day: "+time.plus(2,ChronoUnit.DAYS));
        System.out.println("common() localTime minus 1 hour: " + time.minusHours(1));

        /*
         * 包含日期和时间，但是没有时区偏移量
         * */
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println("common() dateTime now: " + dateTime);
        System.out.println("common() dateTime plus 1 hour: " + dateTime.plusHours(1));


        /*
         * 包含时区的完整日期时间，偏移量以UTC/格林威治时间为准
         * */
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println("common() localDate zonedDateTime now: " + zonedDateTime);
        System.out.println("common() localDate zonedDateTime plus 1 hours: " + zonedDateTime.plusHours(1));


        // 直接创建日期
        LocalDate date2 = LocalDate.of(2019, 9, 12);
        System.out.println("common() ofDate: " + date2);

        // 判断两个日期是否相等
        LocalDate date3 = LocalDate.of(2018, 10, 12);
        LocalDate date4 = LocalDate.of(2018, 10, 12);
        System.out.println("common() equals: " + date3.equals(date4));


        // 时间前后比较，日期或者time
        LocalDate lD1 = LocalDate.of(2019, 10, 1);
        LocalDate lD2 = LocalDate.of(2019, 10, 2);
        System.out.println("common() isAfter: " + lD1.isAfter(lD2));
        System.out.println("common() isBefore: " + lD1.isBefore(lD2));
        System.out.println("common() isEquals: " + lD1.isEqual(lD2));


        // 判断是否是闰年
        LocalDate leapDate = LocalDate.now();
        System.out.println("common() isLeapYear: " + leapDate.isLeapYear());
    }

    /**
     * 时区处理
     * GMT-格林尼治标准时间，格林威治平均时间
     *
     * UTC-世界协调时间，UTC比GMT更加精准
     *
     * CST(Central Standard Time):可以支持表示如下四个不同国家的时区：
     * USA         UTC-6:00
     * Australia   UTC+9:30
     * China       UTC+8:00
     * Cuba        UTC-4:00
     *
     * UTC 和GMT基本差不多，时间都是英国伦敦本土的时间是一样的
     * 中国其实就是常说的东8区，比UTC向后8小时+8小时
     * */
    private static void zone() {
        // 直接用date对象创建，用的是当前时区，CST，也就是北京时间，或者说是上海时间
        System.out.println("zone() CST now : " + new Date());

        // 在亚洲上海时区，获取UTC时区,需要指定时区为UTC，这样就会转换成UTC时区的时间
        ZoneId zone = ZoneId.of("UTC");
        ZonedDateTime utcDateTime = ZonedDateTime.now(zone);
        System.out.println("zone() UTC now :" + utcDateTime);

        // Instant 表示时间戳，可以增减毫秒数，时间戳，是用当前时间-1970-01-01 00：00：00
        Instant instant = Instant.now();// 返回的utc时间
        // 指定时区，采用传入时间戳的方式，指定时区
        ZonedDateTime utcInstant = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        System.out.println("zone() UTC instant now:" + utcInstant);

        // 指定时区创建时间,会帮我们自动更改时差,比如：创建东京的时间，这个一定是比UTC晚9个小时，也就是比我们中国时间靠后1个小时
        ZonedDateTime tokyoDateTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        System.out.println("zone() china to tokyo:" + tokyoDateTime
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        );
        System.out.println("zone() tokyo to china :" + tokyoDateTime
                .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * 每一年的某日，比如是生日，只比较月份和日期
     * */
    private static void mothDay() {
        // 重复日期，判断，比如是生日，每年固定的月份和日期,不管是哪年，到期就发小礼物
        MonthDay birthDay = MonthDay.from(LocalDate.of(1990, 8, 11));
        MonthDay today = MonthDay.from(LocalDate.of(2000, 8, 11));
        System.out.println("mothDay() birthday is :" + birthDay + "today is " + today + " equals: " + birthDay.equals(today));
    }

    /**
     * 时钟
     * 包含获取当前系统格林威治时间，和时区 java8vs before
     * */
    private static void clock() {
        // 获取系统当前时间，long类型，毫秒System与Clock,都是格林威治时间(UTC时间)
        System.out.println("clock() System:" + System.currentTimeMillis());
        System.out.println("clock() clock UTC:" + Clock.systemUTC().millis());

        // 获取时区, TimeZone,与Clock
        System.out.println("clock() timeZone: " + TimeZone.getDefault());
        System.out.println("clock() clock zone: " + Clock.systemDefaultZone());
    }

    /**
     * 处理年月，类似于MothDay
     * 可以获取一个月有多少天，一年有多少天，等等
     * */
    private static void yearMonth() {
        YearMonth yearMonth = YearMonth.of(2019, 2);

        // 获取月有多少天，比如像二月的获取就非常方便，闰年和平年也不一样的天数
        System.out.println("yearMonth() lengthOfMonth: " + yearMonth.lengthOfMonth());

        // 获取一年有多少天，会根据闰年和平年返回不同的天数
        System.out.println("yearMoth() lengthOfYear: " + yearMonth.lengthOfYear());
    }

    /**
     * 获取制定两个日期的直接差值，注意是直接
     * 如果需要计算的比较细的话，比如具体天数，这个就比较粗略了，比如含不含当天啊，等等
     *
     * */
    private static void between() {
        LocalDate begin = LocalDate.of(2019, 1, 1);
        LocalDate end = LocalDate.of(2019, 3, 3);
        // 这个理获取的是直接差值，也就是年相减，月相减，日相减(都是直接相减得出来的差值)
        Period period = Period.between(begin, end);
        System.out.println("between() years: " + period.getYears() + " moths: " + period.getMonths() + " days: " + period.getDays());
    }

    /**
     * 时间戳对象
     * 时间戳对象，用的是格林威治时间，UTC与我们中国东八区差8个小时
     * System.currentTimeMillis() 这个方法与此对象获取的都是UTC时间戳
     * */
    private static void instant() {
        // 获取的就是UTC格林威治时间，时区就是英国伦敦，所以中国需要加上8小时时差
        Instant instant = Instant.now();
        System.out.println("instant() instant now: " + instant);
        System.out.println("instant() instant now: " + instant.toEpochMilli());

        // 这个就是很牛逼，可以把自动把格林威治时间加上时差，变成本地时区的时间
        Date date = Date.from(instant);
        System.out.println("instant() date.from:" + date);

        // 这样又可以继续把时差还原回去到格林威治时间
        System.out.println("instant() toInstant()" + date.toInstant());
    }

    /**
     * 日期格式话
     * */
    private static void formatter() {
        // String 转换成日期
        String strDate = "2019-08-11 10:10:00";
        // 需要用localDateTime 来处理，因为这个虽然也能解析，但是取舍了，值保留了日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        System.out.println("formatter() strDate: " + LocalDate.parse(strDate, formatter));

        // 转换成String
        System.out.println("formatter() LocalDateTime formatter:" + LocalDateTime.now().format(formatter));
    }

    /**
     * 使用需要注意，是需要转换时区，还是转换时区同时把时间差也给改了
     * */
    private static void withZoneSameInstant() {
        ZoneId zone = ZoneId.of("UTC");
        ZonedDateTime utcDateTime = ZonedDateTime.now(zone);
        System.out.println("UTC now :" + utcDateTime);

         /*
        case1:
        withZoneSameLocal只改变时区，不更改时差,
        withZoneSameInstant更改时区和时差，
        并且withZoneSameLocal操作后返回的对象，如果在改变时差也改变不了
        * UTC now :2020-01-18T10:16:50.809Z[UTC]
          chinaTime: 2020-01-18T10:16:50.809+08:00[Asia/Shanghai]
          chinaTime2: 2020-01-18T10:16:50.809+08:00[Asia/Shanghai]
          chinaTime3:2020-01-18T18:16:50.809+08:00[Asia/Shanghai]
        * */

        /*ZonedDateTime chinaTime = utcDateTime.withZoneSameLocal(ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
        System.out.println("chinaTime: " + chinaTime);
        ZonedDateTime chinaTime2 = chinaTime.withZoneSameInstant(ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
        ZonedDateTime chinaTime3 = utcDateTime.withZoneSameInstant(ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
        System.out.println("chinaTime2: " + chinaTime2);
        System.out.println("chinaTime3:" + chinaTime3);*/
        //-----------------------------------------------------

        /*
         * case2:
         * ZonedDateTime.of方法只能更改时区，不能更改时差，
         * 并且ZonedDateTime.of操作后返回的对象，如果在改变时差也改变不了,
         * 同时toLocalDateTime()这个方法返回的时间对象，虽然是本地时间，
         * 但是暴力的舍弃掉了时区，时间还是那个时间
         *  UTC now :2020-01-18T10:27:17.622Z[UTC]
            utc2localTime: 2020-01-18T10:27:17.622
            chinaTime: 2020-01-18T10:27:17.622+08:00[Asia/Shanghai]
            chinaTime2: 2020-01-18T10:27:17.622+08:00[Asia/Shanghai]
            chinaTime3: 2020-01-18T18:27:17.622+08:00[Asia/Shanghai]
         */
        LocalDateTime utc2localTime = utcDateTime.toLocalDateTime();
        System.out.println("utc2localTime: " + utc2localTime);
        ZonedDateTime chinaTime = ZonedDateTime.of(utc2localTime, ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
        System.out.println("chinaTime: " + chinaTime);
        ZonedDateTime chinaTime2 = chinaTime.withZoneSameInstant(ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
        System.out.println("chinaTime2: " + chinaTime2);
        ZonedDateTime chinaTime3 = utcDateTime.withZoneSameInstant(ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
        System.out.println("chinaTime3: " + chinaTime3);
    }
}

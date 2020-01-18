package com.std.y.guava;

import com.google.common.collect.*;
import javafx.scene.control.Cell;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhongkui.YANG on 2018/7/17.
 */
public class UtilsTest {

    //ListMultimap

    /**
     * key 对应的是一个list，get返回的是list
     */
    @Test
    public void listMultimapTest() {
        ListMultimap<String, String> listMultimap = ArrayListMultimap.create();
        listMultimap.put("test01", "y01");
        listMultimap.put("test01", "y02");
        listMultimap.put("test02", "y02");
        listMultimap.put("test03", "y03");
        System.out.println(listMultimap);
        listMultimap.get("test01");
        //Multimap<DynamicInterface,DynamicInterface> edgeMap;
    }

    @Test
    public void multimapTest() {
        Multimap multimap = HashMultimap.create();
        multimap.put("test01", "y01");
        multimap.put("test01", "y02");
        multimap.put("test02", "y02");
        multimap.put("test03", "y03");
        System.out.println(multimap);
        List rsult = (List) multimap.get("test01");
        System.out.println(rsult);
    }

    @Test
    public void setMultimap() {
        SetMultimap<String, Integer> setMultimap = HashMultimap.create();
        setMultimap.put("test01", 1);
        setMultimap.put("test01", 2);
        setMultimap.put("test02", 3);
        setMultimap.put("test02", 4);
        System.out.println(setMultimap);
        Set<Integer> sets = setMultimap.get("test01");
        System.out.println("sets is " + sets);

    }

    @Test
    public void table() {
        /**
         *
         * 创建课程表
         * 1. table 是一个双key 的gmap
         *
         * 2. 双key保证唯一
         *
         * 3. 可以拿rownSet 中的key值获取二级Map
         *
         *
         * */
        Table<String, String, Integer> tables = HashBasedTable.create();
        tables.put("刘备", "javase", 80);
        tables.put("刘备", "guava", 20);
        tables.put("刘备", "guava", 100);//重复会覆盖之前的
        tables.put("关羽", "javaee", 90);
        tables.put("张飞", "javame", 100);
        tables.put("赵云", "guava", 70);
        System.out.println("【table】创建完成=============================");


        /**
         *
         * 得到所有的行数据
         * */
        System.out.println("【table】获取所有的行=============================");
        Set<Table.Cell<String, String, Integer>> cells = tables.cellSet();
        for (Table.Cell<String, String, Integer> cell : cells) {
            System.out.println("rowKey :" + cell.getRowKey() + "columnKey: " + cell.getColumnKey() + " value: " + cell.getValue());

        }


        System.out.println("【table】获取所有学生=============================");

        Set<String> students = tables.rowKeySet();
        for (String name : students) {
            System.out.println("学生：" + name);
        }


        System.out.println("【table】获取所课程=============================");
        Set<String> courses = tables.columnKeySet();
        for (String course : courses) {
            System.out.println("课程: " + course);
        }


        System.out.println("【table】得到所有成绩=============================");
        Collection<Integer> values= tables.values();
        for (int value:values){
            System.out.println("成绩："+value);

        }



        System.out.println("【table】得到学生成绩表=============================");
        for (String studentName:students){ // 获取 rowKey结合，也就是第一层key

            Map<String,Integer>rowMap=tables.row(studentName);
            System.out.println("rowMap="+rowMap);
            Set<Map.Entry<String,Integer>> setentry=rowMap.entrySet();
            for (Map.Entry<String,Integer> entry:setentry){
                System.out.println("姓名："+studentName+" 课程:"+entry.getKey()+" 成绩:"+entry.getValue());
            }
        }

        System.out.println("【table】得到学生 姓名+成绩=============================");

        for (String s :courses){ //遍历所有课程
            Map<String, Integer> rowMap = tables.column(s);
            Set<Map.Entry<String,Integer>> setEntry=rowMap.entrySet();
            for (Map.Entry<String,Integer> entry:setEntry){
                System.out.println("课程:"+s+" 姓名:"+entry.getKey()+" 成绩:"+entry.getValue());
            }


        }
        System.out.println("【table】指定key获取=============================");
        Map<String,Integer> map1=tables.row("刘备");
        System.out.println(map1);
        Map<String,Integer> map2=tables.column("guava");
        System.out.println(map2);


    }

    public static void main(String[] args) {
        System.out.println(2 % 10000);
    }
}

package com.std.y.jdk8;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionStreamY {


    /**
     * jdk 8的集合使用
     * stream 的简单使用
     */
    @Test
    public void stream() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        List<String> converList = list.stream().map(this::stream).collect(Collectors.toList());

        System.out.println("original is " + list);
        System.out.println("convert is " + converList);
    }

    private String stream(Integer num) {
        return "test" + num;

    }



}

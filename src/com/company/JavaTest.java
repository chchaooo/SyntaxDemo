package com.company;

import java.util.ArrayList;
import java.util.List;

public class JavaTest {

    /**
     * for循环对应的等待循环的集合必须要手动保证非空
     * */
    public static void testNull(){
        List<String> strs = new ArrayList<>();
        strs.add(null);
        for(String item : strs){
            System.out.println(item.length());
        }
    }

}

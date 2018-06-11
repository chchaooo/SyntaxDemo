package com.company.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectionTest {

    /**
     * 直接使用index来进行删除，并没有涉及到迭代器
     * 逆序删除时，可以正常执行完成
     * 顺序删除时，则无法执行完成。循环执行到5000时，数组中只有5000个元素，最大下标4999，数组越界
     * => for循环的实现与迭代器不同，如果是迭代器实现，只要删除任意一个元素，就会报错
     * => 迭代器在迭代期间，对应集合的内容不可变动（有变动需要使用迭代器来进行删除）
     * */
    public static void testConcurrentModificationExceptionWithIndex(){
        List<String> strs = new ArrayList<>();
        for(int i=0; i<10000; i++){
            strs.add(String.valueOf(i));
        }
        for(int i=10000-1; i>=0; i--){
            strs.remove(i);
            System.out.println(String.valueOf(i));
        }
        for(int i=0; i<10000; i++){
            strs.remove(i);
            System.out.println(String.valueOf(i));
        }
    }

    /**
     * for each是jdk5.0新增加的一个循环结构，可以用来处理集合中的每个元素而不用考虑集合定下标
     * 使用 Iterator 的好处在于可以使用相同方式去遍历集合中元素，
     * 而不用考虑集合类的内部实现（只要它实现了 java.lang.Iterable 接口），
     * 如果使用 Iterator 来遍历集合中元素，一旦不再使用 List 转而使用 Set 来组织数据，那遍历元素的代码不用做任何修改
     * for each中使用的集合必须是一个实现了Iterator接口的集合
     * 所以下面的测试，在删除第一个元素之后，就报错ConcurrentModificationException
     * */
    public static void testForeach(){
        List<String> strs = new ArrayList<>();
        for(int i=0; i<10000; i++){
            strs.add(String.valueOf(i));
        }
        for (String item: strs) {
            strs.remove(item);
            System.out.println(String.valueOf(strs.size()));
        }
    }

    /**
     * thread1在循环期间，只要thread2向数组中插入一个元素，再次执行thread1中的循环时，
     * 则会出现 ConcurrentModificationException。
     * =>遍历过程中，不可以有改变数组的操作出现
     * */
    public static void testConcurrentModificationException(){
        List<String> strs = new ArrayList<>();
        for(int i=0; i<10000; i++){
            strs.add(String.valueOf(i));
        }
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(String item : strs){
                    System.out.println("Thread1:"+ strs.size() + item);
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(String item : strs){
                    strs.remove(item);
                }
            }
        });
        thread1.start();
        thread2.start();
    }

    /**
     * 使用迭代器的好处：大多数的集合都实现迭代器接口，
     * 如果针对迭代器接口编程，那么更换数据结构时，代码是不需要变化的
     * 使用迭代器时，删除接口必须要使用Iterator中的删除接口
     * */
    public static void testIterator(){
        List<String> strs = new ArrayList<>();
        for(int i=0; i<10000; i++){
            strs.add(String.valueOf(i));
        }
        Iterator<String> it = strs.iterator();
        while (it.hasNext()){
            String item = it.next();
            System.out.println(item);
            it.remove();
        }
    }

    /**
     * 多个线程中分别使用两个迭代器来进行遍历
     * 当thread2中执行了remove操作之后，thread1会因为异常退出。
     * */
    public static void testMultiThreadIteratorDelete(){
        List<String> strs = new ArrayList<>();
        for(int i=0; i<1000; i++){
            strs.add(String.valueOf(i));
        }
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<String> it = strs.iterator();
                while (it.hasNext()){
                    System.out.println(it.next());
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<String> it = strs.iterator();
                while (it.hasNext()){
                    System.out.println("delete"+it.next());
                    it.remove();
                }
            }
        });
        thread1.start();
        thread2.start();
    }
}

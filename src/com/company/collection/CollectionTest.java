package com.company.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectionTest {

    /**
     * 直接使用index来进行删除，并没有涉及到迭代器
     * 逆序删除时，可以正常执行完成
     * 顺序删除时，则无法执行完成。循环执行到5000时，数组中只有5000个元素，最大下标4999，数组越界
     * */
    public static void testIndex(){
        List<String> strs = new ArrayList<>();
        for(int i=0; i<10000; i++){
            strs.add(String.valueOf(i));
        }
        for(int i=10000-1; i>=0; i--){
            strs.remove(i);
            System.out.println(String.valueOf(i));
        }
//        for(int i=0; i<10000; i++){
//            strs.remove(i);
//            System.out.println(String.valueOf(i));
//        }
    }

    /**
     * 如果在迭代器遍历的过程中，当前线程或者其他线程直接改变了遍历集合的内容
     * 此时会出现ConcurrentModificationException
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
            strs.remove(item);
        }
    }

    /**
     * 在迭代器遍历过程中，如果需要变更集合内容，
     * 必须要通过迭代器自身的it.remove()来进行
     * */
    public static void testIteratorRemove(){
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
     * 单个线程使用迭代器遍历时来进行删除没有问题。
     * 多个线程中分别使用两个迭代器来进行遍历，
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

    /**
     * ForEach遍历中，只要删除一个元素之后，
     * 就会抛出ConcurrentModificationException
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

}

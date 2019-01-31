//package com.anbang.fake;
//
//import com.alibaba.fastjson.JSON;
//import com.anbang.fake.model.MajiangPlayerAction;
//import org.junit.Test;
//
//import java.util.*;
//
///**
// * @Author: 吴硕涵
// * @Date: 2019/1/15 1:56 PM
// * @Version 1.0
// */
//
//public class TestClass {
//
//    @Test
//    public void Test2() {
//        int a = 2000;
//        long b = (int) a;
//        try {
//            synchronized (this) {
//                long l = System.currentTimeMillis();
//                Thread thread = Thread.currentThread();
//                wait(2000);
//                long l1 = System.currentTimeMillis();
//                long l2 = l1 - l;
//                return;
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//    @Test
//    public void test5(){
//
//        MajiangPlayerAction[] majiangPlayerActions = new MajiangPlayerAction[10];
//        MajiangPlayerAction action = new MajiangPlayerAction();
//        majiangPlayerActions[5] = action;
//        return;
//    }
//
//    @Test
//    public void Test1() {
//        ArrayList<Integer> list = new ArrayList<>();
//        Integer integer = new Integer(1);
//        list.add(integer);
//        integer = new Integer(2);
//        list.add(integer);
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("token", "123456");
//        map.put("List", list);
//        String s = JSON.toJSONString(map);
//        return;
//    }
//
//    @Test
//    public void TestRun() {
//        timer();
//    }
//
//    public void timer() {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY, 10); // 控制时
//        c.set(Calendar.MINUTE, 0); // 控制分
//        c.set(Calendar.SECOND, 0); // 控制秒
//
//        Date time = c.getTime(); // 得到执行任务的时间,此处为当天的10：00：00
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                System.out.print(1);
//
//            }
//
//        }, 1000);// 这里设定将延时每隔1000毫秒执行一次
//
//    }
//}

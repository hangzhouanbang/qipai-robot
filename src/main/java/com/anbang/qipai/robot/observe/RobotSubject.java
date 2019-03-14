//package com.anbang.qipai.robot.observe;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * @Author: 吴硕涵
// * @Date: 2019/3/4 11:06 AM
// * @Version 1.0
// */
////发布消息的人
//public class RobotSubject implements Subject {
//    //用来存放和记录观察者
////    private List<Observer> observers = new ArrayList<Observer>();
//
//    @Override
//    public void addObserver(Observer obj) {
////        observers.add(obj);
//    }
//
//    @Override
//    public void deleteObserver(Observer obj) {
////        int i = observers.indexOf(obj);
////        if (i >= 0) {
////            observers.remove(obj);
////        }
//    }
//
//    @Override
//    public void notifyObserver() {
//
//        //TODO:调用observer里的方法
//    }
//
//
//    //布置作业的方法,在方法最后,需要调用notifyObserver()方法,通知所有观察者更新状态
//    public void qingqiuTuoGuan() {
//        notifyObserver();
//    }
//}

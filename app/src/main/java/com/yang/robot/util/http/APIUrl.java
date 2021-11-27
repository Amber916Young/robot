package com.yang.robot.util.http;

public class APIUrl {

    final public static String getCurrentRobot =UrlDomain.baseUrl+"robot/query/active/id/ID";
    final public static String APIlogin =UrlDomain.baseUrl+"robot/login";
    final public static String APIregister =UrlDomain.baseUrl+"robot/register";
    final public static String APIlogout =UrlDomain.baseUrl+"robot/logout/id/ID";
    final public static String APIbroken =UrlDomain.baseUrl+"robot/broken/id/ID";
    final public static String APIallRobots =UrlDomain.baseUrl+"robot/query/all";
    final public static String APIupdate =UrlDomain.baseUrl+"robot/update";
    final public static String APIaddTask =UrlDomain.baseUrl+"task/add/new/task";
    // one task may contain many robots
    final public static String APIviewTaskRobots =UrlDomain.baseUrl+"task/query/task/robots/id/ID";
    final public static String APIviewTasks =UrlDomain.baseUrl+"task/query/tasks/robot";
    final public static String APIJoinTask =UrlDomain.baseUrl+"task/join/task";
    final public static String APIFinishTask =UrlDomain.baseUrl+"task/finish/task";
    final public static String APIHiddenTask =UrlDomain.baseUrl+"task/hind/task";
}

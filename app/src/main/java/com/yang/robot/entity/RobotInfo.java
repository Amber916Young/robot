package com.yang.robot.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RobotInfo extends LitePalSupport implements Serializable{
    private long id;
    private String robot_name;
    private String password;
    private String robot_img;
    private Double longitude;//经度
    private Double latitude;
    private String location_name;
    private int state;
    private String activeTime;
    private String registerTime;

    private List<Tasks> tasks;

    public RobotInfo() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RobotInfo(long id, String robot_name, String password, String robot_img, Double longitude, Double latitude, String location_name, int state, String activeTime, String registerTime) {
        this.id = id;
        this.robot_name = robot_name;
        this.robot_name = password;
        this.robot_img = robot_img;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location_name = location_name;
        this.state = state;
        this.activeTime = activeTime;
        this.registerTime = registerTime;
    }

    public RobotInfo(long id, String robot_name, String password, String robot_img, Double longitude, Double latitude, String location_name, int state, String activeTime, String registerTime, List<Tasks> tasks) {
        this.id = id;
        this.robot_name = robot_name;
        this.robot_name = password;
        this.robot_img = robot_img;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location_name = location_name;
        this.state = state;
        this.activeTime = activeTime;
        this.registerTime = registerTime;
        this.tasks = tasks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRobot_name() {
        return robot_name;
    }

    public void setRobot_name(String robot_name) {
        this.robot_name = robot_name;
    }

    public String getRobot_img() {
        return robot_img;
    }

    public void setRobot_img(String robot_img) {
        this.robot_img = robot_img;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public List<Tasks> getTasks() {
        return tasks;
    }

    public void setTasks(List<Tasks> tasks) {
        this.tasks = tasks;
    }
}

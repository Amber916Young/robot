package com.yang.robot.entity;

import java.util.Date;
import java.util.List;

public class Tasks {
    private int id;
    private String task_name;
    private String createTime;
    private String finishTime;
    private int maxmember;


    private String task_detail;
    private Boolean checked;
    private List<RobotInfo> robotInfoList;

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public List<RobotInfo> getRobotInfoList() {
        return robotInfoList;
    }

    public int getMaxmember() {
        return maxmember;
    }

    public void setMaxmember(int maxmember) {
        this.maxmember = maxmember;
    }

    public void setRobotInfoList(List<RobotInfo> robotInfoList) {
        this.robotInfoList = robotInfoList;
    }

    public Tasks() {
    }

    public Tasks(int id, String task_name, String createTime, String finishTime, String task_detail, Boolean checked, List<RobotInfo> robotInfoList) {
        this.id = id;
        this.task_name = task_name;
        this.createTime = createTime;
        this.finishTime = finishTime;
        this.task_detail = task_detail;
        this.checked = checked;
        this.robotInfoList = robotInfoList;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTask_detail() {
        return task_detail;
    }

    public void setTask_detail(String task_detail) {
        this.task_detail = task_detail;
    }
}

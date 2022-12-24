package com.lugt.beans;

/**
 * 数据实体类
 */
public class PedometerBean {
    private int stepCount;  //步数
    private double calorie; //卡路里
    private double distance;    //行走距离
    private int pace;   // 步/分钟
    private double speed;   //速度
    private long startTime; //开始记录时间
    private long lastStepTime;  //最后一步的时间
    private long day;   //以天为单位的时间戳
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getPace() {
        return pace;
    }

    public void setPace(int pace) {
        this.pace = pace;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLastStepTime() {
        return lastStepTime;
    }

    public void setLastStepTime(long lastStepTime) {
        this.lastStepTime = lastStepTime;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public void reset(){
        setDistance(0);
        setCalorie(0);
        setStepCount(0);
    }
}

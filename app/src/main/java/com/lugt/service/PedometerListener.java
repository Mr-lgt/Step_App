package com.lugt.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.lugt.beans.PedometerBean;

/**
 * 传感器监听类
 */
public class PedometerListener implements SensorEventListener {
    private int currentStep = 0;//当前步数
    private float sensitivity = 30;//灵敏度
    private long limit = 300;//单位步频时长限制
    private float lastValue;//上一次的采样数值
    private float scale = -4f;//采样数据的缩放值
    private float offset = 240f;//采样数值的偏移值
    //采样时间
    private float start = 0;
    private float end = 0;
    private float lastDirection;//最后加速度的方向
    private float lastExtremes[][] = new float[2][1];//记录数值
    private float lastDiff;//最后一次的变化量
    private int lastMatch = -1;//是否匹配

    private PedometerBean data;

    public PedometerListener(PedometerBean data) {
        this.data = data;
    }
    public void setCurrentStep(int currentStep){
        this.currentStep = currentStep;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {//是否是加速度传感器
                float sum = 0;
                for (int i = 0; i < 3; i++) { //拿到3个方向的传感器数值
                    //将采到的数据先处理为正值，再缩放
                    float vector = offset + event.values[i] * scale;
                    sum += vector;
                }
                //取得传感器平均值
                float average = sum / 3;
                float dir;
                //判断方向
                if (average > lastValue) {
                    dir = 1;
                } else if (average < lastValue) {
                    dir = -1;
                } else {
                    dir = 0;
                }
                //若最后一次加速度方向和当前方向相反
                if (dir == -lastDirection) {
                    int extType = (dir > 0 ? 0 : 1);
                    //保存数值变化
                    lastExtremes[extType][0] = lastValue;
                    //变化的绝对值
                    float diff = Math.abs(lastExtremes[extType][0] - lastExtremes[1 - extType][0]);
                    if (diff > sensitivity) {//绝对值大于灵敏度，则条件有效
                        //数值与上次的比是否足够大
                        boolean isLargeAsPrevious = diff > (lastDiff * 2 / 3);
                        //数值是否小于上次数值的1/3
                        boolean isPreviousLargeEnough = lastDiff > (diff / 3);
                        //方向判断
                        boolean isNotContra = (lastMatch != 1 - extType);
                        if (isLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            end = System.currentTimeMillis();
                            //判断是否有效记录
                            if (end - start > limit) {
                                currentStep++;
                                lastMatch = extType;
                                start = end;    //记录最后一次时间，设置为下次的开始
                                lastDiff = diff;    //记录最后一次的变化量
                                if (data != null) {
                                    data.setStepCount(currentStep);
                                    data.setLastStepTime(System.currentTimeMillis());
                                }
                            } else {
                                lastDiff = sensitivity;
                            }
                        } else {//未匹配
                            lastMatch = -1;
                            lastDiff = sensitivity;
                        }
                    }
                }
                lastDirection = dir;    //更新最后一次的加速度方向
                lastValue = average;    //更新最后的采样数据
            }
        }
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

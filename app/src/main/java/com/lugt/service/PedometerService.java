package com.lugt.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.lugt.beans.PedometerBean;
import com.lugt.beans.PedometerChartBean;
import com.lugt.database.DataBaseHelper;
import com.lugt.frame.FrameApplication;
import com.lugt.utils.ACache;
import com.lugt.utils.Settings;
import com.lugt.utils.Utils;

/**
 * 传感器服务
 */
public class PedometerService extends Service {
    private SensorManager sensorManager;
    private PedometerBean pedometerBean;
    private PedometerChartBean pedometerChartBean;
    private PedometerListener pedometerListener;
    private Settings settings;
    private int runState;//运行状态
    public static final int STATUS_NOT_RUN = 0;
    public static final int STATUS_RUNNING = 1;
    private static final long SAVE_CHART_TIME = 60000L;//存储数据的时间间隔
    private static Handler handler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (runState == STATUS_RUNNING){
                if (handler != null && pedometerChartBean != null){
                    handler.removeCallbacks(timeRunnable);
                    updateChartData();//更新数据
                    handler.postDelayed(timeRunnable,SAVE_CHART_TIME);
                }
            }
        }
    };

    /**
     * 通过步数计算卡路里
     *
     * @param stepCount
     * @return
     */
    public  double getCalorieBySteps(int stepCount) {
        //步长（暂定）
        float stepLen = settings.getStepLength();
        //体重（暂定）
        float bodyWeight = settings.getBodyWeight();
        double METRIC_WALKING_FACTOR = 0.708;//走路消耗系数
        double METRIC_RUNNING_FACTOR = 1.02784823; //跑步消耗系数
        double calories = (bodyWeight * METRIC_WALKING_FACTOR) * stepLen * stepCount / 100000.0;

        return calories;
    }

    /**
     * 计算路程
     *
     * @param stepCount
     * @return
     */
    public double getStepDistance(int stepCount) {
        float stepLen = settings.getStepLength();
        double distance = (stepCount * (long) (stepLen)) / 100000.0f;
        return distance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pedometerBean = new PedometerBean();
        pedometerChartBean = new PedometerChartBean();
        pedometerListener = new PedometerListener(pedometerBean);
        settings = new Settings(this);
    }

    /**
     * 更新图表数据
     */
    private void updateChartData(){
        if (pedometerChartBean.getIndex() < 1440-1){
            pedometerChartBean.setIndex(pedometerChartBean.getIndex() +1);
            pedometerChartBean.getArrayData()[pedometerChartBean.getIndex()] =
                    pedometerBean.getStepCount();
        }
    }

    /**
     * 将对象保存
     */
    private void saveChartData(){
        String jsonStr = Utils.objToJson(pedometerChartBean);
        ACache.get(FrameApplication.getInstance()).put("JsonChartData",jsonStr);
    }

    private IPedometerService.Stub iPedometerService = new IPedometerService.Stub() {
        @Override
        public void startCount() throws RemoteException {
            if (sensorManager != null && pedometerListener != null) {
                //选择传感器类型
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(pedometerListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                //记录开始时间
                pedometerBean.setStartTime(System.currentTimeMillis());
                //记录是哪天的数据
                pedometerBean.setDay(Utils.getTimeByDay());
                //更新运行状态
                runState = STATUS_RUNNING;
                //触发数据刷新
                handler.postDelayed(timeRunnable,SAVE_CHART_TIME);
            }
        }

        @Override
        public void stopCount() throws RemoteException {
            if (sensorManager != null && pedometerListener != null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.unregisterListener(pedometerListener, sensor);
                runState = STATUS_NOT_RUN;
                handler.removeCallbacks(timeRunnable);
            }
        }

        @Override
        public void resetCount() throws RemoteException {
            if (pedometerBean != null) {
                pedometerBean.reset();
                saveData();
            }
            if (pedometerChartBean != null){
                pedometerChartBean.reset();
                saveChartData();
            }
            if (pedometerListener != null) {
                pedometerListener.setCurrentStep(0);
            }
        }

        @Override
        public int getStepCount() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStepCount();
            }
            return 0;
        }

        @Override
        public double getCalories() throws RemoteException {
            if (pedometerBean != null) {
                return getCalorieBySteps(pedometerBean.getStepCount());
            }
            return 0.0;
        }

        @Override
        public double getDistance() throws RemoteException {
            return getDistanceVal();
        }

        @Override
        public void saveData() throws RemoteException {
            if (pedometerBean != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DataBaseHelper dbHelp = new DataBaseHelper(PedometerService.this, DataBaseHelper.DB_NAME);
                        //设置距离
                        pedometerBean.setDistance(getDistanceVal());
                        //设置热量消耗
                        pedometerBean.setCalorie(getCalorieBySteps(pedometerBean.getStepCount()));
                        //计算时间间隔
                        long time = (pedometerBean.getLastStepTime() - pedometerBean.getStartTime()) / 1000;
                        if (time == 0) {
                            pedometerBean.setPace(0);
                            pedometerBean.setSpeed(0);
                        } else {
                            //求每分钟的步频
                            int pace = Math.round(60 * pedometerBean.getStepCount() / time);
                            pedometerBean.setPace(pace);
                            //求速度，单位千米/小时
                            long speed = Math.round((pedometerBean.getDistance() / 1000) / (time / 60 * 60));
                            pedometerBean.setSpeed(speed);
                        }
                        dbHelp.writeToDB(pedometerBean);
                    }
                }).start();
            }
        }

        @Override
        public void setSensitivity(double sensitivity) throws RemoteException {
//            if (settings != null) {
//                settings.setSensitivity((float) sensitivity);
//            }
            if (pedometerListener != null){
                pedometerListener.setSensitivity((float) sensitivity);
            }
        }

        @Override
        public double getSensitivity() throws RemoteException {
            if (settings != null) {
                return settings.getSensitivity();
            }
            return 0;
        }

        @Override
        public void setInterval(int interval) throws RemoteException {
            if (settings != null) {
                settings.setInterval(interval);
            }
            if (pedometerListener != null){
                pedometerListener.setLimit(interval);
            }
        }

        @Override
        public int getInterval() throws RemoteException {
            if (settings != null) {
                return settings.getInterval();
            }
            return 0;
        }

        @Override
        public long getStartTimeStamp() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStartTime();
            }
            return 0L;
        }

        @Override
        public int getServiceStatus() throws RemoteException {
            return runState;
        }

        @Override
        public PedometerChartBean getChartData() throws RemoteException {
            return pedometerChartBean;
        }

        /**
         * 距离,单位千米
         *
         * @return
         * @throws RemoteException
         */
        public double getDistanceVal() {
            if (pedometerBean != null)
            {
                Settings settings = new Settings(PedometerService.this);
                double distance = (pedometerBean.getStepCount() * (long) settings.getStepLength()) / 100000.0f;
                return distance;
            }
            return 0;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return iPedometerService;
    }
}

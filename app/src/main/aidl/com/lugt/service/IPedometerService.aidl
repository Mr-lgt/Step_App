// IPedometerService.aidl
package com.lugt.service;
import com.lugt.beans.PedometerChartBean;
// Declare any non-default types here with import statements

interface IPedometerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startCount();
        void stopCount();
        void resetCount();
        int getStepCount();
        double getCalories();
        double getDistance();
        void saveData();
        void setSensitivity(double sensitivity);
        double getSensitivity();
        void setInterval(int interval);
        int getInterval();
        long getStartTimeStamp();
        int getServiceStatus();
        PedometerChartBean getChartData();
}
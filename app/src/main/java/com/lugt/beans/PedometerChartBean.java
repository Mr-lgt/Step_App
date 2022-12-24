package com.lugt.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * 图表实体类
 */
public class PedometerChartBean implements Parcelable {
    private int[] arrayData;
    private int index;

    public PedometerChartBean(){
        arrayData = new int [1440];
        index = 0;
    }

    protected PedometerChartBean(Parcel in) {
        arrayData = in.createIntArray();
        index = in.readInt();
    }

    public static final Creator<PedometerChartBean> CREATOR = new Creator<PedometerChartBean>() {
        @Override
        public PedometerChartBean createFromParcel(Parcel in) {
            return new PedometerChartBean(in);
        }

        @Override
        public PedometerChartBean[] newArray(int size) {
            return new PedometerChartBean[size];
        }
    };

    public int[] getArrayData() {
        return arrayData;
    }

    public void setArrayData(int[] arrayData) {
        this.arrayData = arrayData;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(arrayData);
        dest.writeInt(index);
    }

    /**
     * 清空所有数据
     */
    public void reset() {
        index = 0;
        Arrays.fill(arrayData, 0);
    }
}

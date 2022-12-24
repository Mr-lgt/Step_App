package com.lugt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.lugt.beans.PedometerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Helper类
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;//数据库版本
    public static final String TABLE_NAME = "pedometer";//表名
    public static final String DB_NAME = "PedometerDB";
    public static final String[] COLUMNS = {
        "id",
        "stepCount",
        "calorie",
        "distance",
        "pace",
        "speed",
        "startTime",
        "lastStepTime",
        "day",
    };

    public DataBaseHelper(Context context,String name){
        this(context,name,VERSION);
    }

    public DataBaseHelper(Context context,String name,int version){
        this(context,name,null,version);
    }

    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                "(id integer PRIMARY KEY AUTOINCREMENT DEFAULT NULL,"+
                "stepCount integer," +
                "calorie Double," +
                "distance Double DEFAULT null,"+
                "pace integer," +
                "speed double,"+
                "startTime Timestamp DEFAULT NULL,"+
                "lastStepTime Timestamp DEFAULT NULL," +
                "day Timestamp DEFAULT NULL) " );//创建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 写入数据库
     * @param data
     */
    public void writeToDB(PedometerBean data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stepCount",data.getStepCount());
        values.put("calorie",data.getCalorie());
        values.put("distance",data.getDistance());
        values.put("pace",data.getPace());
        values.put("speed",data.getSpeed());
        values.put("startTime",data.getStartTime());
        values.put("lastStepTime",data.getLastStepTime());
        values.put("day",data.getDay());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    /**
     * 以天为单位获取计步数据
     * @param dayTime
     * @return
     */
    public PedometerBean getByDayTime(long dayTime){
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        PedometerBean bean = new PedometerBean();
        cursor = db.rawQuery("select * from " + TABLE_NAME +
                "where day = " + dayTime,null);
        if (cursor != null && cursor.getCount() > 0){
            if (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(COLUMNS[1]));
                double calorie = cursor.getDouble(cursor.getColumnIndex(COLUMNS[2]));
                double distance = cursor.getDouble(cursor.getColumnIndex(COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(COLUMNS[4]));
                double speed = cursor.getDouble(cursor.getColumnIndex(COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(COLUMNS[7]));
                long cTime = cursor.getLong(cursor.getColumnIndex(COLUMNS[8]));

                bean.setId(id);
                bean.setStepCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(cTime);
            }
        }
        cursor.close();
        db.close();
        return bean;
    }

    /**
     * 获取全部数据，分页
     * @param offVal
     * @return
     */
    public List<PedometerBean> getFromDB(int offVal){
        int pageSize = 20;//分页大小
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.query(TABLE_NAME,null,null,null,null,null,
                "day desc limit " + pageSize + " offset "+ offVal,null);
        List<PedometerBean> dataList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0){
            if (cursor.moveToNext()){
                PedometerBean bean = new PedometerBean();
                int id = cursor.getInt(cursor.getColumnIndex(COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(COLUMNS[1]));
                double calorie = cursor.getDouble(cursor.getColumnIndex(COLUMNS[2]));
                double distance = cursor.getDouble(cursor.getColumnIndex(COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(COLUMNS[4]));
                double speed = cursor.getDouble(cursor.getColumnIndex(COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(COLUMNS[7]));
                long cTime = cursor.getLong(cursor.getColumnIndex(COLUMNS[8]));

                bean.setId(id);
                bean.setStepCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(cTime);
                dataList.add(bean);
            }
        }
        cursor.close();
        db.close();
        return dataList;
    }

    /**
     * 更新数据库
     * @param values
     * @param dayTime
     */
    public void updateToDB(ContentValues values,long dayTime){
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME,values,"day=?",new String[]{String.valueOf(dayTime)});
        db.close();
    }
}

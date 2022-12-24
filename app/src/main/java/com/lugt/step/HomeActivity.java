package com.lugt.step;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.lugt.beans.PedometerChartBean;
import com.lugt.frame.BaseActivity;
import com.lugt.frame.LogWriter;
import com.lugt.service.IPedometerService;
import com.lugt.service.PedometerService;
import com.lugt.utils.Utils;
import com.lugt.weights.CircleProgressBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private CircleProgressBar circleProgressBar;
    private TextView calorie;//卡路里
    private TextView time;//时间
    private TextView distance;//公里
    private TextView stepCount;//步数
    private Button btnReset;//重置
    private Button btnStart;//开始
    private ImageView setting;//设置
    private BarChart dataChart;//图表
    private int status = -1;//保存当前状态

    private final static int STATUS_NOT_RUNNING = 0;
    private final static int STATUS_RUNNING = 1;
    private final static int MSG_UPDATE_STEP_COUNT = 1000;
    private final static int MSG_UPDATE_CHART_DATA = 2000;
    private final static int GET_DATA_TIME = 2000;//获取数据的时间间隔
    private final static long UPDATE_CHART_TIME = 60000L;//更新图标的时间间隔

    private boolean isChartUpdate = false;//是否更新数据
    private boolean isRunning = false;//是否正在运行
    private boolean bindService = false;//服务是否绑定

    private IPedometerService remoteService;
    private PedometerChartBean chartBean;
    private MyHandler handler;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IPedometerService.Stub.asInterface(service);
            try {
                status = remoteService.getServiceStatus();
                if (status == STATUS_RUNNING) {
                    btnStart.setText("停止");
                    isChartUpdate = true;
                    isRunning = true;
                    chartBean = remoteService.getChartData();
                    updateChart(chartBean);
                    // TODO: 2022/12/19 启动两个线程，定时更新数据和刷新UI
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                } else if (status == STATUS_NOT_RUNNING) {
                    btnStart.setText("启动");
                }
            } catch (RemoteException e) {
                //打印异常日志
                LogWriter.e(e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onInitVariable() {
        handler = new MyHandler(this);
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        circleProgressBar = findViewById(R.id.progressBar);
//        circleProgressBar.setProgress(5000);
//        circleProgressBar.setMaxProgress(10000);

        calorie = findViewById(R.id.calorie);
        time = findViewById(R.id.time);
        distance = findViewById(R.id.distance);
        stepCount = findViewById(R.id.stepCount);
        btnReset = findViewById(R.id.btnReset);
        btnStart = findViewById(R.id.btnStart);
        dataChart = findViewById(R.id.dataChart);
        setting = findViewById(R.id.setting);

        setting.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        btnReset.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("确认重置");
            builder.setMessage("您的记录将被清除，确定吗？");
            builder.setPositiveButton("确定", (dialog, which) -> {
                if (remoteService != null) {
                    try {
                        remoteService.stopCount();
                        remoteService.resetCount();
                        chartBean = remoteService.getChartData();
                        updateChart(chartBean);
                        status = remoteService.getServiceStatus();
                        if (status == STATUS_RUNNING) {
                            btnStart.setText("停止");
                        } else if (status == STATUS_NOT_RUNNING) {
                            btnStart.setText("启动");
                        }
                    } catch (RemoteException e) {
                        LogWriter.e(e.toString());
                    }
                }
                dialog.dismiss();
            });
            builder.setNegativeButton("取消", null);
            AlertDialog resetDlg = builder.create();
            resetDlg.show();
        });

        btnStart.setOnClickListener(v -> {
            try {
                status = remoteService.getServiceStatus();
                if (status == STATUS_RUNNING && remoteService != null) {
                    remoteService.stopCount();
                    btnStart.setText("停止");
                    isRunning = false;
                    isChartUpdate = false;
                } else if (status == STATUS_NOT_RUNNING && remoteService != null) {
                    remoteService.startCount();
                    btnStart.setText("启动");
                    isRunning = true;
                    isChartUpdate = true;
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                    chartBean = remoteService.getChartData();
                }
            } catch (RemoteException e) {
                LogWriter.e(e.toString());
            }

        });
    }

    @Override
    protected void onRequestData() {
        Intent serviceIntent = new Intent(this, PedometerService.class);
        ;
        /**检查服务是否运行**/
        /**若服务没有运行，则需要绑定**/
        if (!Utils.isServiceRunning(this, PedometerService.class.getName())) {
            /***若服务没有运行，启动服务*/
            startService(serviceIntent);
        } else {
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        /**绑定服务**/
        bindService = bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        /**初始化一些对应的按钮文字**/
        if (bindService && remoteService != null) {
            try {
                status = remoteService.getServiceStatus();
                if (status == PedometerService.STATUS_NOT_RUN) {
                    btnStart.setText("启动");
                } else if (status == PedometerService.STATUS_RUNNING) {
                    btnStart.setText("停止");
                    isChartUpdate = true;
                    isRunning = true;
                    // TODO: 2022/12/19 启动两个线程，定时更新数据和刷新UI
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                }
            } catch (RemoteException e) {
                LogWriter.e(e.toString());
            }
        } else {
            btnStart.setText("启动");
        }
    }

    /**
     * 计步数据线程
     */
    private class StepRunnable implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    status = remoteService.getServiceStatus();
                    if (status == STATUS_RUNNING) {
                        //发送消息之前，先将消息队列的里的同类消息都清除掉
                        handler.removeMessages(MSG_UPDATE_STEP_COUNT);
                        //发送消息，让Handler去更新数据
                        handler.sendEmptyMessage(MSG_UPDATE_STEP_COUNT);
                        Thread.sleep(GET_DATA_TIME);
                    }
                } catch (RemoteException e) {
                    LogWriter.e(e.toString());
                } catch (InterruptedException e) {
                    LogWriter.e(e.toString());
                }
            }
        }
    }

    /**
     * 图表更新线程
     */
    private class ChartRunnable implements Runnable {

        @Override
        public void run() {
            while (isChartUpdate) {
                try {
                    chartBean = remoteService.getChartData();
                    handler.removeMessages(MSG_UPDATE_CHART_DATA);
                    handler.sendEmptyMessage(MSG_UPDATE_CHART_DATA);
                    Thread.sleep(UPDATE_CHART_TIME);
                } catch (RemoteException e) {
                    LogWriter.e(e.toString());
                } catch (InterruptedException e) {
                    LogWriter.e(e.toString());
                }

            }
        }
    }

    /**
     * 更新计步数据
     */
    @SuppressLint("SetTextI18n")
    public void updateStepCount() {
        if (remoteService != null) {
            int stepCountVal = 0;
            double calorieVal = 0;
            double distanceVal = 0;

            try {
                stepCountVal = remoteService.getStepCount();
                calorieVal = remoteService.getCalories();
                distanceVal = remoteService.getDistance();
            } catch (RemoteException e) {
                LogWriter.e(e.toString());
            }
            stepCount.setText(stepCountVal + "步");
            calorie.setText(Utils.getFormatVal(calorieVal) + "大卡");
            distance.setText(Utils.getFormatVal(distanceVal));
            circleProgressBar.setProgress(stepCountVal);
        }
    }

    /**
     * 更新图表数据，x轴表示时间，y轴表示步数
     *
     * @param bean
     */
    @SuppressLint("SetTextI18n")
    private void updateChart(PedometerChartBean bean) {
        // TODO: 2022/12/19 定义图表的x和y轴
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        if (bean != null) {
            for (int i = 0; i < bean.getIndex(); i++) {
                xVals.add(i + "分s");
                int valY = bean.getArrayData()[i];
                yVals.add(new BarEntry(valY, i));
            }
            time.setText(bean.getIndex() + "分");
            BarDataSet set1 = new BarDataSet(yVals, "所走的步数");
            set1.setBarSpacePercent(2f);
            ArrayList<BarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(xVals, dataSets);
            data.setValueTextSize(10f);
            dataChart.setData(data);
            dataChart.invalidate();
        }
    }

    private static class MyHandler extends Handler {
        public final WeakReference<HomeActivity> weakReference;

        private MyHandler(HomeActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            HomeActivity activity = weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_UPDATE_STEP_COUNT:
                        //更新计步数据
                        activity.updateStepCount();
                        break;
                    case MSG_UPDATE_CHART_DATA:
                        //更新图表数据
                        if (activity.chartBean != null) {
                            activity.updateChart(activity.chartBean);
                        }
                        break;
                    default:
                        LogWriter.d("Default = " + msg.what);
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bindService) {
            bindService = false;
            isChartUpdate = false;
            isRunning = false;
            unbindService(serviceConnection);
        }
    }
}

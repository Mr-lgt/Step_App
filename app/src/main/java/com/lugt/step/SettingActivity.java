package com.lugt.step;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lugt.frame.BaseActivity;
import com.lugt.frame.LogWriter;
import com.lugt.service.IPedometerService;
import com.lugt.service.PedometerService;
import com.lugt.utils.Settings;
import com.lugt.utils.Utils;

public class SettingActivity extends BaseActivity {

    private ListView listView;
    private ImageView back;

    public class SettingListAdapter extends BaseAdapter {
        private String[] titleList = {"设置步长", "设置体重", "传感器敏感度", "传感器采样时间"};
        private Settings settings;

        public SettingListAdapter() {
            settings = new Settings(SettingActivity.this);
        }

        @Override
        public int getCount() {
            return titleList.length;
        }

        @Override
        public Object getItem(int position) {
            if (titleList != null && position < titleList.length) {
                return titleList[position];
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("StringFormatMatches")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(SettingActivity.this, R.layout.item_setting, null);
                viewHolder.title = convertView.findViewById(R.id.item_title);
                viewHolder.desc = convertView.findViewById(R.id.desc);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(titleList[position]);
            switch (position) {
                case 0:
                    //默认步长为50cm
                    float stepLen = settings.getStepLength();
                    viewHolder.desc.setText(String.format(getResources().getString(R.string.stepLen), stepLen));
                    convertView.setOnClickListener(v -> {
                        // TODO: 2022/12/22 设置步长
                        stepClick(stepLen);
                    });
                    break;
                case 1:
                    float bodyWeight = settings.getBodyWeight();
                    viewHolder.desc.setText(String.format(getResources().getString(R.string.body_weight), bodyWeight));
                    convertView.setOnClickListener(v -> {
                        // TODO: 2022/12/22 设置体重
                        weightClick(bodyWeight);
                    });
                    break;
                case 2:
                    double sensitivity = settings.getSensitivity();
                    viewHolder.desc.setText(String.format(getResources().getString(R.string.sensitivity), Utils.getFormatVal(sensitivity)));
                    convertView.setOnClickListener(v -> {
                        // TODO: 2022/12/22 设置传感器灵敏度
                        sensitiveClick();
                    });
                    break;
                case 3:
                    int interval = settings.getInterval();
                    viewHolder.desc.setText(String.format(getResources().getString(R.string.interval), Utils.getFormatVal(interval)));
                    convertView.setOnClickListener(v -> {
                        // TODO: 2022/12/22 设置采样时间间隔
                        setInterval();
                    });
                    break;
                default:
                    LogWriter.d("Position = " + position);
                    break;
            }
            return convertView;
        }

        private void stepClick(float stepLen) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setTitle("通过身高计算步长");
            View view = View.inflate(SettingActivity.this, R.layout.view_dialog_input, null);
            EditText input = view.findViewById(R.id.input);
            input.setText(String.valueOf(stepLen));
            builder.setView(view);
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", (dialog, which) -> {
                // TODO: 2022/12/24 H=0.262S+155.911 S = (H - 155.911) / 0.262
                String val = input.getText().toString();
                if (val != null && val.length() > 0) {
                    float len = (float) ((Float.parseFloat(val) - 155.911) / 0.262);
                    settings.setStepLength(len);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "请输入正确的参数！", Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
        }

        private void weightClick(float bodyWeight) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setTitle("设置体重");
            View mView = View.inflate(SettingActivity.this, R.layout.view_dialog_input, null);
            final EditText input = (EditText) mView.findViewById(R.id.input);
            input.setText(String.valueOf(bodyWeight));
            builder.setView(mView);
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", (dialog, which) -> {
                String val = input.getText().toString();
                if (val != null && val.length() > 0) {
                    float bodyWeight1 = Float.parseFloat(val);
                    settings.setBodyWeight(bodyWeight1);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "请输入正确的参数！", Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
        }

        private void sensitiveClick() {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setItems(R.array.sensitive_array, (dialog, which) -> {
                if (remoteService != null) {
                    try {
                        remoteService.setSensitivity(Settings.SENSITIVE_ARRAY[which]);
                    } catch (RemoteException e) {
                        LogWriter.e(e.toString());
                    }
                }
                /**调用服务，设置灵敏度**/
                settings.setSensitivity(Settings.SENSITIVE_ARRAY[which]);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setTitle("设置传感器灵敏度");
            builder.create().show();
        }

        private void setInterval() {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setItems(R.array.interval_array, (dialog, which) -> {
                if (remoteService != null) {
                    try {
                        remoteService.setInterval(Settings.INTERVAL_ARRAY[which]);
                    } catch (RemoteException e) {
                        LogWriter.e(e.toString());
                    }
                }
                settings.setInterval(Settings.INTERVAL_ARRAY[which]);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setTitle("设置传感器采样间隔");
            builder.create().show();
        }
    }

    static class ViewHolder {
        TextView title;
        TextView desc;
    }

    @Override
    protected void onInitVariable() {

    }

    private SettingListAdapter adapter = new SettingListAdapter();
    private IPedometerService remoteService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IPedometerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
        }
    };

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        listView = findViewById(R.id.listView);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());
        listView.setAdapter(SettingActivity.this.adapter);
    }

    @Override
    protected void onRequestData() {
        Intent serviceIntent = new Intent(SettingActivity.this, PedometerService.class);
        if (!Utils.isServiceRunning(this, PedometerService.class.getName())) {
            startService(serviceIntent);
        } else {
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        // 以bindService方法连接绑定服务
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}

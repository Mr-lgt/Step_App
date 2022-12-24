package com.lugt.frame;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
/**
 * Activity的父类
 */
public abstract class BaseActivity extends FragmentActivity {
    protected boolean isHideAppTitle = true;//是否显示程序标题
    protected boolean isHideSysTitle = false;//是否显示系统标题

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.onInitVariable();
        if (isHideAppTitle){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        if (isHideSysTitle){ //隐藏窗口
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
//                final WindowInsetsController controller = getWindow().getInsetsController();
//                if (controller != null){
//                    controller.hide(WindowInsets.Type.statusBars());
//                }
//            }
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);;
        }
        onInitView(savedInstanceState);
        onRequestData();
        FrameApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        FrameApplication.removeActivity(this);
        super.onDestroy();
    }

    /**
     * 初始化变量
     */
    protected abstract void onInitVariable();

    /**
     * 初始化UI
     */
    protected abstract void onInitView(final Bundle savedInstanceState);

    /**
     * 请求数据
     */
    protected abstract void onRequestData();

}

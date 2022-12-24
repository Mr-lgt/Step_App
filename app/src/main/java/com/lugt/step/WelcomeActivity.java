package com.lugt.step;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lugt.frame.BaseActivity;

public class WelcomeActivity extends BaseActivity {
    public static final int DELAY_MILLIS = 3000;
    private Handler handler;
    private Runnable jumpRunnable;

    @Override
    protected void onInitVariable() {
        handler = new Handler();
        jumpRunnable = () -> {
            //跳转到HomeActivity
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this,HomeActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        };
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onRequestData() {
        handler.postDelayed(jumpRunnable, DELAY_MILLIS);
    }
}

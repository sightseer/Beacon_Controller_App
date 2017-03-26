package com.mrwu.demo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PortalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);

        TimerTask launchTimerTask = new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(PortalActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        Timer launchTimer = new Timer();
        launchTimer.schedule(launchTimerTask, 3000);
    }

}
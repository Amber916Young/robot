package com.yang.robot;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AppService extends Service {

    private NotificationManager notificationManager;
    private String notificationId = "channelId";
    private String notificationName = "channelName";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.logo_small)
                .setContentTitle("测试服务")
                .setContentText("我正在运行");
        //设置Notification的ChannelID,否则不能正常显示
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }



}

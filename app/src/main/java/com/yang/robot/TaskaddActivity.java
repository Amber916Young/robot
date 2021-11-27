package com.yang.robot;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.yang.robot.entity.Response;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.util.ToastUtil;
import com.yang.robot.util.http.APIUrl;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class TaskaddActivity extends AppCompatActivity {
    private Button cancelBTN,addBTN =null;
    private TextView taskName,maximum,taskDetail=null;
    private RobotInfo robotInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskadd);
        cancelBTN = findViewById(R.id.cancelTask);
        addBTN = findViewById(R.id.addTask);
        taskDetail = findViewById(R.id.task_detail);
        taskName = findViewById(R.id.TaskName);
        maximum = findViewById(R.id.maximum);
        Bundle bundle = getIntent().getExtras();
        robotInfo = (RobotInfo) bundle.getSerializable("current_robot");



        setListeners();
    }

    private void setListeners() {
        TaskaddActivity.OnClick onClick = new TaskaddActivity.OnClick();
        cancelBTN.setOnClickListener(onClick);
        addBTN.setOnClickListener(onClick);
    }


    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.cancelTask:
                    intent = new Intent(TaskaddActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.addTask:
                    String task_name = taskName.getText().toString();
                    String task_detail =taskDetail.getText().toString();
                    int maxmember = Integer.parseInt(maximum.getText().toString());
                    if(taskName==null||task_detail==null){
                        ToastUtil.showToast(getApplicationContext(),"taskName or task_detail might be null!!!");
                    }else
                        creatTask(task_name,task_detail,maxmember);
                    break;

            }
        }
    }
//    private String id = "channel_1"; //自定义设置通道ID属性
//    private String description = "123";//自定义设置通道描述属性
//    private int importance = NotificationManager.IMPORTANCE_HIGH;//通知栏管理重要提示消息声音设定
//    private final NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);//通知栏管理器（得到系统服务）

    private void creatTask(String task_name, String task_detail,int max) {
        String api = APIUrl.APIaddTask;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("task_name", task_name);
        jsonObject.put("maxmember", max);
        jsonObject.put("task_detail",task_detail);


        String id = "channel_2"; //自定义设置通道ID属性
        String description = "123";//自定义设置通道描述属性
        int importance = NotificationManager.IMPORTANCE_HIGH;//通知栏管理重要提示消息声音设定
        final NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);//通知栏管理器（得到系统服务）

        RxHttp.postJson(api).addAll(jsonObject.toString())
                .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.getErrcode() == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {

                                NotificationChannel mChannel = new NotificationChannel(id, "taskname", importance);//建立通知栏通道类（需要有ID，重要属性）
                                Intent intent = new Intent(getApplicationContext(),
                                        TaskActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("current_robot",robotInfo);
//                                intent.putExtras(bundle);
                                intent.putExtra("yes",true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_MUTABLE);
                                Intent intent2 = new Intent(getApplicationContext(),
                                        MainActivity.class);
                                intent2.putExtra("no",false);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(),1,intent2,PendingIntent.FLAG_MUTABLE);
                                manager.createNotificationChannel(mChannel);
                                Notification notification = new Notification.Builder(getApplicationContext(), id)
                                        .setContentTitle("A new Task is created")
                                        .setContentText("Do you want to join? task name:"+task_name)
                                        .setWhen(System.currentTimeMillis())
                                        .addAction(R.drawable.ic_launcher_foreground,"Join",pendingIntent)
                                        .addAction(R.drawable.ic_launcher_foreground,"no",pendingIntent2)
                                        .setSmallIcon(R.mipmap.ic_launcher).build();
                                manager.notify((int) System.currentTimeMillis(),notification);
                            }
                        }, 2000);
                    }
                }, throwable -> {
                    Log.d("Debug", throwable.toString());
                });

    }

}
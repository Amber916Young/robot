
package com.yang.robot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.yang.robot.adapter.RobotAdapter;
import com.yang.robot.entity.Response;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.ui.login.LogintoActivity;
import com.yang.robot.util.JsonUtils;
import com.yang.robot.util.http.APIUrl;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class robotsActivity extends AppCompatActivity {
    private RecyclerView  recycleRobots;
    private RobotAdapter robotAdapter;
    private List<RobotInfo> robotInfoList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robots);
        recycleRobots = findViewById(R.id.recycleRobots);
        LitQueryDB();
    }

    private void loadData(){
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(robotsActivity.this);
        recycleRobots.setLayoutManager(linearLayoutManager2);
        recycleRobots.setItemAnimator(new DefaultItemAnimator());
        robotAdapter = new RobotAdapter(this, R.layout.activity_allrobots_list, robotInfoList);
        recycleRobots.setAdapter(robotAdapter);
    }

    public void LitQueryDB() {
//        List<RobotInfo> robotInfoList =  LitePal.order("id").find(RobotInfo.class);
//        return robotInfoList;
        JSONObject jsonObject = new JSONObject();
        String api = APIUrl.APIallRobots;
        RxHttp.postJson(api).addAll(jsonObject.toString())//指定在主线程回调
                .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if(data.getErrcode()==0){
                        String r1 = JsonUtils.toJson(data.getData());
                        robotInfoList = JsonUtils.jsonToList(r1,RobotInfo.class);
                        loadData();
                    }
//                    runOnUiThread(new Runnable() {
//                        public void run() {
                            Toast.makeText(getApplicationContext(), data.getErrmsg(), Toast.LENGTH_LONG).show();
//                        }
//                    });
                }, throwable -> {
                    Log.d("Debug",throwable.toString());
                });
    }
}
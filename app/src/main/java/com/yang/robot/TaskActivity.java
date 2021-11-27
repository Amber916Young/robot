package com.yang.robot;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.getBoolean;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.fasterxml.jackson.databind.JsonNode;
import com.yang.robot.adapter.ExpandAdapter;
import com.yang.robot.entity.Response;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.entity.Tasks;
import com.yang.robot.ui.login.LogintoActivity;
import com.yang.robot.util.JsonUtils;
import com.yang.robot.util.ToastUtil;
import com.yang.robot.util.http.APIUrl;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class TaskActivity extends AppCompatActivity {
    private ExpandableListView expandablelistview;
    private RobotInfo robotInfo;
    private ExpandAdapter expandAdapter;
    private List<HashMap>  GroupList = new ArrayList();
    private List<Tasks>  tasksList = new ArrayList();
    private List<List<Tasks>>  ChildList = new ArrayList();
    //是否使用默认的指示器 默认true 使用者可以在这里通过改变这个值观察默认指示器和自定义指示器的区别
    private boolean use_default_indicator = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        expandablelistview = findViewById(R.id.expandableListView);
        Bundle bundle = getIntent().getExtras();
//        robotInfo = (RobotInfo) bundle.getSerializable("current_robot");
        List<RobotInfo> allSongs = LitePal.findAll(RobotInfo.class);
        robotInfo = allSongs.get(0);
        loadList();

    }


    private void loadList() {
        String api = APIUrl.APIviewTasks;
        /**
         *   String r1 = JsonUtils.toJson(data.getData());
         *   RobotInfo robotInfoList = JsonUtils.jsonToPojo(r1,RobotInfo.class);
         * */
        RxHttp.get(api)
                .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.getErrcode() == 0) {
                        String r1 = JsonUtils.toJson(data.getData());
                        JsonNode jsonObject = JsonUtils.stringToJsonNode(r1);
                        for(int i =0;i<jsonObject.size();i++){
                            JsonNode jb = jsonObject.get(i);
                            int id = jb.get("id").asInt();
                            String task_name = jb.get("task_name").asText();
                            String createTime = jb.get("createTime").asText();
                            String finishTime = jb.get("finishTime").asText();
                            int maxmember = jb.get("maxmember").asInt();
                            String task_detail = jb.get("task_detail").asText();
                            Boolean checked  = jb.get("checked").asBoolean();
                            JsonNode robotInfoList = jb.get("robotInfoList");
                            Tasks tasks = new Tasks();
                            tasks.setMaxmember(maxmember);
                            tasks.setChecked(checked);
                            tasks.setCreateTime(createTime);
                            tasks.setTask_name(task_name);
                            tasks.setId(id);
                            tasks.setFinishTime(finishTime);
                            tasks.setTask_detail(task_detail);
                            List<RobotInfo> RobotInfoList = new ArrayList<>();
                            for(int n=0;n< robotInfoList.size();n++){
                                JsonNode jb2 = robotInfoList.get(n);
                                RobotInfo robotInfo = JsonUtils.mapper(jb2,RobotInfo.class);
                                RobotInfoList.add(robotInfo);
                            }
                            tasks.setRobotInfoList(RobotInfoList);
                            tasksList.add(tasks);
                            List temp = new ArrayList();
                            temp.add(tasks);
                            ChildList.add(temp);
                        }
                        for(int i=0;i<tasksList.size();i++){
                            HashMap map = new HashMap();
                            map.put("name",tasksList.get(i).getTask_name());
                            if(tasksList.get(i).getChecked()==TRUE){
                                map.put("over",0);
                            }else {
                                map.put("over",1);
                            }
                            GroupList.add(map);
                        }
                        if ( use_default_indicator ) {
                            //不做处理就是默认
                        } else {
                            expandablelistview.setGroupIndicator(null);
                        }
                        expandAdapter = new ExpandAdapter(robotInfo,getApplicationContext(),GroupList,ChildList,R.layout.task_title_layout,R.layout.task_all_layout);
                        expandablelistview.setAdapter(expandAdapter);
                        expandablelistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {//一级点击监听
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                //如果分组被打开 直接关闭
                                if ( expandablelistview.isGroupExpanded(groupPosition) ) {
                                    expandablelistview.collapseGroup(groupPosition);
                                }else {
                                    parent.expandGroup(groupPosition, false);
                                }
                                return true;
                            }
                        });
                        expandablelistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {//二级点击监听
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                List<Tasks> childModels = ChildList.get(groupPosition);
                                if ( childModels != null ) {
                                    Tasks childModel = childModels.get(childPosition);
                                    if ( childModel != null ) {
                                        String name = childModel.getTask_name();
                                        if ( !TextUtils.isEmpty(name) ) {
                                            ToastUtil.showToast(getApplicationContext(), name + "hey! DO NOT HIT!");
                                        }
                                    }
                                }
                                return false;
                            }
                        });
                    }
                }, throwable -> {
                    Log.d("Debug", throwable.toString());
                });
    }
}
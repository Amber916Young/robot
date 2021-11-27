package com.yang.robot.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yang.robot.HandleData.TasksXMLData;
import com.yang.robot.MapsActivity;
import com.yang.robot.R;
import com.yang.robot.entity.Response;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.entity.Tasks;
import com.yang.robot.util.JsonUtils;
import com.yang.robot.util.ToastUtil;
import com.yang.robot.util.http.APIUrl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class TaskAdapter extends  RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private List<HashMap> dataLiat;
    private int rowId;
    private RobotInfo currentRobot;
    private int position;

    public TaskAdapter(Context context,int rowId, List<HashMap> dataLiat,RobotInfo currentRobot) {
        this.context = context;
        this.rowId = rowId;
        this.dataLiat = dataLiat;
        this.currentRobot = currentRobot;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView task_name ,task_detail,task_accept_time =null;
        private CheckBox checkBox;
        private Button finishBtn,deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
            task_name =(TextView)itemView.findViewById(R.id.task_name);
            task_detail =(TextView)itemView.findViewById(R.id.task_detail);
            finishBtn=itemView.findViewById(R.id.finishBtn);
            deleteBtn=itemView.findViewById(R.id.deleteBtn);
            task_accept_time = (TextView)itemView.findViewById(R.id.task_accept_time);
        }
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(rowId,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        HashMap current = dataLiat.get(position);
        holder.task_name.setText(current.get("task_name").toString());
        holder.task_detail.setText(current.get("task_detail").toString());
        holder.task_accept_time.setText(current.get("joinTime").toString().substring(0,16));
        holder.checkBox.setChecked(Boolean.parseBoolean(current.get("checked").toString()));
        View v = holder.finishBtn.findViewById(R.id.finishBtn);
        if(current.get("checked").toString().equals("TRUE")){
            holder.finishBtn.setVisibility(v.GONE);
            holder.deleteBtn.setVisibility(v.VISIBLE);
        }else {
            holder.deleteBtn.setVisibility(v.GONE);
            holder.finishBtn.setVisibility(v.VISIBLE);
        }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rid",currentRobot.getId());
                jsonObject.put("tid",current.get("id"));
                String api = APIUrl.APIHiddenTask;
                RxHttp.postJson(api).addAll(jsonObject.toString())
                        .asClass(Response.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            ToastUtil.showToastLong(context,data.getErrmsg());
                            if(data.getErrcode()==0){
                                for (int n=0;n<dataLiat.size();n++){
                                    double dtid = Double.valueOf(String.valueOf(dataLiat.get(n).get("id")));
                                    int tid = (int)(dtid);
                                    double dRid= Double.valueOf(String.valueOf(dataLiat.get(n).get("rid")));
                                    int rid = (int)(dRid);
                                    double ccid =Double.valueOf(String.valueOf(current.get("id")));
                                    int cid =  (int)ccid;
                                    if(rid==currentRobot.getId()&&tid==cid){
                                        notifyItemRangeRemoved(n,1);
                                        dataLiat.remove(n);
                                        break;
                                    }
                                }
                            }
                        }, throwable -> {
                            Log.d("error",throwable.toString());
                            ToastUtil.showToastLong(context,throwable.toString());
                        });
            }
        });
        holder.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rid",currentRobot.getId());
                jsonObject.put("tid",current.get("id"));
                String api = APIUrl.APIFinishTask;
                RxHttp.postJson(api).addAll(jsonObject.toString())
                        .asClass(Response.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            ToastUtil.showToast(context,data.getErrmsg());
                            if(data.getErrcode()==0){
                                current.put("checked","TRUE");
                                if(current.get("checked").toString().equals("TRUE")){
                                    holder.finishBtn.setVisibility(v.GONE);
                                    holder.deleteBtn.setVisibility(v.VISIBLE);
                                    holder.checkBox.setChecked(Boolean.parseBoolean(current.get("checked").toString()));
                                }else {
                                    holder.deleteBtn.setVisibility(v.GONE);
                                    holder.finishBtn.setVisibility(v.VISIBLE);

                                }
                            }
                        }, throwable -> {
                            //请求失败
                            Log.d("error",throwable.toString());
                            ToastUtil.showToast(context,throwable.toString());
                        });
            }
        });
    }



    @Override
    public int getItemCount() {
        return dataLiat.size();
    }
}

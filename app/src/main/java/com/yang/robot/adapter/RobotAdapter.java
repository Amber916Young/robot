package com.yang.robot.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yang.robot.HandleData.TasksXMLData;
import com.yang.robot.MainActivity;
import com.yang.robot.MapsActivity;
import com.yang.robot.R;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.entity.Tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RobotAdapter extends  RecyclerView.Adapter<RobotAdapter.ViewHolder> {
    private Context context;
    private List<RobotInfo> dataLiat;
    private int rowId;


    public RobotAdapter(Context context, int rowId, List<RobotInfo> dataLiat) {
        this.context = context;
        this.rowId = rowId;
        this.dataLiat = dataLiat;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView robot_name ,robot_location,robot_register,activeTime =null;
        private ImageView robot_img;
        private Button robotDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            robot_location =(TextView)itemView.findViewById(R.id.robot_location);
            activeTime =(TextView)itemView.findViewById(R.id.activeTime);
            robot_name =(TextView)itemView.findViewById(R.id.robot_name);
            robot_img = (ImageView)itemView.findViewById(R.id.robot_img);
            robotDetail = (Button)itemView.findViewById(R.id.robotDetail);
            robot_register= (TextView)itemView.findViewById(R.id.robot_register);

        }
    }

    @NonNull
    @Override
    public RobotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(rowId,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RobotAdapter.ViewHolder holder, int position) {
        RobotInfo current = dataLiat.get(position);
        holder.robot_location.setText(current.getLocation_name());
        holder.activeTime.setText(current.getActiveTime());
        holder.robot_name.setText(current.getRobot_name());
//        Glide.with(context).load(current.getRobot_img()).into(holder.robot_img);

        RequestOptions mRequestOptions = RequestOptions
                .circleCropTransform()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        Glide.with(context)
                .load(current.getRobot_img())
                .apply(mRequestOptions)
                .into(holder.robot_img);


        holder.robot_register.setText(current.getRegisterTime());

        holder.robotDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("current_robot",current);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return dataLiat.size();
    }
}

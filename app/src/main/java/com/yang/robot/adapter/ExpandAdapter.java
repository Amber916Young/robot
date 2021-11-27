package com.yang.robot.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.yang.robot.R;
import com.yang.robot.entity.Response;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.entity.Tasks;
import com.yang.robot.util.ToastUtil;
import com.yang.robot.util.http.APIUrl;

import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class ExpandAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private RobotInfo robotInfo;

    private List<HashMap> mGroupList;
    List<List<Tasks>> mChildList;//二级List 注意!这里是List里面套了一个List<String>,实际项目你可以写一个pojo类来管理2层数据

    private int mExpandedGroupLayout;
    private int mChildLayout;
    //视图加载器
    private LayoutInflater mInflater;

    public ExpandAdapter(RobotInfo robotInfo,Context mContext, List<HashMap> mGroupList, List<List<Tasks>> mChildList, int mExpandedGroupLayout, int mChildLayout) {
        this.robotInfo = robotInfo;
        this.mContext = mContext;
        this.mGroupList = mGroupList;
        this.mChildList = mChildList;
        this.mExpandedGroupLayout = mExpandedGroupLayout;
        this.mChildLayout = mChildLayout;
        this.mInflater = ( LayoutInflater ) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {//返回第一级List长度
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {//返回指定groupPosition的第二级List长度
        return mChildList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {//返回一级List里的内容
        return mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {//返回二级List的内容
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {//返回一级View的id 保证id唯一
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {//返回二级View的id 保证id唯一
        return groupPosition + childPosition;
    }

    /**
     * 指示在对基础数据进行更改时子ID和组ID是否稳定
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
    /**
     * 创建新的组视图
     *
     * @param parent
     * @return
     */
    public View newGroupView(ViewGroup parent) {
        return mInflater.inflate(mExpandedGroupLayout, parent, false);
    }
    /**
     * 绑定组数据
     *
     * @param view
     * @param data
     * @param isExpanded
     */
    private void bindGroupView(View view, HashMap  data, boolean isExpanded) {
        // 绑定组视图的数据 当然这些都是模拟的
        TextView tv_title =  view.findViewById(R.id.titleView);
        String title = data.get("name").toString();
        if(data.get("over").equals(1)){
            //the task is not finished! show color
            tv_title.setTextColor(view.getResources().getColor(R.color.success));
        }else {
            tv_title.setTextColor(view.getResources().getColor(R.color.black));

        }
        tv_title.setText(title);
//        if ( !use_default_indicator ) {
            ImageView iv_tip = (ImageView) view.findViewById(R.id.iv_tip);
            if ( isExpanded ) {
                iv_tip.setImageResource(R.mipmap.down);
            } else {
                iv_tip.setImageResource(R.mipmap.right);
            }
//        }
    }

    /**
     *  返回一级父View
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // 取得用于显示给定分组的视图。 这个方法仅返回分组的视图对象， 要想获取子元素的视图对象，
        // 就需要调用 getChildView(int, int, boolean, View, ViewGroup)。
        View v;
        if ( convertView == null ) {
            v = newGroupView(parent);
        } else {
            v = convertView;
        }
        bindGroupView(v, mGroupList.get(groupPosition), isExpanded);
        return v;
    }

    /**
     * 创建新的子视图
     *
     * @param parent
     * @return
     */
    public View newChildView(ViewGroup parent) {
        return mInflater.inflate(mChildLayout, parent, false);
    }
    private  ChipGroup chipGroup = null;
    /**
     * 绑定子数据
     *
     * @param view
     */
    private void bindChildView(View view, Tasks tasks) {
        // 绑定组视图的数据 当然这些都是模拟的
        TextView T_detail ,T_create_date,T_finish_date=null;
        Button JoinTask =null;

        T_detail = view.findViewById(R.id.T_detail);
        T_create_date = view.findViewById(R.id.T_create_date);
        T_finish_date = view.findViewById(R.id.T_finish_date);
        JoinTask = view.findViewById(R.id.JoinTask);
        T_detail.setText(tasks.getTask_detail());
        T_create_date.setText(tasks.getCreateTime());
        T_finish_date.setText(tasks.getFinishTime());
        if(tasks.getChecked()){
            JoinTask.setVisibility(view.GONE);
        }
        chipGroup = view.findViewById(R.id.chip_group_main);
        chipGroup.removeAllViews();
        int len =tasks.getRobotInfoList().size();
        for (int i = 0; i <len; i++) {
            Chip chip = null;
            chip = new Chip(chipGroup.getContext());
            RobotInfo info = tasks.getRobotInfoList().get(i);
            if(info.getRobot_name()==null){
                chip.setText("noname");
            }else {
                chip.setText(info.getRobot_name());
            }
            chip.setChipBackgroundColorResource(R.color.active_blue);
            chip.setCloseIconVisible(true);
            chip.setTextColor(view.getResources().getColor(R.color.black));
            chip.setTextAppearance(R.style.ChipTextAppearance);
            chipGroup.addView(chip);
        }

        JoinTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",tasks.getId());
                jsonObject.put("maxmember",tasks.getMaxmember());
                jsonObject.put("rid",robotInfo.getId());
                String api = APIUrl.APIJoinTask;
                RxHttp.postJson(api).addAll(jsonObject.toString())
                        .asClass(Response.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            ToastUtil.showToast(mContext,data.getErrmsg());
                            if(data.getErrcode()==0) {
                                Chip chip = null;
                                chip = new Chip(chipGroup.getContext());
                                chip.setText(robotInfo.getRobot_name());
                                chip.setChipBackgroundColorResource(R.color.active_blue);
                                chip.setCloseIconVisible(true);
                                chip.setTextColor(view.getResources().getColor(R.color.black));
                                chip.setTextAppearance(R.style.ChipTextAppearance);
                                chipGroup.addView(chip);
                                tasks.getRobotInfoList().add(robotInfo);
                            }
                        }, throwable -> {
                            //请求失败
                            Log.d("error",throwable.toString());
                            ToastUtil.showToast(mContext,throwable.toString());
                        });

            }
        });
    }
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // 取得显示给定分组给定子位置的数据用的视图。
        View v;
        if ( convertView == null ) {
            v = newChildView(parent);
        } else {
            v = convertView;
        }
        Tasks t = mChildList.get(groupPosition).get(childPosition);
        bindChildView(v,t);
        return v;
    }



    /**
     *  指定位置的子项是否可选
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

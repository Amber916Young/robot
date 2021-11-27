package com.yang.robot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.yang.robot.DataBase.MyDatabaseHelper;
import com.yang.robot.HandleData.TasksXMLData;
import com.yang.robot.adapter.RobotAdapter;
import com.yang.robot.adapter.TaskAdapter;
import com.yang.robot.entity.Response;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.entity.Tasks;
import com.yang.robot.ui.login.LoginActivity;
import com.yang.robot.ui.login.LogintoActivity;
import com.yang.robot.util.JsonUtils;
import com.yang.robot.util.ToastUtil;
import com.yang.robot.util.http.APIUrl;
import com.yang.robot.util.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class MainActivity extends AppCompatActivity  {
    private RecyclerView recycleTasks, recycleRobots;
    private TaskAdapter taskAdapter;
    private RobotAdapter robotAdapter;
    private TasksXMLData data = null;
    private MyDatabaseHelper databaseHelper;
    private Button button1 ,robotDetail,loginBtn,logoutBtn,BrokenBtn,InactiveBtn;
    private RobotInfo currentRobot;
    private List<RobotInfo> currentRobotList = new ArrayList<>();
    private List<HashMap> TasksList = new ArrayList<>();
    private TextView robot_name,robot_active_time,robot_location;
    private ImageView imageView;
    private GoogleMap mMap;
    private Chip robot_state;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private Button send_notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycleTasks = findViewById(R.id.recycleTasks);
        recycleRobots = findViewById(R.id.recycleRobots);
        loginBtn = findViewById(R.id.loginBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        BrokenBtn = findViewById(R.id.BrokenBtn);
        InactiveBtn = findViewById(R.id.InactiveBtn);
        button1 = findViewById(R.id.b1);
        robot_name = findViewById(R.id.robot_name);
        robot_state = findViewById(R.id.robot_state);
        robot_active_time = findViewById(R.id.robot_active_time);
        robot_location = findViewById(R.id.robot_location);
        imageView = findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            if(currentRobot==null){
                Intent intent = new Intent(MainActivity.this, LogintoActivity.class);
                startActivity(intent);
                return;
            }
        } else {
            if (bundle.getSerializable("current_robot") != null) {
                RobotInfo robotInfo = (RobotInfo) bundle.getSerializable("current_robot");
                if (robotInfo == null) {
                    Intent intent = new Intent(MainActivity.this, LogintoActivity.class);
                    startActivity(intent);
                    return;

                } else {
                    currentRobot = robotInfo;
                }
            } else {
                Intent intent = new Intent(MainActivity.this, LogintoActivity.class);
                startActivity(intent);
                return;
            }
        }
        loadCurrentRobot();
        LitQueryDB();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setListeners();



    }

    @Override
    protected void onResume() {
        System.out.println("onResume");
        loadTasks();
        startNaviGoogle();

        super.onResume();
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart");
        super.onRestart();
    }


    private void loadTasks(){
        String api = APIUrl.APIviewTaskRobots.replaceAll("ID", String.valueOf(currentRobot.getId()));
        RxHttp.get(api)
                .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.getErrcode() == 0) {
                        String r1 = JsonUtils.toJson(data.getData());
                        TasksList = JsonUtils.jsonToList(r1, HashMap.class);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                        recycleTasks.setLayoutManager(linearLayoutManager);
                        recycleTasks.setItemAnimator(new DefaultItemAnimator());
                        taskAdapter = new TaskAdapter(this, R.layout.activity_tasks, TasksList,currentRobot);
                        recycleTasks.setAdapter(taskAdapter);
                    }else {

                    }
                }, throwable -> {
                    Log.d("Debug", throwable.toString());
                });



    }
    private void loadRobotList(){
        //get All active robot
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false);
        recycleRobots.setLayoutManager(linearLayoutManager2);
        recycleRobots.setItemAnimator(new DefaultItemAnimator());
        robotAdapter = new RobotAdapter(this, R.layout.activity_robots_list, currentRobotList);
        recycleRobots.setAdapter(robotAdapter);

        recycleRobots.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,int dx,int dy){
                super.onScrolled(recyclerView,dx,dy);
                int adapterNowPos = linearLayoutManager2.findFirstVisibleItemPosition();
                int allItems = linearLayoutManager2.getItemCount();
                String s = adapterNowPos+1+"/"+allItems;
                Log.d("debug---->", s);

                RobotInfo movetoRobot = currentRobotList.get(adapterNowPos);
                if(movetoRobot.getLatitude()!=null&&movetoRobot.getLongitude()!=null){
                    LatLng latLng = new LatLng(movetoRobot.getLatitude(), movetoRobot.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .snippet(movetoRobot.getLocation_name())
                            .title(movetoRobot.getRobot_name()));
                    mMap.setMaxZoomPreference(10);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        });
    }
    private void setListeners() {
        OnClick onClick = new OnClick();
        loginBtn.setOnClickListener(onClick);
        logoutBtn.setOnClickListener(onClick);
        button1.setOnClickListener(onClick);
    }


    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.robotDetail:
                    intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.b1:
                    intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                    break;
                //active
                case R.id.loginBtn:
                    loadCurrentRobot();
                    break;
                //offline
                case R.id.logoutBtn:
                    Logout();
                    break;
            }
        }
    }

    private void Logout() {
        String api = APIUrl.APIlogout.replaceAll("ID", String.valueOf(currentRobot.getId()));
        RxHttp.get(api)
                .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.getErrcode() == 0) {
                        logoutBtn.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                        robot_state.setChipBackgroundColorResource(R.color.offLine_red);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this,
                                        LogintoActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);
                    }
                }, throwable -> {
                    Log.d("Debug", throwable.toString());
                });

    }

    private void loadCurrentRobot() {
        RobotInfo robotInfo = currentRobot;
        robot_name.setText(robotInfo.getRobot_name());
        robot_location.setText(robotInfo.getLocation_name());
        Glide.with(this).load(robotInfo.getRobot_img()).into(imageView);
        RequestOptions mRequestOptions = RequestOptions
                .circleCropTransform()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(getApplicationContext())
                .load(robotInfo.getRobot_img())
                .apply(mRequestOptions)
                .into(imageView);

        //0 inactive 1 running 2 offline 3 broken

        if (robotInfo.getState() == 2) {
            logoutBtn.setVisibility(View.GONE);
            BrokenBtn.setVisibility(View.GONE);
            InactiveBtn.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
            robot_state.setChipBackgroundColorResource(R.color.offLine_red);
        } else if (robotInfo.getState() == 1) {
            loginBtn.setVisibility(View.GONE);
            BrokenBtn.setVisibility(View.GONE);
            InactiveBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            robot_state.setChipBackgroundColorResource(R.color.active_blue);
        } else if (robotInfo.getState() == 3) {
            loginBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
            InactiveBtn.setVisibility(View.GONE);
            BrokenBtn.setVisibility(View.VISIBLE);
            robot_state.setChipBackgroundColorResource(R.color.black);
        }
        else if (robotInfo.getState() == 0) {
            loginBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
            BrokenBtn.setVisibility(View.GONE);
            InactiveBtn.setVisibility(View.VISIBLE);
            robot_state.setChipBackgroundColorResource(R.color.inactive);
        }
        if (robotInfo.getActiveTime() != null || robotInfo.getActiveTime() != "") {
            robot_active_time.setText(robotInfo.getActiveTime().toString().substring(0, 10));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = null;
        Bundle bundle = new Bundle();

        switch (item.getItemId()){
            case R.id.menuRegister:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                Toast.makeText(getApplicationContext(),"register",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuRobots:
                Toast.makeText(getApplicationContext(),"all robots details",Toast.LENGTH_SHORT).show();
                 intent = new Intent(MainActivity.this, robotsActivity.class);
                break;
            case R.id.menuBroken:
                reportBoken();
                break;
            case R.id.newTasks:
                Toast.makeText(getApplicationContext(),"create a new task ready",Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, TaskaddActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("current_robot",currentRobot);
                intent.putExtras(bundle);
                break;
            case R.id.viewAllTasks:
                Toast.makeText(getApplicationContext(),"view all tasks",Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, TaskActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("current_robot",currentRobot);
                intent.putExtras(bundle);
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void reportBoken() {
        String api = APIUrl.APIbroken.replaceAll("ID", String.valueOf(currentRobot.getId()));
        RxHttp.get(api)
                .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.getErrcode() == 0) {
                        Intent intent= new Intent(MainActivity.this, LogintoActivity.class);
                        startActivity(intent);
                    }
                    ToastUtil.showToast(getApplicationContext(),data.getErrmsg());
                }, throwable -> {
                    Log.d("Debug", throwable.toString());
                });
    }


    public void LitInsertDB() {
        RobotInfo robotInfo = new RobotInfo();
        robotInfo.setId(1);
        robotInfo.setRobot_name("robot1");
        robotInfo.setRobot_img("https://st4.depositphotos.com/17797916/20035/v/450/depositphotos_200357000-stock-illustration-wifi-robot-logo-icon-design.jpg");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        robotInfo.setRegisterTime(formatter.format(date));
        robotInfo.setActiveTime(formatter.format(date));
        robotInfo.setState(1);
        robotInfo.setPassword("123456");
        robotInfo.setLocation_name("College Rd, University College, Cork");
        robotInfo.setLatitude(51.90608856703339);
        robotInfo.setLongitude(-8.511182313598313);

        //返回true代表添加成功，返回flase代表添加失败
        robotInfo.save();

    }


    public void LitQueryDB() {
        String api = APIUrl.getCurrentRobot.replaceAll("ID", String.valueOf(currentRobot.getId()));
        RxHttp.get(api)
                .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data.getErrcode() == 0) {
                        String r1 = JsonUtils.toJson(data.getData());
                        currentRobotList = JsonUtils.jsonToList(r1, RobotInfo.class);
                        loadRobotList();
                    }
                    Toast.makeText(getApplicationContext(), data.getErrmsg(), Toast.LENGTH_LONG).show();
                }, throwable -> {
                    Log.d("Debug", throwable.toString());
                });
    }




    private void InsertDB() {
        databaseHelper = new MyDatabaseHelper(this, "Robot.db", null, 2);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("robot_name", "robot1");
        values.put("robot_img", "1");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println(formatter.format(date));
        values.put("registerTime", formatter.toString());
        values.put("state", 0);


        values.put("robot_name", "robot2");
        values.put("robot_img", "2");
        values.put("registerTime", formatter.toString());
        values.put("state", 1);


        values.put("robot_name", "robot3");
        values.put("robot_img", "3");
        values.put("registerTime", formatter.toString());
        values.put("state", 1);

        values.put("robot_name", "robot4");
        values.put("robot_img", "4");
        values.put("registerTime", formatter.toString());
        values.put("state", 0);
        values.put("robot_name", "robot6");
        values.put("robot_img", "6");
        values.put("registerTime", formatter.toString());
        values.put("state", 0);
        values.put("robot_name", "robot5");
        values.put("robot_img", "5");
        values.put("registerTime", formatter.toString());
        values.put("state", 1);

        values.put("robot_name", "robot7");
        values.put("robot_img", "7");
        values.put("registerTime", formatter.toString());
        values.put("state", 1);
    }



    public void startNaviGoogle() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                for(int i =0;i<currentRobotList.size();i++){
                    if(currentRobotList.get(i).getLatitude()!=null && currentRobotList.get(i).getLongitude()!=null){
                        LatLng latLng = new LatLng(currentRobotList.get(i).getLatitude(), currentRobotList.get(i).getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .snippet(currentRobotList.get(i).getLocation_name())
                                .title(currentRobotList.get(i).getRobot_name()));
                    }

                }
                enableMyLocation();
            }
        });
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;


    private void enableMyLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if ( mMap!= null) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                // Permission to access the location is missing. Show rationale and request permission
                PermissionUtils.requestPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_PERMISSION_REQUEST_CODE );
            }
        }catch (Exception e){
            Log.d("debug====>",e.getMessage());
        }

    }
    private void getCurrentLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this,new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            String addStr = null;

                            List<Address> addresses;
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String zipCode = addresses.get(0).getPostalCode();
                                String country = addresses.get(0).getCountryCode();
                                addStr = address + "," + city + "," + state + "," + country;
                                currentRobot.setLocation_name(addStr);
                                currentRobot.setLatitude(location.getLatitude());
                                currentRobot.setLongitude(location.getLongitude());
                                robot_location.setText(addStr.substring(0, 15) + "...");
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id", currentRobot.getId());
                                jsonObject.put("latitude", location.getLatitude());
                                jsonObject.put("location_name", addStr);
                                jsonObject.put("longitude", location.getLongitude());
                                String api = APIUrl.APIupdate;
                                RxHttp.postJson(api).addAll(jsonObject.toString())
                                        .asClass(Response.class).observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(data -> {
                                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            googleMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .snippet(currentRobot.getLocation_name())
                                                    .title(currentRobot.getRobot_name()));
                                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                            Toast.makeText(getApplicationContext(), data.getErrmsg(), Toast.LENGTH_LONG).show();
                                        }, throwable -> {
                                            Log.d("Debug", throwable.toString());
                                        });
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    });

                }
            }

        });
        fusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("debug----->",e.getMessage());
            }
        });

    }



//    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
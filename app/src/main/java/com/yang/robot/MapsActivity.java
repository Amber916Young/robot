package com.yang.robot;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.yang.robot.databinding.ActivityMapsBinding;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.ui.login.LogintoActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private RobotInfo currentRobot;
    private TextView robot_name,robot_active_time,robot_location,locationLant;
    private ImageView robot_img;
    private Chip robot_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        robot_name = findViewById(R.id.robot_name);
        robot_state = findViewById(R.id.robot_state);
        robot_active_time = findViewById(R.id.robot_active_time);
        robot_location = findViewById(R.id.robot_location);
        locationLant = findViewById(R.id.locationLant);
        robot_img = findViewById(R.id.robot_img);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        } else {
            if (bundle.getSerializable("current_robot") != null) {
                RobotInfo robotInfo = (RobotInfo) bundle.getSerializable("current_robot");
                if (robotInfo == null) {
                    Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                    startActivity(intent);
                    return;

                } else {
                    currentRobot = robotInfo;
                    robot_name.setText(robotInfo.getRobot_name());
                    robot_location.setText(robotInfo.getLocation_name());
                    locationLant.setText(robotInfo.getLatitude()+" , "+robotInfo.getLongitude());
                    Glide.with(this).load(robotInfo.getRobot_img()).into(robot_img);
                    if (robotInfo.getState() == 2) {
                        robot_state.setChipBackgroundColorResource(R.color.offLine_red);
                    } else if (robotInfo.getState() == 1) {
                        robot_state.setChipBackgroundColorResource(R.color.active_blue);
                    } else if (robotInfo.getState() == 3) {
                        robot_state.setChipBackgroundColorResource(R.color.black);
                    }
                    else if (robotInfo.getState() == 0) {
                        robot_state.setChipBackgroundColorResource(R.color.inactive);
                    }


                    robot_active_time.setText(robotInfo.getActiveTime());
                }
            } else {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                return;
            }
        }




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (currentRobot.getLatitude()!=null ||currentRobot.getLongitude()!=null ){
            LatLng cc = new LatLng(currentRobot.getLatitude(), currentRobot.getLongitude());
            mMap.addMarker(new MarkerOptions().position(cc)
                    .snippet(currentRobot.getLocation_name())
                    .title(currentRobot.getRobot_name()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cc));
        }

    }


}
package com.yang.robot.ui.login;

import android.app.Activity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.robot.MainActivity;
import com.yang.robot.R;
import com.yang.robot.TaskActivity;
import com.yang.robot.TaskaddActivity;
import com.yang.robot.entity.Response;
import com.yang.robot.entity.RobotInfo;
import com.yang.robot.databinding.ActivityLoginRBinding;
import com.yang.robot.util.JsonUtils;
import com.yang.robot.util.http.APIUrl;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class LogintoActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginRBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginRBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });



    }


    private void updateUiWithUser(LoggedInUserView model) {

        String robotName= binding.username.getText().toString();
        String password= binding.password.getText().toString();

    //List<Song> songs = LitePal.where("name like ? and duration < ?", "song%", "200").order("duration").find(Song.class);
//        List<RobotInfo> robotInfoList =  LitePal.where("password = ? and robot_name = ? ",password,robotName).find(RobotInfo.class);
        JSONObject res = new JSONObject();
        try {
//            {"type":"login","robotName":"robot1","password":"123456"}
            res.put("password",password);
            res.put("robot_name",robotName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String api = APIUrl.APIlogin;
        RxHttp.postJson(api).addAll(res.toString())
                .asClass(Response.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if(data.getErrcode()==0){
                        System.out.println(data.getData());
                        String r1 = JsonUtils.toJson(data.getData());
                        RobotInfo robotInfoList = JsonUtils.jsonToPojo(r1,RobotInfo.class);
                        success(robotInfoList);
                    }else {
                        Toast.makeText(getApplicationContext(), data.getErrmsg(), Toast.LENGTH_LONG).show();
                    }

                }, throwable -> {
                    //请求失败
                    Log.d("error",throwable.toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "login fail!", Toast.LENGTH_LONG).show();
                        }
                    });
                });
    }

    private void success(RobotInfo robotInfo){
        RobotInfo autoUpdate = LitePal.find(RobotInfo.class, robotInfo.getId());
        if(autoUpdate==null){
            robotInfo.save();
        }
        runOnUiThread(new Runnable() {
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LogintoActivity.this,
                                MainActivity.class);
                        Bundle bundle = new Bundle();
                        // bundle序列化
                        bundle.putSerializable("current_robot",robotInfo);
                        // put 到 Intent中
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                }, 1000);
            }
        });



    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
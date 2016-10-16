package com.example.myandroid;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.utils.SqliteUtil;
import com.example.utils.TimeUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity{
    public MyApplication myApplication;

    //variables of utils
    public TimeUtil timeUtil;
    public SqliteUtil sqliteUtil;

    //db variables
    private String ApplicationFileDir;
    private SQLiteDatabase db;

    //settings variables
    private int flag;
    private int isRememberPwd = 0, isAutoLogin = 0;
    private boolean isLogining;
    private boolean isShowPwd, isRememberPwdChecked = false, isAutoLoginChecked = false;
    private long mExitTime;
    private final static String Log_Tag = "LoginActivity";

    //login components
    public RelativeLayout loginLayout, loginLoadingLayout;
    public LinearLayout usernameInput, pwdInput;
    public EditText usernameEditText, pwdEditText;
    public ImageView pwdHideShow, remeberPwdCheck, autoLoginCheck;
    public Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIInitial();            //UI initial
        CheckForAutoLogin();    //check if the auto-login been checked

        //avoid focus the editText when open the window
        loginLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });
        //xml focus style don't work(to be modify)
        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    usernameInput.setBackgroundResource(R.drawable.input_focused_style);
                }
                else{
                    usernameInput.setBackgroundResource(R.drawable.input_normal_style);
                }
            }
        });
        pwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pwdInput.setBackgroundResource(R.drawable.input_focused_style);
                } else {
                    pwdInput.setBackgroundResource(R.drawable.input_normal_style);
                }
            }
        });

        //hide or show the pwd
        pwdHideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowPwd){
                    pwdHideShow.setImageResource(R.drawable.hide_pwd_icon);
                    isShowPwd = false;
                    pwdEditText.setInputType(129);
                    pwdEditText.postInvalidate();
                    pwdEditText.setSelection(pwdEditText.getText().length());
                }
                else{
                    pwdHideShow.setImageResource(R.drawable.show_pwd_icon);
                    isShowPwd = true;
                    pwdEditText.setInputType(128);
                    pwdEditText.postInvalidate();
                    pwdEditText.setSelection(pwdEditText.getText().length());
                }
            }
        });

        //remember-pwd and auto-login check
        remeberPwdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRememberPwdChecked){
                    remeberPwdCheck.setImageResource(R.drawable.checked);
                    isRememberPwdChecked = false;
                    isRememberPwd = 1;
                } else {
                    remeberPwdCheck.setImageResource(R.drawable.unchecked);
                    isRememberPwdChecked = true;
                    isRememberPwd = 0;
                }
            }
        });
        autoLoginCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAutoLoginChecked){
                    autoLoginCheck.setImageResource(R.drawable.checked);
                    isAutoLoginChecked = false;
                    isAutoLogin = 1;
                } else {
                    autoLoginCheck.setImageResource(R.drawable.unchecked);
                    isAutoLoginChecked = true;
                    isAutoLogin = 0;
                }
            }
        });
    }

    //UI initial
    public void UIInitial(){
        //utils variables initial
        timeUtil = new TimeUtil();
        sqliteUtil = new SqliteUtil();

        myApplication = (MyApplication)getApplication();
        ApplicationFileDir = getApplicationContext().getFilesDir() + "/ApplicationDB.db";

        //hiding the status navigation-bar
        flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = LoginActivity.this.getWindow();
        window.setFlags(flag, flag);

        setContentView(R.layout.login_activity_layout);

        //hiding the login-loading layout
        loginLoadingLayout = (RelativeLayout)findViewById(R.id.login_loading_layout);
        loginLoadingLayout.setVisibility(View.INVISIBLE);

        //login components initial
        loginLayout      = (RelativeLayout)findViewById(R.id.login_layout);
        usernameInput    = (LinearLayout)findViewById(R.id.user_name_input);
        usernameEditText = (EditText)findViewById(R.id.user_name_edit_text);
        pwdInput         = (LinearLayout)findViewById(R.id.pwd_input);
        pwdEditText      = (EditText)findViewById(R.id.pwd_edit_text);
        pwdHideShow      = (ImageView)findViewById(R.id.pwd_show_hide);

        remeberPwdCheck  = (ImageView)findViewById(R.id.remember_pwd_check);
        autoLoginCheck   = (ImageView)findViewById(R.id.auto_login_check);

        loginBtn         = (Button)findViewById(R.id.login_btn);
    }

    //check auto-login
    public void CheckForAutoLogin(){
        File dbFile = new File(ApplicationFileDir);

        if(dbFile.exists()){
            db = getApplicationContext().openOrCreateDatabase(ApplicationFileDir, getApplicationContext().MODE_PRIVATE, null);

            if(sqliteUtil.isTableExists(db, "user_login_info_table")){
                Cursor cr = db.rawQuery("SELECT * FROM user_login_info_table LIMIT 1 OFFSET (SELECT COUNT(*) - 1 FROM user_login_info_table)", null);

                if(cr != null && cr.moveToNext()){
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date lastLoginTime = simpleDateFormat.parse(cr.getString(5));

                        Map<String, Long> timeMap = new HashMap<String, Long>();
                        timeMap = timeUtil.caculateTimeDifference(timeUtil.getCurrenctTime(), lastLoginTime);

                        //clear the input message if it's a long time until last login
                        if (timeMap.get("days") >= 15) {
                            Toast.makeText(LoginActivity.this, "距离上次登录过去太久，请重新登录", Toast.LENGTH_LONG).show();
                            usernameEditText.setText("");
                            pwdEditText.setText("");
                            remeberPwdCheck.setImageResource(R.drawable.unchecked);
                            autoLoginCheck.setImageResource(R.drawable.unchecked);
                        } else {
                            /*
                            Toast.makeText(LoginActivity.this, "距离上次登录已经过去"
                                    + timeMap.get("days") + "天" + "，"
                                    + timeMap.get("hours") + "小时" + "，"
                                    + timeMap.get("minutes") + "分钟" + "，"
                                    + timeMap.get("seconds") + "秒", Toast.LENGTH_LONG).show();
                             */
                            //complete the username message
                            usernameEditText.setText(cr.getString(1));

                            //check for remember-pwd
                            if ("1".equals(cr.getString(3))) {
                                pwdEditText.setText(cr.getString(2));
                                remeberPwdCheck.setImageResource(R.drawable.checked);

                                //check for auto-login
                                if("1".equals(cr.getString(4))){
                                    autoLoginCheck.setImageResource(R.drawable.checked);
                                    OnLogin(loginBtn);
                                }
                                else{
                                    autoLoginCheck.setImageResource(R.drawable.unchecked);
                                }
                            } else {
                                pwdEditText.setText("");
                                remeberPwdCheck.setImageResource(R.drawable.unchecked);
                            }
                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }

                cr.close();
            } else {
                Log.v(Log_Tag, "您还没有登录过哦！");
            }

            db.close();
        }
    }

    //login
    public void OnLogin(View view){
        final String usernameValue = usernameEditText.getText().toString();
        final String pwdValue      = pwdEditText.getText().toString();

        if ("".equals(usernameValue) || "".equals(pwdValue)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if("15313250579".equals(usernameValue) && "9901140472".equals(pwdValue)){
                //show the login loading
                isLogining = true;
                loginLoadingLayout.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "正在登录，请稍候...", Toast.LENGTH_LONG).show();

                //avoid click event when login
                loginLoadingLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });

                //record the login message and go to home page
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //record login message
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());
                        String timeTag = simpleDateFormat.format(curDate);

                        SQLiteDatabase db = LoginActivity.this.getApplicationContext().openOrCreateDatabase(LoginActivity.this.ApplicationFileDir, LoginActivity.this.getApplicationContext().MODE_PRIVATE, null);
                        db.execSQL("CREATE TABLE IF NOT EXISTS user_login_info_table(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, isRememberPwd TEXT, isAutoLogin TEXT, timeStamp TEXT)");
                        db.execSQL("INSERT INTO user_login_info_table(id, username, password, isRememberPwd, isAutoLogin, timestamp) VALUES("
                                + null + ",'"
                                + usernameValue + "','"
                                + pwdValue + "','"
                                + isRememberPwd + "','"
                                + isAutoLogin + "','"
                                + timeTag + "')");
                        db.close();

                        //hide login loading
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        loginLoadingLayout.setVisibility(View.GONE);
                        //go to home page
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        LoginActivity.this.startActivity(intent);
                    }
                };
                handler.postDelayed(runnable, 3000);
            } else {
                Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //contacts click
    public void ContactsClick(View view){
        Log.v(Log_Tag, String.valueOf(view.getId()));

        switch (view.getId()){
            case R.id.term_of_use:
                Toast.makeText(LoginActivity.this, "使用条例", Toast.LENGTH_SHORT).show();
                break;
            case R.id.privacy_policy:
                Toast.makeText(LoginActivity.this, "隐私政策", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about_us:
                Toast.makeText(LoginActivity.this, "关于我们", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //exists app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(LoginActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }

        return false;
    }
}

package com.example.myandroid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.homesliding.BaseActivity;
import com.example.homesliding.SlidingLayout;
import com.example.utils.GifUtil;
import com.example.utils.GpsUtil;
import com.nineoldandroids.view.ViewHelper;

public class HomeActivity extends BaseActivity{
    private int flag;
    private long mExitTime;
    private final static String Log_Tag = "HomeActivity";

    private SlidingLayout slidingLayout;

    public NavigationBar navigationBar;
    public ImageView headImageView, menuImageView;

    private ImageView userHeadImage;

    //weather
    private GifUtil weatherGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        //hide the status navigation-bar
        flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = HomeActivity.this.getWindow();
        window.setFlags(flag, flag);

        //init layout
        InitLayout();
    }

    public void InitLayout(){
        slidingLayout = (SlidingLayout)findViewById(R.id.home_page);
        slidingLayout.setDragListener(new SlidingLayout.DragListener(){
            @Override
            public void onOpen() {
                //
            }

            @Override
            public void onClose() {
                //
            }

            @Override
            public void onDrag(float slidingRate) {
                //
            }
        });

        navigationBar = (NavigationBar) super.findViewById(R.id.navigation_bar);
        navigationBar.setTitle("首页");
        headImageView = navigationBar.getBackImageView();
        headImageView.setImageResource(R.drawable.username_icon);
        menuImageView = navigationBar.getMenuImageView();
        menuImageView.setImageResource(R.drawable.settings_icon);

        navigationBar.setClickCallBack(new NavigationBar.ClickCallBack(){
            @Override
            public void onBackClick() {
                return;
            }
            @Override
            public void onMenuClick() {
                return;
            }
        });

        //head block
        userHeadImage = (ImageView) findViewById(R.id.head_img);
        userHeadImage.setImageResource(R.drawable.ic_launcher);

        //weather block
        weatherGif = (GifUtil) findViewById(R.id.weather_gif);
        weatherGif.setMovieResource(R.drawable.dayu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }

        return false;
    }
}

/*
public class HomeActivity extends Activity{
    private long mExitTime;
    private final static String Log_Tag = "HomeActivity";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    public NavigationBar navigationBar;
    public ImageView headImageView, menuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        navigationBar = (NavigationBar) super.findViewById(R.id.navigation_bar);
        navigationBar.setTitle("首页");
        headImageView = navigationBar.getBackImageView();
        headImageView.setImageResource(R.drawable.username_icon);
        menuImageView = navigationBar.getMenuImageView();
        menuImageView.setImageResource(R.drawable.settings_icon);

        navigationBar.setClickCallBack(new NavigationBar.ClickCallBack(){
            @Override
            public void onBackClick() {


                return;
            }
            @Override
            public void onMenuClick() {
                return;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }

        return false;
    }
}
*/
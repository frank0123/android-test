package com.example.myandroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavigationBar extends RelativeLayout implements View.OnClickListener{
    public ImageView backImageView;
    public TextView titleView;
    public ImageView menuImageView;

    public ClickCallBack clickCallBack;

    //constructor
    public NavigationBar(Context context){
        this(context, null);
    }
    public NavigationBar(Context context, AttributeSet attrs){
        super(context, attrs);

        View view     = LayoutInflater.from(context).inflate(R.layout.navigation_bar_layout, this, true);
        backImageView = (ImageView) view.findViewById(R.id.back_icon);
        titleView     = (TextView) view.findViewById(R.id.title);
        menuImageView = (ImageView) view.findViewById(R.id.menu_icon);
        backImageView.setOnClickListener(this);
        menuImageView.setOnClickListener(this);
    }

    //get and set
    public ImageView getBackImageView(){
        return backImageView;
    }
    public TextView getTitleView(){
        return titleView;
    }
    public ImageView getMenuImageView(){
        return menuImageView;
    }
    public void setTitle(String title){
        titleView.setText(title);
    }

    //hide back & menu
    public void hideBack(Boolean bool){
        if(bool){
            backImageView.setVisibility(GONE);
        }
    }
    public void hideMenu(Boolean bool){
        if(bool){
            menuImageView.setVisibility(GONE);
        }
    }

    //back click & menu click
    public void setClickCallBack(ClickCallBack clickCallBack){
        this.clickCallBack = clickCallBack;
    }
    public static interface ClickCallBack{
        void onBackClick();
        void onMenuClick();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_icon:
                clickCallBack.onBackClick();
                break;
            case R.id.menu_icon:
                clickCallBack.onMenuClick();
                break;
            default:
                break;
        }
        /*
        int id = view.getId();

        if(id == R.id.back_icon){
            clickCallBack.onBackClick();
            return;
        }
        if(id == R.id.menu_icon){
            clickCallBack.onMenuClick();
            return;
        }
        */
    }
}

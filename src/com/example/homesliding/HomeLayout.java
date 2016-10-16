package com.example.homesliding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class HomeLayout extends RelativeLayout {
    private SlidingLayout slidingLayout;

    //constructor
    public HomeLayout(Context context){
        super(context);
    }
    public HomeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public HomeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSlidingLayout(SlidingLayout slidingLayout){
        this.slidingLayout = slidingLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(slidingLayout.getStatus() != SlidingLayout.Status.CLOSE){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slidingLayout.getStatus() != SlidingLayout.Status.CLOSE) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                slidingLayout.SlidingClose();
            }
            return true;
        }

        return super.onTouchEvent(event);
    }
}

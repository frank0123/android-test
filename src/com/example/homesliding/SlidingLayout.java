package com.example.homesliding;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.nineoldandroids.view.ViewHelper;

public class SlidingLayout extends FrameLayout {
    private Context context;
    private Status status = Status.CLOSE;                   //close sliding in default
    private static final boolean IS_SHOW_SHADOW = true;

    private GestureDetectorCompat gestureDetectorCompat;    //gesture detector
    private ViewDragHelper viewDragHelper;                  //view drag helper
    private DragListener dragListener;                    //drag listener

    private int distance;                                   //the distance for drag in X-coordinate
    private int width, height;
    private int leftDistance;                               //the distance of the main-page after sliding

    private RelativeLayout slidingLayout;                   //left sliding-layout
    private HomeLayout homeLayout;                          //home layout
    private ImageView shadow;                               //shadow effect after sliding

    //drag interface
    public interface DragListener {
        public void onOpen();
        public void onClose();
        public void onDrag(float slidingRate);
    }
    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    //sliding-status
    public enum Status {
        DRAG, OPEN, CLOSE
    }
    public Status getStatus(){
        if(leftDistance == 0){
            status = Status.CLOSE;
        } else if (leftDistance == distance) {
            status = Status.OPEN;
        } else {
            status = Status.DRAG;
        }

        return status;
    }

    //implement the view drag callback function
    private final ViewDragHelper.Callback dragCallBack = new ViewDragHelper.Callback(){
        //the distance detector for drag in X-coordinate
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (leftDistance + dx < 0) {
                return 0;
            } else if (leftDistance + dx > distance) {
                return distance;
            } else {
                return left;
            }
        }
        //capture all child-views
        @Override
        public boolean tryCaptureView(View view, int i) {
            return true;
        }
        //set the biggest distance for drag in X-coordinate
        @Override
        public int getViewHorizontalDragRange(View child) {
            return width;
        }
        //detector if release the sliding-view when the distance change
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > 0) {
                SlidingOpen();
            } else if (xvel < 0) {
                SlidingClose();
            } else if (releasedChild == homeLayout && leftDistance > distance * 0.3) {
                SlidingOpen();
            } else if (releasedChild == slidingLayout && leftDistance > distance * 0.7) {
                SlidingOpen();
            } else {
                SlidingClose();
            }
        }
        //set the sliding-layout position when position changed
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if(changedView == homeLayout){
                leftDistance = left;
            } else {
                leftDistance = leftDistance + left;
            }

            if(leftDistance < 0){
                leftDistance = 0;
            } else if (leftDistance > distance) {
                leftDistance = distance;
            }

            if(IS_SHOW_SHADOW){
                shadow.layout(leftDistance, 0, leftDistance + width, height);
            }

            if(changedView == slidingLayout){
                slidingLayout.layout(0, 0, width, height);
                homeLayout.layout(leftDistance, 0, leftDistance + width, height);
            }

            dispatchDragEvent(leftDistance);
        }
    };

    //scroll detector
    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            return Math.abs(dy) <= Math.abs(dx);
        }
    }
    //constructor
    public SlidingLayout(Context context) {
        this(context, null);
    }
    public SlidingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }
    public SlidingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetectorCompat = new GestureDetectorCompat(context, new YScrollDetector());
        viewDragHelper = ViewDragHelper.create(this, dragCallBack);
    }

    //initial
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if(IS_SHOW_SHADOW){
            shadow = new ImageView(context);
            //TODO
            shadow.setImageResource(android.R.mipmap.sym_def_app_icon);

            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(shadow, 1, layoutParams);
        }

        slidingLayout = (RelativeLayout) getChildAt(0);
        slidingLayout.setClickable(true);
        homeLayout = (HomeLayout) getChildAt(IS_SHOW_SHADOW ? 2 : 1);
        homeLayout.setSlidingLayout(this);
        homeLayout.setClickable(true);
    }
    //detector when size changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = slidingLayout.getMeasuredWidth();
        height = slidingLayout.getMeasuredHeight();
        distance = (int) (width * 0.8f);
    }
    //detector laod layout
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        slidingLayout.layout(0, 0, width, height);
        homeLayout.layout(leftDistance, 0, leftDistance + width, height);
    }
    //capture touch event
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev) && gestureDetectorCompat.onTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            viewDragHelper.processTouchEvent(event);
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    //calculate the scroll speed even there're accelerate exist but not stop the animation
    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //sliding animation
    private void SlidingAnimate(float slidingRate){
        float rate = 1 - slidingRate * 0.5f;

        //set the distance
        ViewHelper.setTranslationX(slidingLayout, -slidingLayout.getWidth() / 2.5f + slidingLayout.getWidth() / 2.5f * slidingRate);
        //set the shadow effect
        if(IS_SHOW_SHADOW){
            ViewHelper.setScaleX(shadow, rate * 1.2f * (1 - slidingRate * 0.10f));
            ViewHelper.setScaleY(shadow, rate * 1.85f * (1 - slidingRate * 0.10f));
        }
    }

    //dispatch the drag event
    private void dispatchDragEvent(int leftDistance){
        if(dragListener == null){
            return;
        }

        //the release-rate when drag-event dispatch
        float slidingRate = leftDistance / (float) distance;
        SlidingAnimate(slidingRate);

        //reset the status
        Status lastStatus = status;
        if(lastStatus != getStatus() && status == Status.CLOSE){
            dragListener.onClose();
        } else if (lastStatus != getStatus() && status == Status.OPEN) {
            dragListener.onOpen();
        }
    }

    //sliding open-close
    public void SlidingOpen(){
        Open(true);
    }
    public void Open(boolean animate){
        if(animate){
            if(viewDragHelper.smoothSlideViewTo(homeLayout, distance, 0)){
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            homeLayout.layout(distance, 0, distance * 2, height);
        }
    }
    public void SlidingClose(){
        Close(true);
    }
    public void Close(boolean animate){
        if(animate){
            if(viewDragHelper.smoothSlideViewTo(homeLayout, 0, 0)){
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            homeLayout.layout(0, 0, width, height);
            dispatchDragEvent(0);
        }
    }

    //self-functions
    public ViewGroup getHomeLayout() {
        return homeLayout;
    }
    public ViewGroup getSlidingLayout() {
        return slidingLayout;
    }
}

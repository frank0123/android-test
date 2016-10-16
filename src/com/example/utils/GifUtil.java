package com.example.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import com.example.myandroid.R;

public class GifUtil extends View {
    private Context context;

    private Movie mMovie;
    private int mMovieResourceId;                               //id of movie resource
    private long mMovieStart;                                   //time of the movie play
    private int mCurrentAnimationTime = 0;
    private static final int DEFAULT_MOVIE_DURATION = 1000;     //gif play frame second

    private float mTop;
    private float mLeft;
    private float mScale;

    private int mMeasuredMovieWidth;
    private int mMeasuredMovieHeight;

    private boolean mVisible = true;
    private volatile boolean mPaused = false;

    //constructor
    public GifUtil(Context context){
        this(context, null);
    }
    public GifUtil(Context context, AttributeSet attributeSet){
        this(context, attributeSet, R.styleable.CustomTheme_gifViewStyle);
    }
    public GifUtil(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
        setViewAttributes(context, attributeSet, defStyle);
    }

    //initial the arguments of constructor
    private void setViewAttributes(Context context, AttributeSet attributeSet, int defStyle){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //get the value of gif from the gif-file, and create a movie instance
        final TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.GifView, defStyle, R.style.Widget_GifView);
        mMovieResourceId = typedArray.getResourceId(R.styleable.GifView_gif, -1);
        mPaused = typedArray.getBoolean(R.styleable.GifView_paused, false);

        if(mMovieResourceId != -1){
            mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        }
    }

    //initial the gif resource
    public void setMovieResource(int id){
        this.mMovieResourceId = id;
        mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        //set the gif layout
        requestLayout();
    }
    //initial settings of movie
    public void setMovie(Movie movie){
        this.mMovie = movie;
        requestLayout();
    }
    public void setMovieTime(int time){
        mCurrentAnimationTime = time;
        invalidate();
    }
    public Movie getMovie(){
        return mMovie;
    }

    //gif settings
    public void setmPaused(boolean paused){
        this.mPaused = paused;
        if(!paused){
            mMovieStart = android.os.SystemClock.uptimeMillis() - mCurrentAnimationTime;
        }

        invalidate();
    }
    public boolean isPaused(){
        return this.mPaused;
    }

    //override functions
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mMovie != null){
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int maxWidth = MeasureSpec.getSize(widthMeasureSpec);

            float scaleWidth = (float) movieWidth / (float) maxWidth;
            mScale = 1f / scaleWidth;
            mMeasuredMovieWidth = maxWidth;
            mMeasuredMovieHeight = (int) (movieHeight * mScale);

            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f;
        mVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mMovie != null){
            if(!mPaused){
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);

        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    //update the frame of the gif
    private void updateAnimationTime(){
        long curTime = android.os.SystemClock.uptimeMillis();
        //get the first frame of the gif
        if(mMovieStart == 0){
            mMovieStart = curTime;
        }
        //a complete duration
        int gifDuration = mMovie.duration();
        //exception
        if(gifDuration == 0 ){
            gifDuration = DEFAULT_MOVIE_DURATION;
        }
        //calculate the current frame
        mCurrentAnimationTime = (int) ((curTime - mMovieStart) % gifDuration);
    }
    //draw the frame
    private void drawMovieFrame(Canvas canvas){
        mMovie.setTime(mCurrentAnimationTime);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScale, mScale);
        mMovie.draw(canvas, mLeft / mScale, mTop / mScale);

        canvas.restore();
    }
    //invalidate view
    private void invalidateView(){
        if(mVisible){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }
}

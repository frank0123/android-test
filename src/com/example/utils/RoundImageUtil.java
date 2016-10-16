package com.example.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.example.myandroid.R;

public class RoundImageUtil extends ImageView {
    private Context mContext;

    private int defaultColor = 0xffffff;
    private int defaultWidth = 0;
    private int defaultHeight = 0;
    private int roundBorderThickness = 0;
    private int roundBorderInsideColor = 0;
    private int roundBorderOutsideColor = 0;

    //constructor
    public RoundImageUtil(Context context){
        super(context);
        this.mContext = context;
    }
    public RoundImageUtil(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        this.mContext = context;
        //initial arguments of constructor
        setCustomAttributes(attributeSet);
    }
    public RoundImageUtil(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
        this.mContext = context;
        setCustomAttributes(attributeSet);
    }
    //initial arguments of constructor
    private void setCustomAttributes(AttributeSet attributes){
        TypedArray typedArray = mContext.obtainStyledAttributes(attributes, R.styleable.round_image_view);
        this.roundBorderThickness    = typedArray.getDimensionPixelSize(R.styleable.round_image_view_border_thickness, 0);
        this.roundBorderInsideColor  = typedArray.getColor(R.styleable.round_image_view_border_inside_color, defaultColor);
        this.roundBorderOutsideColor = typedArray.getColor(R.styleable.round_image_view_border_outside_color, defaultColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if(drawable == null){return;}
        if(getWidth() == 0 || getHeight() == 0){return;}

        this.measure(0, 0);

        if(drawable.getClass() == NinePatchDrawable.class){return;}

        Bitmap bitmap     = ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        if(defaultWidth == 0){this.defaultWidth = getWidth();}
        if(defaultHeight == 0){this.defaultHeight = getHeight();}

        //draw the circle
        int radius = 0;
        //the out/in-side effects
        if(roundBorderInsideColor != defaultColor && roundBorderOutsideColor != defaultColor){
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * roundBorderThickness;
            //inside circle
            drawCircleBorder(canvas, radius + roundBorderThickness / 2, roundBorderInsideColor);
            //outside circle
            drawCircleBorder(canvas, radius + roundBorderThickness + roundBorderThickness / 2, roundBorderOutsideColor);
        }
        //only out/in-side effect
        else if(roundBorderInsideColor != defaultColor && roundBorderOutsideColor == defaultColor){
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - roundBorderThickness;
            drawCircleBorder(canvas, radius + roundBorderThickness / 2, roundBorderInsideColor);
        }
        else if(roundBorderInsideColor == defaultColor && roundBorderOutsideColor != defaultColor){
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - roundBorderThickness;
            drawCircleBorder(canvas, radius + roundBorderThickness / 2, roundBorderOutsideColor);
        }
        //no effects
        else{
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        }

        Bitmap roundBitmap = getCroppedRoundBitmap(bitmapCopy, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
    }
    //draw out/in-side effects
    private void drawCircleBorder(Canvas canvas, int radius, int color){
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundBorderThickness);

        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

    //get the picture after cut by circle type
    public Bitmap getCroppedRoundBitmap(Bitmap bitmap, int radius){
        Bitmap scaledSourceBitmap;
        int diameter = radius * 2;

        //get the biggest square part in the central of the rectangle picture, to avoid out of shape
        int bitmapWidth  = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int squareWidth, squareHeight = 0;
        //regard the top-left-corner as the begging cut point
        int x, y = 0;
        Bitmap squareBitmap;

        //height is larger than width
        if(bitmapWidth > bitmapHeight){
            squareWidth = squareHeight = bitmapHeight;
            x = (bitmapWidth - bitmapHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bitmap, x, y, squareWidth, squareHeight);
        }
        //width is larger than height
        else if(bitmapWidth < bitmapHeight){
            squareWidth = squareHeight = bitmapWidth;
            x = 0;
            y = (bitmapHeight - bitmapWidth) / 2;
            squareBitmap =  Bitmap.createBitmap(bitmap, x, y, squareWidth, squareHeight);
        }
        else{
            squareBitmap = bitmap;
        }

        //add the scale function
        if(squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter){
            scaledSourceBitmap = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        }
        else{
            scaledSourceBitmap = squareBitmap;
        }

        //draw and render the picture
        Bitmap output = Bitmap.createBitmap(scaledSourceBitmap.getWidth(), scaledSourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0 , scaledSourceBitmap.getWidth(), scaledSourceBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSourceBitmap.getWidth() / 2, scaledSourceBitmap.getHeight() / 2, scaledSourceBitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSourceBitmap, rect, rect, paint);

        //release the resources
        bitmap = null;
        squareBitmap = null;
        scaledSourceBitmap = null;

        return output;
    }
}

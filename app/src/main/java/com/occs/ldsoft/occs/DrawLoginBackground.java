package com.occs.ldsoft.occs;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by yeliu on 15/7/25.
 */
public class DrawLoginBackground extends View {

    Paint paint = new Paint();
    int width = 100;
    int height = 200;

    public DrawLoginBackground(Context context) {
        super(context);
        init(context);
    }

    public DrawLoginBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawLoginBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        }else{
            width = display.getWidth();
            height = display.getHeight();
        }

        Shader shader = new LinearGradient(0, 0, 0, height, Color.WHITE, Color.rgb(217,217,217), Shader.TileMode.CLAMP);
        paint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(new RectF(0,0,width,height),paint);
    }
}

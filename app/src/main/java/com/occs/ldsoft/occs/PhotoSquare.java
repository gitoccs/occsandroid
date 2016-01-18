package com.occs.ldsoft.occs;

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
 * Created by yeliu on 15/9/9.
 */
public class PhotoSquare extends View {
    Paint paint = new Paint();
    int width = 200;
    int height = 200;

    public PhotoSquare(Context context) {
        super(context);
        init(context);
    }

    public PhotoSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhotoSquare(Context context, AttributeSet attrs, int defStyleAttr) {
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

        paint.setColor(Color.argb(185,255,255,255));
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(new RectF(2,(height - width)/2,width-4,width-4 + (height - width)/2),paint);
    }
}

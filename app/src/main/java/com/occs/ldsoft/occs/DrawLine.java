package com.occs.ldsoft.occs;

/**
 * Created by yeliu on 15/7/29.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawLine extends View {
    Paint paint = new Paint();

    public DrawLine(Context context) {
        super(context);
        paint.setColor(getResources().getColor(R.color.light_light_gray));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(0, 0, 1000, 0, paint);
    }

}
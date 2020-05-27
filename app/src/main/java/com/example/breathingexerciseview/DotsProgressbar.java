package com.example.breathingexerciseview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class DotsProgressbar extends View {

    private Paint paint1;
    float angle = 5;
    float radius = 100;

    public DotsProgressbar(Context context) {
        super(context);
        init();

    }
    public DotsProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DotsProgressbar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
        init();
    }

    public void init(){
        paint1 = new Paint();
        paint1.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);

        int x = getWidth() / 2;
        int y = getHeight() / 2;

        float cx = (float) (x + Math.cos(angle * Math.PI / 180F) * radius);
        float cy = (float) (y + Math.sin(angle * Math.PI / 180F) * radius);
        canvas.drawCircle(cx, cy, 20, paint1);

        startAnimation();
    }


    public void startAnimation(){
        angle = (angle + 1) % 360;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        new Handler().postDelayed(runnable,10);
    }

}
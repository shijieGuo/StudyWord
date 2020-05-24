package com.example.guoshijie.studyword.Dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class loading extends View {
    private int width, height;
    private Paint backCirclePaint;//画背景圆
    private Paint SmallCirclePaint;
    private Paint TextPaint;
    private String text="loading...";
    private ObjectAnimator objectAnimator;

    private Timer timer;
    private TimerTask timerTask;
    private int angle=0;

    public loading(Context context) {
        super(context);
        init(context);
    }

    public loading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        startAnim();

    }

    private void startAnim() {
//        objectAnimator=ObjectAnimator.ofFloat(this,"rotation",width / 2,height / 3);
//        objectAnimator.setDuration(300);
//        objectAnimator.start();
        startTimer();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = width / 2;
        int centerY = height / 3;
        float radius = height / 6;
        backCirclePaint = new Paint();
        backCirclePaint.setAntiAlias(true);
        backCirclePaint.setStrokeWidth(height/12);
        backCirclePaint.setColor(Color.parseColor("#e6e9ed"));
        backCirclePaint.setStyle(Paint.Style.STROKE);
        SmallCirclePaint=new Paint();
        SmallCirclePaint.setAntiAlias(true);
//        SmallCirclePaint.setStrokeWidth(height/6);
        SmallCirclePaint.setColor(Color.parseColor("#4fc1e9"));
        SmallCirclePaint.setStyle(Paint.Style.FILL);
        TextPaint = new Paint();
        TextPaint.setAntiAlias(true);
        TextPaint.setColor(Color.parseColor("#656d78"));
        TextPaint.setTextSize(height/6);
        Rect textRect = new Rect();
        TextPaint.getTextBounds(text, 0, text.length(), textRect);
        canvas.drawText(text, width / 2 - textRect.width() / 2, height / 4*3 + textRect.height() / 2, TextPaint);
        canvas.drawCircle(centerX, centerY, radius, backCirclePaint);
        canvas.drawCircle((float) (centerX+radius*Math.sin(Math.toRadians(angle))), (float) (centerY-radius*Math.cos(Math.toRadians(angle))), height/24, SmallCirclePaint);
    }

    private void startTimer()
    {
        timerTask=new TimerTask() {
            @Override
            public void run() {
                angle=angle+15;
                if(angle==360)
                    angle=0;
                invalidate();
            }
        };
        timer=new Timer();
        timer.schedule(timerTask,0,100);
    }
}

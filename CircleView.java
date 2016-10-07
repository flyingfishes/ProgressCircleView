package com.pdm.progresscircleview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * Author:pdm on 2016/9/23 9:09
 * Email:aiyh0202@163.com
 */
public class CircleView extends View {
    public CircleView(Context context) {
        super(context);
        startTime();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        startTime();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 1、绘制圆形（圆心的位置，半径，画笔）
         */
        canvas.drawCircle(0,0,100,paint);
        /**
         *  2、绘制矩形
         */
        RectF rect = new RectF(50, 50, 200, 200);
        canvas.drawRect(rect, paint);
        /**
         *  3、绘制文字,矩形区域，横坐标偏移量，纵坐标偏移量
         */
        canvas.translate(100,100);
        Paint textPaint = new Paint(paint);
        textPaint.setTextSize(25);
        textPaint.setStrokeWidth(1);
        textPaint.setColor(Color.RED);
        //这里的path指的是绘制的路径
        Path path = new Path();
        path.addArc(new RectF(0,0,200,200),-180,180);
        //hOffset   距离路径开始的距离
        //vOffset   离路径线的上下高度
        canvas.drawTextOnPath("http://www.pdm888.com",path,0,0,textPaint);
        //4、旋转坐标轴，顺时针，相当于画布逆时针旋转
        canvas.rotate(360);

        initPaint();
        drawWatch(canvas);
        drawTime(canvas);
    }

    private void drawWatch(Canvas canvas) {
        //设置抗锯齿
        paint.setAntiAlias(true);
        //设置画笔类型,这里是普通画笔
        paint.setStyle(Paint.Style.STROKE);
        //注意这一步之后我们的坐标原点处在最中心位置
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2); //将位置移动画纸的坐标点
        canvas.drawCircle(0, 0, 200, paint); //画圆

        //使用path绘制路径文字
        canvas.save();//此刻原点在屏幕中心位置
        //canvas平移x轴-90，y轴160，绘制文字部分
        canvas.translate(-90, -160);
        Path path = new Path();
        path.addArc(new RectF(0, 0, 180, 180), -145, 145);
        Paint textPaint = new Paint(paint);
        textPaint.setTextSize(25);
        textPaint.setStrokeWidth(1);
        String text = "TWE-7300.com";
        canvas.drawTextOnPath(text, path, 0, 0, textPaint);
        canvas.restore();

        float y_Line = 200;
        int count = 60;
        for (int i = 60; i > 0; i--) {
            if (i % 5 == 0) {
                //在底部的位置绘制
                canvas.drawLine(0f, -y_Line, 0f, -(y_Line + 12f), paint);
                if (i == 30) {
                    //这里是为了避免6倒过来，变成了9，因为canvas有旋转
                    canvas.drawText(String.valueOf(9), -6f, -(y_Line + 25f), textPaint);
                } else {
                    canvas.drawText(String.valueOf(i / 5), -6f, -(y_Line + 25f), textPaint);
                }
            } else {
                canvas.drawLine(0f, -y_Line, 0f, -(y_Line + 5f), paint);
            }
            canvas.rotate(-(360 / count), 0f, 0f);//每一次绘制后，旋转坐标轴，再绘制,每次旋转都是相对上一次的位置
        }
        //绘制中心的三层圆形
        paint.setColor(Color.rgb(135, 206, 255));
        paint.setStrokeWidth(12);
//        canvas.drawArc(new RectF(-20, -20, 20, 20), 0, 360, true , paint);
        canvas.drawCircle(0, 0, 20, paint);
        paint.setStrokeWidth(5);
        paint.setColor(Color.rgb(30, 144, 255));
        //设置为实心，画圆
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(0, 0, 15, paint);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(0, 0, 5, paint);
    }

    private void drawTime(Canvas canvas) {
        //时针,这里需要保存旋转前位置，绘制完，再恢复到原来位置
        canvas.save();
        //这里旋转的是坐标轴
        canvas.rotate((hour * 30 + minute / 60f * 30), 0f, 0f);
        canvas.drawLine(0, 0, 0, -140, paint);
        canvas.restore();
        //分针
        canvas.save();
        canvas.rotate((minute * 6 + sec /60f * 6), 0f, 0f);
        canvas.drawLine(0, 0, 0, -190, paint);
        canvas.restore();
        //秒针
        paint.setStrokeWidth(3);
        paint.setColor(Color.rgb(30, 144, 255));
        paint.setAntiAlias(true);
        canvas.save();
        canvas.rotate((sec * 6), 0f, 0f);
        canvas.drawLine(0, 30, 0, -190, paint);
        canvas.restore();
    }

    public void startTime() {
        handler.post(run);
    }

    private Calendar calendar = null;
    Handler handler = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            invalidate();
            handler.removeCallbacks(run);
            handler.postDelayed(run, 1000);
        }
    };

    private int sec = 0;
    private int minute = 0;
    private int hour = 0;

    private Paint paint;

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.rgb(30, 144, 255));
        paint.setStrokeCap(Paint.Cap.ROUND);//设置笔触风格为圆角
        paint.setStrokeJoin(Paint.Join.ROUND);//设置接洽处的风格为圆角
        paint.setStrokeWidth(3);
    }
}

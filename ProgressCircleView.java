package com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sideslip_qq.R;

/**
 * Author:pdm on 2016/9/26 9:45
 * Email:aiyh0202@163.com
 * 水波纹进度条,相对比较完善,适合所有布局方式
 * 双击重置进度,单击开始、暂停进度
 * 通过setCurrentProgress(int currentProgress)进行进度更新，已有刷新操作
 * 设置OnProgressStateListener进度条状态监听器,方便使用者针对进度条状态进行相应操作,比如：停止、开始数据传送等
 */
public class ProgressCircleView extends View {
    private Context context;
    private int windowWidth;
    private int windowHeigth;
    //圆半径
    private float radius;
    //圆的颜色
    private int circleColor;
    //进度条的颜色
    private int progressColor;
    //中间百分比字体颜色
    private int centerTextColor;
    //中间百分比字体大小
    private float centerTextSize;
    //水波纹的密度(为了美观，尽可能在20-50之间，理论上没有限制)
    private int progressDensity;
    //圆、进度、字体画笔
    private Paint circlePaint, progressPaint, fontPaint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    //控件本身的宽和高
    private float width, heigth;
    private boolean isPaintInit = false;

    public ProgressCircleView(Context context) {
        super(context);
    }

    public ProgressCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        windowWidth = context.getResources().getDisplayMetrics().widthPixels;
        windowHeigth = context.getResources().getDisplayMetrics().heightPixels;
        getCustomAttribute(context, attrs);
    }

    /**
     * 初始化控件属性值
     *
     * @param context
     * @param attrs
     */
    private void getCustomAttribute(Context context, AttributeSet attrs) {
        //获取自定义属性集
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressCircleView);
        //获取圆半径
        radius = typedArray.getDimension(R.styleable.ProgressCircleView_radius, 200f);
        //获取圆的颜色
        circleColor = typedArray.getColor(R.styleable.ProgressCircleView_circleColor, Color.RED);
        //获取进度颜色
        progressColor = typedArray.getColor(R.styleable.ProgressCircleView_progressColor, Color.GREEN);
        //获取中心显示文字颜色
        centerTextColor = typedArray.getColor(R.styleable.ProgressCircleView_centerTextColor, Color.BLACK);
        //获取中心文字大小
        centerTextSize = typedArray.getDimension(R.styleable.ProgressCircleView_centerTextSize, 30f);
        //获取进度波纹密度
        progressDensity = typedArray.getInt(R.styleable.ProgressCircleView_progressDensity, 20);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取测量模式：EXACTLY:(确切的）AT_MOST :(至多的)UNSPECIFIED:(不确定的)
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //获取测量宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int w, h;
        if (widthMode == MeasureSpec.EXACTLY) {
            w = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            w = (int) Math.min(widthSize, radius * 2);
        } else {
            w = windowWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            h = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            h = (int) Math.min(heightSize, radius * 2);
        } else {
            h = windowHeigth;
        }
        //测量后再确定半径，避免图形不完整
        radius = Math.min(Math.min(w/2, h/2), radius);
        setMeasuredDimension(w, h);
    }

    private int currentProgress = 0;

    public void setClickble(boolean isClick){
        setClickable(isClick);
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * 用于外部使用
     *
     * @param currentProgress
     */
    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();
    }

    private int maxProgress = 100;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化画笔,这里必须在这里初始化画笔，因为radius需要在Measure后才能确定
        if (!isPaintInit) {
            initPaint();
        }
        startDraw(canvas);
    }

    private void startDraw(Canvas canvas) {
        //计算缓冲区画布的宽高，即直径
        width = 2 * radius;
        heigth = width;
        //绘制圆
        bitmapCanvas.drawCircle(width / 2, heigth / 2, radius, circlePaint);
        //绘制贝塞尔曲线
        drawQuad(0);
        //绘制文字，这里需要注意文本基线问题
        String text = currentProgress + "%";
        String centerText = "10%";
        float textWdith = fontPaint.measureText(centerText);
        float x = width / 2 - textWdith / 2;
        //获取文字上坡度(为负数)和下坡度的高度
        Paint.FontMetrics font = fontPaint.getFontMetrics();
        float y = -(font.ascent + font.descent) / 2;
        y = heigth / 2 + y;//下移一部分，确保文字局中
        bitmapCanvas.drawText(text, x, y, fontPaint);

        //计算整个画布canvas的宽高，并将缓冲画布绘制在中心处
        float w = getWidth();
        float h = getHeight();
        float cx = w / 2 - radius;
        float cy = h / 2 - radius;
        canvas.drawBitmap(bitmap, cx, cy, null);

        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(context, new MyGestureDetector());

            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
        setClickable(true);
    }

    /**
     * @param direction 0从左至右，1从右至左(绘制)
     */
    private void drawQuad(int direction) {
        //绘制进度波纹
        path.reset();
        //计算画笔所在的Y坐标值，直径 - 进度移动距离
        float py = (1 - (float) currentProgress / maxProgress) * 2 * radius;
        switch (direction) {
            case 0:
                //向Y轴方向移动画笔（这里为向上）
                path.moveTo(0, py);
                //默认水波纹半径
                float pRadius = 2f * radius / progressDensity;//progressDensity为水波纹的密度
                //水波纹当前半径
                float cRadius = (1 - (float) currentProgress / maxProgress) * pRadius;
                for (int i = 0; i < progressDensity; i++) {
                    //这里是在一条直线上绘制的是上下循环的贝塞尔曲线
                    //下曲线，这里可以去掉,但不去掉会更美观
                    path.rQuadTo(pRadius, cRadius, 2 * pRadius, 0);//绘制贝塞尔曲线，每次绘制相对上一条的位置开始
                    //上曲线
                    path.rQuadTo(pRadius, -cRadius, 2 * pRadius, 0);
                }
                path.lineTo(width, py);
                path.lineTo(width, heigth);
                path.lineTo(0, heigth);
                break;
            case 1:
                //向Y轴方向移动画笔（这里为向上）
                path.moveTo(width, py);
                //默认水波纹半径
                float pRadius1 = 2f * radius / progressDensity;//progressDensity为水波纹的密度
                //水波纹当前半径
                float cRadius1 = (1 - (float) currentProgress / maxProgress) * pRadius1;
                for (int i = 0; i < progressDensity; i++) {
                    //这里是在一条直线上绘制的是上下循环的贝塞尔曲线
                    //下曲线，这里可以去掉,但不去掉会更美观
                    path.rQuadTo(-pRadius1, cRadius1, -2 * pRadius1, 0);//绘制贝塞尔曲线，每次绘制相对上一条的位置开始
                    //上曲线
                    path.rQuadTo(-pRadius1, -cRadius1, -2 * pRadius1, 0);
                }
                path.lineTo(0, heigth);
                path.lineTo(width, heigth);
                path.lineTo(width, py);
                break;
            default:
                break;
        }
        path.close();
        bitmapCanvas.drawPath(path, progressPaint);
    }

    Path path = new Path();

    private void initPaint() {
        //圆的画笔
        circlePaint = new Paint();
        circlePaint.setColor(circleColor);
        circlePaint.setAntiAlias(true);//抗锯齿

        //进度的画笔
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);
        //PorterDuff.Mode.SRC_IN:上下层都显示。下层居上显示。(后来居上)
        //PorterDuff.Mode.DST_IN:取两层绘制交集。显示上层。
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //字体画笔
        fontPaint = new Paint();
        fontPaint.setTextSize(centerTextSize);
        fontPaint.setColor(centerTextColor);
        fontPaint.setAntiAlias(true);
        //设置字体为粗体
        fontPaint.setFakeBoldText(true);

        //初始化缓冲区位图画布
        bitmap = Bitmap.createBitmap((int) radius * 2, (int) radius * 2, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);

        isPaintInit = true;
    }

    private GestureDetector gestureDetector;
    private boolean isStart = false;
    private boolean isPause = false;

    //手势监听,触摸监听
    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        //双击事件
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //重置
            getHandler().removeCallbacks(run);
            run = null;
            currentProgress = 0;
            invalidate();
            isStart = false;
            isPause = false;
            return super.onDoubleTap(e);
        }
        //单击事件
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (currentProgress != 100) {
                if (!isStart) {
                    startProgress();
                    isStart = true;
                }if (!isPause){//开始
                    if (listener != null){//当外部设置监听器时，由用户控制进度
                        listener.onState(true);
                    }else {//当外部未设置监听器时，显示默认效果
                        getHandler().postDelayed(run,100);
                        isPause = true;
                    }
                }else {//暂停
                    if (listener != null){
                        listener.onState(false);
                    }else {
                        getHandler().removeCallbacks(run);
                        isPause = false;
                    }
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void startProgress() {
        if (run == null) {
            run = new Runnable() {
                @Override
                public void run() {
                    if (currentProgress < maxProgress) {
                        invalidate();
                        currentProgress++;
                        getHandler().removeCallbacks(run);
                        getHandler().postDelayed(run, 100);
                    } else {
                        getHandler().removeCallbacks(run);
                    }
                }
            };
        }
    }

    private Runnable run = null;
    //设置进度条状态监听接口
    public interface OnProgressStateListener{
        void onState(boolean state);

    }
    private OnProgressStateListener listener;
    public void setOnProgressStateListener(OnProgressStateListener listener){
        this.listener = listener;
    }


}

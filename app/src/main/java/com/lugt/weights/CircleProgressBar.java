package com.lugt.weights;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleProgressBar extends View {

    private int progress = 0;
    private int maxProgress = 100;

    //绘图的Paint
    private Paint pathPaint;
    private Paint fillPaint;
    //绘图的矩形区域
    private RectF oval;

    private int[] arcColors = {0xFF02C016, 0xFF3DF346, 0xFF40F1D5, 0xFF02C016};
    //灰色轨迹
    private static final int PATH_COLOR = 0xFFF0EEDF;
    //边框灰色
    private int pathBorderColor = 0xFFD2D1C4;
    //环的路径宽度
    private int pathWidth = 35;
    private int width;
    private int height;
    //圆的半径
    private int radius = 120;
    //梯度渲染
    private SweepGradient sweepGradient;
    //重置
    private boolean reset = false;


    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化绘制
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);//开启抗锯齿
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除抗锯齿带来的CPU消耗
        pathPaint.setStyle(Paint.Style.STROKE);//样式设置
        pathPaint.setDither(true);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);//开启抗锯齿
        fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除抗锯齿带来的CPU消耗
        fillPaint.setStyle(Paint.Style.STROKE);//样式设置为中空
        fillPaint.setDither(true);
        fillPaint.setStrokeJoin(Paint.Join.ROUND);

        oval = new RectF();
        sweepGradient = new SweepGradient((float) width / 2, (float) height / 2, arcColors, null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reset) {
            canvas.drawColor(0xFFFFFFF);
            reset = false;
        }
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        radius = getMeasuredWidth() / 2 - pathWidth;
        //设置背景颜色
        pathPaint.setColor(PATH_COLOR);
        //设置画笔宽度
        pathPaint.setStrokeWidth(pathWidth);
        //绘制背景
        canvas.drawCircle((float) width / 2, (float) height / 2, radius, pathPaint);

        pathPaint.setStrokeWidth(0.5f);
        pathPaint.setColor(pathBorderColor);
        canvas.drawCircle((float) width / 2, (float) height / 2, (float) (radius + pathWidth / 2) + 0.5f, pathPaint);
        canvas.drawCircle((float) width / 2, (float) height / 2, (float) (radius + pathWidth / 2) - 0.5f, pathPaint);
        fillPaint.setShader(sweepGradient);
        fillPaint.setStrokeCap(Paint.Cap.ROUND);
        fillPaint.setStrokeWidth(pathWidth);
        oval.set((float) (width / 2 - radius), (float) (height / 2 - radius),
                (float) (width / 2 + radius), (float) (height / 2 + radius));
        canvas.drawArc(oval, -90.0f, ((float) progress / (float) maxProgress) * 360.0f, false, fillPaint);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    public int getPathBorderColor() {
        return pathBorderColor;
    }

    public void setPathBorderColor(int pathBorderColor) {
        this.pathBorderColor = pathBorderColor;
    }

    public int getPathWidth() {
        return pathWidth;
    }

    public void setPathWidth(int pathWidth) {
        this.pathWidth = pathWidth;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
        if (reset) {
            progress = 0;
            invalidate();
        }
    }
}

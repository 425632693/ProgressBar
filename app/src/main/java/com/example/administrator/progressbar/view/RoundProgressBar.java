package com.example.administrator.progressbar.view;

/**
 * Created by Administrator on 2017/6/25.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.example.administrator.progressbar.R;

/**
 * 自定义圆形进度条的自定义控件的类
 *      只定义了一个圆的半径的属性,其他属性都继承了水平进度条的属性
 */
public class RoundProgressBar extends HorizontalProgressbar{

    private int mRadius = dp2px(30);//声明一个半径
    private int mMaxPaintWidth;//画笔的宽度

    public RoundProgressBar(Context context) {
        this(context,null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 获取自定义属性
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //没什么实际意义
        mReachHeight = (int) (mUnReachHeight * 2.5f);
        //获取自定义的属性的总xml
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        //获取自定义属性的id,第二个参数为默认值
        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBar_radius,mRadius);

        //清空TypedArray资源
        ta.recycle();

        //定义画笔
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }


    /**
     * 测量位置
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算圆的最大宽度(圆的直径)
        mMaxPaintWidth = Math.max(mReachHeight,mUnReachHeight);
        //默认设置四个padding一致(计算半径的值)
        int expect = mRadius*2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
        //测量宽和高(根据不同的模式传入不同的值)
        int width = resolveSize(expect,widthMeasureSpec);
        int height = resolveSize(expect,heightMeasureSpec);

        int readWidth = Math.min(width,height);//圆的最大宽度(总宽度)
        //计算圆的半径
        mRadius = (readWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth)/2;
        //决定当前view视图的大小并且放到ScrollView中(如果没有这个方法ScrollView不会生效)
        setMeasuredDimension(readWidth,readWidth);


    }

    /**
     * 开始绘制
     * @param canvas
     * ascent是指从一个字的基线(baseline)到最顶部的距离，descent是指一个字的基线到最底部的距离
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress()+"%";
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent()+mPaint.ascent())/2;

        //保存画笔
        canvas.save();

        canvas.translate(getPaddingLeft()+mMaxPaintWidth/2,getPaddingTop()+mMaxPaintWidth/2);
        //定义画笔
        mPaint.setStyle(Paint.Style.STROKE);
        //绘制未加载的圆
        mPaint.setColor(mUnReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        //绘制一个圆(三个参数分别是:1.圆的半径  2.圆的半径  3.圆的半径  4.画笔)
        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);

        //绘制加载后的圆
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        //绘制一个圆(三个参数分别是:1.圆的半径  2.圆的半径  3.圆的半径  4.画笔)
//        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
        //计算圆的弧度
        float sweepAngle = getProgress()*1.0f/getMax()*360;
        //根据圆心绘制一个圆的弧度
        canvas.drawArc(new RectF(0,0,mRadius*2,mRadius*2),0,sweepAngle,false,mPaint);
        //绘制文本
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text,mRadius - textWidth/2,mRadius - textHeight,mPaint);

        //刷新画笔
        canvas.restore();
    }
}

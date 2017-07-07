package com.example.administrator.progressbar.view;

/**
 * Created by Administrator on 2017/6/25.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.example.administrator.progressbar.R;

/**
 * 自定义水平进度条的自定义控件类
 */
public class HorizontalProgressbar extends ProgressBar{

    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0xFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOLR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp


    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnReachColor = DEFAULT_COLOR_UNREACH;
    protected int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);//unReach是可达到的意思(未加载)
    protected int mReachColor = DEFAULT_COLOLR_REACH;           //reach是到达的意思(已加载)
    protected int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    protected int mTextOffset = dp2px(DEFAULT_TEXT_SIZE);

    protected Paint mPaint = new Paint();

    //记录进度条的宽度
    protected int mRealWidth;


    public HorizontalProgressbar(Context context) {
        this(context,null);
    }

    public HorizontalProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }


    public HorizontalProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainStyledAttrs(attrs);


    }


    /**
     * 获取自定义属性
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs) {
        //第二个值为自定义属性的结果集
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressbar);

        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressbar_progress_text_sixe,mTextSize);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressbar_progress_text_color,mTextColor);
        mTextOffset = (int) ta.getDimension(R.styleable.HorizontalProgressbar_progress_text_offset,mTextOffset);
        mUnReachColor = ta.getColor(R.styleable.HorizontalProgressbar_progress_unreach_color,mUnReachColor);
        mUnReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressbar_progress_reach_height,mUnReachHeight);
        mReachColor = ta.getColor(R.styleable.HorizontalProgressbar_progress_reach_color,mReachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressbar_progress_reach_height,mReachHeight);

        //格式化代码
        ta.recycle();
        //设置字体的大小
        mPaint.setTextSize(mTextSize);
    }

    /**
     * 控件的测量
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//获取宽度的模式
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);//获取宽度的值

        //获取高度的模式和值
        int height = measureHeight(heightMeasureSpec);

        //确定view的宽和高
        setMeasuredDimension(widthVal,height);

        //计算出进度条的宽度并赋值给变量
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

    }

    /**
     * 计算绘制的三种模式
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;//获取最后的结果
        //获取高度的模式
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        //获取高度的值
        int size = MeasureSpec.getSize(heightMeasureSpec);
        //判断高度的模式如果等于用户给的精确的值,直接把值赋给结果变量    (EXACTLY)确定的值,直接传入父控件的值
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }else{  //另外两种模式(UNSPECIFIED)测量直接所期望的值,(AT_MOST),需要自己测量尺寸,最大值不能超过父控件传给的值
            //计算文字的高度
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            //前两个值是必须加的(上边距和下边距) 文本在进度条的中间
            result = getPaddingTop()+getPaddingBottom()
                    //先获取(mReachHeight),(mUnReachHeight)两个参数的最大值,然后再加上(textHeight) 计算一下最大值
                    +Math.max(Math.max(mReachHeight,mUnReachHeight),Math.abs(textHeight));
            //测量的值不能超过给的的值(size)
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }

        }

        return result;
    }

    /**
     * 绘制进度条
     * @param canvas
     * ascent是指从一个字的基线(baseline)到最顶部的距离，descent是指一个字的基线到最底部的距离
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        float radio = 0;
        //锁定画布(为了保存之前画布的状态)
        canvas.save();
        //把当前画布的原点移动到指定位置
        canvas.translate(getPaddingLeft(),getHeight()/2);//移动到画布左边中间的位置(getHeight()/2:垂直高度的一半)
        //声明一个boolean值来决定是否需要绘制UnRech部分(也就是未加载到的部分)
        boolean noNeedUnRech = false;
        //获取进度条上的文本
        String text = getProgress()+"%";
        //计算文本的宽度
        int textWidth = (int) mPaint.measureText(text);
        //计算当前已加载的进度条的百分比
        float radio = getProgress()*1.0f/getMax();
        //获取光进度条的总宽度
        float progressX = radio * mRealWidth;
        //如果进度条和文本的宽度之和大于进度条的总宽度的话说明进度条已经加载完了
        if(progressX + textWidth > mRealWidth){
            //调整进度条已加载的最后位置为总宽度减去文本的宽度
            progressX = mRealWidth - textWidth;
            //进度条加载完
            noNeedUnRech = true;
        }
        //进度条的最终宽度(进度条减去文本值的前面的空隙)
        float endX = radio * mRealWidth - mTextOffset/2 - textWidth;
        //总进度条大于0 的时候开始绘制一条线(进度条)
        if(endX>0){
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0,0,endX,0,mPaint);
        }

        //绘制显示的文本
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent())/2);
        canvas.drawText(text,progressX,y,mPaint);

        //绘制文本值后面的进度条(未加载的进度条)
        //如果需要绘制的话
        if(!noNeedUnRech){
            //计算获取进度条的已加载的总进度(进度条已加载的宽 + 文本前面的宽 + 文本的宽)
            float start = progressX + mTextOffset/2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start,0,mRealWidth,0,mPaint);
        }


        canvas.restore();
    }




    /**
     * dp和sp的转换方法
     * @param dpVal
     * @return
     */
    protected int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal,getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal,getResources().getDisplayMetrics());
    }


}

package com.li914.customviews.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.li914.customviews.R;

/**
 * Created by 18377 on 2018-2-20.
 */
/**
 * 模仿QQ底部状态栏图片拖动
 * */

public class NavBottomBarView extends LinearLayout {
    private Context mContext;
    private FrameLayout frameLayout;
    private ImageView mBigIcon,mSmallIcon;
    private TextView nameText;

    private String mName;
    private float mNameSize;
    private int mNameColor;

    /**
     * icon宽度,icon高度,外层icon资源,内层icon资源
     * */
    private float mIconWidth,mIconHeight;
    private int mBigIconSrc,mSmallIconSrc;

    /**
     * 拖动幅度较大半径,拖动幅度小半径,拖动范围 可调
     * */
    private float mBigRadius,mSmallRadius;
    private float mRange;

    private float lastX,lastY;

    public NavBottomBarView(Context context) {
//        super(context);
        this(context,null);
    }

    public NavBottomBarView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
        this(context,attrs,0);
    }

    public NavBottomBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext=context;

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.NavBottomBarView,defStyleAttr,0);
        this.mBigIconSrc=typedArray.getResourceId(R.styleable.NavBottomBarView_mBigIconSrc,0);
        this.mSmallIconSrc=typedArray.getResourceId(R.styleable.NavBottomBarView_mSmallIconSrc,0);
        this.mIconHeight=typedArray.getDimension(R.styleable.NavBottomBarView_mIconHeight,0);
        this.mIconWidth=typedArray.getDimension(R.styleable.NavBottomBarView_mIconWidth,0);
        this.mRange=typedArray.getFloat(R.styleable.NavBottomBarView_mRange,1);
        this.mName=typedArray.getString(R.styleable.NavBottomBarView_mName);
        this.mNameSize=typedArray.getDimension(R.styleable.NavBottomBarView_mNameSize,0);
        this.mNameColor=typedArray.getColor(R.styleable.NavBottomBarView_mNameColor,0);
        typedArray.recycle();

        //默认垂直排列
        setOrientation(LinearLayout.VERTICAL);
        //设置可传递move事件
        setClickable(true);

        initView(context);
    }

    private void initView(Context context) {
        frameLayout=new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams= new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        frameLayout.setLayoutParams(layoutParams);

        mBigIcon=new ImageView(context);
        mSmallIcon=new ImageView(context);

        frameLayout.addView(mBigIcon);
        frameLayout.addView(mSmallIcon);

        mSmallIcon.setImageResource(mSmallIconSrc);
        mBigIcon.setImageResource(mBigIconSrc);

        this.setWidthAndHeight(mBigIcon);
        this.setWidthAndHeight(mSmallIcon);

        LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity=Gravity.CENTER_HORIZONTAL;
        frameLayout.setLayoutParams(lp);

        addView(frameLayout);

        nameText=new TextView(context);
        nameText.setGravity(Gravity.CENTER);
        nameText.setTextColor(mNameColor);
        nameText.setTextSize(px2sp(context,mNameSize));
        nameText.setText(mName);
        addView(nameText);

    }

    /**
     * 设置icon的宽度高度
     * @param view
     * */
    private void setWidthAndHeight(View view){
        FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)view.getLayoutParams();
        lp.height=(int)mIconHeight;
        lp.width=(int)mIconWidth;
        view.setLayoutParams(lp);
    }

    /**
     * 确定imageview以及拖动相关参数
     * */
    private void setUpView(){

        //根据imageview的宽度高度确定可拖动半径的大小
        mSmallRadius=0.1f*Math.min(frameLayout.getMeasuredWidth(),frameLayout.getMeasuredHeight())*mRange;
        mBigRadius=1.2f*mSmallRadius;

        //设置imageview的padding，不然拖动时图片边缘部分会消失
        int padding =(int)mBigRadius;
        mBigIcon.setPadding(padding,padding,padding,padding);
        mSmallIcon.setPadding(padding,padding,padding,padding);

    }

    private void measureDimension(int widthMeasureSpec,int heightMeasureSpec){
        int sizeWidth=MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight=MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth=MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight=MeasureSpec.getMode(heightMeasureSpec);
        int width=0,height=0;
        for (int i=0;i<getChildCount();++i){
            View chlid=getChildAt(i);
            if (chlid.getVisibility()!=View.GONE){
                measureChild(chlid,widthMeasureSpec,heightMeasureSpec);
                LayoutParams lp=(LayoutParams)chlid.getLayoutParams();
                int chlidWidth=chlid.getMeasuredWidth()-(lp.leftMargin+lp.rightMargin);
                int chlidHeight=chlid.getMeasuredHeight()-(lp.topMargin+lp.bottomMargin);
                width+=chlidWidth;
                height+=chlidHeight;
            }
        }
        width-=(getPaddingLeft()+getPaddingRight());
        height-=(getPaddingTop()+getPaddingBottom());
        setMeasuredDimension((modeWidth==MeasureSpec.EXACTLY)?sizeWidth:width,
                (modeHeight==MeasureSpec.EXACTLY)?sizeHeight:height);
    }

    private void moveEvent(View view,float deltaX,float deltaY,float radius){
        //计算拖动的距离
        float distance=getDistanse(deltaX,deltaY);

        //拖动的方位角,
        double degree=Math.atan2(deltaY,deltaX);

        //如果大于临界半径 就不再拖动
        if (distance>radius){
            view.setX(view.getLeft()+(float)(radius*Math.cos(degree)));
            view.setY(view.getTop()+(float)(radius*Math.sin(degree)));
        }else {
            view.setX(view.getLeft()+deltaX);
            view.setY(view.getTop()+deltaY);
        }
    }

    private float getDistanse(float deltaX, float deltaY) {
        return (float) Math.sqrt(deltaX*deltaX+deltaY*deltaY);
    }

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(1, dpVal, context.getResources().getDisplayMetrics());
    }

    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public void setmBigIcon(int src){
        this.mBigIcon.setImageResource(src);
    }

    public void setmSmallIcon(int src){
        this.mSmallIcon.setImageResource(src);
    }

    public void setNameTextColor(int color){
        nameText.setTextColor(mContext.getResources().getColor(color));
    }

    public void setmRange(float range){
        this.mRange=range;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setUpView();
        measureDimension(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int chlidLeft,chlidTop=0;
        for (int i=0;i<getChildCount();++i){
            View chlid=getChildAt(i);
            LayoutParams lp=(LayoutParams)chlid.getLayoutParams();
            if (chlid.getVisibility()!=View.GONE){
                int chlidWidth=chlid.getMeasuredWidth();
                int chlidHeight=chlid.getMeasuredHeight();
                chlidLeft=(getWidth()-chlidWidth)/2;

                chlidTop-=lp.topMargin;
                chlid.layout(chlidLeft,chlidTop,chlidLeft+chlidWidth,chlidTop+chlidHeight);
                chlidTop+=chlidHeight-lp.bottomMargin;
//                chlidTop+=chlidHeight;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX=x;
                lastY=y;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX=x-lastX;
                float deltaY=y-lastY;

                moveEvent(mBigIcon,deltaX,deltaY,mSmallRadius);
                moveEvent(mSmallIcon,1.2f*deltaX,1.2f*deltaY,mBigRadius);

                break;
            case MotionEvent.ACTION_UP:
                mBigIcon.setX(0);
                mBigIcon.setY(0);
                mSmallIcon.setX(0);
                mSmallIcon.setY(0);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}

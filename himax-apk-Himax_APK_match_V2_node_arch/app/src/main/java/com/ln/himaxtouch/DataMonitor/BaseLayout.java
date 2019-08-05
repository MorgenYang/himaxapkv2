package com.ln.himaxtouch.DataMonitor;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.RelativeLayout;

/**
 * Created by 903622 on 2018/6/27.
 */

public class BaseLayout extends RelativeLayout {

    private float mDownX;
    private float mDownY;
    private int mWidth;
    private int mHeight;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private boolean isZooming = false;
    public boolean isEnableDrag = false;
    public boolean isEnableBlack = false;

    public float mScale = 1;
    private float mPreScale = 1;
    public int mLeftP = 0;
    public int mRightP = mLeftP + getWidth();
    public int mTopP = 0;
    public int mBottomP = mTopP + getHeight();

    public boolean isMoved = false;

    public BaseLayout(Context context) {
        super(context);

        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureListener());
    }

    public BaseLayout(Context context, AttributeSet attrs) {
       super(context, attrs);
        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureListener());
    }

    public BaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureListener());
    }

    public BaseLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureListener());
    }

    public void resumeScaleAndPosition() {
        setScaleX(1);
        setScaleY(1);
        this.layout(0, 0, mWidth, mHeight);
    }

    public void setPreviousScaleAndPosition(float scale, int left, int top, int right, int bottom) {
        this.setScaleX(scale);
        this.setScaleY(scale);
        mScale = scale;
        mPreScale = scale;
        mLeftP = left;
        mTopP = top;
        mRightP = right;
        mBottomP = bottom;
    }

    public void resumeOriginPosition() {
        if(this.getScaleX() != 1) {
            if(mLeftP != this.getLeft() || mTopP != this.getTop() || mRightP != this.getRight() || mBottomP != this.getBottom()) {
                this.layout(mLeftP,mTopP,mRightP,mBottomP);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (isEnableDrag) {
            if (event.getPointerCount() > 1) {
                isZooming = true;
                return mScaleGestureDetector.onTouchEvent(event);
            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isZooming) {
                            mDownX = event.getX();
                            mDownY = event.getY();
                            mWidth = getWidth();
                            mHeight = getHeight();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isZooming) {
                            float scale = getScaleX();
                            final float xDistance = (event.getX() - mDownX)*scale;
                            final float yDistance = (event.getY() - mDownY)*scale;
                            if (xDistance != 0 && yDistance != 0) {
                                mLeftP = (int) (getLeft() + xDistance);
                                mRightP = mLeftP + mWidth;
                                mTopP = (int) (getTop() + yDistance);
                                mBottomP = mTopP + mHeight;
                                checkLayoutMargin(scale);
                                this.layout(mLeftP, mTopP, mRightP, mBottomP);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isZooming) {
                            isZooming = false;
                        }
                        setPressed(false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (isZooming) {
                            isZooming = false;
                        }
                        setPressed(false);
                        break;
                }
            }
            return true;
        }
        return false;
    }


    public class ScaleGestureListener implements
            ScaleGestureDetector.OnScaleGestureListener {

        private final double MAXSCALE = 1;
        private final double MINSCALE = 0.5;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float previousSpan = detector.getPreviousSpan();
            float currentSpan = detector.getCurrentSpan();
            if (currentSpan < previousSpan) {
                // 缩小
                float temp = mPreScale - (previousSpan - currentSpan) / 3000;
                if(temp >= MINSCALE) {
                    mScale = temp;
                } else {
                    mScale = (float)  MINSCALE;
                }
            } else {
                // 放大
                float temp = mPreScale - (previousSpan - currentSpan) / 3000;
                if(temp <= MAXSCALE) {
                    mScale = temp;
                } else {
                    mScale = (float) MAXSCALE;
                }
            }

            // 缩放view
            setScaleX(mScale);// x方向上缩小
            setScaleY(mScale);// y方向上缩小

            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // 一定要返回true才会进入onScale()这个函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mPreScale = mScale;//记录本次缩放比例
        }
    }

    private void checkLayoutMargin(float scale) {
        //horizontal margin
        int marginW = (int) (getWidth() * (1-scale) / 2);
        if(mLeftP < -marginW) {
            mLeftP = -marginW;
            mRightP = mLeftP + getWidth();
        }
        if(mRightP > marginW + getWidth()) {
            mRightP = marginW + getWidth();
            mLeftP = mRightP - getWidth();
        }

        //vertical margin
        int maringH = (int) (getHeight() * (1-scale) / 2);
        if(mTopP < -maringH) {
            mTopP = -maringH;
            mBottomP = mTopP + getHeight();
        }
        if(mBottomP > maringH + getHeight()) {
            mBottomP = maringH + getHeight();
            mTopP = mBottomP -getHeight();
        }


    }
}

package io.luciferldy.bnimation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by lian_ on 2017/10/14.
 * 水纹扩散效果，实际为一个圆圈有小变大的过程
 */

public class RippleView extends View {

    private int mCenterX;
    private int mCenterY;
    private int mRadius;
    private float mFraction = 0;
    private Paint mPaint;
    private ValueAnimator mAnimator;
    private Callback callback;

    public RippleView(Context context) {
        super(context);
        init();
    }

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final int strokeWidth = 8;
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#D4583D"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mCenterX = getWidth()/2;
                mCenterY = getHeight()/2;
                mRadius = mCenterX > mCenterY ? mCenterY : mCenterX;
                mRadius -= strokeWidth; // 剪掉边框宽度
                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        mAnimator = ValueAnimator.ofFloat(0, 1).setDuration(300);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mFraction = (float) valueAnimator.getAnimatedValue();
                if (callback != null) {
                    callback.update(mFraction);
                }
                invalidate();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (callback != null) {
                    callback.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null) {
                    callback.end();
                    callback = null;
                }
            }
        });
    }

    public void start(Callback callback) {
        this.callback = callback;
        mAnimator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAlpha((int) (255 - 255 * mFraction));
        canvas.drawCircle(mCenterX, mCenterY, mRadius * mFraction, mPaint);
    }

    public interface Callback {
        default void start() {}
        void update(float fraction);
        void end();
    }
}

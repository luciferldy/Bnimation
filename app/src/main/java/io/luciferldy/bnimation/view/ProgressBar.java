package io.luciferldy.bnimation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import io.luciferldy.bnimation.R;
import io.luciferldy.bnimation.utils.CommentUtils;

/**
 * Created by lian_ on 2017/10/18.
 * 模仿彗星形状的 ProgressBar
 */

public class ProgressBar extends View {

    private static final String TAG = ProgressBar.class.getSimpleName();
    // 默认有5层圆圈
    private static final int DEFAULT_COUNT = 5;
    private static final float DEFAULT_STROKEWIDTH = 2;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_ANGLE_OFFSET = 3;
    private Paint mPaint;
    private float mRadius;
    private float mStrokeWidth = 6;
    private int mCx;
    private int mCY;
    private int mCount;
    private int mAngleOffset;
    private int mFromColor;
    private int mToColor;
    private ValueAnimator mValueAnimator;
    private OnUpdateListener mUpdateListener;

    public ProgressBar(Context context) {
        super(context);
        init(null);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        mCount = DEFAULT_COUNT;
        mAngleOffset = DEFAULT_ANGLE_OFFSET;
        mFromColor = Color.BLACK;
        mToColor = Color.WHITE;
        if (attrs != null) {
            TypedArray t = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressBar, 0, 0);
            try {
                mStrokeWidth = t.getDimension(R.styleable.ProgressBar_stroke_width, CommentUtils.dip2px(getContext(), DEFAULT_STROKEWIDTH));
                mFromColor = t.getColor(R.styleable.ProgressBar_from_color, Color.BLACK);
                mToColor = t.getColor(R.styleable.ProgressBar_to_color, Color.WHITE);
                mCount = t.getInt(R.styleable.ProgressBar_count, DEFAULT_COUNT);
                mAngleOffset = t.getInt(R.styleable.ProgressBar_angle_offset, DEFAULT_ANGLE_OFFSET);
                mRadius = t.getDimension(R.styleable.ProgressBar_radius, 0);
            } finally {
                t.recycle();
            }
        }

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mCx = getWidth()/2;
                mCY = getHeight()/2;
                // 扫描是顺时针的
                SweepGradient shader = new SweepGradient(mCx, mCY, mToColor, mFromColor);
                mPaint.setShader(shader);
                float t = mCx > mCY ? mCY-mStrokeWidth : mCx-mStrokeWidth;
                if (mRadius == 0 || mRadius > t) {
                    mRadius = t;
                }
                Log.d(TAG, String.format("onPreDraw width: %d, height: %d, radius: %f", getWidth(), getHeight(), mRadius));

                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        int offset = 0;
        for (int i = 0; i < mCount; i++) {
            canvas.rotate(offset, mCx, mCY);
            float t = mRadius + mStrokeWidth*i;
            Path path = new Path();
            path.addArc(mCx-t, mCY-t, mCx+t, mCY+t, 0, -350);
            canvas.drawPath(path, mPaint);
            offset -= mAngleOffset;
        }
        canvas.restore();
    }

    public void setFromColor(int color) {
        this.mFromColor = color;
        SweepGradient sweep = new SweepGradient(mCx, mCY, mToColor, mFromColor);
        mPaint.setShader(sweep);
        if (getParent() != null)
            invalidate();
    }

    public void setToColor(int color) {
        this.mToColor = color;
        SweepGradient sweep = new SweepGradient(mCx, mCY, mToColor, mFromColor);
        mPaint.setShader(sweep);
        if (getParent() != null)
            invalidate();
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
        if (getParent() != null)
            invalidate();
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void start() {
        if (mValueAnimator == null) {
            mValueAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 360)
                    .setDuration(2000);
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                if (mUpdateListener != null) {
                    mUpdateListener.update(value);
                }
            });
            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mUpdateListener != null)
                        mUpdateListener.end();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    if (mUpdateListener != null)
                        mUpdateListener.start();
                }
            });

        }
        mValueAnimator.start();
    }

    public void end() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.end();
        }
    }

    public void setUpdateListener (OnUpdateListener listener) {
        this.mUpdateListener = listener;
    }

    public interface OnUpdateListener {
        void update(float value);
        void start();
        void end();
    }
}

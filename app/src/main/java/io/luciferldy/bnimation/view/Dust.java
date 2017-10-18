package io.luciferldy.bnimation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lian_ on 2017/10/17.
 * 尘埃，包含了在某一处喷射的所有 {@link Element} 的集合
 */

public class Dust {
    private static final String TAG = Dust.class.getSimpleName();

    private static final int DEFAULT_ELEMENT_COUNT = 12;
    private static final float DEFAULT_ELEMENT_SIZE = 6;
    private static final int DEFAULT_DURATION = 300;
    private static final int DEFAULT_LAUNCH_SPEED = 10;

    private Paint mPaint;
    private int mCount;
    // 由于模仿彗星的效果，尘埃的方向应为尘埃所在位置与轨道的切角左右30度，并与彗星前进方向相反
    private int mDuration;
    private float mLaunchSpeed;
    private float mLocationX;
    private float mLocationY;
    private float mElementSize;
    private float mAngle;
    private ValueAnimator mAnimator;
    private float mAnimatedValue;
    private AnimatorEndListener mListener;

    private ArrayList<Element> elements = new ArrayList<>();

    public Dust(float angle, float cx, float cy, float radius) {

        this.mAngle = angle;
        mCount = DEFAULT_ELEMENT_COUNT;
        mElementSize = DEFAULT_ELEMENT_SIZE;
        mLaunchSpeed = DEFAULT_LAUNCH_SPEED;
        mLocationX = cx + (float) (radius * Math.cos(Math.toRadians(angle)));
        mLocationY = cy + (float) (radius * Math.sin(Math.toRadians(angle)));
        init();
    }

    private void init() {

        // 0~360
        float direction;
        if (mAngle >= 0 && mAngle < 90) {
            // 270~360
            direction = 270+mAngle;
        } else if (mAngle >= 90 && mAngle < 180) {
            // 0~90
            direction = mAngle-90;
        } else if (mAngle >= 180 && mAngle < 270) {
            // 90~180
            direction = mAngle-90;
        } else {
            // 180~270
            direction = mAngle-90;
        }

        Random random = new Random();
        for (int i = 0; i < mCount; i++) {
            // 偏移角度在 -30~30
            float t = random.nextInt(60) - 30;
            t = direction + t;
            if (t > 360) {
                t = t - 360;
            }
            if (t < 0) {
                t = t + 360;
            }
            t = (float) Math.toRadians(t);
            elements.add(new Element(t, mLaunchSpeed * random.nextFloat()));
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);

        mAnimator = ValueAnimator.ofFloat(1, 0);
        mAnimator.setDuration(DEFAULT_DURATION);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addUpdateListener(animator -> {
            mAnimatedValue = (float) animator.getAnimatedValue();
            // 计算每粒尘埃的位置
            for (Element element: elements) {
                element.x += (float) (Math.cos(element.direction) * element.speed) * mAnimatedValue;
                element.y += (float) (Math.sin(element.direction) * element.speed) * mAnimatedValue;
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null)
                    mListener.onAnimatorEnd();
            }
        });
    }

    public void fire() {
        mAnimator.start();
    }

    public void draw(Canvas canvas) {
        mPaint.setAlpha((int) (255 * mAnimatedValue));
        elements.forEach(e ->
                canvas.drawCircle(mLocationX + e.x, mLocationY + e.y, mElementSize, mPaint)
        );
    }

    public void addAnimatorListener(AnimatorEndListener listener) {
        this.mListener = listener;
    }

    interface AnimatorEndListener {
        void onAnimatorEnd();
    }
}

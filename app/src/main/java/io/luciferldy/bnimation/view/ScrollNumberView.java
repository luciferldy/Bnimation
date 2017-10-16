package io.luciferldy.bnimation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.luciferldy.bnimation.R;
import io.luciferldy.bnimation.utils.CommentUtils;

/**
 * Created by lian_ on 2017/10/14.
 * 数字滚动效果
 */

public class ScrollNumberView extends View {

    /**
     * 在滑动前应该先判断前一个数和后一个数是多少，然后选择区域进行滑动
     */
    private static final String TAG = ScrollNumberView.class.getSimpleName();
    private int mNum;
    private String mNumOld;
    private String mNumNew;
    private float mTextSize;
    private float mPaddingStart = 0;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private boolean mScrolling = false;
    private int mScrollingStart;
    private DIRECTION mDirection = DIRECTION.UP;
    private float mFraction = 0;
    private ValueAnimator mAnimator;
    private int mDuration = 600;

    private enum DIRECTION {
        UP,
        DOWN
    }

    public ScrollNumberView(Context context) {
        super(context);
        Log.d(TAG, "1 param");
        init(null);
    }

    public ScrollNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "2 params");
        init(attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        // 也可以用 onPreDrawListener 监听
        Log.d(TAG, "onSizeChanged w " + w + ", height " + h);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        // 使用吸管工具获得的灰色为 #CCCCCC
        mPaint.setColor(Color.GRAY);

        if (attrs != null) {
            TypedArray t = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ScrollNumberView, 0, 0);
            try {
                mTextSize = t.getDimension(R.styleable.ScrollNumberView_text_size, CommentUtils.dip2px(getContext(), 8));
                mPaint.setTextSize(mTextSize);
                mNum = t.getInt(R.styleable.ScrollNumberView_number, 0);
                mNumOld = String.valueOf(mNum);
                mPaddingStart = t.getDimension(R.styleable.ScrollNumberView_padding_start, 0);
                mDuration = t.getInt(R.styleable.ScrollNumberView_duration, 600);
            } finally {
                t.recycle();
            }
        }

        // 每个 [0, 1, 2, 3, ...] 的 measureText 宽度是不一样的
        // 从 mScrollingStart 开始进行翻转

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(mDuration);
        mAnimator.addUpdateListener(animation ->  {
                mFraction = (float) animation.getAnimatedValue();
                invalidate();
            });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mScrolling = false;
                mNumOld = mNumNew;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mScrolling = true;
            }
        });
    }

    public void setNumber(int number) {
        this.mNum = number;
        if (number < 0)
            return;
        mNumOld = String.valueOf(mNum);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();

        float startY = mHeight/2 +  (Math.abs(fontMetrics.ascent - fontMetrics.descent)) / 2;
        float offsetX = mPaddingStart;

        // old number
        float alpha = 1 - mFraction;
        float offset = mPaint.getFontSpacing() * mFraction;
        float[] oldWidth = new float[mNumOld.length()];
        mPaint.getTextWidths(mNumOld, oldWidth);
        for (int i = 0; i < mNumOld.length(); i++) {
            if (mScrolling) {
                if (i < mScrollingStart) {
                    mPaint.setAlpha(255);
                    canvas.drawText(mNumOld, i, i+1, offsetX, startY, mPaint);
                    offsetX += oldWidth[i];
                    continue;
                }

                if (mDirection == DIRECTION.UP) {
                    // 透明度修改
                    mPaint.setAlpha((int) (255 * alpha));
                    // 偏移位置修改
                    canvas.drawText(mNumOld, i, i+1, offsetX, startY - offset, mPaint);
                } else {
                    mPaint.setAlpha((int) (255 * alpha));
                    canvas.drawText(mNumOld, i, i+1, offsetX, startY + offset, mPaint);
                }
                offsetX += oldWidth[i];

            } else {
                mPaint.setAlpha(255);
                canvas.drawText(mNumOld, i, i+1, offsetX, startY, mPaint);
                offsetX += oldWidth[i];
            }
        }

        // new number
        alpha = mFraction;
        offset = mPaint.getFontSpacing() * (1 - mFraction);
        offsetX = mPaddingStart;
        if (mScrolling) {
            float[] newWidth = new float[mNumNew.length()];
            mPaint.getTextWidths(mNumNew, newWidth);
            for (int i = 0; i < mNumNew.length(); i++) {
                // mScrollingStart 之前的数字不用显示
                if (i < mScrollingStart) {
                    offsetX += newWidth[i];
                    continue;
                }


                if (mDirection == DIRECTION.UP) {
                    mPaint.setAlpha((int) (255 * alpha));
                    canvas.drawText(mNumNew, i, i + 1, offsetX, startY + offset, mPaint);
                } else {
                    mPaint.setAlpha((int) (255 * alpha));
                    canvas.drawText(mNumNew, i, i + 1, offsetX, startY - offset, mPaint);
                }
                offsetX += newWidth[i];
            }
        }

    }

    /**
     * 自增1
     */
    public void increment() {
        if (mNum == Integer.MAX_VALUE) {
            return;
        }
        mScrollingStart = 0;
        mNumNew = String.valueOf(++mNum);
        for (int i = 0; i < mNumOld.length(); i++) {
            if (mNumOld.charAt(i) != mNumNew.charAt(i)) {
                mScrollingStart = i;
                break;
            }
        }

        mDirection = DIRECTION.UP;
        mAnimator.start();
    }

    /**
     * 自减1
     */
    public void decrement() {
        if (mNum-1 < 0) {
            return;
        }
        mNumNew = String.valueOf(--mNum);
        mScrollingStart = 0;
        for (int i = 0; i < mNumNew.length(); i++) {
            if (mNumNew.charAt(i) != mNumOld.charAt(i)) {
                mScrollingStart = i;
                break;
            }
        }

        mDirection = DIRECTION.DOWN;
        mAnimator.start();
    }
}

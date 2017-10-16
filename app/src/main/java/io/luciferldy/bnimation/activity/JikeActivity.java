package io.luciferldy.bnimation.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import io.luciferldy.bnimation.R;
import io.luciferldy.bnimation.view.RippleView;
import io.luciferldy.bnimation.view.ScrollNumberView;

/**
 * Created by lian_ on 2017/10/14.
 */

public class JikeActivity extends AppCompatActivity {

    private boolean mSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jike);

        /**
         * 点赞动画应为 like 图标缩小->切换为红色，like 图标增大，水纹动画，shining 动画-> like 图标 over
         */

        final ScrollNumberView messageLikeNumber = (ScrollNumberView) findViewById(R.id.message_like_number);
        messageLikeNumber.setNumber(1989);
        final ImageView likeSelected = (ImageView) findViewById(R.id.like_selected);
        final ImageView shining = (ImageView) findViewById(R.id.like_selected_shining);
        final RippleView rippleView = (RippleView) findViewById(R.id.ripple_view);

        // 点赞与取消赞 like 按钮的 scale 变化 (1.0) -> (0.8) -> (1.0)
        PropertyValuesHolder hx1 = PropertyValuesHolder.ofFloat("scaleX", 0.8f);
        PropertyValuesHolder hy1 = PropertyValuesHolder.ofFloat("scaleY", 0.8f);
        ObjectAnimator oa1 = ObjectAnimator.ofPropertyValuesHolder(likeSelected, hx1, hy1);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa1.setDuration(300);

        PropertyValuesHolder hx2 = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder hy2 = PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator oa2 = ObjectAnimator.ofPropertyValuesHolder(likeSelected, hx2, hy2);
        oa2.setInterpolator(new OvershootInterpolator());
        oa2.setDuration(300);
        oa2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mSelected) {
                    likeSelected.setImageResource(R.drawable.ic_messages_like_selected);
                    rippleView.start(null);
                }
                else
                    likeSelected.setImageResource(R.drawable.ic_messages_like_unselected);
            }
        });

        // shining 图标的 scale 变化 from (0.0) to (1.0)
        PropertyValuesHolder hx3 = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder hy3 = PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator oa3 = ObjectAnimator.ofPropertyValuesHolder(shining, hx3, hy3);
        oa3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                shining.setAlpha(1.0f);
            }
        });
        oa2.setDuration(300);

        final AnimatorSet set1 = new AnimatorSet();
        set1.play(oa2).with(oa3).after(oa1);

        /**
         * 取消赞的动画 like 图标缩小，shining 图标变小，同时变透明->like 图标变为灰色，like 图标放大， shining 图标变大，透明->
         */
        Keyframe k1 = Keyframe.ofFloat(0, 1);
        Keyframe k2 = Keyframe.ofFloat(0.5f, 0.8f);
        Keyframe k3 = Keyframe.ofFloat(1, 1);
        PropertyValuesHolder hx4 = PropertyValuesHolder.ofKeyframe("scaleX", k1, k2, k3);
        PropertyValuesHolder hy4 = PropertyValuesHolder.ofKeyframe("scaleY", k1, k2, k3);

        Animator oa5 = ObjectAnimator.ofPropertyValuesHolder(shining, hx4, hy4);
        oa5.setDuration(300);
        Animator os6 = ObjectAnimator.ofFloat(shining, "alpha", 1, 0);
        os6.setDuration(300);
        final AnimatorSet set2 = new AnimatorSet();
        set2.play(oa1).with(oa5).with(os6).before(oa2);

        findViewById(R.id.message_like).setOnClickListener( v -> {
                mSelected = !mSelected;
                if (mSelected) {
                    set1.start();
                    messageLikeNumber.increment();
                } else {
                    set2.start();
                    messageLikeNumber.decrement();
                }
            });

        findViewById(R.id.ripple).setOnClickListener(v ->
                rippleView.start(new RippleView.Callback() {

                    @Override
                    public void start() {
                        shining.animate().scaleX(1).scaleY(1).alpha(1)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        shining.setScaleX(0);
                                        shining.setScaleY(0);
                                        shining.setAlpha(0.0f);
                                    }
                                })
                                .setDuration(300)
                                .start();
                    }

                    @Override
                    public void update(float fraction) {}

                    @Override
                    public void end() {}
                })
        );
//
//        // 点赞动画
//        Keyframe f1 = Keyframe.ofFloat(0, 1);
//        Keyframe f2 = Keyframe.ofFloat(0.3f, 0.8f);
//        Keyframe f3 = Keyframe.ofFloat(0.6f, 1);
//        Keyframe f4 = Keyframe.ofFloat(0.8f, 1.2f);
//        Keyframe f5 = Keyframe.ofFloat(1, 1f);
//
//        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofKeyframe("scaleX", f1, f2, f3, f4, f5);
//        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofKeyframe("scaleY", f1, f2, f3, f4, f5);
//        final ObjectAnimator select = ObjectAnimator.ofPropertyValuesHolder(likeSelected, scaleXHolder, scaleYHolder).setDuration(600);
//        select.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float value = (float) valueAnimator.getAnimatedValue();
//                Log.d("JikeActivity", "value -> " + value);
//                // 等于 0.8f
//                if (value - 0.3 < 0.01f) {
//                    Log.d("JikeActivity", "0.8f equals value ->" + value);
//                    likeSelected.setImageResource(R.drawable.ic_messages_like_selected);
//                }
//            }
//        });
//
//        select.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
//            }
//        });
//
//        // 取消赞动画
//        Keyframe k1 = Keyframe.ofFloat(0, 1);
//        Keyframe k2 = Keyframe.ofFloat(0.5f, 0.8f);
//        Keyframe k3 = Keyframe.ofFloat(1, 1);
//        PropertyValuesHolder scaleXHolder2 = PropertyValuesHolder.ofKeyframe("scaleX", k1, k2, k3);
//        PropertyValuesHolder scaleYHolder2 = PropertyValuesHolder.ofKeyframe("scaleY", k1, k2, k3);
//        final ObjectAnimator unSelect = ObjectAnimator.ofPropertyValuesHolder(likeSelected, scaleXHolder2, scaleYHolder2).setDuration(600);
//        unSelect.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float value = (float) valueAnimator.getAnimatedValue();
//                if (value - 0.8 < 0.01f) {
//                    likeSelected.setImageResource(R.drawable.ic_messages_like_unselected);
//                }
//            }
//        });
//
//        FrameLayout messageLike = (FrameLayout) findViewById(R.id.message_like);
//        messageLike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mSelected = !mSelected;
//                if (!mSelected) {
//                    unSelect.start();
//                } else {
//                    select.start();
//                }
//
//            }
//        });



        final ScrollNumberView show = (ScrollNumberView) findViewById(R.id.show);

        findViewById(R.id.add1).setOnClickListener(v -> {
                messageLikeNumber.increment();
                show.increment();
            });

        findViewById(R.id.delete1).setOnClickListener(v -> {
                messageLikeNumber.decrement();
                show.decrement();
            });

    }
}

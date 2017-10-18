package io.luciferldy.bnimation.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import io.luciferldy.bnimation.R;
import io.luciferldy.bnimation.view.Comet;
import io.luciferldy.bnimation.view.ProgressBar;

/**
 * Created by lian_ on 2017/10/17.
 */

public class MiSportActivity extends AppCompatActivity {

    boolean rotate = false;
    boolean running = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misport);

        final String start = "start";
        final String stop = "stop";
        Comet comet = (Comet) findViewById(R.id.comet);
        ProgressBar progressbar = (ProgressBar) findViewById(R.id.progress_bar);
        progressbar.setUpdateListener(new ProgressBar.OnUpdateListener() {
            @Override
            public void update(float value) {
                comet.start(value);
            }

            @Override
            public void start() {
            }

            @Override
            public void end() {
                comet.end();
            }
        });
        Button ss = (Button) findViewById(R.id.ss);
        ss.setText(start);
        ss.setOnClickListener(v -> {
            rotate = !rotate;
            if (rotate) {
                ss.setText(stop);
                progressbar.start();
            } else {
                ss.setText(start);
                progressbar.end();
            }
        });

        ImageView run = (ImageView) findViewById(R.id.run);
        AnimationDrawable anim = (AnimationDrawable) run.getDrawable();
        run.setOnClickListener(v -> {
            running = !running;
            if (running) {
                anim.start();
            } else {
                anim.stop();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


}

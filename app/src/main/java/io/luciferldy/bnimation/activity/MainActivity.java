package io.luciferldy.bnimation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.luciferldy.bnimation.R;

/**
 * Created by lian_ on 2017/10/18.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoJike(View view) {
        Intent intent = new Intent(MainActivity.this, JikeActivity.class);
        startActivity(intent);
    }

    public void gotoMiSport(View view) {
        Intent intent = new Intent(MainActivity.this, MiSportActivity.class);
        startActivity(intent);
    }
}

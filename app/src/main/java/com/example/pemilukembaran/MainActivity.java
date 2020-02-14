package com.example.pemilukembaran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvSplash1, tvSplash2, tvSplash3;
    private ImageView logo;
    private static int splashTimeOut=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo=findViewById(R.id.logo);
        tvSplash1=findViewById(R.id.tvSplash1);
        tvSplash2=findViewById(R.id.tvSplash2);
        tvSplash3=findViewById(R.id.tvSplash3);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this,LoginAct.class);
                startActivity(i);
                finish();
            }
        },splashTimeOut);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.anim);
        logo.startAnimation(myanim);
        tvSplash1.startAnimation(myanim);
        tvSplash2.startAnimation(myanim);
        tvSplash3.startAnimation(myanim);
    }
}

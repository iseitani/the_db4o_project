package com.example.finalthesis.db4o_the_project;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int delay = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //startActivity(new Intent(Splash.this, KATI.class));
                finish();
            }
        }, delay * 1000);
    }
}

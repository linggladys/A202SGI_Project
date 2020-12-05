package com.example.a202sgi_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH = 3000;

    Animation mimgAnim, mtxtappAnim;
    ImageView mimgApp;
    TextView mtvApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this is used to hide the status bar and make the splash screen
        //as a full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //when I run this application, the taskbar has been removed
        setContentView(R.layout.activity_main);

        mimgAnim = AnimationUtils.loadAnimation(this,R.anim.img_animation);
        mtxtappAnim = AnimationUtils.loadAnimation(this,R.anim.textapp_animation);

        mimgApp = findViewById(R.id.imgApp);
        mtvApp = findViewById(R.id.tvApp);

        mimgApp.setAnimation(mimgAnim);
        mtvApp.setAnimation(mtxtappAnim);

        //loading after the animation is over
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                //where the user clicks back, this splash screen will load again
            }
        },SPLASH);
    }
}
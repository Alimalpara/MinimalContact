package com.alm.minimalcontact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button start1;



    private AnimationDrawable animDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();
        ConstraintLayout constraintLayout = findViewById(R.id.startlayout);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fromtop);


        start1=findViewById(R.id.btnStart);
        start1.startAnimation(animation);
        start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,MainActivity.class));
                finish();
            }
        });

    }
}
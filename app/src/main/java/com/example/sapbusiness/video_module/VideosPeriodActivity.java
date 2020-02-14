package com.example.sapbusiness.video_module;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sapbusiness.R;
import com.example.sapbusiness.video_module.video_finals.Finals_Videos;
import com.example.sapbusiness.video_module.video_midterm.Midterm_Videos;
import com.example.sapbusiness.video_module.video_prelim.Prelim_Videos;

public class VideosPeriodActivity extends AppCompatActivity {

    private Button btn_prelim;
    private Button btn_midterm;
    private Button btn_finals;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videosperiod);
        toolbar = findViewById(R.id.toolbar);

        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }




        btn_prelim = findViewById(R.id.prelim);
        btn_midterm = findViewById(R.id.midterm);
        btn_finals = findViewById(R.id.finals);;
        btn_prelim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideosPeriodActivity.this, Prelim_Videos.class));

            }
        });
        btn_midterm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideosPeriodActivity.this, Midterm_Videos.class));

            }
        });
        btn_finals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideosPeriodActivity.this, Finals_Videos.class));

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}


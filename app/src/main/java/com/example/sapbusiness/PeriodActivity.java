package com.example.sapbusiness;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.sapbusiness.topics_module.topics_finals.Finals_Topics;
import com.example.sapbusiness.topics_module.topics_midterm.Midterm_Topics;
import com.example.sapbusiness.topics_module.topics_prelim.Prelim_Topics;

public class PeriodActivity extends AppCompatActivity {
    private Button btn_prelim;
    private Button btn_midterm;
    private Button btn_finals;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_period);
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
                startActivity(new Intent(PeriodActivity.this, Prelim_Topics.class));

            }
        });
        btn_midterm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PeriodActivity.this, Midterm_Topics.class));

            }
        });
        btn_finals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PeriodActivity.this, Finals_Topics.class));

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

package com.example.sapbusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sapbusiness.quiz_module.QuizPeriodActivity;
import com.example.sapbusiness.simulation.Simulation;
import com.example.sapbusiness.video_module.VideosPeriodActivity;

public class MainActivity extends AppCompatActivity {
    private Button btn_topics;
    private Button btn_videos;
    private Button btn_quizzes;
    private Button btn_simulation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_topics  = findViewById(R.id.topics);
        btn_videos = findViewById(R.id.videos);
        btn_quizzes = findViewById(R.id.quizzes);
        btn_simulation= findViewById(R.id.simulation);
        btn_topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,PeriodActivity.class));

            }
        });
        btn_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, VideosPeriodActivity.class));

            }
        });
        btn_quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QuizPeriodActivity.class));

            }
        });
        btn_simulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Simulation.class));


            }
        });

    }
}

package com.example.sapbusiness.quiz_module.quizitem_list;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sapbusiness.R;
import com.example.sapbusiness.adapters.QuizManage_Adapter;
import com.example.sapbusiness.model.Question;
import com.example.sapbusiness.model.Quiz;
import com.example.sapbusiness.model.Topic;
import com.example.sapbusiness.quiz_module.add_edit_quizitem.Add_Edit_QuizItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Question_List extends AppCompatActivity {
    private Toolbar toolbar;
    private ArrayList<Question> questions;
    @Override
    protected void onPause() {
        super.onPause();
        fb_quizRef.removeEventListener(valueEventListener);
    }
    private RecyclerView recyclerView;
    private QuizManage_Adapter adapter;
    private FloatingActionButton btn_add;
    private SwipeRefreshLayout swipeRefreshLayout;
    private void initUI(){
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = findViewById(R.id.recycleview);
        btn_add = findViewById(R.id.add);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        adapter = new QuizManage_Adapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        questions = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getQuizes();
            }
        });

    }
    private DatabaseReference fb_quizRef;
    private ValueEventListener valueEventListener;
    public static Quiz quiz;
    public static Topic topic;
    private void initFirebase(){
        fb_quizRef = FirebaseDatabase
                .getInstance()
                .getReference().child("Topics")
                .child(topic.getKey())
                .child("Quiz");


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list);
        toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        initUI();
        initFirebase();

        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Question_List.this, Add_Edit_QuizItem.class);
                startActivity(intent);
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


    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        getQuizes();


    }
    private void getQuizes(){
        valueEventListener = fb_quizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                questions.clear();
                for (DataSnapshot questionData : dataSnapshot.getChildren()) {
                    Question question = questionData.getValue(Question.class);
                    question.setKey(questionData.getKey());
                    questions.add(question);

                }
                swipeRefreshLayout.setRefreshing(false);
                if(questions.size() == 0){
                    adapter.SetData(new ArrayList<Question>());
                    fb_quizRef.removeEventListener(valueEventListener);
                    showMessage("Failed","No Question here");

                }else{

                    adapter.SetData(questions);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                showMessage("Failed",databaseError.getMessage());

            }
        });
    }
    private void showMessage(String title,String message){
        new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }



}


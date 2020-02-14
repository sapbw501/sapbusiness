package com.example.sapbusiness.lesson_module.lesson_list;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sapbusiness.R;
import com.example.sapbusiness.adapters.LessonList_Adapter;
import com.example.sapbusiness.lesson_module.add_edit_lesson.Add_Edit_Lesson;
import com.example.sapbusiness.model.Lesson;
import com.example.sapbusiness.model.Topic;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lesson_List extends AppCompatActivity {
    private FloatingActionButton btn_Add;
    /*firebase*/
    private DatabaseReference topicRef;
    private ValueEventListener valueEventListener;
    /*uis*/
    private SwipeRefreshLayout sr_swipeRefresh;
    private RecyclerView rv_recycleView;
    public static String activity;
    public static Lesson lesson;
    /*initialize*/
    private void initUI(){
        btn_Add = findViewById(R.id.add);
        sr_swipeRefresh = findViewById(R.id.sr_swiperefresh);
        rv_recycleView = findViewById(R.id.rv_recycleview);
        rv_recycleView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
    }

    private void initFirebase(){
        topicRef = FirebaseDatabase.getInstance().getReference()
                .child("Topics")
                .child(topic.getKey())
                .child("Lesson");
    }
    private ArrayList<Lesson> lessons;
    private LessonList_Adapter adapter;
    public static Topic topic;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_list_activity);
        toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initUI();
        initFirebase();
        lessons = new ArrayList<>();
        adapter  = new LessonList_Adapter(this);
        rv_recycleView.setAdapter(adapter);
        btn_Add = findViewById(R.id.add);
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToAddEditLesson();
            }
        });
        sr_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTopics();
            }
        });

    }
    private void intentToAddEditLesson(){
        Add_Edit_Lesson.topic = topic;
        Intent intent = new Intent(this, Add_Edit_Lesson.class);
        intent.putExtra("activity","Midterm");
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sr_swipeRefresh.setRefreshing(true);
        getAllTopics();
    }
    @Override
    protected void onPause() {
        super.onPause();
        topicRef.removeEventListener(valueEventListener);
    }
    private void getAllTopics(){

        valueEventListener = topicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessons.clear();
                for(DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    Lesson lesson = dataSnap.getValue(Lesson.class);
                    lesson.setKey(dataSnap.getKey());
                    lessons.add(lesson);
                }
                sr_swipeRefresh.setRefreshing(false);
                if(lessons.size()>0){
                    adapter.SetData(lessons);
                }else{
                    adapter.SetData(new ArrayList<Lesson>());
                    showMessage("No Lesson","No Data Found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Error",databaseError.getMessage());
            }
        });
    }
    private void showMessage(String title,String message){
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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

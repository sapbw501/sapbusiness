package com.example.sapbusiness.quiz_module.quiz_prelim;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import com.example.sapbusiness.adapters.QuizTopicsList_Adapter;
import com.example.sapbusiness.model.Topic;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Prelim_Quizes extends AppCompatActivity {
    private FloatingActionButton btn_Add;
    /*firebase*/
    private DatabaseReference topicRef;
    private ValueEventListener valueEventListener;
    /*uis*/
    private SwipeRefreshLayout sr_swipeRefresh;
    private RecyclerView rv_recycleView;
    public static String activity;
    /*initialize*/
    @SuppressLint("RestrictedApi")
    private void initUI(){

        btn_Add = findViewById(R.id.add);
        sr_swipeRefresh = findViewById(R.id.sr_swiperefresh);
        rv_recycleView = findViewById(R.id.rv_recycleview);
        rv_recycleView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        btn_Add.setVisibility(View.GONE);
        sr_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTopics();
            }
        });
    }

    private void initFirebase(){
        topicRef = FirebaseDatabase.getInstance().getReference()
                .child("Topics");
    }
    private ArrayList<Topic> topics;
    private QuizTopicsList_Adapter adapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_activity);
        toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        initFirebase();
        initUI();
        adapter = new QuizTopicsList_Adapter(this);
        topics = new ArrayList<>();
        rv_recycleView.setAdapter(adapter);


    }



    @Override
    protected void onResume() {
        super.onResume();
        getAllTopics();
    }
    @Override
    protected void onPause() {
        super.onPause();
        topicRef.removeEventListener(valueEventListener);
    }
    private void getAllTopics(){

        sr_swipeRefresh.setRefreshing(true);
        valueEventListener = topicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topics.clear();
                for(DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    Topic topic = dataSnap.getValue(Topic.class);
                    if(topic.getPeriod().equals("Prelim")) {
                        topic.setKey(dataSnap.getKey());
                        topics.add(topic);
                    }

                }
                sr_swipeRefresh.setRefreshing(false);
                if(topics.size()>0){
                    adapter.SetData(topics);
                }else{
                    adapter.SetData(new ArrayList<Topic>());
                    showMessage("No Topics","No Data Found");
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
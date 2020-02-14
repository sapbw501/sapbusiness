package com.example.sapbusiness.lesson_module.lesson_view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sapbusiness.R;
import com.example.sapbusiness.adapters.LessonView_Adapter;
import com.example.sapbusiness.lesson_module.add_edit_lesson_item.Add_Edit_LessonItem;
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.model.Lesson;
import com.example.sapbusiness.model.LessonItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LessonView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private LessonView_Adapter adapter;
    private ArrayList<LessonItem> lessonItems;
    public static Lesson lesson;
    private void initUI(){
        lessonItems = new ArrayList<>();
        adapter = new LessonView_Adapter(this);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);

        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(lesson.getName());
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getLessonItem();
            }
        });
    }

    private DatabaseReference fb_lessonItemRef;
    private ValueEventListener valueEventListener;
    private void initFirebase(){
        fb_lessonItemRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Topics")
                .child(Lesson_List.topic.getKey())
                .child("Lesson")
                .child(lesson.getKey())
                .child("LessonItem");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =  item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add:
                Add_Edit_LessonItem.lesson_key = lesson.getKey();
                Intent intent = new Intent(this, Add_Edit_LessonItem.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessonitem);
        initUI();
        initFirebase();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lessonitem_menu, menu);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Add_Edit_LessonItem.lessonItem_Edit = null;
        swipeRefreshLayout.setRefreshing(true);
        getLessonItem();

    }
    private void getLessonItem(){
        valueEventListener = fb_lessonItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessonItems.clear();
                for(DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    LessonItem lessonItem = dataSnap.getValue(LessonItem.class);
                    lessonItem.setKey(dataSnap.getKey());
                    lessonItems.add(lessonItem );

                }
                swipeRefreshLayout.setRefreshing(false);
                if(lessonItems.size()==0){
                    adapter.SetData(new ArrayList<LessonItem>());
                    showMessage("No Data","No Lessons Available");
                }else{
                    adapter.SetData(lessonItems);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed",databaseError.getMessage());

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        fb_lessonItemRef.removeEventListener(valueEventListener);

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


}

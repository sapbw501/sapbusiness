package com.example.sapbusiness.ilo_module.ilo_list;

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
import com.example.sapbusiness.adapters.ILO_Adapter;
import com.example.sapbusiness.ilo_module.add_edit_ilo.Add_Edit_ILO;
import com.example.sapbusiness.lesson_module.add_edit_lesson.Add_Edit_Lesson;
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.model.ILO;
import com.example.sapbusiness.model.Lesson;
import com.example.sapbusiness.model.Topic;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ILO_List extends AppCompatActivity {
    private FloatingActionButton btn_Add;
    /*firebase*/
    private DatabaseReference fb_iloRef;
    private ValueEventListener valueEventListener;
    /*uis*/
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_recycleView;
    public static String activity;
    public static Lesson lesson;
    /*initialize*/
    private void initUI(){
        swipeRefreshLayout = findViewById(R.id.sr_swiperefresh);
        rv_recycleView = findViewById(R.id.rv_recycleview);
        rv_recycleView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
    }

    private void initFirebase(){
        fb_iloRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Topics")
                .child(Lesson_List.topic.getKey())
                .child("Lesson")
                .child(lesson.getKey())
                .child("ILO");
    }
    private ArrayList<ILO> ilos;
    private ILO_Adapter adapter;
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
            //   toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        initUI();
        initFirebase();
        ilos = new ArrayList<>();
        adapter  = new ILO_Adapter(this);
        rv_recycleView.setAdapter(adapter);
        btn_Add = findViewById(R.id.add);
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToAddEditILO();
            }
        });
        //  no_data.setText("No ILO Available");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTopics();
            }
        });



    }
    private void intentToAddEditILO(){
        Add_Edit_Lesson.topic = topic;
        Intent intent = new Intent(this, Add_Edit_ILO.class);
        //  intent.putExtra("activity","Midterm");
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();

        getAllTopics();
    }
    @Override
    protected void onPause() {
        super.onPause();
        fb_iloRef.removeEventListener(valueEventListener);
    }
    private void getAllTopics(){
        swipeRefreshLayout.setRefreshing(true);
        valueEventListener = fb_iloRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ilos.clear();
                for(DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    ILO ilo = dataSnap.getValue(ILO.class);
                    ilo.setKey(dataSnap.getKey());
                    ilos.add(ilo);
                }
                swipeRefreshLayout.setRefreshing(false);
                if(ilos.size()>0){
                    adapter.SetData(ilos);
                }else{
                    adapter.SetData(new ArrayList<ILO>());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
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

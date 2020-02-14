package com.example.sapbusiness.topics_module.topic_add_edit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sapbusiness.R;
import com.example.sapbusiness.model.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_Edit_Topic extends AppCompatActivity {

    /*firebase*/
    private DatabaseReference topicRef;
    /*uis*/
    private Button btn_Add;
    private EditText ed_topic_name;
    private EditText ed_topic_description;
    private ProgressDialog progressDialog;
    /*initialize*/
    private void initUI(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding new topic..");
        progressDialog.setCancelable(false);
        btn_Add = findViewById(R.id.add);
        ed_topic_name = findViewById(R.id.ed_topic_name);
        ed_topic_description = findViewById(R.id.ed_topic_description);

    }
    public static Topic topic;
    public static String activity;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_update_topic);
        initUI();
        toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        activity = getIntent().getStringExtra("activity");
        if(topic!=null){
            activity = topic.getPeriod();
            topicRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Topics")
                    .child(topic.getKey());
            progressDialog.setMessage("Updating new topic..");
            btn_Add.setText("Update Topic");
            ed_topic_name.setText(topic.getName());
            ed_topic_description.setText(topic.getDescription());
        }else{
            btn_Add.setText("Add Topic");
            DatabaseReference topicRefKey = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Topics");
            topicRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Topics")
                    .child(topicRefKey.push().getKey());

        }
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = ed_topic_name.getText().toString();
                String topicDescription= ed_topic_description.getText().toString();

                if(TextUtils.isEmpty(topicName)){
                    showMessage("Error","Empty topic name");
                }else if(TextUtils.isEmpty(topicDescription)){
                    showMessage("Error","Empty topic description");
                }else{
                    progressDialog.show();
                    Topic topicData = new Topic();
                    topicData.setName(topicName);
                    topicData.setDescription(topicDescription);
                    topicData.setPeriod(activity);
                    topicRef.child("name").setValue(topicName);
                    topicRef.child("description").setValue(topicDescription);
                    topicRef.child("period").setValue(activity)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        String title  = "Added";
                                        String message = "Topic has been added";
                                        if(topic!=null){
                                            title = "Updated";
                                            message = "Topic has been updated";

                                        }
                                        new AlertDialog.Builder(Add_Edit_Topic.this)
                                                .setTitle(title)
                                                .setMessage(message)
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                }).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            showMessage("Error",e.getMessage());
                        }
                    });


                }
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
    public void onBackPressed() {
        super.onBackPressed();
        topic = null;
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
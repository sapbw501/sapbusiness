package com.example.sapbusiness.lesson_module.add_edit_lesson;

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
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.model.Lesson;
import com.example.sapbusiness.model.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_Edit_Lesson extends AppCompatActivity {
    private DatabaseReference lessonRef;
    /*uis*/
    private Button btn_Add;
    private EditText ed_topic_name;
    private EditText ed_topic_description;
    private ProgressDialog progressDialog;

    /*initialize*/
    private void initUI(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding new lesson..");
        progressDialog.setCancelable(false);
        btn_Add = findViewById(R.id.add);
        ed_topic_name = findViewById(R.id.ed_lesson_name);
        ed_topic_description = findViewById(R.id.ed_lesson_description);


    }
    public static  Lesson lesson;
    public static Topic topic;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_update_lesson);
        toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        initUI();

        if(lesson!=null){
            ed_topic_description.setText(lesson.getDescription());
            ed_topic_name.setText(lesson.getName());
            lessonRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Lesson_List.topic.getKey())
                    .child("Lesson")
                    .child(lesson.getKey());
            progressDialog.setMessage("Updating lesson..");
            btn_Add.setText("Update Lesson");
        }else{
            btn_Add.setText("Add Lesson");
            DatabaseReference lessonRefKey = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Lesson_List.topic.getKey())
                    .child("Lesson");
            lessonRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Lesson_List.topic.getKey())
                    .child("Lesson")
                    .child(lessonRefKey.push().getKey());

        }
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lessonName = ed_topic_name.getText().toString();
                String lessonDescription = ed_topic_description.getText().toString();

                if(TextUtils.isEmpty(lessonName)){
                    showMessage("Error","Empty lesson name");
                }else if(TextUtils.isEmpty(lessonDescription)){
                    showMessage("Error","Empty lesson description");
                }else{
                    progressDialog.show();
                    Lesson lessonData = new Lesson();
                    lessonData.setName(lessonName);
                    lessonData.setDescription(lessonDescription);
                    lessonRef.child("name").setValue(lessonName);
                    lessonRef.child("description").setValue(lessonDescription)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        String title  = "Added";
                                        String message = "Lesson has been added";
                                        if(lesson!=null){
                                            title = "Updated";
                                            message = "Lesson has been updated";

                                        }
                                        new AlertDialog.Builder(Add_Edit_Lesson.this)
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}

package com.example.sapbusiness.ilo_module.add_edit_ilo;

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
import com.example.sapbusiness.ilo_module.ilo_list.ILO_List;
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.model.ILO;
import com.example.sapbusiness.model.Lesson;
import com.example.sapbusiness.model.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_Edit_ILO extends AppCompatActivity {
    private DatabaseReference lessonRef;
    /*uis*/
    private Button btn_Add;
    private EditText ed_ilo_name;
    private ProgressDialog progressDialog;

    /*initialize*/
    private void initUI(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding new ilo..");
        progressDialog.setCancelable(false);
        btn_Add = findViewById(R.id.add);
        ed_ilo_name= findViewById(R.id.ed_ilo_name);


    }
    public static Lesson lesson;
    public static Topic topic;
    private Toolbar toolbar;
    private DatabaseReference fb_iloRef;

    private void initFirebase(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Lesson Item..");
    }
    public static ILO ilo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_update_ilo);
        toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //toolbar.setNavigationIcon(R.drawable.back);

        }

        initUI();
        initFirebase();
        if(ilo!=null){
            ed_ilo_name.setText(ilo.getName());
            progressDialog.setMessage("Updating ILO..");
            btn_Add.setText("Update ILO");
            fb_iloRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Lesson_List.topic.getKey())
                    .child("Lesson")
                    .child(ILO_List.lesson.getKey())
                    .child("ILO")
                    .child(ilo.getKey());

        }else{
            btn_Add.setText("Add ILO");
            DatabaseReference iloRef  = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Lesson_List.topic.getKey())
                    .child("Lesson")
                    .child(ILO_List.lesson.getKey())
                    .child("ILO");
            String key = iloRef.push().getKey();

            fb_iloRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Lesson_List.topic.getKey())
                    .child("Lesson")
                    .child(ILO_List.lesson.getKey())
                    .child("ILO").child(key);

        }
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iloName = ed_ilo_name.getText().toString();

                if(TextUtils.isEmpty(iloName)){
                    showMessage("Failed","Empty ilo name");
                }else{
                    progressDialog.show();
                    fb_iloRef.child("name").setValue(iloName)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        String title  = "Added";
                                        String message = "ILO has been added";
                                        if(ilo!=null){
                                            title = "Updated";
                                            message = "ILO has been updated";

                                        }
                                        new AlertDialog.Builder(Add_Edit_ILO.this)
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

package com.example.sapbusiness.video_module.add_edit_videos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.sapbusiness.model.Video;
import com.example.sapbusiness.video_module.video_list.Video_List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_Edit_Video extends AppCompatActivity {

    private Toolbar toolbar;

    private Button btn_add;
    private EditText ed_video_name;
    private EditText ed_video_description;
    private EditText ed_video_url;

    private void initUI(){
        toolbar = findViewById(R.id.toolbar);
        ed_video_name = findViewById(R.id.ed_video_name);;
        ed_video_description = findViewById(R.id.ed_video_description);;
        ed_video_url = findViewById(R.id.ed_video_url);;
        btn_add = findViewById(R.id.btn_add);

        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Add Video");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

    }




    private DatabaseReference videoRef;
    private ProgressDialog progressDialog;
    public static Video video;
    private void initFirebase(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Video..");


    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_update_video);
        initUI();
        initFirebase();
        if(video!=null){

            videoRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Video_List.topic.getKey())
                    .child("Video")
                    .child(video.getKey());
            progressDialog.setMessage("Updating Video..");
            getSupportActionBar().setTitle("Update Video");
            btn_add.setText("Update Video");
            ed_video_description.setText(video.getDescription());
            ed_video_name.setText(video.getName());
            ed_video_url.setText(video.getUrl());
        }else{
            DatabaseReference videoRefKey =
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Topics")
                            .child(Video_List.topic.getKey())
                            .child("Video");
            videoRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Topics")
                    .child(Video_List.topic.getKey())
                    .child("Video")
                    .child(videoRefKey.push().getKey());
        }

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ed_video_name.getText().toString();
                final String description = ed_video_description.getText().toString();
                final String url = ed_video_url.getText().toString();
                if(TextUtils.isEmpty(name)){
                    showMessage("Failed","Video name is empty");
                }else if(TextUtils.isEmpty(description)){
                    showMessage("Failed","Video name is empty");
                }else if(TextUtils.isEmpty(url)){
                    showMessage("Failed","Video url is empty");
                }else{
                    final Video videoData = new Video();
                    videoData.setDescription(description);
                    videoData.setName(name);
                    videoData.setUrl(url);
                    progressDialog.show();
                    videoRef.setValue(videoData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();

                                        String title  = "Added";
                                        String message = "Video has been added";
                                        if(video!=null){
                                            title = "Updated";
                                            message = "Video has been updated";

                                        }
                                        new AlertDialog.Builder(Add_Edit_Video.this)
                                                .setTitle(title)
                                                .setMessage(message)
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                        video =null;
                                                    }
                                                }).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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
        int id =  item.getItemId();
        switch (id){
            case android.R.id.home:;
                video = null;
                onBackPressed();
                break;

        }
        return true;
    }


}

package com.example.sapbusiness.lesson_module.add_edit_lesson_item;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.sapbusiness.R;
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.lesson_module.lesson_view.LessonView;
import com.example.sapbusiness.model.LessonItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

public class Add_Edit_LessonItem extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView iv_image;
    private Button btn_add;
    private EditText ed_text,ed_title;
    private void initUI(){
        toolbar = findViewById(R.id.toolbar);
        iv_image = findViewById(R.id.iv_image);
        btn_add = findViewById(R.id.btn_add);
        ed_text = findViewById(R.id.ed_text);
        ed_title = findViewById(R.id.ed_title);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setTitle("Add Lesson Item");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

    }

    private String imagePath;
    private Uri imageUri;
    private static final int REQUEST_CODE = 1;
    private StorageReference mStorage;

    private DatabaseReference fb_lessonItemRef;
    private ProgressDialog progressDialog;
    public static LessonItem lessonItem_Edit;
    public static String lesson_key;
    private void initFirebase(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Lesson Item..");
        imagePath = "images/image-" + getRandomString(20);
        mStorage = FirebaseStorage.getInstance().getReference().child(imagePath);
        fb_lessonItemRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Topics")
                .child(Lesson_List.topic.getKey())
                .child("Lesson")
                .child(LessonView.lesson.getKey())
                .child("LessonItem");
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_update_lesson_item);
        initUI();
        initFirebase();
        if(lessonItem_Edit!=null){
            getSupportActionBar().setTitle("Update Lesson Item");
            ed_text.setText(lessonItem_Edit.getText());
            ed_title.setText(lessonItem_Edit.getTitle());
            if(lessonItem_Edit.getImage()!=null){
                Glide.with(this).load(FirebaseStorage.getInstance().getReference().child(lessonItem_Edit.getImage()))
                        .into(iv_image);
            }
            btn_add.setText("Update Lesson");
        }

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);

            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String text = ed_text.getText().toString();
                final String title = ed_title.getText().toString();
                if(TextUtils.isEmpty(text)){
                    showMessage("Failed","Text is empty");
                }else{
                    progressDialog.show();
                    if(imageUri==null){
                        new AlertDialog.Builder(Add_Edit_LessonItem.this)
                                .setCancelable(false)
                                .setTitle("No Image Attach")
                                .setMessage("You dont have image, Pressed proceed to continue")
                                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SaveLectureItem(text,title);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(progressDialog!=null){
                                    progressDialog.dismiss();
                                }
                                dialog.dismiss();
                            }
                        }).show();

                    }else{
                        SaveLectureItem(text,title);
                    }
                }

            }
        });
    }
    private void SaveLectureItem(final String text,final String title){
        final String key = fb_lessonItemRef.push().getKey();
        final LessonItem lessonItem = new LessonItem();
        if(imageUri!=null){
            mStorage.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isComplete()) {

                        if(lessonItem_Edit!=null){

                            lessonItem.setText(text);
                            lessonItem.setTitle(title);
                            lessonItem.setImage(imagePath);

                            fb_lessonItemRef.child(lessonItem_Edit.getKey())
                                    .setValue(lessonItem)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                showMessage("Success", "Lesson updated");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    showMessage("Update Failed", e.getMessage());
                                }
                            });

                        }else {
                            lessonItem.setTitle(title);
                            lessonItem.setText(text);
                            lessonItem.setImage(imagePath);
                            fb_lessonItemRef.child(key)
                                    .setValue(lessonItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        showMessage("Success", "Lesson item added");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    showMessage("Failed", e.getMessage());
                                }
                            });
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    showMessage("Failed",e.getMessage());
                }
            });

        }else{
            if(lessonItem_Edit!=null){
                lessonItem.setTitle(title);
                lessonItem.setText(text);
                lessonItem.setImage(lessonItem_Edit.getImage());
                fb_lessonItemRef.child(lessonItem_Edit.getKey())
                        .setValue(lessonItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            showMessage("Success", "Lesson updated");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showMessage("Update Failed", e.getMessage());
                    }
                });
            }else {
                lessonItem.setTitle(title);
                lessonItem.setText(text);
                fb_lessonItemRef.child(key)
                        .setValue(lessonItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            showMessage("Success", "Lesson item added");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showMessage("Failed", e.getMessage());
                    }
                });
            }
        }


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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Glide.with(getApplicationContext())
                        .load(imageUri)
                        .into(iv_image);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }









    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =  item.getItemId();
        switch (id){
            case android.R.id.home:;
                onBackPressed();
                break;
            case R.id.add:
                Intent intent = new Intent(this, Add_Edit_LessonItem.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
    protected String getRandomString(int size) {
        String ASCIICode = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sBuilder = new StringBuilder();
        Random rnd = new Random();
        while (sBuilder.length() < size) { // length of the random string.
            int index = rnd.nextInt(ASCIICode.length() - 1);
            sBuilder.append(ASCIICode.charAt(index));
        }
        String generatedString = sBuilder.toString();
        return generatedString;

    }


}

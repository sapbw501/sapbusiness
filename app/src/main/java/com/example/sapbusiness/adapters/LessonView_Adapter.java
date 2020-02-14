package com.example.sapbusiness.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sapbusiness.R;
import com.example.sapbusiness.lesson_module.add_edit_lesson_item.Add_Edit_LessonItem;
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.lesson_module.lesson_view.LessonView;
import com.example.sapbusiness.model.LessonItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class LessonView_Adapter extends RecyclerView.Adapter<LessonView_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<LessonItem> lessonItems;
    private DatabaseReference fb_lessonItemRef;
    private ProgressDialog progressDialog;
    public LessonView_Adapter(Context context){
        this.context=context;
        progressDialog= new ProgressDialog(context);
        progressDialog.setCancelable(false);
        fb_lessonItemRef = FirebaseDatabase.
                getInstance()
                .getReference()
                .child("Topics")
                .child(Lesson_List.topic.getKey())
                .child("Lesson")
                .child(LessonView.lesson.getKey())
                .child("LessonItem");
    }
    public void SetData(ArrayList<LessonItem> lessonItems){
        this.lessonItems = lessonItems;
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemview_lessonitem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final LessonItem lessonItem = lessonItems.get(position);
        if(lessonItem.getTitle() == null ){
            holder.tv_title.setVisibility(View.GONE);
        } else {
            if (lessonItem.getTitle().equals("")) {
                holder.tv_title.setVisibility(View.GONE);

            } else {
                holder.tv_title.setVisibility(View.VISIBLE);

            }
        }
        holder.iv_image.setVisibility(View.VISIBLE);
        holder.tv_text.setText(lessonItem.getText());
        holder.tv_title.setText(lessonItem.getTitle() != null ? lessonItem.getTitle() : "");
        if(lessonItem.getImage()!=null){
            Glide.with(context).load(FirebaseStorage.getInstance().getReference().child(lessonItem.getImage())).into(holder.iv_image);
        }else{
            holder.iv_image.setVisibility(View.GONE);

        }
        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v,lessonItem);
            }
        });



    }
    private void showPopupMenu(View view, final LessonItem lessonItem) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.lessonitem, popup.getMenu());

        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {

        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.edit:
                        Add_Edit_LessonItem.lessonItem_Edit= lessonItem;
                        Intent intent = new Intent(context, Add_Edit_LessonItem.class);
                        context.startActivity(intent);
                        break;
                    case R.id.delete:
                        progressDialog.setMessage("Deleting Lesson Item..");
                        progressDialog.dismiss();
                        if(lessonItem.getImage()!=null) {
                            FirebaseStorage.getInstance().getReference().child(lessonItem.getImage()).delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                fb_lessonItemRef.child(lessonItem.getKey()).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    showMessage("Success", "Lesson Item deleted");
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
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    showMessage("Failed", e.getMessage());
                                }
                            });
                        }else{
                            fb_lessonItemRef.child(lessonItem.getKey()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                showMessage("Success", "Lesson Item deleted");
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
                        break;

                }
                return true;
            }
        });
        popup.show();




    }
    private void showMessage(String title,String message){
        new AlertDialog.Builder(context)
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
    public int getItemCount() {
        return lessonItems!=null?lessonItems.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_text,tv_title;
        public ImageView iv_image,iv_more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.text);
            tv_title = itemView.findViewById(R.id.title);
            iv_more = itemView.findViewById(R.id.more);
            iv_image = itemView.findViewById(R.id.image);

        }
    }
}

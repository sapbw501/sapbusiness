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

import com.example.sapbusiness.R;
import com.example.sapbusiness.ilo_module.ilo_list.ILO_List;
import com.example.sapbusiness.lesson_module.add_edit_lesson.Add_Edit_Lesson;
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.lesson_module.lesson_view.LessonView;
import com.example.sapbusiness.model.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class LessonList_Adapter extends RecyclerView.Adapter<LessonList_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Lesson> lessons;
    private ProgressDialog progressDialog;
    public LessonList_Adapter(Context context){
        progressDialog = new ProgressDialog(context);
        this.context = context;

    }
    public void SetData(ArrayList<Lesson> lessons){
        this.lessons = lessons;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemview_lessonlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Lesson lesson = lessons.get(position);
        holder.tv_lesson_description.setText(lesson.getDescription());
        holder.tv_lesson_name.setText(lesson.getName());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopupMenu(v,lesson);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LessonView.lesson = lesson;
                context.startActivity(new Intent(context, LessonView.class));

            }
        });
    }
    private void showPopupMenu(View view, final Lesson lesson) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.lessonitem_with_ilo, popup.getMenu());

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
                        Add_Edit_Lesson.lesson=lesson;
                        Intent intent = new Intent(context, Add_Edit_Lesson.class);
                        context.startActivity(intent);

                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(context)
                                .setTitle("Deleting Lesson")
                                .setMessage("Are you sure do you want to delete this lesson?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setMessage("Deleting lesson..");
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Topics")
                                                .child(Lesson_List.topic.getKey())
                                                .child("Lesson")
                                                .child(lesson.getKey())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            showMessage("Success","Lesson has been deleted");
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                showMessage("Failed",e.getMessage());

                                            }
                                        });
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();


                        break;
                    case R.id.ilo:
                        ILO_List.lesson=lesson;
                        Intent intentILO = new Intent(context, ILO_List.class);
                        context.startActivity(intentILO);


                        break;

                }
                return true;
            }
        });
        popup.show();




    }
    @Override
    public int getItemCount() {
        return lessons!=null?lessons.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_lesson_description;
        public TextView tv_lesson_name;
        public ImageView more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_lesson_description = itemView.findViewById(R.id.tv_lesson_description);
            tv_lesson_name= itemView.findViewById(R.id.tv_lesson_name);
            more = itemView.findViewById(R.id.more);
        }
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
}


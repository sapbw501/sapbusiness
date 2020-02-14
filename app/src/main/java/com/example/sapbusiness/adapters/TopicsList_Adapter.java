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
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.model.Topic;
import com.example.sapbusiness.topics_module.topic_add_edit.Add_Edit_Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TopicsList_Adapter extends RecyclerView.Adapter<TopicsList_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Topic> topics;
    private ProgressDialog progressDialog;
    public TopicsList_Adapter(Context context){
        progressDialog = new ProgressDialog(context);
        this.context = context;

    }
    public void SetData(ArrayList<Topic> topics){
        this.topics = topics;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemview_topiclist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Topic topic = topics.get(position);
        holder.tv_topic_description.setText(topic.getDescription());
        holder.tv_topic_name.setText(topic.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lesson_List.topic = topic;
                context.startActivity(new Intent(context, Lesson_List.class));
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v,topic);
            }
        });
    }
    private void showPopupMenu(View view, final Topic topic) {
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
                        Add_Edit_Topic.topic= topic;
                        Intent intent = new Intent(context, Add_Edit_Topic.class);
                        context.startActivity(intent);

                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(context)
                                .setTitle("Deleting Topic")
                                .setMessage("Are you sure do you want to delete this topic?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        progressDialog.setMessage("Deleting question item..");
                                        FirebaseDatabase.getInstance().getReference().child("Topics").child(topic.getKey()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            showMessage("Success","Topic has been deleted");
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
        return topics!=null?topics.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_topic_description;
        public TextView tv_topic_name;
        public ImageView more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            more = itemView.findViewById(R.id.more);
            tv_topic_description = itemView.findViewById(R.id.tv_topic_description);
            tv_topic_name = itemView.findViewById(R.id.tv_topic_name);

        }
    }
}


package com.example.sapbusiness.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapbusiness.R;
import com.example.sapbusiness.model.Video;
import com.example.sapbusiness.video_module.add_edit_videos.Add_Edit_Video;
import com.example.sapbusiness.video_module.video_list.Video_List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class VideoList_Adapter extends RecyclerView.Adapter<VideoList_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Video> videos;
    private ProgressDialog progressDialog;
    public VideoList_Adapter(Context context){
        progressDialog = new ProgressDialog(context);
        this.context = context;

    }
    public void SetData(ArrayList<Video> videos){
        this.videos= videos;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemview_videolist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Video video = videos.get(position);
        holder.tv_lesson_description.setText(video.getDescription());
        holder.tv_lesson_name.setText(video.getName());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopupMenu(v,video);
            }
        });
        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUrl()));
                intent.putExtra("force_fullscreen",true);
                context.startActivity(intent);

            }
        });

    }
    private void showPopupMenu(View view, final Video video) {
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
                        Add_Edit_Video.video =video;
                        Intent intent = new Intent(context, Add_Edit_Video.class);
                        context.startActivity(intent);

                        break;
                    case R.id.delete:
                        progressDialog.setMessage("Deleting Video..");
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Topics")
                                .child(Video_List.topic.getKey())
                                .child("Video")
                                .child(video.getKey())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            showMessage("Success","Video has been deleted");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                showMessage("Failed",e.getMessage());

                            }
                        });

                        break;

                }
                return true;
            }
        });
        popup.show();




    }
    @Override
    public int getItemCount() {
        return videos!=null?videos.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_lesson_description;
        public TextView tv_lesson_name;
        public ImageView more;
        public Button btn_play;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_play = itemView.findViewById(R.id.btn_play);
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

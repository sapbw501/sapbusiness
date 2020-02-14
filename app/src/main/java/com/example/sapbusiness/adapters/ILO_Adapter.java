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
import com.example.sapbusiness.ilo_module.add_edit_ilo.Add_Edit_ILO;
import com.example.sapbusiness.ilo_module.ilo_list.ILO_List;
import com.example.sapbusiness.lesson_module.lesson_list.Lesson_List;
import com.example.sapbusiness.model.ILO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ILO_Adapter extends RecyclerView.Adapter<ILO_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<ILO> ilos;
    private ProgressDialog progressDialog;
    public ILO_Adapter(Context context){
        progressDialog = new ProgressDialog(context);
        this.context = context;

    }
    public void SetData(ArrayList<ILO> ilos){
        this.ilos = ilos;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemview_ilolist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ILO ilo = ilos.get(position);
        holder.tv_ilo_name.setText(ilo.getName());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v,ilo);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                LessonView.lesson = lesson;
                context.startActivity(new Intent(context, LessonView.class));
*/

            }
        });
    }
    private void showPopupMenu(View view, final ILO ilo) {
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

                        Add_Edit_ILO.ilo=ilo;
                        Intent intent = new Intent(context, Add_Edit_ILO.class);
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
                                                .child(ILO_List.lesson.getKey())
                                                .child("ILO")
                                                .child(ilo.getKey())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            showMessage("Success","ILO has been deleted");
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
    @Override
    public int getItemCount() {
        return ilos!=null?ilos.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_ilo_name;
        public ImageView more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ilo_name= itemView.findViewById(R.id.tv_ilo_name);
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

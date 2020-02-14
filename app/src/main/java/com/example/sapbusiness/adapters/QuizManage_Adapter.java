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
import com.example.sapbusiness.model.Question;
import com.example.sapbusiness.quiz_module.add_edit_quizitem.Add_Edit_QuizItem;
import com.example.sapbusiness.quiz_module.quizitem_list.Question_List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class QuizManage_Adapter extends RecyclerView.Adapter<QuizManage_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Question> questions;
    private DatabaseReference quizItemRef;
    private ProgressDialog progressDialog;
    public QuizManage_Adapter(Context context){
        this.context = context;
        progressDialog = new ProgressDialog(context);
        questions = new ArrayList<>();
        quizItemRef = FirebaseDatabase
                .getInstance().getReference()
                .child("Topics")
                .child(Question_List.topic.getKey())
                .child("Quiz");


    }
    public ArrayList<Question> getData(){

        return this.questions;
    }
    public void SetData(ArrayList<Question> questions){
        this.questions = questions;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.itemview_quizmanage,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Question question = questions.get(position);
        holder.tv_question.setText(position+1+".) "+question.getQuestion());
        holder.choiceA.setText("A.)\t"+question.getChoiceA());
        holder.choiceB.setText("B.)\t"+question.getChoiceB());
        holder.choiceC.setText("C.)\t"+question.getChoiceC());
        holder.choiceD.setText("D.)\t"+question.getChoiceD());
        holder.type.setText(question.getQuestionType());
        holder.answer.setText(question.getAnswer());
        /*
        if(question.getExplanation()!=null) {
            holder.explanation.setText("Explanation :\n\t" + question.getExplanation());
        }
        */
        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v,question);
            }
        });

        if(question.getQuestionType()!=null) {

            switch (question.getQuestionType()) {
                case "Multiple Choice":
                    holder.choiceA.setVisibility(View.VISIBLE);
                    holder.choiceB.setVisibility(View.VISIBLE);
                    holder.choiceC.setVisibility(View.VISIBLE);
                    holder.choiceD.setVisibility(View.VISIBLE);

                    break;
                case "True or False":
                    holder.choiceA.setVisibility(View.GONE);
                    holder.choiceB.setVisibility(View.GONE);
                    holder.choiceC.setVisibility(View.GONE);
                    holder.choiceD.setVisibility(View.GONE);
                    break;
                case "Identification":

                    holder.choiceA.setVisibility(View.GONE);
                    holder.choiceB.setVisibility(View.GONE);
                    holder.choiceC.setVisibility(View.GONE);
                    holder.choiceD.setVisibility(View.GONE);
                    break;
            }
        }

    }
    private void showPopupMenu(View view, final Question question) {
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
                        Add_Edit_QuizItem.question_model= question;
                        Intent intent = new Intent(context, Add_Edit_QuizItem.class);
                        context.startActivity(intent);
                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(context)
                                .setTitle("Deleting Question")
                                .setMessage("Are you sure do you want to delete this question?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        progressDialog.setMessage("Deleting question item..");
                                        quizItemRef.child(question.getKey()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            showMessage("Success","Question item has been deleted");
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
        return questions!=null?questions.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView choiceA,choiceB,choiceC,choiceD,type,answer;
        public TextView tv_question;
        public ImageView iv_more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_question = itemView.findViewById(R.id.mult_Question);
            choiceA = itemView.findViewById(R.id.choiceA);
            choiceB = itemView.findViewById(R.id.choiceB);
            choiceC = itemView.findViewById(R.id.choiceC);
            choiceD = itemView.findViewById(R.id.choiceD);
            iv_more = itemView.findViewById(R.id.more);
            type = itemView.findViewById(R.id.type);
            answer = itemView.findViewById(R.id.answer);


        }
    }

}

package com.example.sapbusiness.quiz_module.add_edit_quizitem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sapbusiness.R;
import com.example.sapbusiness.model.Question;
import com.example.sapbusiness.model.Quiz;
import com.example.sapbusiness.model.Topic;
import com.example.sapbusiness.quiz_module.quizitem_list.Question_List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_Edit_QuizItem extends AppCompatActivity { private EditText ed_question, ed_choiceA, ed_choiceB, ed_choiceC, ed_choiceD, ed_answer;
    private Button btn_addQuiz;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    public static Question question_model;
    public static Topic topic;
    public static Quiz quiz;
    private LinearLayout MULTCHOICE,TRUEORFALSE;
    private String questionType;
    private void initUI(){
        toolbar = findViewById(R.id.toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading New Question...");
        btn_addQuiz = findViewById(R.id.btn_add_lecture);
        ed_question = findViewById(R.id.question);
        ed_choiceA = findViewById(R.id.choiceA);
        ed_choiceB = findViewById(R.id.choiceB);
        ed_choiceC = findViewById(R.id.choiceC);
        ed_choiceD = findViewById(R.id.choiceD);
        ed_answer = findViewById(R.id.answer);
        MULTCHOICE = findViewById(R.id.MULTCHOICE);
        TRUEORFALSE = findViewById(R.id.TRUEORFALSE);

        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setTitle("Add Quiz Item");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

    }
    private DatabaseReference fb_quizRef;
    private void initFirebase(){

        fb_quizRef = FirebaseDatabase
                .getInstance()
                .getReference().child("Topics")
                .child(Question_List.topic.getKey())
                .child("Quiz");

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_update_quizitem);
        toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        initUI();
        initFirebase();



        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.question_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                questionType = parent.getItemAtPosition(position).toString();

                switch (parent.getItemAtPosition(position).toString()){
                    case "Multiple Choice":
                        MULTCHOICE.setVisibility(View.VISIBLE);
                        TRUEORFALSE.setVisibility(View.GONE);
                        break;
                    case "True or False":

                        MULTCHOICE.setVisibility(View.GONE);
                        TRUEORFALSE.setVisibility(View.GONE);
                        break;
                    case "Identification":
                        MULTCHOICE.setVisibility(View.GONE);
                        TRUEORFALSE.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });














        if(question_model!=null){
            int pos = 0;
            switch (question_model.getQuestionType()){
                case "Multiple Choice":
                    ed_question.setText(question_model.getQuestion());
                    ed_choiceA.setText(question_model.getChoiceA());
                    ed_choiceB.setText(question_model.getChoiceB());
                    ed_choiceC.setText(question_model.getChoiceC());
                    ed_choiceD.setText(question_model.getChoiceD());
                    ed_answer.setText(question_model.getAnswer());
                    pos = 0;
                    MULTCHOICE.setVisibility(View.VISIBLE);
                    TRUEORFALSE.setVisibility(View.GONE);
                    break;
                case "True or False":
                    pos = 1;
                    MULTCHOICE.setVisibility(View.GONE);
                    TRUEORFALSE.setVisibility(View.GONE);
                    ed_question.setText(question_model.getQuestion());
                    ed_choiceA.setText("");
                    ed_choiceB.setText("");
                    ed_choiceC.setText("");
                    ed_choiceD.setText("");
                    ed_answer.setText(question_model.getAnswer());

                    break;
                case "Identification":
                    pos = 2;
                    MULTCHOICE.setVisibility(View.GONE);
                    TRUEORFALSE.setVisibility(View.GONE);
                    ed_question.setText(question_model.getQuestion());
                    ed_choiceA.setText("");
                    ed_choiceB.setText("");
                    ed_choiceC.setText("");
                    ed_choiceD.setText("");
                    ed_answer.setText(question_model.getAnswer());
                    break;
            }
            spinner.setSelection(pos);



        }
        btn_addQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _choiceA = ed_choiceA.getText().toString();
                String _choiceB = ed_choiceB.getText().toString();
                String _choiceC = ed_choiceC.getText().toString();
                String _choiceD = ed_choiceD.getText().toString();
                String _answer = ed_answer.getText().toString();
                String _question = ed_question.getText().toString();

                Question question = new Question();
                progressDialog.show();
                switch (questionType){
                    case "Multiple Choice":
                        if (TextUtils.isEmpty(_choiceA)) {
                            showMessage("Failed", "Empty Choice A");
                            return;
                        } else if (TextUtils.isEmpty(_choiceB)) {
                            showMessage("Failed", "Empty Choice B");
                            return;

                        } else if (TextUtils.isEmpty(_choiceC)) {
                            showMessage("Failed", "Empty Choice C");
                            return;

                        } else if (TextUtils.isEmpty(_choiceD)) {
                            showMessage("Failed", "Empty Choice D");
                            return;

                        }else {
                            question.setAnswer(_answer);
                            question.setChoiceA(_choiceA);
                            question.setChoiceB(_choiceB);
                            question.setChoiceC(_choiceC);
                            question.setChoiceD(_choiceD);
                            question.setQuestion(_question);
                            question.setQuestionType(questionType);
                        }
                        break;
                    case "True or False":
                        if(_answer.equals("True")||_answer.equals("False")){
                            question.setAnswer(_answer);
                            question.setChoiceA("");
                            question.setChoiceB("");
                            question.setChoiceC("");
                            question.setChoiceD("");
                            question.setQuestion(_question);
                            question.setQuestionType(questionType);

                        }else{
                            progressDialog.dismiss();
                            showMessage("Failed","Answer must be True or False only");
                            return;
                        }
                        break;
                    case "Identification":
                        question.setAnswer(_answer);
                        question.setChoiceA("");
                        question.setChoiceB("");
                        question.setChoiceC("");
                        question.setChoiceD("");
                        question.setQuestion(_question);
                        question.setQuestionType(questionType);
                        break;
                }

                if(question_model!=null){

                    fb_quizRef.child(question_model.getKey())
                            .setValue(question)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    showMessage("Success","Quiz item updated");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            showMessage("Update Failed",e.getMessage());

                        }
                    });
                }else{
                    String key = fb_quizRef.push().getKey();
                    fb_quizRef.child(key)
                            .setValue(question)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    showMessage("Success","Quiz item saved");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            showMessage("Failed",e.getMessage());
                        }
                    });
                }

                /*
                String _question = ed_question.getText().toString();
                String _choiceA = ed_choiceA.getText().toString();
                String _choiceB = ed_choiceB.getText().toString();
                String _choiceC = ed_choiceC.getText().toString();
                String _choiceD = ed_choiceD.getText().toString();
                String _answer = ed_answer.getText().toString();
                if (TextUtils.isEmpty(_question)) {
                    showMessage("Failed", "Empty Question");
                } else if (TextUtils.isEmpty(_choiceA)) {
                    showMessage("Failed", "Empty Choice A");
                } else if (TextUtils.isEmpty(_choiceB)) {
                    showMessage("Failed", "Empty Choice B");

                } else if (TextUtils.isEmpty(_choiceC)) {
                    showMessage("Failed", "Empty Choice C");

                } else if (TextUtils.isEmpty(_choiceD)) {
                    showMessage("Failed", "Empty Choice D");

                } else if (TextUtils.isEmpty(_answer)) {
                    showMessage("Failed", "Empty Answer");

                } else {
                    Question question = new Question();
                    progressDialog.show();
                    if(question_model!=null){
                        question.setAnswer(_answer);
                        question.setChoiceA(_choiceA);
                        question.setChoiceB(_choiceB);
                        question.setChoiceC(_choiceC);
                        question.setChoiceD(_choiceD);
                        question.setQuestion(_question);
                        //question.setExplanation(_explanation);
                        fb_quizRef.child(question_model.getKey())
                                .setValue(question)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        showMessage("Success","Quiz item updated");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                showMessage("Update Failed",e.getMessage());

                            }
                        });
                    }else{
                        String key = fb_quizRef.push().getKey();
                        question.setAnswer(_answer);
                        question.setChoiceA(_choiceA);
                        question.setChoiceB(_choiceB);
                        question.setChoiceC(_choiceC);
                        question.setChoiceD(_choiceD);
                        question.setQuestion(_question);

                        fb_quizRef.child(key)
                                .setValue(question)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        showMessage("Success","Quiz item saved");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                showMessage("Failed",e.getMessage());
                            }
                        });
                    }

                }
                   */



            }
        });




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

}

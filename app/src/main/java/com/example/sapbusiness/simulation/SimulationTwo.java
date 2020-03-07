package com.example.sapbusiness.simulation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sapbusiness.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class SimulationTwo extends AppCompatActivity {
    TextView text1, label;
    TextView editText1, editText2, editText3, editText4, editText5,
            editText6;
    FloatingActionButton next, check;
    Button correct_answer, back;
    LinkedList<String> simul2_choiceList;
    String simul2_choices[];
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_simulation2);

        text1 = findViewById(R.id.simul2_text1);
        label = findViewById(R.id.simul2_label);
        label.setText("Choices");
        Randomize();
        next = findViewById(R.id.simul2_next);
        next.setClickable(true);
        next.setVisibility(View.INVISIBLE);
        check =  findViewById(R.id.simul2_check);
        check.setVisibility(View.INVISIBLE);

        correct_answer = findViewById(R.id.simul2_correct_answer);
        correct_answer.setVisibility(View.INVISIBLE);

        back = findViewById(R.id.simul2_back);

        editText1 = findViewById(R.id.simul2_global_bike);
        editText2 = findViewById(R.id.simul2_global_bike_inc);
        editText3 = findViewById(R.id.simul2_global_bike_germany);
        editText4 = findViewById(R.id.simul2_gbca);
        editText5 = findViewById(R.id.simul2_bikes);
        editText6 = findViewById(R.id.simul2_global_credit);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(simul2_choiceList.size() ==0){
                    Toast.makeText(SimulationTwo.this, "Don't have any choices", Toast.LENGTH_SHORT).show();
                }
                else{
                    Next();
                }
            }
        });



        text1.setOnTouchListener(new SimulationTwo.ChoiceTouchListener());

        editText1.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText2.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText3.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText4.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText5.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText6.setOnDragListener(new SimulationTwo.ChoiceDragListener());

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });

        correct_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCorrectAnswer();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void Next() {
        text1.setText(simul2_choiceList.pollLast());
        if(simul2_choiceList.size() == 0){
            Randomize();
        }
        else{

        }
    }


    private void Check() {
        Drawable customErrorDrawable = getResources().getDrawable(R.drawable.correct);
        customErrorDrawable.setBounds(0, 0, customErrorDrawable.getIntrinsicWidth(), customErrorDrawable.getIntrinsicHeight());
        int i = 0;
        ArrayList<Integer> total = new ArrayList<>();
        if (!editText1.getText().toString().equals("Global Bike")) {
            editText1.setError("Wrong");
            i=+1;
            total.add(i);
        } else {
            editText1.setError("Correct", customErrorDrawable);
        }
        if (!editText2.getText().toString().equals("Global Bike Inc")) {
            editText2.setError("Wrong");
            i=+1;
            total.add(i);
        } else {
            editText2.setError("Correct", customErrorDrawable);

        }
        if (!editText3.getText().toString().equals("Global Bike Germany GmbH")) {
            editText3.setError("Wrong");
            i=+1;
            total.add(i);
        } else {
            editText3.setError("Correct", customErrorDrawable);

        }
        if (!editText4.getText().toString().equals("Global Bike Chart of Accounts")) {
            editText4.setError("Wrong");
            i=+1;
            total.add(i);
        } else {
            editText4.setError("Correct", customErrorDrawable);

        }
        if (!editText5.getText().toString().equals("Bikes")) {
            editText5.setError("Wrong");
            i=+1;
            total.add(i);
        } else {
            editText5.setError("Correct", customErrorDrawable);

        }
        if (!editText6.getText().toString().equals("Global Credit Control")) {
            editText6.setError("Wrong");
            i=+1;
            total.add(i);
        } else {
            editText6.setError("Correct", customErrorDrawable);

        }

        label.setText("Score: ");
        int size1 = total.size();
        int size2 = simul2_choices.length;
        int size = size2-size1;
        text1.setText(size+"/"+size2);
        int show_correct = size2/2;
        if(size<show_correct){
            correct_answer.setVisibility(View.VISIBLE);
            Failed("You Failed");
        }
        else{
            correct_answer.setVisibility(View.INVISIBLE);
            Passed("Congrats, you pass!");

        }



    }
    private void Passed(String message) {
        final Dialog dialog = new Dialog(SimulationTwo.this);

        dialog.setCancelable(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        dialog.setContentView(R.layout.result);
        TextView txt = dialog.findViewById(R.id.txt_result);
        txt.setText(message);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    private void Failed(String message) {
        final Dialog dialog = new Dialog(SimulationTwo.this);

        dialog.setCancelable(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        dialog.setContentView(R.layout.result);
        TextView txt = dialog.findViewById(R.id.txt_result);
        txt.setText(message);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void Randomize() {
        simul2_choices = getResources().getStringArray(R.array.simul2);
        simul2_choiceList = new LinkedList<String>();
        for (String i : simul2_choices)
            simul2_choiceList.add(i);
        Collections.shuffle(simul2_choiceList);
        getRandomText();
    }


    private void getRandomText() {
        text1.setText("");
        text1.setText(simul2_choiceList.pollLast());
    }


    private class ChoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                /*
                 * Drag details: we only need default behavior
                 * - clip data could be set to pass data as part of drag
                 * - shadow can be tailored
                 */
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {

                return false;
            }
        }
    }

    private class ChoiceDragListener implements View.OnDragListener {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:

                    View view = (View) event.getLocalState();
                    TextView dropTarget = (TextView) v;
                    TextView dropped = (TextView) view;
                    dropTarget.setText(dropped.getText().toString());
                    dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
                    dropTarget.setTag(dropped.getId());
                    dropTarget.setOnDragListener(null);
                    getRandomText();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(simul2_choiceList.size()== 0){
                        check.setVisibility(View.VISIBLE);
                    }
                    else{
                        check.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }
    @SuppressLint("RestrictedApi")
    public void reset(View view)
    {
        Randomize();
        text1.setVisibility(TextView.VISIBLE);
        label.setText("Choices: ");
        check.setVisibility(View.INVISIBLE);
        correct_answer.setVisibility(View.INVISIBLE);
        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        editText4.setText("");
        editText5.setText("");
        editText6.setText("");

        editText1.setTag(null);
        editText2.setTag(null);
        editText3.setTag(null);
        editText4.setTag(null);
        editText5.setTag(null);
        editText6.setTag(null);

        editText1.setError(null);
        editText2.setError(null);
        editText3.setError(null);
        editText4.setError(null);
        editText5.setError(null);
        editText6.setError(null);

        editText1.setTypeface(Typeface.DEFAULT);
        editText2.setTypeface(Typeface.DEFAULT);
        editText3.setTypeface(Typeface.DEFAULT);
        editText4.setTypeface(Typeface.DEFAULT);
        editText5.setTypeface(Typeface.DEFAULT);
        editText6.setTypeface(Typeface.DEFAULT);

        editText1.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText2.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText3.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText4.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText5.setOnDragListener(new SimulationTwo.ChoiceDragListener());
        editText6.setOnDragListener(new SimulationTwo.ChoiceDragListener());
    }

    private void ShowCorrectAnswer() {
        final Dialog dialog = new Dialog(SimulationTwo.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        dialog.setContentView(R.layout.answer_simulation_two);


        Button dialogButton = (Button) dialog.findViewById(R.id.cancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }


}


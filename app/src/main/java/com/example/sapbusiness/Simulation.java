package com.example.sapbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sapbusiness.simulation.SimulationOne;
import com.example.sapbusiness.simulation.SimulationTwo;
import com.example.sapbusiness.simulation.SimulationThree;
import com.example.sapbusiness.simulation.SimulationFour;

public class Simulation extends AppCompatActivity {

    Button simul1, simul2, simul3, simul4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        simul1 = findViewById(R.id.simulation1);
        simul1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Simulation.this, SimulationOne.class));
            }
        });
        simul2 = findViewById(R.id.simulation2);
        simul2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Simulation.this, SimulationTwo.class));
            }
        });
        simul3 = findViewById(R.id.simulation3);
        simul3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Simulation.this, SimulationThree.class));
            }
        });
        simul4 = findViewById(R.id.simulation4);
        simul4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Simulation.this, SimulationFour.class));
            }
        });
    }

}

package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import maes.tech.intentanim.CustomIntent;

public class Notes extends AppCompatActivity {

    EditText notesTf1;
    Button sendBtn, prescriptionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        notesTf1 = findViewById(R.id.notesTf1);
        sendBtn = findViewById(R.id.sendBtn1);
        prescriptionBtn = findViewById(R.id.prescriptionBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
           String notes = notesTf1.getText().toString().trim();
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(notes)) {
                    notesTf1.setError("Cannot have Empty Field");
                    return;
                }
                startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
                CustomIntent.customType(Notes.this, "fadein-to-fadeout");
            }
        });

        prescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Prescription.class));
                CustomIntent.customType(Notes.this, "fadein-to-fadeout");
            }
        });
    }
}

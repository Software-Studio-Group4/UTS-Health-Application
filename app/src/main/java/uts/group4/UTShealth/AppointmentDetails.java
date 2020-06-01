package uts.group4.UTShealth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;

public class AppointmentDetails extends AppCompatActivity {
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    Bundle extras;
    ConstraintLayout rootLayout;
    TextView dateTextView;
    TextView weekdayTextView;
    TextView doctorTextView;
    TextView patientTextView;
    Button chatBtn;
    Button cancelApptBtn;
    String appointmentID = null;
    boolean isDoctor = false;

    //fields for cosmetic manipulation
    TextView cosmetic_1;
    TextView cosmetic_2;
    TextView cosmetic_3;
    TextView cosmetic_4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_details_layout);

        //initialise fields//
        rootLayout = findViewById(R.id.root);
        extras = getIntent().getExtras();
        dateTextView = findViewById(R.id.dateTextView);
        weekdayTextView = findViewById(R.id.weekdayTextView);
        doctorTextView = findViewById(R.id.drNameTextView);
        patientTextView = findViewById(R.id.patientNameTextView);
        doctorTextView = findViewById(R.id.drNameTextView);
        chatBtn = findViewById(R.id.chatBtn);
        cancelApptBtn = findViewById(R.id.cancelApptBtn);
        if(extras != null){
            appointmentID = extras.getString("appointmentID");
            isDoctor = extras.getBoolean("isDoctor");
        }
        //set theme colours based on doctor or patient


    }


}

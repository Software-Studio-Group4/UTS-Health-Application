package uts.group4.UTShealth;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Util.DATParser;

public class AppointmentDetails extends AppCompatActivity {
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    Bundle extras;
    ConstraintLayout rootLayout;
    TextView dateTextView;
    TextView weekdayTextView;
    TextView doctorTextView;
    TextView patientTextView;
    TextView timeTextView;
    Button chatBtn;
    Button backBtn;
    Button cancelApptBtn;
    String appointmentID = null;
    boolean isDoctor = false;
    boolean isEditing = false;
    //fields for cosmetic manipulation
    TextView cosmetic_1;
    TextView cosmetic_2;
    TextView cosmetic_3;
    TextView cosmetic_4;

    //urgentAppointment
    TextView urgentStatus;
    boolean isUrgent = false;

    //editAppointment
    Button editBtn;
    ImageButton editTimeBtn;
    ImageButton editDateBtn;
    ImageButton editDoctorBtn;
    FloatingActionButton confirmChanges;
    FloatingActionButton discardChanges;

    //appointmentDetails
    String chatCode;
    String apptTime;
    String apptDate;
    String apptDay;
    String doctorName;
    String doctorID;
    String patientID;
    Timestamp apptTimestamp;



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
        timeTextView = findViewById(R.id.timeTextView);
        patientTextView = findViewById(R.id.patientNameTextView);
        doctorTextView = findViewById(R.id.drNameTextView);
        chatBtn = findViewById(R.id.chatBtn);
        cancelApptBtn = findViewById(R.id.cancelApptBtn);
        backBtn = findViewById(R.id.backBtn3);
        urgentStatus = findViewById(R.id.urgentTextView);
        confirmChanges = findViewById(R.id.confirmChangesBtn);
        discardChanges = findViewById(R.id.discardChangesBtn);
        editBtn = findViewById(R.id.editBtn);
        editTimeBtn = findViewById(R.id.editTimeBtn);
        editDateBtn = findViewById(R.id.editDateBtn);
        editDoctorBtn = findViewById(R.id.editDocBtn);

        if(extras != null){
            appointmentID = extras.getString("appointmentID");
            isDoctor = extras.getBoolean("isDoctor");
        }

        //setting cosmetics
        if(isUrgent){
            editBtn.setVisibility(View.GONE);
        }
        editTimeBtn.setVisibility(View.GONE);
        editDateBtn.setVisibility(View.GONE);
        editDoctorBtn.setVisibility(View.GONE);
        confirmChanges.setVisibility(View.GONE);
        discardChanges.setVisibility(View.GONE);






        if(isDoctor){
            /********************setting up the page to be a doctor page...***********************/
            //*********************COSMETIC CHANGES******************************************
            //sets all the text to white
            cosmetic_1 = findViewById(R.id.welcomeText);
            cosmetic_1.setTextColor(Color.parseColor("#FFFFFF"));
            cosmetic_2 = findViewById(R.id.upcomingApptText);
            cosmetic_2.setTextColor(Color.parseColor("#FFFFFF"));
            cosmetic_3 = findViewById(R.id.upcomingApptText3);
            cosmetic_3.setTextColor(Color.parseColor("#FFFFFF"));
            cosmetic_4 = findViewById(R.id.upcomingApptText4);
            cosmetic_4.setTextColor(Color.parseColor("#FFFFFF"));
            dateTextView.setTextColor(Color.parseColor("#FFFFFF"));
            weekdayTextView.setTextColor(Color.parseColor("#FFFFFF"));
            doctorTextView.setTextColor(Color.parseColor("#FFFFFF"));
            patientTextView.setTextColor(Color.parseColor("#FFFFFF"));
            doctorTextView.setTextColor(Color.parseColor("#FFFFFF"));
            //sets background assets
            rootLayout.setBackgroundResource(R.drawable.staff_profile_bg);
            backBtn.setBackgroundResource(R.drawable.back_btn_staff);

        }
        else{
            //sets patient layout unique actions
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference apptRef = fStore.collection("Appointment").document(appointmentID);

        //SET INITIAL DATA BEFORE EDITS//
        apptRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    chatCode = documentSnapshot.getString("ChatCode");
                    apptDate  = documentSnapshot.getString("Date");
                    apptDay = DATParser.weekDayAsString(DATParser.getWeekDay(apptDate));
                    apptTime = documentSnapshot.getString("Time");
                    apptTimestamp = documentSnapshot.getTimestamp("TimeStamp");
                    doctorName  = documentSnapshot.getString("DoctorFullName");
                    patientID = documentSnapshot.getString("patientID");
                    doctorID = documentSnapshot.getString("doctorID");

                    dateTextView.setText(apptDate);
                    timeTextView.setText(apptTime);
                    weekdayTextView.setText(apptDay);
                    doctorTextView.setText(doctorName);
                    patientTextView.setText(documentSnapshot.getString("PatientFullName"));

                } else {
                    Toast.makeText(getApplicationContext(), "Error: couldn't retrieve appointment data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*******************************BUTTON METHODS*******************************************************/

    public void goToChat(View view){
        if (chatCode != null) {
            Intent i = new Intent(AppointmentDetails.this, Chat.class);
            i.putExtra("chatroomcode", chatCode);
            startActivity(i);
            CustomIntent.customType(AppointmentDetails.this, "right-to-left");
        }
        else {
            Toast.makeText(AppointmentDetails.this, "NO CHAT ROOM CODE FOUND", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteAppt(View view){
        fStore.collection("Appointment").document(appointmentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Appointment deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        finish();
    }

    public void back(View view){
        finish();
    }
}

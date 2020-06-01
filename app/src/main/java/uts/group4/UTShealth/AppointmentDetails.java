package uts.group4.UTShealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.ActivityFragments.ChooseADoctorFragment;
import uts.group4.UTShealth.ActivityFragments.DatePickerFragment;
import uts.group4.UTShealth.ActivityFragments.EditShiftFragment;
import uts.group4.UTShealth.ActivityFragments.TimePickerFragment;
import uts.group4.UTShealth.Util.DATParser;

public class AppointmentDetails extends AppCompatActivity {
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    DocumentReference apptRef;
    Calendar cal = Calendar.getInstance();
    Bundle extras;
    ConstraintLayout rootLayout;
    static TextView dateTextView;
    static TextView weekdayTextView;
    TextView doctorTextView;
    TextView patientTextView;
    static TextView timeTextView;
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

        if (extras != null) {
            appointmentID = extras.getString("appointmentID");
            isDoctor = extras.getBoolean("isDoctor");
            apptRef = fStore.collection("Appointment").document(appointmentID);
        }

        //setting cosmetics
        if (isUrgent) {
            editBtn.setVisibility(View.GONE);
        }
        editTimeBtn.setVisibility(View.GONE);
        editDateBtn.setVisibility(View.GONE);
        editDoctorBtn.setVisibility(View.GONE);
        confirmChanges.setVisibility(View.GONE);
        discardChanges.setVisibility(View.GONE);


        if (isDoctor) {
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
            timeTextView.setTextColor(Color.parseColor("#FFFFFF"));
            editBtn.setVisibility(View.GONE); //DOCTORS CANT EDIT JUST YET
            //sets background assets
            rootLayout.setBackgroundResource(R.drawable.staff_profile_bg);
            backBtn.setBackgroundResource(R.drawable.back_btn_staff);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //SET INITIAL DATA BEFORE EDITS//
        apptRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    chatCode = documentSnapshot.getString("ChatCode");
                    apptDate = documentSnapshot.getString("Date");
                    apptDay = DATParser.weekDayAsString(DATParser.getWeekDay(apptDate));
                    apptTime = documentSnapshot.getString("Time");
                    apptTimestamp = documentSnapshot.getTimestamp("TimeStamp");
                    doctorName = documentSnapshot.getString("DoctorFullName");
                    patientID = documentSnapshot.getString("patientID");
                    doctorID = documentSnapshot.getString("doctorID");

                    dateTextView.setText(apptDate);
                    timeTextView.setText(apptTime);
                    weekdayTextView.setText(apptDay);
                    doctorTextView.setText(doctorName);
                    patientTextView.setText(documentSnapshot.getString("PatientFullName"));

                } else {
                    Toast.makeText(AppointmentDetails.this, "Error: couldn't retrieve appointment data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*******************************BUTTON METHODS*******************************************************/

    public void goToChat(View view) {
        if (chatCode != null) {
            Intent i = new Intent(AppointmentDetails.this, Chat.class);
            i.putExtra("chatroomcode", chatCode);
            i.putExtra("isDoctor", isDoctor);
            startActivity(i);
            CustomIntent.customType(AppointmentDetails.this, "right-to-left");
        } else {
            Toast.makeText(AppointmentDetails.this, "NO CHAT ROOM CODE FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAppt(View view) {
        fStore.collection("Appointment").document(appointmentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AppointmentDetails.this, "Appointment deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AppointmentDetails.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    public void editButton(View view) {
        editBtn.setVisibility(View.GONE);
        editDateBtn.setVisibility(View.VISIBLE);
        editDoctorBtn.setVisibility(View.VISIBLE);
        editTimeBtn.setVisibility(View.VISIBLE);
        confirmChanges.setVisibility(View.VISIBLE);
        discardChanges.setVisibility(View.VISIBLE);
        chatBtn.setVisibility(View.GONE);
        cancelApptBtn.setVisibility(View.GONE);

        isEditing = true;
    }

    public void discardChanges(View view) {
        editBtn.setVisibility(View.VISIBLE);
        editDateBtn.setVisibility(View.GONE);
        editDoctorBtn.setVisibility(View.GONE);
        editTimeBtn.setVisibility(View.GONE);
        confirmChanges.setVisibility(View.GONE);
        discardChanges.setVisibility(View.GONE);
        chatBtn.setVisibility(View.VISIBLE);
        cancelApptBtn.setVisibility(View.VISIBLE);

        dateTextView.setText(apptDate);
        timeTextView.setText(apptTime);
        weekdayTextView.setText(apptDay);
        doctorTextView.setText(doctorName);
        dateTextView.setTextColor(Color.parseColor("#185586"));
        timeTextView.setTextColor(Color.parseColor("#185586"));
        weekdayTextView.setTextColor(Color.parseColor("#185586"));
        doctorTextView.setTextColor(Color.parseColor("#185586"));
        isEditing = false;
        Toast.makeText(AppointmentDetails.this, "Changes discarded", Toast.LENGTH_SHORT);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void confirmChanges(View view){
        //update fields in firebase
        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("Date", dateTextView.getText().toString());
        appointmentData.put("Time", timeTextView.getText().toString());
        appointmentData.put("WeekDay", weekdayTextView.getText().toString());
        appointmentData.put("TimeStamp", DATParser.dateToTimestamp(dateTextView.getText().toString()));
        apptRef.update(appointmentData);

        apptDate = dateTextView.getText().toString();
        apptTime = timeTextView.getText().toString();
        apptDay = weekdayTextView.getText().toString();

        editBtn.setVisibility(View.VISIBLE);
        editDateBtn.setVisibility(View.GONE);
        editDoctorBtn.setVisibility(View.GONE);
        editTimeBtn.setVisibility(View.GONE);
        confirmChanges.setVisibility(View.GONE);
        discardChanges.setVisibility(View.GONE);
        chatBtn.setVisibility(View.VISIBLE);
        cancelApptBtn.setVisibility(View.VISIBLE);

        dateTextView.setText(apptDate);
        timeTextView.setText(apptTime);
        weekdayTextView.setText(apptDay);
        doctorTextView.setText(doctorName);
        dateTextView.setTextColor(Color.parseColor("#185586"));
        timeTextView.setTextColor(Color.parseColor("#185586"));
        weekdayTextView.setTextColor(Color.parseColor("#185586"));
        doctorTextView.setTextColor(Color.parseColor("#185586"));
        isEditing = false;

        Toast.makeText(AppointmentDetails.this, "Changes saved!", Toast.LENGTH_SHORT);
    }

    public void editDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", "AppointmentDetails");
        bundle.putString("doctorID", doctorID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "date picker");
    }

    public void editTime(View view) {
        DialogFragment fragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", "AppointmentDetails");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "time picker");

    }

    public void editDoctor(View view) {
        ChooseADoctorFragment chooseDoc = new ChooseADoctorFragment();
        chooseDoc.show(getSupportFragmentManager(), "ChooseDoc");
    }

    public void back(View view) {
        finish();
    }

    /********************************************EDIT METHODS************************************************/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void editDateText(int year, int month, int day) {
        dateTextView.setTextColor(Color.parseColor("#F9C70C"));
        weekdayTextView.setTextColor(Color.parseColor("#F9C70C"));
        if (month < 10) {
            dateTextView.setText(day + "/0" + month + "/" + year);
        } else {
            dateTextView.setText(day + "/" + month + "/" + year);
        }
        weekdayTextView.setText(DATParser.weekDayAsString(DATParser.getWeekDay(dateTextView.getText().toString())));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public static void editTimeText(int hour, int minute) {
        timeTextView.setTextColor(Color.parseColor("#F9C70C"));
        String amPm;
        int reformattedHour = hour;
        if(hour > 12){
            reformattedHour = (hour - 12);
        }
        if (hour >= 12) {
            amPm = " PM";
        } else
            amPm = " AM";
        timeTextView.setText(String.format("%02d:%02d", reformattedHour, minute) + amPm);
    }

}

package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static TextView dateTextView;
    public static TextView timeTextView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        Spinner docSpinner = findViewById(R.id.doctorSpinner);

        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.doctorNames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        docSpinner.setAdapter(adapter);
        docSpinner.setOnItemSelectedListener(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        backBtn = findViewById(R.id.backBtn14);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
                CustomIntent.customType(BookAppointment.this, "fadein-to-fadeout");
            }
        });
    }


    @Override
    public <T extends View> T findViewById(int id) {
        return super.findViewById(id);
    }

    public void btn_PickerDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date picker");
    }

    public static void populateSetDateText(int year, int month, int day) {
        dateTextView.setText(day + "/" + month + "/" + year);
    }

    public void btn_PickerTime(View view) {
        DialogFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(), "time picker");
    }

    //Changes the heading of the calendar view
    public static void populateSetTimeText(int hour, int minute) {
        String amPm;
        if (hour >= 12) {
            amPm = "PM";
        } else
            amPm = "AM";
        timeTextView.setText(String.format("%02d:%02d", hour, minute) + amPm);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // confirm the appointment (data stored in firestore)
    public void confirmAppt(View view) {
        String userID = fAuth.getCurrentUser().getUid();
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();
        String appointmentID = (userID + date + time).replaceAll("[/:]", ""); //this makes an appointment easier to find.

        DocumentReference appointmentRef = fStore.collection("Appointment").document(appointmentID); //sets reference to this appointment object


        if (TextUtils.isEmpty(date)) {
            Toast.makeText(BookAppointment.this, "Must select date", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(time)) {
            Toast.makeText(BookAppointment.this, "Must select time", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            // sets the target document reference to the Appointment collection in the firestore.
            Map<String, Object> appointmentData = new HashMap<>(); //
            appointmentData.put("patientID", userID);
            appointmentData.put("Date", date);
            appointmentData.put("Time", time);

            //CREATES AN APPOINTMENT OBJECT IN THE FIRESTORE.
            appointmentRef.set(appointmentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(BookAppointment.this, "Success", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BookAppointment.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

            //ADDS THIS APPOINTMENT ID INTO THE 'Appointments' LIST IN THE PATIENT OBJECT.
            DocumentReference patientDocRef = fStore.collection("Patient").document(userID); //setting a document reference to the patient's data path
            patientDocRef.update("Appointments", FieldValue.arrayUnion(appointmentID));//appends the same appointment ID to the list of strings so we can search for this appointment.
        }
        startActivity(new Intent(getApplicationContext(), PatientDashboard.class));

    }

}

package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uts.group4.UTShealth.Model.ChatMessage;

public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static TextView dateTextView;
    public static TextView timeTextView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DatabaseReference dbRef;
    Spinner docSpinner;
    final List<String> doctors = new ArrayList<>();
    final List<String> doctorIds = new ArrayList<>();
    String patientFullName = null;
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd hh:mm a");
    String dateAndTime = formatter.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        docSpinner = findViewById(R.id.doctorSpinner);
        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

    // the following block of code populates the patientName string with their full name to be used to be stored in the appointments object;
                DocumentReference nameRef = fStore.collection("Patients").document(fAuth.getUid());
                nameRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                patientFullName = document.getString("First Name") + " " + document.getString("Last Name");
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
            }
        });




        /******************This code block sets the options in the spinner to doctor names from the database****************************/
        CollectionReference doctorsRef = fStore.collection("Doctor");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, doctors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        docSpinner.setAdapter(adapter);
        doctorsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String doctorName = document.getString("First Name") + " " + document.getString("Last Name");
                        doctors.add(doctorName);
                        doctorIds.add(document.getId());
                        Log.i("INFO", "DOCTOR FOUND: " + document.getString("First Name"));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public <T extends View> T findViewById(int id) {
        return super.findViewById(id);
    }

    public void btn_PickerDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", "BookAppointment");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "date picker");
    }

    public static void populateSetDateText(int year, int month, int day) {
        if(month < 10){
            dateTextView.setText(day + "/0" + month + "/" + year);
        }
        else{
        dateTextView.setText(day + "/" + month + "/" + year);
    }
    }

    public void btn_PickerTime(View view) {
        DialogFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(), "time picker");
    }

    //Changes the heading of the calendar view
    public static void populateSetTimeText(int hour, int minute) {
        String amPm;
        if (hour >= 12) {
            amPm = " PM";
        } else
            amPm = " AM";
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
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(BookAppointment.this, "Must select time", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            //initialise A Chat Object in the RealTimeDatabase
            dbRef = FirebaseDatabase.getInstance().getReference().child("Chats/" + "CHAT" + appointmentID);
            ChatMessage initMessage = new ChatMessage("Welcome to your appointment!", "SYSTEM", null, dateAndTime);
            dbRef.push().setValue(initMessage);

            // sets the target document reference to the Appointment collection in the firestore.
            Map<String, Object> appointmentData = new HashMap<>(); //
            appointmentData.put("patientID", userID);
            appointmentData.put("Date", date);
            appointmentData.put("Time", time);
            appointmentData.put("ChatCode", "CHAT" + appointmentID);

            //grab the doctorID of the currently selected doctor in the spinner
            String selectedDoc = docSpinner.getSelectedItem().toString();
            appointmentData.put("doctorID", (doctorIds.get(doctors.indexOf(selectedDoc))));
            appointmentData.put("DoctorFullName", selectedDoc);
            appointmentData.put("PatientFullName", patientFullName);

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

            //ADDS THIS APPOINTMENT ID INTO THE 'Appointments' LIST IN THE DOCTOR OBJECT.
            DocumentReference doctorDocRef = fStore.collection("Patient").document(userID); //setting a document reference to the patient's data path
            doctorDocRef.update("Appointments", FieldValue.arrayUnion(appointmentID));//appends the same appointment ID to the list of strings so we can search for this appointment.

        }
        startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
    }

    public void backBtnPressed(View view) {
        startActivity(new Intent(getApplicationContext(), PatientDashboard.class));

    }
}

package uts.group4.UTShealth;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static TextView dateTextView;
    public static TextView timeTextView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        Spinner spinner = findViewById(R.id.doctorSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.doctorNames,  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    public void btn_PickerDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date picker");
        dateTextView = findViewById(R.id.dateTextView);
    }

    public static void populateSetDateText(int year, int month, int day) {
        dateTextView.setText(day + "/" + month + "/" + year);
    }

    public void btn_PickerTime(View view) {
        DialogFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(), "time picker");
        timeTextView = findViewById(R.id.timeTextView);
    }

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

    // Save appointment in Firesbase
    public void confirmAppt(View view) {
        String userID = fAuth.getCurrentUser().getUid();
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();
        DocumentReference docRef = fStore.collection("Patients").document(userID).collection("Appointments").document();
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("Date", date);
        appointment.put("Time", time);
        fStore.collection("Patients").document(userID).collection("Appointments").document(String.valueOf(docRef))
                .set(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }
}

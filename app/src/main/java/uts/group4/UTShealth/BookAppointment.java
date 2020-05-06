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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import maes.tech.intentanim.CustomIntent;


public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static TextView dateTextView;
    public static TextView timeTextView;
    Button bookBtn;
    private static String date;
    private static String time;
    private DatabaseReference gDatabase;
    Appointment appointment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        bookBtn = findViewById(R.id.bookBtn);
        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);

        appointment = new Appointment();
        gDatabase = FirebaseDatabase.getInstance().getReference().child("Appointments");



        Spinner spinner = findViewById(R.id.doctorSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.doctorNames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date = dateTextView.getText().toString().trim();
                time = timeTextView.getText().toString().trim();
                if (TextUtils.isEmpty(date)) {
                    dateTextView.setError("Must select date");
                    Toast.makeText(BookAppointment.this, "Must select date", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(time)) {
                    timeTextView.setError("Must select time");
                    Toast.makeText(BookAppointment.this, "Must select time", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Toast.makeText(BookAppointment.this, "Date and Time successfully selected!", Toast.LENGTH_SHORT).show();
                }

                startActivity(new Intent(getApplicationContext(), RegisterPassPge.class));
                CustomIntent.customType(BookAppointment.this, "fadein-to-fadeout");


                gDatabase.child("Appointments").push().setValue(appointment);

            }
        });
    }


        public void btn_PickerTime (View view){
            DialogFragment fragment = new TimePickerFragment();
            fragment.show(getSupportFragmentManager(), "time picker");

        }

        public static void populateSetTimeText ( int hour, int minute){
            String amPm;
            if (hour >= 12) {
                amPm = "PM";
            } else
                amPm = "AM";
            timeTextView.setText(String.format("%02d:%02d", hour, minute) + amPm);


        }

        public void btn_PickerDate (View view){
            DialogFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "date picker");

        }

        public static void populateSetDateText ( int year, int month, int day){
            dateTextView.setText(day + "/" + month + "/" + year);

        }

        @Override
        public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected (AdapterView < ? > parent){

        }

    public static class Appointment {
        private String date;
        private String time;

        public Appointment() {

        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }


    }


}
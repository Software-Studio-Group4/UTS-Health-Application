package uts.group4.UTShealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;


public class BookAppointment extends AppCompatActivity{


    public static TextView dateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
    }

    public void btn_PickerDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date picker");

        dateTextView = findViewById(R.id.dateTextView);
    }

    public static void populateSetDateText(int year, int month, int day) {
        dateTextView.setText(day + "/" + month + "/" + year);
    }

}

package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import maes.tech.intentanim.CustomIntent;

// Upcoming Appointments Page

public class PatientDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard);
    }

    public void pastAppt(View view) {
        startActivity(new Intent(getApplicationContext(), PatientPastAppointments.class));
        CustomIntent.customType(PatientDashboard.this, "fadein-to-fadeout");
    }
    public void bookAppt(View view) {
        startActivity(new Intent(getApplicationContext(), BookAppointment.class));
    }
    public void userProfile(View view) {
        startActivity(new Intent(getApplicationContext(), PatientProfilePage.class));
    }
    public void openChat(View view) {
        startActivity(new Intent(getApplicationContext(), Chat.class));

    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    } // Fade transition

    // Past Appointments Page

    public static class PatientPastAppointments extends AppCompatActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.past_appointments);
        }

        public void upcomingAppt(View view) {
            startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
            CustomIntent.customType(PatientPastAppointments.this, "fadein-to-fadeout");
        }

        public void bookAppt(View view) {
            startActivity(new Intent(getApplicationContext(), BookAppointment.class));
        }

        @Override
        public void finish() {
            super.finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        } // Fade transition
    }
}

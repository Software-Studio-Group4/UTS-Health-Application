package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

public class PatientProfilePage extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile);

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        CustomIntent.customType(PatientProfilePage.this, "fadein-to-fadeout");
        finish();
    }

    public void backBtnPressed (View view) {
        startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
        CustomIntent.customType(PatientProfilePage.this, "right-to-left");
    }
}

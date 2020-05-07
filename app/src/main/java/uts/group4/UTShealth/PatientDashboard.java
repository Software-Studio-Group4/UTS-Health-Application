package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

public class PatientDashboard extends AppCompatActivity {

    Button logoutBtn;
    Button chatBtn;
    Button bookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        logoutBtn = findViewById(R.id.logoutBtn);
        chatBtn = findViewById(R.id.chatBtn);
        bookBtn = findViewById(R.id.bookBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                CustomIntent.customType(PatientDashboard.this, "fadein-to-fadeout");
                finish();
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Chat.class));
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookAppointment.class));
                CustomIntent.customType(PatientDashboard.this, "left-to-right");
            }
        });
    }


}

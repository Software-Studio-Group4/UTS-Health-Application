package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import maes.tech.intentanim.CustomIntent;

// Homepage

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signupBtn = findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientRegistration.class));
                CustomIntent.customType(MainActivity.this, "right-to-left");
            }
        });

        Button loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientLogin.class));
                CustomIntent.customType(MainActivity.this, "left-to-right");
            }
        });

        Button staffBtn = findViewById(R.id.staffBtn);
        staffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StaffHomepage.class));
                CustomIntent.customType(MainActivity.this, "up-to-bottom");
            }
        });
    }
}

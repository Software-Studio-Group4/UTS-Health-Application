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

        Button signup_button = findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterEmailPge.class); //opens Register class when sign up btn is clicked
                startActivity(intent);
                CustomIntent.customType(MainActivity.this, "right-to-left");
            }
        });

        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class); //opens Login class when login_bg btn is clicked
                startActivity(intent);
                CustomIntent.customType(MainActivity.this, "left-to-right");
            }
        });
    }
}

package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import maes.tech.intentanim.CustomIntent;

// Email Registration page

public class RegisterEmailPge extends AppCompatActivity {
    private static String email;
    EditText emailTf;
    Button nextBtn;

    public static String getEmail() {
        return email;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_emailpge);

        nextBtn = findViewById(R.id.nextBtn);
        emailTf = findViewById(R.id.emailTf);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailTf.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailTf.setError("Cannot have an empty field");
                    return;
                }
                startActivity(new Intent(getApplicationContext(), RegisterPassPge.class));
                CustomIntent.customType(RegisterEmailPge.this, "fadein-to-fadeout");

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    } // Fade transition
}


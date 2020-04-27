package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import maes.tech.intentanim.CustomIntent;

import static uts.group4.UTShealth.RegisterEmailPge.getEmail;

// Password Registration page

public class RegisterPassPge extends AppCompatActivity {
    private static final String TAG = "RegisterPassPge";
    private static String password;
    EditText passwordTf;
    Button nextBtn2;
    String userEmail = getEmail();

    public static String getPass() {
        return password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_passpge);
        Log.d(TAG, "oncreate: " + userEmail);

        passwordTf = findViewById(R.id.passwordTf);
        nextBtn2 = findViewById(R.id.nextBtn);


        nextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordTf.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {
                    passwordTf.setError("Cannot have Empty Field");
                } else {
                    startActivity(new Intent(getApplicationContext(), RegisterDetailsPge.class));
                    CustomIntent.customType(RegisterPassPge.this, "fadein-to-fadeout");
                }

            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    } // Fade transition
}
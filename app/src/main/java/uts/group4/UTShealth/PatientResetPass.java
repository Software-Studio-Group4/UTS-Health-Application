package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

public class PatientResetPass extends AppCompatActivity {
    private EditText emailTf;
    private String userEmail;
    Button resetBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpass_layout);
        resetBtn = findViewById(R.id.resetBtn);
        emailTf = findViewById(R.id.emailTf);
        fAuth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = emailTf.getText().toString().trim();

                if (TextUtils.isEmpty(userEmail)) {
                    emailTf.setError("Cannot have an empty field");
                    return;
                }

                fAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PatientResetPass.this, "Password reset email sent", Toast.LENGTH_LONG).show();
                            emailTf.getText().clear();
                        } else {
                            Toast.makeText(PatientResetPass.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    public void backBtnPressed (View view) {
        startActivity(new Intent(getApplicationContext(), PatientLogin.class));
        CustomIntent.customType(PatientResetPass.this, "right-to-left");
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    } // Fade transition
}
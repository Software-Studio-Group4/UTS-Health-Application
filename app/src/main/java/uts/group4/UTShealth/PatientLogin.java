package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

// Patient Login Page

public class PatientLogin extends AppCompatActivity {
    private EditText emailTf, passwordTf;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    public void forgotPass(View view) {
        startActivity(new Intent(getApplicationContext(), PatientResetPass.class));
        CustomIntent.customType(PatientLogin.this, "left-to-right");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        emailTf = findViewById(R.id.emailTf);
        passwordTf = findViewById(R.id.passwordTf);
        Button userLoginBtn = findViewById(R.id.userLoginBtn);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);

        progressBar.setVisibility(View.INVISIBLE);

        userLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTf.getText().toString().trim();
                String password = passwordTf.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {
                    passwordTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailTf.setError("Cannot have Empty Field");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
                            CustomIntent.customType(PatientLogin.this, "left-to-right");
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(PatientLogin.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    } // Fade transition
}
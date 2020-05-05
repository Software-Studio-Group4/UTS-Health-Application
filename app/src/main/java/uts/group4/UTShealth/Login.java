package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

// Patient Login Page

public class Login extends AppCompatActivity {
    private EditText emailTf, passwordTf;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        emailTf = findViewById(R.id.emailTf);
        passwordTf = findViewById(R.id.passwordTf);
        Button userLoginBtn = findViewById(R.id.userLoginBtn);
        Button forgotpassBtn = findViewById(R.id.forgotpassBtn);
        fAuth = FirebaseAuth.getInstance();

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

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
                        } else {
                            Toast.makeText(Login.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        forgotpassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPassword.class));
                CustomIntent.customType(Login.this, "left-to-right");
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    } // Fade transition
}

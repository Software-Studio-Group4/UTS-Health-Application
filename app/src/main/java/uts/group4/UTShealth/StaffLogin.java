
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.Doctor;
import uts.group4.UTShealth.Model.DoctorClient;

// Staff Homepage

public class StaffLogin extends AppCompatActivity {
    EditText emailTf, passwordTf;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button backBtn;

    public void forgotPass(View view) {
        startActivity(new Intent(getApplicationContext(), StaffResetPass.class));
        CustomIntent.customType(StaffLogin.this, "left-to-right");
    }

    public void requestAcc(View view) {
        startActivity(new Intent(getApplicationContext(), StaffRequestAcc.class));
        CustomIntent.customType(StaffLogin.this, "left-to-right");
    }

    public void createAcc(View view) {
        startActivity(new Intent(getApplicationContext(), StaffCreateProfile.class));
        CustomIntent.customType(StaffLogin.this, "right-to-left");
    }

    public void backBtnPressed (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        CustomIntent.customType(StaffLogin.this, "bottom-to-up");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stafflogin_layout);
        final Button loginBtn = findViewById(R.id.loginBtn);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        backBtn = findViewById(R.id.backBtn);
        progressBar.setVisibility(View.INVISIBLE);
        emailTf = findViewById(R.id.emailTf);
        passwordTf = findViewById(R.id.passwordTf);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                final String userEmail = emailTf.getText().toString().trim();
                final String userPass = passwordTf.getText().toString().trim();
                if (TextUtils.isEmpty(userEmail)) {
                    emailTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(userPass)) {
                    passwordTf.setError("Cannot have Empty Field");
                    return;
                }

                fAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference staffRef = fStore.collection("Doctor").document(userID);
                            staffRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        startActivity(new Intent(getApplicationContext(), StaffDashboard.class));
                                        CustomIntent.customType(StaffLogin.this, "fadein-to-fadeout");
                                        progressBar.setVisibility(View.INVISIBLE);
                                        loginBtn.setVisibility(View.VISIBLE);

                                    } else {
                                        Toast.makeText(StaffLogin.this, "Invalid account", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        loginBtn.setVisibility(View.VISIBLE);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StaffLogin.this, "Database Error", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    loginBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            Toast.makeText(StaffLogin.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            loginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "bottom-to-up");
    } // Fade transition
}
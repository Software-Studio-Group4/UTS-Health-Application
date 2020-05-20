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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import maes.tech.intentanim.CustomIntent;

public class StaffRequestAcc extends AppCompatActivity {
    EditText fullNameTf, emailTf, phoneNumberTf;
    Button requestBtn;
    DatabaseReference ref;
    Member member;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffrequestacc_layout);

        fullNameTf = findViewById(R.id.fullNameTf);
        emailTf = findViewById(R.id.emailTf);
        phoneNumberTf = findViewById(R.id.phoneNumberTf);
        requestBtn = findViewById(R.id.requestBtn);

        member = new Member();
        ref = FirebaseDatabase.getInstance().getReference().child("StaffAccReq");
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = fullNameTf.getText().toString().trim();
                final String email = emailTf.getText().toString().trim();
                final String phoneNumber = phoneNumberTf.getText().toString().trim();
                member.setFullName(fullNameTf.getText().toString().trim());
                member.setEmail(emailTf.getText().toString().trim());
                member.setPhoneNumber(phoneNumberTf.getText().toString().trim());

                if (TextUtils.isEmpty(fullName)) {
                    fullNameTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberTf.setError("Cannot have Empty Field");
                    return;
                }

                ref.push().setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StaffRequestAcc.this, "Request received", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(StaffRequestAcc.this, "Invalid request", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    public static class Member {
        private String fullName;
        private String email;
        private String phoneNumber;

        public Member() {

        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

    }

    public void backBtnPressed (View view) {
        startActivity(new Intent(getApplicationContext(), StaffLogin.class));
        CustomIntent.customType(StaffRequestAcc.this, "right-to-left");
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    } // Fade transition
}


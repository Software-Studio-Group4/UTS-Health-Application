package uts.group4.UTShealth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

// Staff Profile Login Page

public class StaffCreateProfile extends AppCompatActivity {
    Button nextBtn;
    Button backBtn;
    EditText emailTf, passwordTf;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    private static String userEmail;

    public static String getEmail() {
        return userEmail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffcreateacc_layout);

        emailTf = findViewById(R.id.emailTf);
        passwordTf = findViewById(R.id.passwordTf);
        nextBtn = findViewById(R.id.nextBtn);
        backBtn = findViewById(R.id.backBtn7);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StaffHomepage.class));
                CustomIntent.customType(StaffCreateProfile.this, "fadein-to-fadeout");
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = emailTf.getText().toString().trim();
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
                                        startActivity(new Intent(getApplicationContext(), StaffAddDetails.class));
                                        CustomIntent.customType(StaffCreateProfile.this, "fadein-to-fadeout");
                                    } else {
                                        Toast.makeText(StaffCreateProfile.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StaffCreateProfile.this, "Database Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(StaffCreateProfile.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    } // Fade transition

    // Staff Profile Add Details

    public static class StaffAddDetails extends AppCompatActivity {
        EditText firstNameTf, lastNameTf, phoneNumberTf, streetAddressTf,
                cityTf, stateTf, postCodeTf;
        Button createProfileBtn;
        Button backBtn;
        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        String userID;
        String userEmail = getEmail();

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.staffadddetails_layout);

            createProfileBtn = findViewById(R.id.createProfileBtn);
            backBtn =findViewById(R.id.backBtn8);
            firstNameTf = findViewById(R.id.firstNameTf);
            lastNameTf = findViewById(R.id.lastNameTf);
            phoneNumberTf = findViewById(R.id.phoneNumberTf);
            streetAddressTf = findViewById(R.id.streetAddressTf);
            cityTf = findViewById(R.id.cityTf);
            stateTf = findViewById(R.id.stateTf);
            postCodeTf = findViewById(R.id.postCodeTf);
            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();

            phoneNumberTf.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final String phoneNumber = phoneNumberTf.getText().toString().trim();
                    if (phoneNumber.length()>2) {
                        String code = phoneNumber.substring(0, 2);
                        if (!code.equals("04")){
                            phoneNumberTf.setError("Must be a valid number");
                            Toast toast = Toast.makeText(StaffAddDetails.this, "Invalid Number entered!", Toast.LENGTH_SHORT);
                            TextView v = toast.getView().findViewById(android.R.id.message);
                            toast.getView().setBackgroundColor(Color.RED);
                            v.setTextColor(Color.WHITE);
                            toast.show();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), StaffCreateProfile.class));
                    CustomIntent.customType(StaffAddDetails.this, "fadein-to-fadeout");
                }
            });

            createProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String firstName = firstNameTf.getText().toString().trim();
                    final String lastName = lastNameTf.getText().toString().trim();
                    final String phoneNumber = phoneNumberTf.getText().toString().trim();
                    final String streetAddress = streetAddressTf.getText().toString().trim();
                    final String city = cityTf.getText().toString().trim();
                    final String state = stateTf.getText().toString().trim();
                    final String postCode = postCodeTf.getText().toString().trim();

                    if (TextUtils.isEmpty(firstName)) {
                        firstNameTf.setError("Cannot have Empty Field");
                        return;
                    }
                    if (TextUtils.isEmpty(lastName)) {
                        lastNameTf.setError("Cannot have Empty Field");
                        return;
                    }
                    if (TextUtils.isEmpty(phoneNumber)) {
                        phoneNumberTf.setError("Cannot have Empty Field");
                        return;
                    }
                    if (TextUtils.isEmpty(streetAddress)) {
                        streetAddressTf.setError("Cannot have Empty Field");
                        return;
                    }
                    if (TextUtils.isEmpty(city)) {
                        cityTf.setError("Cannot have Empty Field");
                        return;
                    }
                    if (TextUtils.isEmpty(state)) {
                        stateTf.setError("Cannot have Empty Field");
                        return;
                    }
                    if (TextUtils.isEmpty(postCode)) {
                        postCodeTf.setError("Cannot have Empty Field");
                        return;
                    }

                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Doctor").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("First Name", firstName);
                    user.put("Last Name", lastName);
                    user.put("Email", userEmail);
                    user.put("Phone Number", phoneNumber);
                    user.put("Street Address", streetAddress);
                    user.put("Suburb", city);
                    user.put("State", state);
                    user.put("Post Code", postCode);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(getApplicationContext(), StaffFinishPage.class));
                            CustomIntent.customType(StaffAddDetails.this, "fadein-to-fadeout");
                        }
                    });
                }
            });
        }

        @Override
        public void finish() {
            super.finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        } // Fade transition
    }

    // Staff Profile finish page

    public static class StaffFinishPage extends AppCompatActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.stafffinish_layout);
            Button loginBtn = findViewById(R.id.loginBtn);
            Button backBtn = findViewById(R.id.backBtn9);

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), StaffAddDetails.class));
                    CustomIntent.customType(StaffFinishPage.this, "fadein-to-fadeout");
                }
            });

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), StaffHomepage.class));
                    CustomIntent.customType(StaffFinishPage.this, "left-to-right");
                }
            });
        }

        @Override
        public void finish() {
            super.finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        } // Fade transition
    }
}
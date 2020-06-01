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

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

/**********************************************************************************************
 * Staff create profile login page
 * manipulates the page where the staff member can login to create their profile
 ************************************************************************************************/

public class StaffCreateProfile extends AppCompatActivity {
    Button loginBtn;
    EditText emailTf, passwordTf;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    private static String userEmail;
    private static String userPass;

    public static String getEmail() {
        return userEmail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffcreateacc_layout);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        emailTf = findViewById(R.id.emailTf);
        passwordTf = findViewById(R.id.passwordTf);
        loginBtn = findViewById(R.id.loginBtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                userEmail = emailTf.getText().toString().trim();
                userPass = passwordTf.getText().toString().trim();
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
                                        progressBar.setVisibility(View.INVISIBLE);
                                        loginBtn.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(StaffCreateProfile.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        loginBtn.setVisibility(View.VISIBLE);
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StaffCreateProfile.this, "Database Error", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    loginBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            Toast.makeText(StaffCreateProfile.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            loginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

        });
    }

    public void backBtnPressed(View view) {
        startActivity(new Intent(getApplicationContext(), StaffLogin.class));
        CustomIntent.customType(StaffCreateProfile.this, "left-to-right");
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    } // Fade transition

    /**********************************************************************************************
     * Staff add details page
     * manipulates the page where the staff inputs registration details (such as name, address etc)
     ************************************************************************************************/

    public static class StaffAddDetails extends AppCompatActivity {
        EditText firstNameTf, lastNameTf, phoneNumberTf, streetAddressTf,
                cityTf, stateTf, postCodeTf;
        Button createProfileBtn, backBtn;
        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        String userID;
        String userEmail = getEmail();

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.staffadddetails_layout);

            createProfileBtn = findViewById(R.id.createProfileBtn);
            backBtn = findViewById(R.id.backBtn);
            firstNameTf = findViewById(R.id.firstNameTf);
            lastNameTf = findViewById(R.id.lastNameTf);
            phoneNumberTf = findViewById(R.id.phoneNumberTf);
            streetAddressTf = findViewById(R.id.streetAddressTf);
            cityTf = findViewById(R.id.cityTf);
            stateTf = findViewById(R.id.stateTf);
            postCodeTf = findViewById(R.id.postCodeTf);

            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();

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
                    user.put("UrgentStatus", false);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(getApplicationContext(), StaffFinishPage.class));
                            CustomIntent.customType(StaffAddDetails.this, "fadein-to-fadeout");
                        }
                    });
                }
            });

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    CustomIntent.customType(StaffAddDetails.this, "fadein-to-fadeout");
                }
            });
        }

        @Override
        public void finish() {
            super.finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        } // Fade transition
    }

    /**********************************************************************************************
     * Staff registration complete page
     * manipulates the page where it informs the staff that registration is complete
     ************************************************************************************************/

    public static class StaffFinishPage extends AppCompatActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.stafffinish_layout);
            Button loginBtn = findViewById(R.id.loginBtn);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), StaffLogin.class));
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
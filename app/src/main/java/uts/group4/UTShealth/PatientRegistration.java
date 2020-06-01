package uts.group4.UTShealth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import android.text.TextWatcher;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maes.tech.intentanim.CustomIntent;

import static uts.group4.UTShealth.PatientRegistration.RegisterPassPge.getPass;

// Enter email address page

public class PatientRegistration extends AppCompatActivity {
    private static String email;
    EditText emailTf;
    Button nextBtn, backBtn;

    public static String getEmail() {
        return email;
    }

    /*PAGE MANIPULATION METHODS*/

    /**********************************************************************************************
     * onCreate
     *manipulates the page where the patient inputs email.
     **********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_emailpge);
        nextBtn = findViewById(R.id.nextBtn);
        emailTf = findViewById(R.id.emailTf);
        backBtn = findViewById(R.id.backBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailTf.getText().toString().trim();
                String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";  //Email regex. Change this to change email format required.
                Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = emailPattern.matcher(email);
                boolean isValid = matcher.find();

                if (TextUtils.isEmpty(email)) {
                    emailTf.setError("Cannot have an empty field");
                    return;
                }
                if (!isValid) {
                    emailTf.setError("Not a valid email address");
                    return;
                }
                startActivity(new Intent(getApplicationContext(), RegisterPassPge.class));
                CustomIntent.customType(PatientRegistration.this, "fadein-to-fadeout");

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                CustomIntent.customType(PatientRegistration.this, "left-to-right");
            }
        });
    }


    /**********************************************************************************************
     * RegisterPassPge
     *manipulates the page where the patient registers password
     ************************************************************************************************/
    public static class RegisterPassPge extends AppCompatActivity {
        private static String password;
        EditText passwordTf;
        Button nextBtn2, backBtn;

        public static String getPass() {
            return password;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reg_passpge);
            passwordTf = findViewById(R.id.passwordTf);
            nextBtn2 = findViewById(R.id.nextBtn);
            backBtn = findViewById(R.id.backBtn);

            nextBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password = passwordTf.getText().toString().trim();
                    String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.{6,})";  //Password regex. Change this to change password complexity requirements
                    Pattern emailPattern = Pattern.compile(passwordRegex);
                    Matcher matcher = emailPattern.matcher(password);
                    boolean isValid = matcher.find();

                    if (TextUtils.isEmpty(password)) {
                        passwordTf.setError("Cannot have Empty Field");
                        return;
                    }
                    if (!isValid) {                                            //Checks for password complexity and lets users know what they're missing
                        if (!password.matches("(.{6,})")) {
                            passwordTf.setError("Password must contain 6 characters or more");
                            return;
                        }else if (!password.matches("(.*[a-z])")) {
                            passwordTf.setError("Password must contain at least one lowercase character");
                            return;
                        }else if (!password.matches("(.*[A-Z])")) {
                            passwordTf.setError("Password must contain at least one uppercase character");
                            return;
                        }
                    }
                        startActivity(new Intent(getApplicationContext(), RegisterDetailsPge.class));
                        CustomIntent.customType(RegisterPassPge.this, "fadein-to-fadeout");
                }
            });

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), PatientRegistration.class));
                    CustomIntent.customType(RegisterPassPge.this, "fadein-to-fadeout");
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
     * RegisterDetailsPge
     * manipulates the page where the patient inputs registration details (such as name, address etc)
     ************************************************************************************************/
    public static class RegisterDetailsPge extends AppCompatActivity {
        String userEmail = getEmail();
        String userPass = getPass();

        EditText firstNameTf, lastNameTf, medicareNumberTf, streetAddressTf,
                cityTf, stateTf, postCodeTf, phoneNumberTf;
        Button nextBtn2, backBtn;
        ProgressBar progressBar;
        Switch billingSwitch;
        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        String userID;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reg_detailspage);

            // GET ALL THE OBJECTS FROM THE VIEW TO MANIPULATE
            nextBtn2 = findViewById(R.id.nextBtn2);
            backBtn = findViewById(R.id.backBtn);
            billingSwitch = findViewById(R.id.billing_switch);
            firstNameTf = findViewById(R.id.firstNameTf);
            lastNameTf = findViewById(R.id.lastNameTf);
            medicareNumberTf = findViewById(R.id.medicareNumberTf);
            streetAddressTf = findViewById(R.id.streetAddressTf);
            cityTf = findViewById(R.id.cityTf);
            stateTf = findViewById(R.id.stateTf);
            postCodeTf = findViewById(R.id.postCodeTf);
            phoneNumberTf = findViewById(R.id.phoneNumberTf);
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

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
                            Toast toast = Toast.makeText(RegisterDetailsPge.this, "Invalid Number entered!", Toast.LENGTH_SHORT);
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

            nextBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextBtn2.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    final String city = cityTf.getText().toString().trim();
                    final String firstName = firstNameTf.getText().toString().trim();
                    final String lastName = lastNameTf.getText().toString().trim();
                    final String medicareNumber = medicareNumberTf.getText().toString().trim();
                    final String phoneNumber = phoneNumberTf.getText().toString().trim();
                    final String postCode = postCodeTf.getText().toString().trim();
                    final String state = stateTf.getText().toString().trim();
                    final String streetAddress = streetAddressTf.getText().toString().trim();
                    final ArrayList<String> appointments = new ArrayList<String>();


                    if (TextUtils.isEmpty(firstName)) {
                        firstNameTf.setError("Cannot have Empty Field");
                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn2.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (TextUtils.isEmpty(lastName)) {
                        lastNameTf.setError("Cannot have Empty Field");
                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn2.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (TextUtils.isEmpty(phoneNumber)) {
                        phoneNumberTf.setError("Cannot have Empty Field");
                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn2.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (TextUtils.isEmpty(streetAddress)) {
                        streetAddressTf.setError("Cannot have Empty Field");
                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn2.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (TextUtils.isEmpty(city)) {
                        cityTf.setError("Cannot have Empty Field");
                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn2.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (TextUtils.isEmpty(state)) {
                        stateTf.setError("Cannot have Empty Field");
                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn2.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (TextUtils.isEmpty(postCode)) {
                        postCodeTf.setError("Cannot have Empty Field");
                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn2.setVisibility(View.VISIBLE);
                        return;
                    }

                    fAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("Patients").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("Email", userEmail);
                                user.put("First Name", firstName);
                                user.put("Last Name", lastName);
                                user.put("City", city);
                                user.put("Medicare Number", medicareNumber);
                                user.put("Phone Number", phoneNumber);
                                user.put("Post Code", postCode);
                                user.put("State", state);
                                user.put("Street Address", streetAddress);
                                user.put("Appointments", appointments);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (billingSwitch.isChecked()) {
                                            startActivity(new Intent(getApplicationContext(), RegisterFinishPge.class));
                                            CustomIntent.customType(RegisterDetailsPge.this, "fadein-to-fadeout");
                                            progressBar.setVisibility(View.INVISIBLE);
                                            nextBtn2.setVisibility(View.VISIBLE);
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), reg_billing_address.class));
                                            CustomIntent.customType(RegisterDetailsPge.this, "left-to-right");
                                            progressBar.setVisibility(View.INVISIBLE);
                                            nextBtn2.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterDetailsPge.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                nextBtn2.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterPassPge.class));
                    CustomIntent.customType(RegisterDetailsPge.this, "fadein-to-fadeout");
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
     * Register Billing Address
     * manipulates the page where the patient enters their billing address
     ************************************************************************************************/
    public static class reg_billing_address extends AppCompatActivity {
        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        String userID;
        Button backBtn;
        private static final String TAG = "Billing Address Page";

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reg_billingaddress);
            backBtn = findViewById(R.id.backBtn);

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterDetailsPge.class));
                    CustomIntent.customType(reg_billing_address.this, "fadein-to-fadeout");
                }
            });
        }

        public void nextPage(View view) {
            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();
            EditText streetAddressTf = findViewById(R.id.streetAddressTf);
            EditText cityTf = findViewById(R.id.cityTf);
            EditText stateTf = findViewById(R.id.stateTf);
            EditText postcodeTf = findViewById(R.id.postCodeTf);

            final String streetAddress = streetAddressTf.getText().toString().trim();
            final String city = cityTf.getText().toString().trim();
            final String state = stateTf.getText().toString().trim();
            final String postcode = postcodeTf.getText().toString().trim();

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
            if (TextUtils.isEmpty(postcode)) {
                postcodeTf.setError("Cannot have Empty Field");
                return;
            }

            userID = fAuth.getCurrentUser().getUid();
            DocumentReference documentReference = fStore.collection("Patients").document(userID);
            Map<String, Object> user = new HashMap<>();
            user.put("Billing Street Address", streetAddress);
            user.put("Billing City", city);
            user.put("Billing State", state);
            user.put("Billing Postcode", postcode);
            documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(getApplicationContext(), RegisterFinishPge.class));
                    CustomIntent.customType(reg_billing_address.this, "fadein-to-fadeout");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(reg_billing_address.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**********************************************************************************************
     * RegisterFinishPge
     * manipulates the page where it informs the patient that the registration is complete
     ************************************************************************************************/
    public static class RegisterFinishPge extends AppCompatActivity {

        Button loginBtn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reg_finishpge);

            loginBtn = findViewById(R.id.loginBtn);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), PatientLogin.class));
                    CustomIntent.customType(RegisterFinishPge.this, "left-to-right");
                }
            });

        }

        @Override
        public void finish() {
            super.finish();
            CustomIntent.customType(this, "right-to-left");
        } // Fade transition
    }


    /**********************************************************************************************
     * OTHER METHODS
     ************************************************************************************************/
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    } // Fade transition
}

package uts.group4.UTShealth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

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
    Button nextBtn;
    Button backBtn;


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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                CustomIntent.customType(PatientRegistration.this, "fadein-to-fadeout");
            }
        });

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
    }

    /**********************************************************************************************
     * RegisterPassPge
     *manipulates the page where the patient registers password
     ************************************************************************************************/
    public static class RegisterPassPge extends AppCompatActivity {
        private static final String TAG = "RegisterPassPge";
        private static String password;
        EditText passwordTf;
        Button nextBtn2;
        Button backBtn2;
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
            backBtn2 = findViewById(R.id.backBtn2);

            backBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), PatientRegistration.class));
                    CustomIntent.customType(RegisterPassPge.this, "fadein-to-fadeout");
                }
            });


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
        private static final String TAG = "RegisterDetailsPge";
        String userEmail = getEmail();
        String userPass = getPass();

        EditText firstNameTf, lastNameTf, medicareNumberTf, streetAddressTf,
                cityTf, stateTf, postCodeTf, phoneNumberTf;
        Button nextBtn2;
        Button backBtn3;
        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        String userID;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reg_detailspage);
            Log.d(TAG, "onCreate " + userEmail + userPass);

            // GET ALL THE OBJECTS FROM THE VIEW TO MANIPULATE
            nextBtn2 = findViewById(R.id.nextBtn2);
            backBtn3 = findViewById(R.id.backBtn3);
            firstNameTf = findViewById(R.id.firstNameTf);
            lastNameTf = findViewById(R.id.lastNameTf);
            medicareNumberTf = findViewById(R.id.medicareNumberTf);
            streetAddressTf = findViewById(R.id.streetAddressTf);
            cityTf = findViewById(R.id.cityTf);
            stateTf = findViewById(R.id.stateTf);
            postCodeTf = findViewById(R.id.postCodeTf);
            phoneNumberTf = findViewById(R.id.phoneNumberTf);

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

            backBtn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterPassPge.class));
                    CustomIntent.customType(RegisterDetailsPge.this, "fadein-to-fadeout");
                }
            });

            nextBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String city = cityTf.getText().toString().trim();
                    final String firstName = firstNameTf.getText().toString().trim();
                    final String lastName = lastNameTf.getText().toString().trim();
                    final String medicareNumber = medicareNumberTf.getText().toString().trim();
                    final String phoneNumber = phoneNumberTf.getText().toString().trim();
                    final String postCode = postCodeTf.getText().toString().trim();
                    final String state = stateTf.getText().toString().trim();
                    final String streetAddress = streetAddressTf.getText().toString().trim();
                    final Map<String, Object> appointments = new HashMap<>();


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

                    if (TextUtils.isEmpty(medicareNumber)) {
                        medicareNumberTf.setError("Cannot have Empty Field");
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

                    fAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), RegisterFinishPge.class));
                                CustomIntent.customType(RegisterDetailsPge.this, "fadein-to-fadeout");
                                Toast.makeText(RegisterDetailsPge.this, "Registration successful", Toast.LENGTH_SHORT).show();
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
                                        Log.d(TAG, "onSucess: user profile has been created for user" + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure" + e.toString());
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterDetailsPge.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

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

    /**********************************************************************************************
     * RegisterFinishPge
     * manipulates the page where it informs the patient that the registration is complete
     ************************************************************************************************/
    public static class RegisterFinishPge extends AppCompatActivity {

        Button loginBtn;
        Button backBtn4;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reg_finishpge);

            loginBtn = findViewById(R.id.loginBtn);
            backBtn4 = findViewById(R.id.backBtn4);

            backBtn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterDetailsPge.class));
                    CustomIntent.customType(RegisterFinishPge.this, "fadein-to-fadeout");
                }
            });
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

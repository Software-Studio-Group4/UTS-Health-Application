package uts.group4.UTShealth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText emailTf, passwordTf, firstNameTf, lastNameTf, ageTf, medicareNumberTf, streetAddressTf,
             cityTf, stateTf, postCodeTf, confirmPasswordTf, phoneNumberTf;
    Button registerBtn;
    DatabaseReference dbRef;
    Patient patient;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // GET ALL THE OBJECTS FROM THE VIEW TO MANIPULATE
        registerBtn = (Button) findViewById(R.id.registerBtn);

        emailTf = (EditText)findViewById(R.id.emailTf);
        passwordTf = (EditText)findViewById(R.id.passwordTf);
        firstNameTf = (EditText)findViewById(R.id.firstNameTf);
        lastNameTf = (EditText) findViewById(R.id.lastNameTf);
        confirmPasswordTf = (EditText) findViewById((R.id.confirmPasswordTf));
        ageTf = (EditText) findViewById(R.id.ageTf);
        medicareNumberTf = (EditText) findViewById(R.id.medicareNumberTf);
        streetAddressTf = (EditText) findViewById(R.id.streetAddressTf);
        cityTf = (EditText) findViewById(R.id.cityTf);
        stateTf = (EditText) findViewById(R.id.stateTf);
        postCodeTf = (EditText) findViewById(R.id.postCodeTf);
        phoneNumberTf = (EditText) findViewById(R.id.phoneNumberTf);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailTf.getText().toString().trim();
                String password = passwordTf.getText().toString().trim();
                int age = Integer.parseInt(ageTf.getText().toString().trim());
                final String city = cityTf.getText().toString().trim();
                final String firstName = firstNameTf.getText().toString().trim();
                final String lastName = lastNameTf.getText().toString().trim();
                final String medicareNumber = medicareNumberTf.getText().toString().trim();
                final String phoneNumber = medicareNumberTf.getText().toString().trim();
                final String postCode = postCodeTf.getText().toString().trim();
                final String state = stateTf.getText().toString().trim();
                final String streetAddress = streetAddressTf.getText().toString().trim();

                if(TextUtils.isEmpty(password)){
                    passwordTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(city)){
                    cityTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(firstName)){
                    firstNameTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(lastName)){
                    lastNameTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(medicareNumber)){
                    medicareNumberTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    phoneNumberTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(postCode)){
                    postCodeTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(state)){
                    stateTf.setError("Cannot have Empty Field");
                    return;
                }
                if(TextUtils.isEmpty(streetAddress)){
                    streetAddressTf.setError("Cannot have Empty Field");
                    return;
                }
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                                Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("Patients").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("firstName", firstName);
                            user.put("lastName", lastName);
                            user.put("city", city);
                            user.put("medicareNumber", medicareNumber);
                            user.put("phoneNumber", phoneNumber);
                            user.put("postCode", postCode);
                            user.put("state", state);
                            user.put("streetAddress", streetAddress);
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
                                startActivity(new Intent(getApplicationContext(), Login.class));
                        }else{
                            Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        }

    }



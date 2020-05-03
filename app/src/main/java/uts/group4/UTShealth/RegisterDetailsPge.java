package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import maes.tech.intentanim.CustomIntent;

import static uts.group4.UTShealth.RegisterEmailPge.getEmail;
import static uts.group4.UTShealth.RegisterPassPge.getPass;

// Patient details Registration page

public class RegisterDetailsPge extends AppCompatActivity {
    private static final String TAG = "RegisterDetailsPge";
    String userEmail = getEmail();
    String userPass = getPass();

    EditText firstNameTf, lastNameTf, medicareNumberTf, streetAddressTf,
            cityTf, stateTf, postCodeTf, phoneNumberTf;
    Button nextBtn2;
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

        nextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String city = cityTf.getText().toString().trim();
                final String firstName = firstNameTf.getText().toString().trim();
                final String lastName = lastNameTf.getText().toString().trim();
                final String medicareNumber = medicareNumberTf.getText().toString().trim();
                final String phoneNumber = medicareNumberTf.getText().toString().trim();
                final String postCode = postCodeTf.getText().toString().trim();
                final String state = stateTf.getText().toString().trim();
                final String streetAddress = streetAddressTf.getText().toString().trim();


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
                            user.put("email", userEmail);
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


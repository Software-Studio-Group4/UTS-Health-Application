package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import maes.tech.intentanim.CustomIntent;

public class PatientProfilePage extends AppCompatActivity {
    TextView patientName, street, city, state, postCode, phoneNumber, medicare, email;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile);
        patientName = findViewById(R.id.nameTf);
        street = findViewById(R.id.streetTf);
        city = findViewById(R.id.cityTf);
        state = findViewById(R.id.stateTf);
        postCode = findViewById(R.id.postCodeTf);
        phoneNumber = findViewById(R.id.numberTf);
        medicare = findViewById(R.id.medicareTf);
        email = findViewById(R.id.emailTf);
        DocumentReference docRef = fStore.collection("Patients").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("First Name");
                    String lastName = documentSnapshot.getString("Last Name");
                    patientName.setText("Name: " + firstName + " " + lastName); // Displays doctor's name
                    String streetText = documentSnapshot.getString("Street Address");
                    street.setText("Street: " + streetText); // Displays street
                    String cityText = documentSnapshot.getString("City");
                    city.setText("City: " + cityText); // Displays city
                    String stateText = documentSnapshot.getString("State");
                    state.setText("State: " + stateText); // Displays state
                    String postText = documentSnapshot.getString("Post Code");
                    postCode.setText("Post Code: " + postText); // Displays post code
                    String phoneText = documentSnapshot.getString("Phone Number");
                    phoneNumber.setText("Phone Number: " + phoneText); // Displays phone number
                    if (documentSnapshot.getString("Medicare Number") != ""){
                        String medicareText = documentSnapshot.getString("Medicare Number");
                        medicare.setText("Medicare Number: " + medicareText); // Displays medicare number
                    } else {
                        medicare.setText("No Medicare Number provided.");
                    }
                    String emailText = documentSnapshot.getString("Email");
                    email.setText("Email: " + emailText); // Displays email
                } else {
                    Toast.makeText(PatientProfilePage.this, "Cannot retrieve details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        CustomIntent.customType(PatientProfilePage.this, "fadein-to-fadeout");
        finish();
    }

    public void backBtnPressed (View view) {
        finish();
        CustomIntent.customType(PatientProfilePage.this, "right-to-left");
    }
}

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

public class StaffProfilePage extends AppCompatActivity {
    TextView doctorName, street, city, state, postCode, phoneNumber;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_profile);
        doctorName = findViewById(R.id.doctorTf);
        street = findViewById(R.id.streetTf);
        city = findViewById(R.id.cityTf);
        state = findViewById(R.id.stateTf);
        postCode = findViewById(R.id.postCodeTf);
        phoneNumber = findViewById(R.id.numberTf);
        DocumentReference docRef = fStore.collection("Doctor").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("First Name");
                    String lastName = documentSnapshot.getString("Last Name");
                    doctorName.setText("Name: " + firstName + " " + lastName); // Displays doctor's name
                    String streetText = documentSnapshot.getString("Street Address");
                    street.setText("Street: " + streetText); // Displays street
                    String cityText = documentSnapshot.getString("Suburb");
                    city.setText("City: " + cityText); // Displays city
                    String stateText = documentSnapshot.getString("State");
                    state.setText("State: " + stateText); // Displays state
                    String postText = documentSnapshot.getString("Post Code");
                    postCode.setText("Post Code: " + postText); // Displays post code
                    String phoneText = documentSnapshot.getString("Phone Number");
                    phoneNumber.setText("Phone Number: " + phoneText); // Displays phone number
                } else {
                    Toast.makeText(StaffProfilePage.this, "Cannot retrieve details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        CustomIntent.customType(StaffProfilePage.this, "fadein-to-fadeout");
        finish();
    }

    public void backBtnPressed (View view) {
        startActivity(new Intent(getApplicationContext(), StaffDashboard.class));
        CustomIntent.customType(StaffProfilePage.this, "right-to-left");
    }
}

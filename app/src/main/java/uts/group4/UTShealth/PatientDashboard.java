package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import maes.tech.intentanim.CustomIntent;

/**********************************************************************************************
 * Patient Dashboard / Upcoming appointments
 * manipulates the landing page for the patient. Also where upcoming appointments are displayed
 ************************************************************************************************/

public class PatientDashboard extends AppCompatActivity {
    private static final String KEY_NAME = "First Name";
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    CollectionReference appointmentRef = fStore.collection("Appointment");
    DocumentReference nameRef;
    TextView welcomeText;
    TextView textViewData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard);
        welcomeText = findViewById(R.id.welcomeText);
        textViewData = findViewById(R.id.textViewData);
        nameRef = fStore.collection("Patients").document(userID);
        welcomeMessage();
        showAppts();
    }

public void showAppts () { // Method to display upcoming appointments from Firestore
        appointmentRef.whereEqualTo("patientID", userID) // Filter by patientID in Firestore "Appointment" collection
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Appointment appointment = documentSnapshot.toObject(Appointment.class);
                    appointment.setAppointmentID(documentSnapshot.getId());

                    String date = appointment.getDate();
                    String time = appointment.getTime();

                    data += "Date: " + date + "\nTime: " + time + "\n\n";
                }
                textViewData.setText(data);
            }
        });
}

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    } // Fade transition

    /**********************************************************************************************
     * Past appointmetns page
     * manipulates the page where past appointments are shown
     ************************************************************************************************/

    public static class PatientPastAppointments extends AppCompatActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.past_appointments);
        }

        public void upcomingAppt(View view) {
            startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
            CustomIntent.customType(PatientPastAppointments.this, "fadein-to-fadeout");
        }

        public void bookAppt(View view) {
            startActivity(new Intent(getApplicationContext(), BookAppointment.class));
        }

        @Override
        public void finish() {
            super.finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        } // Fade transition
    }

    /**********************************************************************************************
     * Onlclick and other methods
     ************************************************************************************************/

    public void pastAppt(View view) {
        startActivity(new Intent(getApplicationContext(), PatientPastAppointments.class));
        CustomIntent.customType(PatientDashboard.this, "fadein-to-fadeout");
    }

    public void bookAppt(View view) {
        startActivity(new Intent(getApplicationContext(), BookAppointment.class));
    }

    public void userProfile(View view) {
        startActivity(new Intent(getApplicationContext(), PatientProfilePage.class));
    }

    public void openChat(View view) {
        startActivity(new Intent(getApplicationContext(), Chat.class));
    }

    public void welcomeMessage() {
        nameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString(KEY_NAME);
                    welcomeText.setText("Welcome, " + name + "!");
                } else {
                    Toast.makeText(PatientDashboard.this, "Error: Welcome message", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PatientDashboard.this, "Error: Welcome message", Toast.LENGTH_SHORT).show();
            }
        });
    } // Method to display welcome message and get name from Firestore
}



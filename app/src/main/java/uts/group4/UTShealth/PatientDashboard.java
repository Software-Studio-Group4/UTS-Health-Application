package uts.group4.UTShealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;

/**********************************************************************************************
 * Patient Dashboard / Upcoming appointments
 * manipulates the landing page for the patient. Also where upcoming appointments are displayed
 ************************************************************************************************/

public class PatientDashboard extends AppCompatActivity {
    private static final String FIELD_NAME = "First Name";
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    CollectionReference appointmentRef = fStore.collection("Appointment");
    DocumentReference nameRef = fStore.collection("Patients").document(userID);
    TextView textViewData;
    TextView welcomeText;
    private RecyclerView appointmentsRecyclerView;
    private FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder> appointmentAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard);
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        welcomeText = findViewById(R.id.welcomeText);
        Query appointmentQuery = appointmentRef.whereEqualTo("patientID", userID).orderBy("Date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<AppointmentModel> options = new FirestoreRecyclerOptions.Builder<AppointmentModel>().setQuery(appointmentQuery, AppointmentModel.class).build();

        appointmentAdapter = new FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AppointmentViewHolder appointmentViewHolder, int position, @NonNull AppointmentModel appointmentModel) {
                appointmentViewHolder.setAppointmentName(appointmentModel.getDate(), appointmentModel.getTime(), appointmentModel.getDoctorFullName(), appointmentModel.getChatCode());
            }

            @NonNull
            @Override
            public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
                return new AppointmentViewHolder(view);
            }
        };
      appointmentsRecyclerView.setAdapter(appointmentAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        appointmentAdapter.startListening();

        nameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString(FIELD_NAME);
                    welcomeText.setText("Welcome, " + name + "!"); // Display welcome text
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
    }

    // Fade transition
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (appointmentAdapter != null) {
            appointmentAdapter.stopListening();
        }
    }

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
    /**********************************************************************************************
     * Private Class for the recycler
     ************************************************************************************************/
    private class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private View view;

        AppointmentViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setAppointmentName(String date, String time, String doctor, final String chatCode) {
            TextView textView = view.findViewById(R.id.appointmentTextView);
            textView.setText("Date: " + date + "\nTime: " + time  + "\nDoctor: " + doctor + "\n");

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chatCode != null){

                        Intent i = new Intent(PatientDashboard.this, Chat.class);
                        i.putExtra("chatroomcode", chatCode);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(PatientDashboard.this, "NO CHAT ROOM CODE FOUND", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}



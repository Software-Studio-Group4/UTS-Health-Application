package uts.group4.UTShealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;
import uts.group4.UTShealth.Util.DATParser;

/**********************************************************************************************
 * Patient Dashboard / Upcoming appointments
 * manipulates the landing page for the patient. Also where upcoming appointments are displayed
 ************************************************************************************************/

public class PatientDashboard extends AppCompatActivity {
    private static final String FIELD_NAME = "First Name";
    private static final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private static final String userID = fAuth.getCurrentUser().getUid();
    private static final CollectionReference appointmentRef = fStore.collection("Appointment");
    DocumentReference nameRef = fStore.collection("Patients").document(userID);
    TextView textViewData;
    TextView welcomeText;
    String appointmentID;
    private RecyclerView appointmentsRecyclerView;
    private FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder> appointmentAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard);
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        welcomeText = findViewById(R.id.welcomeText);
        Query appointmentQuery = appointmentRef.whereEqualTo("patientID", userID).whereEqualTo("CompletionStatus", false).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<AppointmentModel> options = new FirestoreRecyclerOptions.Builder<AppointmentModel>().setQuery(appointmentQuery, AppointmentModel.class).build();

        appointmentAdapter = new FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull AppointmentViewHolder appointmentViewHolder, int position, @NonNull AppointmentModel appointmentModel) {
                Log.i("DASHBOARD","timestamp status: " + appointmentModel.getTimeStamp());
                String appointmentID = getSnapshots().getSnapshot(position).getId();
                appointmentViewHolder.setAppointmentName(appointmentModel.getDate(), appointmentModel.getTime(), appointmentModel.getDoctorFullName(), appointmentModel.getChatCode(), appointmentID, appointmentModel.getTimeStamp());
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
     * Methods for deleting appointments
     ************************************************************************************************/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_1:
                // Appointment deleted here -->
                fStore.collection("Appointment").document(appointmentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PatientDashboard.this, "Appointment deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PatientDashboard.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**********************************************************************************************
     * Past appointmetns page
     * manipulates the page where past appointments are shown
     ************************************************************************************************/

    public static class PatientPastAppointments extends AppCompatActivity {
        private RecyclerView appointmentsRecycler;
        private FirestoreRecyclerAdapter<AppointmentModel, PastAppointmentViewHolder> appointmentAdapter;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.past_appointments);
            appointmentsRecycler = findViewById(R.id.pastAppointmentsRecycler);
            appointmentsRecycler.setLayoutManager(new LinearLayoutManager(this));
            Query appointmentQuery = appointmentRef.whereEqualTo("patientID", userID).whereEqualTo("CompletionStatus", true).orderBy("TimeStamp", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<AppointmentModel> options = new FirestoreRecyclerOptions.Builder<AppointmentModel>().setQuery(appointmentQuery, AppointmentModel.class).build();

            appointmentAdapter = new FirestoreRecyclerAdapter<AppointmentModel, PastAppointmentViewHolder>(options) {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                protected void onBindViewHolder(@NonNull PastAppointmentViewHolder appointmentViewHolder, int position, @NonNull AppointmentModel appointmentModel) {
                    Log.i("DASHBOARD","timestamp status: " + appointmentModel.getTimeStamp());
                    String appointmentID = getSnapshots().getSnapshot(position).getId();
                    appointmentViewHolder.setPastAppointmentData(appointmentModel.getDate(), appointmentModel.getTime(), appointmentModel.getDoctorFullName(), appointmentID);
                }

                @NonNull
                @Override
                public PastAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
                    return new PastAppointmentViewHolder(view);
                }
            };
            appointmentsRecycler.setAdapter(appointmentAdapter);
        }

        public void upcomingAppt(View view) {
            startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
            CustomIntent.customType(PatientPastAppointments.this, "fadein-to-fadeout");
        }

        public void bookAppt(View view) {
            startActivity(new Intent(getApplicationContext(), BookAppointment.class));
        }

        public void userProfile(View view) {
            startActivity(new Intent(getApplicationContext(), PatientProfilePage.class));
            CustomIntent.customType(PatientPastAppointments.this, "left-to-right");
        }

        @Override
        protected void onStart(){
            super.onStart();
            appointmentAdapter.startListening();
        }

        @Override
        protected void onStop(){
            super.onStop();
            if(appointmentAdapter != null){
                appointmentAdapter.stopListening();
            }
        }

        @Override
        public void finish() {
            super.finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        } // Fade transition
        /**********************************************************************************************
         * Private Class for the past appointments recycler
         ************************************************************************************************/
        private class PastAppointmentViewHolder extends RecyclerView.ViewHolder{
            private View view;

            PastAppointmentViewHolder(View itemView) {
                super(itemView);
                view = itemView;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            void setPastAppointmentData(String date, String time, String doctor, String documentID){
                TextView appointmentTextView = view.findViewById(R.id.appointmentTextView);
                appointmentTextView.setText("Date: " + DATParser.weekDayAsString(DATParser.getWeekDay(date)) + " " + date + "\nTime: " + time + "\nPhysician: Dr. " + doctor + "\n");
            }
        }
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
        CustomIntent.customType(PatientDashboard.this, "left-to-right");
    }

    public void doctorProfile(View view) {
        startActivity(new Intent(getApplicationContext(), PatientDoctorView.class));
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

        @RequiresApi(api = Build.VERSION_CODES.N)
        void setAppointmentName(String date, String time, String doctor, final String chatCode, String documentID, final Timestamp apptTime) {
            TextView appointmentTextView = view.findViewById(R.id.appointmentTextView);
            registerForContextMenu(appointmentTextView);
            if(apptTime != null){
                Log.i("DASHBOARD", "Timestamp found for " + documentID);
                final Calendar apptTimeCalendar = Calendar.getInstance();
                apptTimeCalendar.setTime(apptTime.toDate());

                Calendar apptCloseTime = Calendar.getInstance();
                apptCloseTime.setTime(apptTime.toDate());
                apptCloseTime.setTimeInMillis(apptCloseTime.getTimeInMillis() + ( 30 * DATParser.ONE_MINUTE_IN_MILLIS)); //add 30 minutes
                Timestamp apptCloseTS = new Timestamp(apptCloseTime.getTime());

                Log.i("DASHBOARD", documentID + " : appointment close time comparison result: " + apptCloseTS.compareTo(Timestamp.now()));

                if(apptCloseTS.compareTo(Timestamp.now()) < 0){
                    Log.i("DASHBOARD", "found an appointment past its close time. Updating completion status");
                    DocumentReference appointmentRef = fStore.collection("Appointment").document(documentID);
                    appointmentRef.update("CompletionStatus", true); //update the completion status
                    Toast.makeText(getApplicationContext(), "Appointment on " + apptTimeCalendar.getTime().toString() + " has passed.", Toast.LENGTH_SHORT).show();
                }
            }
            else if(time != null && date != null){
                //set timestamp in the document
                DocumentReference appointmentRef = fStore.collection("Appointment").document(documentID);
                appointmentRef.update("TimeStamp", DATParser.dateToTimeStamp(date, time)); //update the completion status
                Log.i("DASHBOARD", "updating TimeStamp for null TimeStamps");
            }

            if(chatCode != null){
            appointmentID = chatCode.substring(4);
            }

            appointmentTextView.setText("Date: " + DATParser.weekDayAsString(DATParser.getWeekDay(date)) + " " + date + "\nTime: " + time + "\nPhysician: Dr. " + doctor + "\n");
            appointmentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chatCode != null) {

                        Intent i = new Intent(PatientDashboard.this, Chat.class);
                        i.putExtra("chatroomcode", chatCode);
                        startActivity(i);
                        CustomIntent.customType(PatientDashboard.this, "right-to-left");
                    } else {
                        Toast.makeText(PatientDashboard.this, "NO CHAT ROOM CODE FOUND", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}



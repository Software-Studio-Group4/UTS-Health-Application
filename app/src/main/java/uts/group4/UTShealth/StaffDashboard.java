package uts.group4.UTShealth;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;
import uts.group4.UTShealth.Util.DATParser;

public class StaffDashboard extends AppCompatActivity {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    CollectionReference appointmentRef = fStore.collection("Appointment");
    TextView welcomeText;
    private RecyclerView appointmentsRecyclerView;
    private FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder> appointmentAdapter;
    private static final String FIELD_NAME = "Last Name";



    /**********************************************************************************************
     * onCreate
     ************************************************************************************************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffdashboard_layout);
        welcomeText = findViewById(R.id.welcomeText);


        //Setup the recycler
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query appointmentQuery = appointmentRef.whereEqualTo("doctorID", userID).whereEqualTo("CompletionStatus", false).orderBy("Date", Query.Direction.DESCENDING).orderBy("Time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<AppointmentModel> options = new FirestoreRecyclerOptions.Builder<AppointmentModel>().setQuery(appointmentQuery, AppointmentModel.class).build();
        appointmentAdapter = new FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull AppointmentViewHolder appointmentViewHolder, int position, @NonNull AppointmentModel appointmentModel) {
                String appointmentID = getSnapshots().getSnapshot(position).getId();
                appointmentViewHolder.setAppointmentName(appointmentModel.getDate(), appointmentModel.getTime(), appointmentModel.getPatientFullName(), appointmentModel.getChatCode(), appointmentID, appointmentModel.getTimeStamp());
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

    /**********************************************************************************************
     * Finish
     ************************************************************************************************/
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    } // Fade transition

    /**********************************************************************************************
     * onStart, onStop
     ************************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        appointmentAdapter.startListening();
        DocumentReference nameRef = fStore.collection("Doctor").document(userID);
        nameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString(FIELD_NAME);
                    welcomeText.setText("Welcome, Dr " + name); // Displays welcome text
                } else {
                    Toast.makeText(StaffDashboard.this, "Error: Welcome message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (appointmentAdapter != null) {
            appointmentAdapter.stopListening();
        }
    }

    /**********************************************************************************************
     * Methods for AVAILABILITY, PROFILE and PATIENT buttons
     ************************************************************************************************/
    public void goToAvailabilityPage(View view) {
        startActivity(new Intent(getApplicationContext(), DoctorAvailability.class));
        CustomIntent.customType(StaffDashboard.this, "left-to-right");
    }

    public void userProfile(View view) {
        startActivity(new Intent(getApplicationContext(), StaffProfilePage.class));
        CustomIntent.customType(StaffDashboard.this, "left-to-right");

    }

    public void goToPatientsPage() {

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
        void setAppointmentName(String date, String time, String patient, final String chatCode, final String documentID, final Timestamp apptTime) {
            TextView appointmentTextView = view.findViewById(R.id.appointmentTextView);
            appointmentTextView.setText("Date: " + DATParser.weekDayAsString(DATParser.getWeekDay(date)) + " " + date + "\nTime: " + time + "\nPatient: " + patient + "\n");
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
            appointmentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (chatCode != null) {
//
//                        Intent i = new Intent(StaffDashboard.this, Chat.class);
//                        i.putExtra("chatroomcode", chatCode);
//                        startActivity(i);
//                        CustomIntent.customType(StaffDashboard.this, "right-to-left");
//                    } else {
//                        Toast.makeText(StaffDashboard.this, "NO CHAT ROOM CODE FOUND", Toast.LENGTH_SHORT).show();
//                    }
//
                    Intent i = new Intent(StaffDashboard.this, AppointmentDetails.class);
                    i.putExtra("appointmentID", documentID);
                    i.putExtra("isDoctor", true);
                    startActivity(i);
                    CustomIntent.customType(StaffDashboard.this, "right-to-left");
                }
            });
        }
    }




}

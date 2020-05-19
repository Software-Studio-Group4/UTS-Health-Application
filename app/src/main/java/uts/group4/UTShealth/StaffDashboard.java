package uts.group4.UTShealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class StaffDashboard extends AppCompatActivity {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    CollectionReference appointmentRef = fStore.collection("Appointment");
    private RecyclerView appointmentsRecyclerView;
    private FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder> appointmentAdapter;



    /**********************************************************************************************
     * onCreate
     ************************************************************************************************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffdashboard_layout);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        //Setup the recycler
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query appointmentQuery = appointmentRef.whereEqualTo("doctorID", userID).orderBy("Date", Query.Direction.DESCENDING).orderBy("Time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<AppointmentModel> options = new FirestoreRecyclerOptions.Builder<AppointmentModel>().setQuery(appointmentQuery, AppointmentModel.class).build();
        appointmentAdapter = new FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AppointmentViewHolder appointmentViewHolder, int position, @NonNull AppointmentModel appointmentModel) {
                appointmentViewHolder.setAppointmentName(appointmentModel.getDate(), appointmentModel.getTime(), appointmentModel.getPatientFullName(), appointmentModel.getChatCode());
            }

            @NonNull
            @Override
            public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
                return new AppointmentViewHolder(view);
            }
        };
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        fAuth = FirebaseAuth.getInstance();
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
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
    protected void onStart(){
        super.onStart();
        appointmentAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();

        if (appointmentAdapter != null) {
            appointmentAdapter.stopListening();
        }
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

        void setAppointmentName(String date, String time, String patient, final String chatCode) {
            TextView textView = view.findViewById(R.id.appointmentTextView);
            textView.setText("Date: " + date + "\nTime: " + time  + "\nPatient: " + patient + "\n");

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chatCode != null){

                        Intent i = new Intent(StaffDashboard.this, Chat.class);
                        i.putExtra("chatroomcode", chatCode);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(StaffDashboard.this, "NO CHAT ROOM CODE FOUND", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
package uts.group4.UTShealth;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;
import uts.group4.UTShealth.Util.DATParser;

public class PatientPastAppointments extends AppCompatActivity {
    private RecyclerView appointmentsRecycler;
    private static final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private static final String userID = fAuth.getCurrentUser().getUid();
    private static final CollectionReference appointmentRef = fStore.collection("Appointment");
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

    public void backBtnPressed(View view) {
        finish();
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

package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;
import uts.group4.UTShealth.Model.ChatMessage;
import uts.group4.UTShealth.Model.Doctor;
import uts.group4.UTShealth.Model.TimeOffModel;
import uts.group4.UTShealth.Util.DATParser;

public class BookAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static TextView dateTextView;
    public static TextView timeTextView;
    public TextView chosenDoctorTextView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DatabaseReference dbRef;
    //Spinner docSpinner;
    final List<String> doctors = new ArrayList<>();
    final List<String> doctorIds = new ArrayList<>();
    String patientFullName = null;
    String chosenDoctorId = null;
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd hh:mm a");
    String dateAndTime = formatter.format(date);
    private RecyclerView doctorRecycler;
    private FirestoreRecyclerAdapter<Doctor, DoctorViewHolder> doctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        //docSpinner = findViewById(R.id.doctorSpinner);
        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);
        chosenDoctorTextView = findViewById(R.id.chosenDoctor);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

    // the following block of code populates the patientName string with their full name to be used to be stored in the appointments object;
                DocumentReference nameRef = fStore.collection("Patients").document(fAuth.getUid());
                nameRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                patientFullName = document.getString("First Name") + " " + document.getString("Last Name");
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
            }
        });

        final CollectionReference doctorRef = fStore.collection("Doctor");
        doctorRecycler = findViewById(R.id.doctorRecycler);
        doctorRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query doctorQuery = doctorRef.orderBy("First Name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Doctor> options = new FirestoreRecyclerOptions.Builder<Doctor>().setQuery(doctorQuery, Doctor.class).build();
        doctorAdapter = new FirestoreRecyclerAdapter<Doctor, DoctorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder doctorViewHolder, int position, @NonNull Doctor doctorModel) {
                String doctorId = getSnapshots().getSnapshot(position).getId();
                doctorViewHolder.setDoctorData(doctorModel.getFirstName(), doctorModel.getLastName(), doctorId);
            }
            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctorselect, parent, false);
                return new DoctorViewHolder(view);
            }
        };
        doctorRecycler.setAdapter(doctorAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        doctorAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (doctorAdapter != null) {
            doctorAdapter.stopListening();
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return super.findViewById(id);
    }

    public void btn_PickerDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", "BookAppointment");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "date picker");
    }

    public static void populateSetDateText(int year, int month, int day) {
        if(month < 10){
            dateTextView.setText(day + "/0" + month + "/" + year);
        }
        else{
        dateTextView.setText(day + "/" + month + "/" + year);
    }
    }

    public void btn_PickerTime(View view) {
        DialogFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(), "time picker");
    }

    //Changes the heading of the calendar view
    public static void populateSetTimeText(int hour, int minute) {
        String amPm;
        if (hour >= 12) {
            amPm = " PM";
        } else
            amPm = " AM";
        timeTextView.setText(String.format("%02d:%02d", hour, minute) + amPm);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // confirm the appointment (data stored in firestore)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void confirmAppt(View view) {
        String userID = fAuth.getCurrentUser().getUid();
        if(dateTextView.getText().toString().equals("") && dateTextView.getText().length() <= 0){
            Toast.makeText(BookAppointment.this, "please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if(timeTextView.getText().toString().equals("") && timeTextView.getText().length() <= 0){
            Toast.makeText(BookAppointment.this, "please select a time", Toast.LENGTH_SHORT).show();
            return;
        }
        if(chosenDoctorTextView.getText().toString().equals("") && chosenDoctorTextView.getText().length() <= 0){
            Toast.makeText(BookAppointment.this, "please select a doctor", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();
        String doctorFullName = chosenDoctorTextView.getText().toString();
        String weekDay = DATParser.weekDayAsString(DATParser.getWeekDay(date));
        Log.i("LOGGER",  "week day found : " + weekDay + DATParser.getWeekDay(date));
        String appointmentID = (userID + date + time).replaceAll("[/:]", ""); //this makes an appointment easier to find.

        DocumentReference appointmentRef = fStore.collection("Appointment").document(appointmentID); //sets reference to this appointment object


            //initialise A Chat Object in the RealTimeDatabase
            dbRef = FirebaseDatabase.getInstance().getReference().child("Chats/" + "CHAT" + appointmentID);
            ChatMessage initMessage = new ChatMessage("Welcome to your appointment!", "SYSTEM", null, dateAndTime);
            dbRef.push().setValue(initMessage);

            // sets the target document reference to the Appointment collection in the firestore.
            Map<String, Object> appointmentData = new HashMap<>(); //
            appointmentData.put("patientID", userID);
            appointmentData.put("Date", date);
            appointmentData.put("Time", time);
            appointmentData.put("WeekDay", weekDay);
            appointmentData.put("ChatCode", "CHAT" + appointmentID);
            appointmentData.put("DoctorFullName", doctorFullName);
            appointmentData.put("PatientFullName", patientFullName);

            //CREATES AN APPOINTMENT OBJECT IN THE FIRESTORE.
            appointmentRef.set(appointmentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(BookAppointment.this, "Success", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BookAppointment.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

            //ADDS THIS APPOINTMENT ID INTO THE 'Appointments' LIST IN THE PATIENT OBJECT.
            DocumentReference patientDocRef = fStore.collection("Patient").document(userID); //setting a document reference to the patient's data path
            patientDocRef.update("Appointments", FieldValue.arrayUnion(appointmentID));//appends the same appointment ID to the list of strings so we can search for this appointment.

            //ADDS THIS APPOINTMENT ID INTO THE 'Appointments' LIST IN THE DOCTOR OBJECT.
            DocumentReference doctorDocRef = fStore.collection("Patient").document(userID); //setting a document reference to the patient's data path
            doctorDocRef.update("Appointments", FieldValue.arrayUnion(appointmentID));//appends the same appointment ID to the list of strings so we can search for this appointment.


        startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
    }

    public void backBtnPressed(View view) {
        startActivity(new Intent(getApplicationContext(), PatientDashboard.class));

    }

    /**********************************************************************************************
     * Private Class for the doctor recycler
     ************************************************************************************************/
    private class DoctorViewHolder extends RecyclerView.ViewHolder {
        private View view;

        DoctorViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setDoctorData(final String doctorfName, final String doctorlName, final String doctorID){
            final ArrayList<TimeOffModel> timeOff = new ArrayList<>();
            ConstraintLayout doctorItem = view.findViewById(R.id.doctorItem);
            TextView text = view.findViewById(R.id.doctorTextView);

            text.setText("Dr." + doctorfName + " " + doctorlName + "\nSpecialty: ");

            //get the doctor's time off
            CollectionReference timeOffRef = fStore.collection("Doctor").document(doctorID).collection("Time Off");
            timeOffRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            timeOff.add(new TimeOffModel(document.get("Date").toString(), document.get("Day").toString(), document.get("Month").toString(), document.get("Year").toString()));
                            Log.i("LOG", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d("LOG", "Error getting subcollection.", task.getException());
                    }
                }
            });

            //On Click
            doctorItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dateTextView.getText().toString().equals("") && dateTextView.getText().length() <= 0){
                        Toast.makeText(getApplicationContext(), "Please choose a date first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(timeTextView.getText().toString().equals("") && timeTextView.getText().length() <= 0){
                        Toast.makeText(getApplicationContext(), "Please choose a time first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for(TimeOffModel date : timeOff){
                        if(date.getDate().equals(dateTextView.getText().toString())){
                            Toast.makeText(getApplicationContext(), "Dr." +doctorfName + " " + doctorlName +" is not available on " + date.getDate(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    chosenDoctorTextView.setText(doctorfName + " " + doctorlName);
                    chosenDoctorId =  doctorID;
                    Toast.makeText(getApplicationContext(), "Chose a doctor!:" + doctorfName, Toast.LENGTH_SHORT).show();
                }
            });
        }

        //void setAppointmentName(String date, String time, String doctor, final String chatCode) { }
    }

}

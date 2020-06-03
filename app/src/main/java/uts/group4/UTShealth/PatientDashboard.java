package uts.group4.UTShealth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;
import uts.group4.UTShealth.Model.Patient;
import uts.group4.UTShealth.Model.PatientLocation;
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

    private static final String TAG = "Pat Dash";
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient client;
    private PatientLocation patientLocation;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard);
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        client = LocationServices.getFusedLocationProviderClient(this);
        welcomeText = findViewById(R.id.welcomeText);
        Query appointmentQuery = appointmentRef.whereEqualTo("patientID", userID).whereEqualTo("CompletionStatus", false).orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<AppointmentModel> options = new FirestoreRecyclerOptions.Builder<AppointmentModel>().setQuery(appointmentQuery, AppointmentModel.class).build();

        appointmentAdapter = new FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull AppointmentViewHolder appointmentViewHolder, int position, @NonNull AppointmentModel appointmentModel) {
                String appointmentID = getSnapshots().getSnapshot(position).getId();
                appointmentViewHolder.setAppointmentName(appointmentModel.getDate(), appointmentModel.getTime(), appointmentModel.getDoctorFullName(), appointmentModel.getChatCode(), appointmentID, appointmentModel.getTimeStamp(), appointmentModel.isUrgentStatus());
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
        CustomIntent.customType(PatientDashboard.this, "left-to-right");
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

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.N)
        void setAppointmentName(String date, String time, String doctor, final String chatCode, final String documentID, final Timestamp apptTime, final boolean urgentStatus) {
            TextView appointmentTextView = view.findViewById(R.id.appointmentTextView);
            ImageView urgentIcon = view.findViewById(R.id.urgentIcon);
            if(urgentStatus){
                urgentIcon.setVisibility(View.VISIBLE);
            }
            registerForContextMenu(appointmentTextView);
            appointmentID = documentID;
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

            appointmentTextView.setText("Date: " + DATParser.weekDayAsString(DATParser.getWeekDay(date)) + " " + date + "\nTime: " + time + "\nPhysician: Dr. " + doctor + "\n");
            appointmentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PatientDashboard.this, AppointmentDetails.class);
                    i.putExtra("appointmentID", documentID);
                    i.putExtra("isDoctor", false);
                        startActivity(i);
                        CustomIntent.customType(PatientDashboard.this, "right-to-left");
                }
            });
        }
    }
    /**********************************************************************************************
     * Location data and permissions
     * When the doctor logs in to the application the location will be saved to the database
     ************************************************************************************************/
    private void getPatientDetails() {
        if (patientLocation == null) {
            patientLocation = new PatientLocation();

            DocumentReference doctorRef = fStore.collection("Patients").document(fAuth.getUid());
            doctorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Get Patient Successful");
                        Patient patient = task.getResult().toObject(Patient.class);
                                patientLocation.setPatient(patient);
                        getLastKnowLocation();
                    }
                }
            });
        }
    }

    private void savePatientLocation() {
        if (patientLocation != null) {
            DocumentReference locationReference = fStore.collection("Patient Locations").document(fAuth.getUid());
            locationReference.set(patientLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "saveDoctorLocation /ninserted Doctor Location into Database" +
                                "/n Latitude: " + patientLocation.getGeoPoint().getLatitude() +
                                "/n Longitude " + patientLocation.getGeoPoint().getLongitude());
                    }
                }
            });
        }
    }

    private void getLastKnowLocation() {
        Log.d(TAG, "getLastKnownLocation: called!");
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) { // Fixed bug where null Latitude would crash app
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: Latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: Longitude: " + geoPoint.getLongitude());
                    patientLocation.setGeoPoint(geoPoint);
                    patientLocation.setTimestamp(null);
                    savePatientLocation();
                }
            }
        });
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            return isMapsEnabled();
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getPatientDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PatientDashboard.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PatientDashboard.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getPatientDetails();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getPatientDetails();
            } else {
                getLocationPermission();
            }
        }
    }

}



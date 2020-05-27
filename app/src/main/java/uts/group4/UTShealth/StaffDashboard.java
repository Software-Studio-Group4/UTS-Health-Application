package uts.group4.UTShealth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;
import uts.group4.UTShealth.Model.Doctor;
import uts.group4.UTShealth.Model.DoctorLocation;

public class StaffDashboard extends AppCompatActivity {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    CollectionReference appointmentRef = fStore.collection("Appointment");
    TextView welcomeText;
    private RecyclerView appointmentsRecyclerView;
    private FirestoreRecyclerAdapter<AppointmentModel, AppointmentViewHolder> appointmentAdapter;
    private static final String FIELD_NAME = "Last Name";

    private static final String TAG = "Staff Dash";
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient client;
    private DoctorLocation doctorLocation;

    /**********************************************************************************************
     * onCreate
     ************************************************************************************************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffdashboard_layout);
        welcomeText = findViewById(R.id.welcomeText);
        client = LocationServices.getFusedLocationProviderClient(this);

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

        void setAppointmentName(String date, String time, String patient, final String chatCode) {
            TextView appointmentTextView = view.findViewById(R.id.appointmentTextView);
            appointmentTextView.setText("Date: " + date + "\nTime: " + time + "\nPatient: " + patient + "\n");

            appointmentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chatCode != null) {

                        Intent i = new Intent(StaffDashboard.this, Chat.class);
                        i.putExtra("chatroomcode", chatCode);
                        startActivity(i);
                    } else {
                        Toast.makeText(StaffDashboard.this, "NO CHAT ROOM CODE FOUND", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    /**********************************************************************************************
     * Location data and permissions
     * When the doctor logs in to the application the location will be saved to the database
     ************************************************************************************************/
    private void getDoctorDetails() {
        if (doctorLocation == null) {
            doctorLocation = new DoctorLocation();

            DocumentReference doctorRef = fStore.collection("Doctor").document(fAuth.getUid());

            doctorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Get Doctor Successful");
                        Doctor doctor = task.getResult().toObject(Doctor.class);
                        doctorLocation.setDoctor(doctor);
                        getLastKnowLocation();
                    }
                }
            });
        }
    }

    private void saveDoctorLocation() {
        if (doctorLocation != null) {
            DocumentReference locationReference = fStore.collection("Doctor Locations").document(fAuth.getUid());
            locationReference.set(doctorLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "saveDoctorLocation /ninserted Doctor Location into Database" +
                                "/n Latitude: " + doctorLocation.getGeoPoint().getLatitude() +
                                "/n Longitude " + doctorLocation.getGeoPoint().getLongitude());
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
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: Latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: Longitude: " + geoPoint.getLongitude());
                    doctorLocation.setGeoPoint(geoPoint);
                    doctorLocation.setTimestamp(null);
                    saveDoctorLocation();
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
            getDoctorDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(StaffDashboard.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(StaffDashboard.this, available, ERROR_DIALOG_REQUEST);
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
                    getDoctorDetails();
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
                getDoctorDetails();
            } else {
                getLocationPermission();
            }
        }
    }

}

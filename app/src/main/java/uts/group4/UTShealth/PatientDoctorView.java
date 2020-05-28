package uts.group4.UTShealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import uts.group4.UTShealth.Model.AppointmentModel;
import uts.group4.UTShealth.Model.Doctor;
import uts.group4.UTShealth.Model.DoctorLocation;


public class PatientDoctorView extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mMapView;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private RecyclerView doctorProfileRecyclerView;
    DoctorLocation doctorLocation;
    CollectionReference doctorRef = database.collection("Doctor");
    DocumentReference dr = doctorRef.document("First Name");
    private FirestoreRecyclerAdapter<Doctor, PatientDoctorView.DoctorProfileViewHolder> doctorAdapter;
    LatLng latLng;
    private LinearLayoutManager linearLayoutManager;
    private static final String FIELD_NAME = "First Name";
    String drName = dr.toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_doctor_view);
        mMapView = findViewById(R.id.mapView);
        initGoogleMap(savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true);
        doctorProfileRecyclerView = findViewById(R.id.doctorProfileRecyclerView);
       /* dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null){
                    drName = documentSnapshot.getString(FIELD_NAME);
                }
            }
        });
        */
        setRecyclerView();

    }
    private void setRecyclerView(){
        Query query = doctorRef.orderBy("First Name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Doctor> options = new FirestoreRecyclerOptions.Builder<Doctor>().setQuery(query, Doctor.class).build();
        doctorAdapter = new FirestoreRecyclerAdapter<Doctor, DoctorProfileViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DoctorProfileViewHolder holder, int position, @NonNull Doctor model) {
                holder.setDrName(drName);
            }

            @NonNull
            @Override
            public DoctorProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dr_item, parent, false);
                return new DoctorProfileViewHolder(view);

            }
        };
        doctorProfileRecyclerView.setLayoutManager(linearLayoutManager);
        doctorProfileRecyclerView.setAdapter(doctorAdapter);
    }


/*
    private void getGeoPoint() {
        doctorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Doctor doctor = task.getResult().toObject(Doctor.class);
                    doctorLocation.setDoctor(doctor);
                    double lat = doctorLocation.getGeoPoint().getLatitude();
                    double lng = doctorLocation.getGeoPoint().getLongitude();
                     latLng = new LatLng(lat, lng);
                }
            }
        });
    }

 */


    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null){
                    drName = documentSnapshot.getString(FIELD_NAME);
                }
            }
        });
        doctorAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        doctorAdapter.stopListening();

    }

    private class DoctorProfileViewHolder extends RecyclerView.ViewHolder {
        private View view;

        DoctorProfileViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        @SuppressLint("SetTextI18n")
        void setDrName(String name) {
            TextView textView = view.findViewById(R.id.drNameTextView);
            textView.setText("Dr: " + name);

    }

}
}


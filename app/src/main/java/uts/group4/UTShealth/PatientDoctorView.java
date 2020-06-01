package uts.group4.UTShealth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.Doctor;
import uts.group4.UTShealth.Model.DoctorLocation;
import uts.group4.UTShealth.Model.PatientLocation;


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
    private DatabaseReference refDatabase;
    private GoogleMap mMap;
    private LatLngBounds latLngBounds;
    private PatientLocation pLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_doctor_view);
        mMapView = findViewById(R.id.mapView);
        initGoogleMap(savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true);
        doctorProfileRecyclerView = findViewById(R.id.doctorProfileRecyclerView);
        setRecyclerView();
        refDatabase = FirebaseDatabase.getInstance().getReference().child("Location");
    }


    private void setRecyclerView(){
        Query query = doctorRef.orderBy("First Name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Doctor> options = new FirestoreRecyclerOptions.Builder<Doctor>().setQuery(query, Doctor.class).build();
        doctorAdapter = new FirestoreRecyclerAdapter<Doctor, DoctorProfileViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DoctorProfileViewHolder holder, int position, @NonNull Doctor model) {
                holder.setDrName((model.getFirstName() + " " + model.getLastName()), model.getSpecialisation());
            }
            @NonNull
            @Override
            public DoctorProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctorselect, parent, false);
                return new DoctorProfileViewHolder(view);
            }
        };
        doctorProfileRecyclerView.setLayoutManager(linearLayoutManager);
        doctorProfileRecyclerView.setAdapter(doctorAdapter);
    }

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
       mMap = map;
        refDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                LatLng newLocation = new LatLng(
                        dataSnapshot.child("latitude").getValue(Double.class),
                        dataSnapshot.child("longitude").getValue(Double.class)
                );
                mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .title("Dr: " + dataSnapshot.getKey()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final LatLng SYDNEY = new LatLng(-33.88,151.21);
        map.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10));
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
    public void backBtnPressed(View view) {
        finish();
    }
    private class DoctorProfileViewHolder extends RecyclerView.ViewHolder {
        private View view;

        DoctorProfileViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        @SuppressLint("SetTextI18n")
        void setDrName(String name, String specialisation) {
            TextView textView = view.findViewById(R.id.doctorTextView);
            textView.setText("Dr: " + name + "\nSpecialisation: " + specialisation);

    }

}

}


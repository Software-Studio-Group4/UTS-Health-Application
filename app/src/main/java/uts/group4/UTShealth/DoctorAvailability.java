package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import uts.group4.UTShealth.Model.ShiftModel;
import uts.group4.UTShealth.Util.DATParser;

public class DoctorAvailability extends AppCompatActivity {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    CollectionReference shiftRef = fStore.collection("Doctor").document(userID).collection("Shifts");
    private RecyclerView sunRecyclerView;
    private FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder> sunAdapter;
    private RecyclerView monRecyclerView;
    private FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder> monAdapter;
    private RecyclerView tueRecyclerView;
    private FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder> tueAdapter;
    private RecyclerView wedRecyclerView;
    private FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder> wedAdapter;
    private RecyclerView thuRecyclerView;
    private FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder> thuAdapter;
    private RecyclerView friRecyclerView;
    private FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder> friAdapter;
    private RecyclerView satRecyclerView;
    private FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder> satAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctoravailability_layout);

        /***********setting the recycler views**************/
        initShifts();
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
        sunAdapter.startListening();
        monAdapter.startListening();
        tueAdapter.startListening();
        wedAdapter.startListening();
        thuAdapter.startListening();
        friAdapter.startListening();
        satAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();

        if (sunAdapter != null) {
            sunAdapter.stopListening();
        }
        if (tueAdapter != null) {
            tueAdapter.stopListening();
        }
        if (wedAdapter != null) {
            wedAdapter.stopListening();
        }
        if (thuAdapter != null) {
            thuAdapter.stopListening();
        }
        if (friAdapter != null) {
            friAdapter.stopListening();
        }
        if (satAdapter != null) {
            satAdapter.stopListening();
        }
        if (monAdapter != null) {
            monAdapter.stopListening();
        }
    }

    /**********************************************************************************************
     * Edit Shifts
     *********************************************************************************************/
    public void newShift(View view){
        NewShiftFragment shiftEditorFragment = new NewShiftFragment();
        shiftEditorFragment.show(getSupportFragmentManager(), "New Shift");
    }


    public void editShift(Bundle bundle){
        EditShiftFragment editShiftFragment = new EditShiftFragment();
        editShiftFragment.setArguments(bundle);
        editShiftFragment.show(getSupportFragmentManager(), "Edit Shift");
    }
    /**********************************************************************************************
     * Initialise the shift recyclers (this is huge)
     ************************************************************************************************/
    public void initShifts(){
        //SUNDAY
        sunRecyclerView = findViewById(R.id.sunRecycler);
        sunRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query sundayShiftsQuery = shiftRef.whereEqualTo("Day", "Sunday").orderBy("StartTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShiftModel> options = new FirestoreRecyclerOptions.Builder<ShiftModel>().setQuery(sundayShiftsQuery, ShiftModel.class).build();
        sunAdapter = new FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder shiftViewHolder, int position, @NonNull ShiftModel shiftModel) {
                shiftViewHolder.setShiftData(shiftModel.getStartTime(), shiftModel.getEndTime(), shiftModel.getDay(), getSnapshots().getSnapshot(position).getId());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };
        sunRecyclerView.setAdapter(sunAdapter);

        //MONDAY
        monRecyclerView = findViewById(R.id.monRecycler);
        monRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query mondayShiftsQuery = shiftRef.whereEqualTo("Day", "Monday").orderBy("StartTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShiftModel> optionsMon = new FirestoreRecyclerOptions.Builder<ShiftModel>().setQuery(mondayShiftsQuery, ShiftModel.class).build();
        monAdapter = new FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder>(optionsMon) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder shiftViewHolder, int position, @NonNull ShiftModel shiftModel) {
                shiftViewHolder.setShiftData(shiftModel.getStartTime(), shiftModel.getEndTime(), shiftModel.getDay(), getSnapshots().getSnapshot(position).getId());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };
        monRecyclerView.setAdapter(monAdapter);

        //TUESDAY
        tueRecyclerView = findViewById(R.id.tueRecycler);
        tueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query tuesdayShiftsQuery = shiftRef.whereEqualTo("Day", "Tuesday").orderBy("StartTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShiftModel> tueOptions = new FirestoreRecyclerOptions.Builder<ShiftModel>().setQuery(tuesdayShiftsQuery, ShiftModel.class).build();
        tueAdapter = new FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder>(tueOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder shiftViewHolder, int position, @NonNull ShiftModel shiftModel) {
                shiftViewHolder.setShiftData(shiftModel.getStartTime(), shiftModel.getEndTime(), shiftModel.getDay(), getSnapshots().getSnapshot(position).getId());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };
        tueRecyclerView.setAdapter(tueAdapter);

        //WEDNESDAY
        wedRecyclerView = findViewById(R.id.wedRecycler);
        wedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query wednesdayShiftsQuery = shiftRef.whereEqualTo("Day", "Wednesday").orderBy("StartTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShiftModel> wedOptions = new FirestoreRecyclerOptions.Builder<ShiftModel>().setQuery(wednesdayShiftsQuery, ShiftModel.class).build();
        wedAdapter = new FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder>(wedOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder shiftViewHolder, int position, @NonNull ShiftModel shiftModel) {
                shiftViewHolder.setShiftData(shiftModel.getStartTime(), shiftModel.getEndTime(), shiftModel.getDay(), getSnapshots().getSnapshot(position).getId());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };
        wedRecyclerView.setAdapter(wedAdapter);

        //THURSDAY
        thuRecyclerView = findViewById(R.id.thuRecycler);
        thuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query thursdayShiftsQuery = shiftRef.whereEqualTo("Day", "Thursday").orderBy("StartTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShiftModel> thuOptions = new FirestoreRecyclerOptions.Builder<ShiftModel>().setQuery(thursdayShiftsQuery, ShiftModel.class).build();
        thuAdapter = new FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder>(thuOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder shiftViewHolder, int position, @NonNull ShiftModel shiftModel) {
                shiftViewHolder.setShiftData(shiftModel.getStartTime(), shiftModel.getEndTime(), shiftModel.getDay(), getSnapshots().getSnapshot(position).getId());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };
        thuRecyclerView.setAdapter(thuAdapter);

        //FRIDAY
        friRecyclerView = findViewById(R.id.friRecycler);
        friRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query fridayShiftsQuery = shiftRef.whereEqualTo("Day", "Friday").orderBy("StartTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShiftModel> friOptions = new FirestoreRecyclerOptions.Builder<ShiftModel>().setQuery(fridayShiftsQuery, ShiftModel.class).build();
        friAdapter = new FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder>(friOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder shiftViewHolder, int position, @NonNull ShiftModel shiftModel) {
                shiftViewHolder.setShiftData(shiftModel.getStartTime(), shiftModel.getEndTime(), shiftModel.getDay(), getSnapshots().getSnapshot(position).getId());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };
        friRecyclerView.setAdapter(friAdapter);

        //SATDAY
        satRecyclerView = findViewById(R.id.satRecycler);
        satRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query saturdayShiftsQuery = shiftRef.whereEqualTo("Day", "Saturday").orderBy("StartTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ShiftModel> satOptions = new FirestoreRecyclerOptions.Builder<ShiftModel>().setQuery(saturdayShiftsQuery, ShiftModel.class).build();
        satAdapter = new FirestoreRecyclerAdapter<ShiftModel, ShiftViewHolder>(satOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ShiftViewHolder shiftViewHolder, int position, @NonNull ShiftModel shiftModel) {
                shiftViewHolder.setShiftData(shiftModel.getStartTime(), shiftModel.getEndTime(), shiftModel.getDay(), getSnapshots().getSnapshot(position).getId());
            }

            @NonNull
            @Override
            public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
                return new ShiftViewHolder(view);
            }
        };
        satRecyclerView.setAdapter(satAdapter);
    }

    /**********************************************************************************************
     * Private Class for the recycler
     ************************************************************************************************/
    private class ShiftViewHolder extends RecyclerView.ViewHolder {
        private View view;

        ShiftViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setShiftData(String startTime, String endTime, String day, String id) {
            LinearLayout block = view.findViewById(R.id.availBlockInstance);
            TextView startTimeTv = view.findViewById(R.id.startTime);
            TextView endTimeTv = view.findViewById(R.id.endTime);


            startTimeTv.setText(DATParser.timeIntToStr(Integer.parseInt(startTime)));
            endTimeTv.setText(DATParser.timeIntToStr(Integer.parseInt(endTime)));

            final Bundle bundle = new Bundle();
            bundle.putString("currentStart", startTime);
            bundle.putString("currentEnd", endTime);
            bundle.putString("currentDay", day);
            bundle.putString("documentID", id);

            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                    editShift(bundle);
                }
            });
        }
    }
}

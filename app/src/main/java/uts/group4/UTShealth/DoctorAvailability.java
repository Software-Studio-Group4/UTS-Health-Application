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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctoravailability_layout);

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
    }

    @Override
    protected void onStop(){
        super.onStop();

        if (sunAdapter != null) {
            sunAdapter.stopListening();
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
                    Toast.makeText(DoctorAvailability.this, "clicked on the shift", Toast.LENGTH_SHORT).show();
                    editShift(bundle);
                }
            });
        }
    }
}

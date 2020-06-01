package uts.group4.UTShealth.ActivityFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import uts.group4.UTShealth.R;


public class RemoveTimeOffFragment extends DialogFragment {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private String documentID;

    private String day;
    private String month;
    private String year;

    private TextView dateDisplayTv;
    private Button cancelButton;
    private Button removeButton;



    //mandatory empty constructor
    public RemoveTimeOffFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.removetimeoff_fragment, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        cancelButton = view.findViewById(R.id.cancelBtn);
        removeButton = view.findViewById(R.id.removeBtn);
        dateDisplayTv = view.findViewById(R.id.dateDisplayTv);




        //onclick for the remove button
        removeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteTimeOff();
            }
        });

        //onclick for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            documentID = bundle.getString("documentId");
            day = bundle.getString("day");
            month = bundle.getString("month");
            year = bundle.getString("year");

            dateDisplayTv.setText(day + " " + month + " " + year);
        }

    }

    /**********************************************************************************************
     * Delete Shift
     **********************************************************************************************/
    public void deleteTimeOff(){
        DocumentReference timeOffRef = fStore.collection("Doctor").document(userID).collection("Time Off").document(documentID);
        timeOffRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                Toast.makeText( getActivity(), "Time Off Deleted!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
    }

}
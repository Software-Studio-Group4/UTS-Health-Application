package uts.group4.UTShealth.ActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import uts.group4.UTShealth.R;


public class ChooseADoctorFragment extends DialogFragment {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private RecyclerView doctorRecycler;
//    private FirestoreRecyclerAdapter<Doctor, DoctorViewHolder> doctorAdapter;



    //mandatory empty constructor
    public ChooseADoctorFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);
       View view = inflater.inflate(R.layout.fragment_edit_selected_doctor, container, false);
       fAuth = FirebaseAuth.getInstance();
       fStore = FirebaseFirestore.getInstance();

       //initialise the spinners and all information in the spinners

        //fills a list full of this doctor's shifts



        return view;

    }

    /***doctor view holder private class***/


}

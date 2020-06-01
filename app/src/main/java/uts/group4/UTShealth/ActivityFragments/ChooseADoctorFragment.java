package uts.group4.UTShealth.ActivityFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uts.group4.UTShealth.Model.Doctor;
import uts.group4.UTShealth.R;
import uts.group4.UTShealth.Util.DATParser;
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

package uts.group4.UTShealth.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/********This class contains functions which can be used to query the database to return results********
 * Purpose : to prevent reuse of code when querying the database.
 * All methods are static and should be able to be called globally.
 * ***************************************************************************************************/


public class QueryDB {


    //returns a list of doctorIDs for all doctors with a certain specialisation
    //Status : COMPLETE  NOT TESTED
    public static ArrayList<String> getDocBySpecialisation(String specialisation, FirebaseFirestore fStore){
        final ArrayList<String> doctors = new ArrayList<String>();

        fStore.collection("Doctor").whereEqualTo("Specialisation", specialisation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    doctors.add((String) document.get("Specialisation"));
                            }
                        } else {
                            Log.d("t", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return doctors;
    }

    //returns a doctor's name when you give it the doctor's ID.
    //char parameter will take f, l, or a (f gives first name, l gives last name, anything else will give a full name)
    //Status : COMPLETE   NOT TESTED
    public static String getDocName(String doctorID, FirebaseFirestore fStore, char format){
        final ArrayList<String> firstName = new ArrayList<String>();
        final ArrayList<String> lastName = new ArrayList<String>();

        DocumentReference doctor = fStore.collection("Doctor").document(doctorID);
        doctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                       firstName.add(document.get("First Name").toString());
                       lastName.add(document.get("First Name").toString());

                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        switch(format){
            case 'f' :
                return firstName.get(0);
            case 'l' :
                return lastName.get(0);
            default :
                return firstName.get(0) + " " + lastName.get(0);
        }
    }

    //returns a list of all FULL NAMES of doctors
    //Status : COMPLETE  NOT TESTED
    public static List<String> getAllDocNames(FirebaseFirestore fStore){

        final ArrayList<String> doctors = new ArrayList<String>();

        fStore.collection("Doctor").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                for(QueryDocumentSnapshot document : task.getResult()){
                    doctors.add(document.get("First Name").toString() + " " + document.get("Last Name").toString());
                }
            }
        });
        return doctors;
    }

}

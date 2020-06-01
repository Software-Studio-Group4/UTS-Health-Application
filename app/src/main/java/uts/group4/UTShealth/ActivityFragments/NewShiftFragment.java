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

import uts.group4.UTShealth.R;
import uts.group4.UTShealth.Util.DATParser;


public class NewShiftFragment extends DialogFragment {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    List<String> timeSpinnerArray = new ArrayList<String>();
    List<String> daySpinnerArray = new ArrayList<String>();
    Spinner daySpinner;
    Spinner startTimeSpinner;
    Spinner endTimeSpinner;
    Button confirmButton;
    ArrayList<ArrayList<ArrayList<String>>> AllShifts = new ArrayList<>();
    ArrayList<ArrayList<String>> sundayShifts = new ArrayList<>();
    ArrayList<ArrayList<String>> mondayShifts = new ArrayList<>();
    ArrayList<ArrayList<String>> tuesdayShifts = new ArrayList<>();
    ArrayList<ArrayList<String>> wednesdayShifts = new ArrayList<>();
    ArrayList<ArrayList<String>> thursdayShifts = new ArrayList<>();
    ArrayList<ArrayList<String>> fridayShifts = new ArrayList<>();
    ArrayList<ArrayList<String>> saturdayShifts = new ArrayList<>();




    //mandatory empty constructor
    public NewShiftFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);
       View view = inflater.inflate(R.layout.newshiftfragment_layout, container, false);

       fAuth = FirebaseAuth.getInstance();
       fStore = FirebaseFirestore.getInstance();
       userID = fAuth.getCurrentUser().getUid();
       confirmButton = view.findViewById(R.id.confirmBtn);

       //initialise the spinners and all information in the spinners
       daySpinner = view.findViewById(R.id.daySpinner);
       startTimeSpinner = view.findViewById(R.id.startTimeSpinner);
       endTimeSpinner = view.findViewById(R.id.endTimeSpinner);
       ArrayAdapter<String> daySpinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, daySpinnerArray);
       ArrayAdapter<String> startTimeSpinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, timeSpinnerArray);
       ArrayAdapter<String> endTimeSpinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, timeSpinnerArray);
       daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       startTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       endTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       initSpinnerArrays();
       daySpinner.setAdapter(daySpinnerAdapter);
       startTimeSpinner.setAdapter(startTimeSpinnerAdapter);
       endTimeSpinner.setAdapter(endTimeSpinnerAdapter);

        //fills a list full of this doctor's shifts
        AllShifts.add(sundayShifts);
        AllShifts.add(mondayShifts);
        AllShifts.add(tuesdayShifts);
        AllShifts.add(wednesdayShifts);
        AllShifts.add(thursdayShifts);
        AllShifts.add(fridayShifts);
        AllShifts.add(saturdayShifts);
        CollectionReference shiftsRef = fStore.collection("Doctor").document(userID).collection("Shifts");
        shiftsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        ArrayList<String> data = new ArrayList<>();
                        data.add(document.getString("Day"));
                        data.add(document.getString("StartTime"));
                        data.add(document.getString("EndTime"));
                        AllShifts.get((DATParser.weekDayAsInt(document.getString("Day")) - 1)).add(data);
                    }
                }
            }
        });





       //onclick for the confirm button
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                confirmShift(v);
            }
        });

        return view;

    }
    /**********************************************************************************************
     * Confirm shift
     **********************************************************************************************/
    public void confirmShift(View view){
        final String workDay = daySpinner.getSelectedItem().toString();
        String startTime = startTimeSpinner.getSelectedItem().toString();
        String endTime = endTimeSpinner.getSelectedItem().toString();
        Map<String, Object> shiftData = new HashMap<>();
        shiftData.put("StartTime", (DATParser.timeStrToInt(startTime) + ""));
        shiftData.put("EndTime", (DATParser.timeStrToInt(endTime) + ""));
        shiftData.put("Day", workDay);

        switch(checkShiftValidity(workDay, startTime, endTime)){
            case 0:
                DocumentReference docRef = fStore.collection("Doctor").document(userID).collection("Shifts").document();
                docRef.set(shiftData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("TAG", "successfully added new shift");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                dismiss();
                break;
            case 1:
                Toast.makeText( getActivity(), "A shift cannot be shorter than 3 hours", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText( getActivity(), "A shift cannot be longer than 9 hours", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText( getActivity(), "Your shift can't overlap with another shift!", Toast.LENGTH_SHORT).show();
                break;

            default : Toast.makeText( getActivity(), "OOPS SOMETHING WENT REALLY WRONG", Toast.LENGTH_SHORT).show(); break;
        }


    }

    //return value guide :
    // 0 means no problems were encountered
    // 1 means the length of the shift is too short
    // 2 means the length of the shift is too long
    // 3 means it overlaps with another shift
    public int checkShiftValidity(String day, String startTime, String endTime){
        // -a shift must be at least 3 hours long
        if((DATParser.timeStrToInt(endTime) - DATParser.timeStrToInt(startTime) < 300)){
            return 1;
        }
        //-a shift cannot be longer than 9 hours
        if((DATParser.timeStrToInt(endTime) - DATParser.timeStrToInt(startTime) > 900)){
            return 2;
        }
        // -start time cannot be within the time of another shift
        // -end time cannot be within the time of another shift
        for (ArrayList<String> shift : AllShifts.get((DATParser.weekDayAsInt(day) - 1))){
            int selectedStart = DATParser.timeStrToInt(startTime);
            int selectedEnd = DATParser.timeStrToInt(endTime);
            int checkedStart = DATParser.timeStrToInt(shift.get(1));
            int checkedEnd = DATParser.timeStrToInt(shift.get(2));
            if(selectedStart >= checkedStart && selectedStart <= checkedEnd){
                return 3;
            }
            else if(selectedEnd >= checkedStart && selectedEnd <= checkedEnd){
                return 3;
            }

        }



        return 0;
    }
    /**********************************************************************************************
     * Initialisation and miscellaneous methods
     **********************************************************************************************/
    public void initSpinnerArrays(){

        for(int i = 0; i < 24; i++){
            if(i < 10){
                timeSpinnerArray.add("0" + i + ":00AM");
            }
            else if(i == 11 || i == 10){
                timeSpinnerArray.add(i + ":00AM");
            }
            else if(i == 12){
                timeSpinnerArray.add("12:00PM");
            }
            else if((i - 12) < 10){
                timeSpinnerArray.add("0" + (i - 12) + ":00PM");
            }
            else{
                timeSpinnerArray.add((i - 12) + ":00PM");
            }
        }
        daySpinnerArray.add("Sunday");
        daySpinnerArray.add("Monday");
        daySpinnerArray.add("Tuesday");
        daySpinnerArray.add("Wednesday");
        daySpinnerArray.add("Thursday");
        daySpinnerArray.add("Friday");
        daySpinnerArray.add("Saturday");
    }

}

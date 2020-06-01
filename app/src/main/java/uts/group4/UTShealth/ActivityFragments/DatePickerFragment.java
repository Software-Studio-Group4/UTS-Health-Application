package uts.group4.UTShealth.ActivityFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uts.group4.UTShealth.R;
import uts.group4.UTShealth.Util.DATParser;

import static uts.group4.UTShealth.AppointmentDetails.editDateText;
import static uts.group4.UTShealth.BookAppointment.populateSetDateText;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private String sourceActivity;
    private String userID;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    //for appointments
    String doctorName;
    List<String> availableWeekdays = new ArrayList();
    List<Date> unavailableDates = new ArrayList();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerTheme,this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        return datePickerDialog;
    }

    @Override
    public void dismiss(){
        if(sourceActivity.equals("AppointmentDetails")){
            super.dismiss();
        }
        else{
            super.dismiss();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        String doctorID = null;
        final Calendar cal = Calendar.getInstance();
        Bundle bundle = this.getArguments();
        if(bundle != null){
            sourceActivity = bundle.getString("source");
            userID = bundle.getString("userID");
            if(sourceActivity.equals("AppointmentDetails")){
                doctorID = bundle.getString("doctorID");
            }
        }

        if(doctorID != null){
            CollectionReference docTimeOffRef = fStore.collection("Doctor").document(doctorID).collection("Time Off");
            CollectionReference docShiftsRef = fStore.collection("Doctor").document(doctorID).collection("Shifts");

            /***get doctor's shifts**/
            docShiftsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.isEmpty()){
                        return;
                    }
                    for(DocumentSnapshot document : queryDocumentSnapshots){
                        availableWeekdays.add(document.getString("Day"));
                    }
                }
            });
            /***get doctor's unavailable days**/
            docTimeOffRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.isEmpty()){
                        return;
                    }
                    for(DocumentSnapshot document : queryDocumentSnapshots){
                        String date = document.getString("Date");
                        cal.set(DATParser.getYear(date), DATParser.getMonthAsInt(date) - 1, DATParser.getDay(date));
                        unavailableDates.add(cal.getTime());
                    }
                }
            });
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        switch(sourceActivity){
            case("BookAppointment") : populateSetDateText(year,month + 1, day); break;
            case("DoctorAvailability") : newTimeOff(year, month, day); break;
            case("AppointmentDetails") : editDateText(year, month + 1, day); break;
            default : break;
        }

    }

    public void newTimeOff(int year, int month, int day){
        int correctMonth = month + 1;
        String stringMonth;
        if(correctMonth < 10){
            stringMonth = "0" + correctMonth;
        }
        else{
            stringMonth = "" + correctMonth;
        }
        String stringDay;
        if(day < 10){
            stringDay = "0" + day;
        }
        else{
            stringDay = "" + day;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("Date", stringDay + "/" + stringMonth + "/" + year);
        data.put("Day", stringDay);
        data.put("Month", stringMonth);
        data.put("Year", year + "");

        //do something to check validity//
        DocumentReference docRef = fStore.collection("Doctor").document(userID).collection("Time Off").document();
        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("TAG", "added a time off");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
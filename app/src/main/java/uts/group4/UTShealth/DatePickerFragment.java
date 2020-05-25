package uts.group4.UTShealth;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static uts.group4.UTShealth.BookAppointment.populateSetDateText;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    String sourceActivity;
    String userID;
    FirebaseFirestore fStore;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), R.style.DatePickerTheme,this, year, month, day);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            sourceActivity = bundle.getString("source");
            userID = bundle.getString("userID");
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        switch(sourceActivity){
            case("BookAppointment") : populateSetDateText(year,month + 1, day); break;
            case("DoctorAvailability") : newTimeOff(year, month, day); break;
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

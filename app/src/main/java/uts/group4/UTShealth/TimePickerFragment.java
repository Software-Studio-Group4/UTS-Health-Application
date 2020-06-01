package uts.group4.UTShealth;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import static uts.group4.UTShealth.BookAppointment.populateSetTimeText;
import static uts.group4.UTShealth.AppointmentDetails.editTimeText;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    String sourceActivity;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), R.style.DatePickerTheme,this, hour, minute, DateFormat.is24HourFormat(getContext()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        switch(sourceActivity){
            case "BookAppointment" : populateSetTimeText(hour, minute); break;
            case "AppointmentDetails": editTimeText(hour, minute); break;
            default: break;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            sourceActivity = bundle.getString("source");
        }
    }
}

package uts.group4.UTShealth;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


public class NewShiftFragment extends DialogFragment {

    //mandatory empty constructor
    public NewShiftFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);
       return inflater.inflate(R.layout.newshiftfragment_layout, container, false);

    }
}

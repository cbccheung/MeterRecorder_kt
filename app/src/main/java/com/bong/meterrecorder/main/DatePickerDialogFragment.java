package com.bong.meterrecorder.main;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private static final String KEY_YEAR = "KEY_YEAR";
    private static final String KEY_MONTH = "KEY_MONTH";
    private static final String KEY_DAY = "KEY_DAY";

    // This interface is implemented by this fragment's container activity.
    // In your case, it will be implemented by MainActivity.
    // When the container activity creates an instance of this fragment, it passes in a reference to this
    // method.  This allows the current fragment to use that reference to pass the entered date values back 
    // to that container activity as soon as they are entered by the user.
    public interface DatePickedListener
    {
        void onDatePicked(int year, int month, int day);
    }

    private DatePickedListener listener;

    public static DatePickerDialogFragment newInstance(int year, int month, int day) {

        Bundle args = new Bundle();
        args.putInt(KEY_YEAR, year);
        args.putInt(KEY_MONTH, month);
        args.putInt(KEY_DAY, day);

        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        // when the fragment is initially attached to the activity, 
        // cast the activity to the callback interface type
        super.onAttach(context);

        if(context instanceof DatePickedListener) {
            listener = (DatePickedListener) context;
        } else {
            listener = (DatePickedListener) getParentFragment();
        }

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        // the activity passes in these arguments when it 
        // creates the dialog. use them to create the dialog
        // with these initial values set
        Bundle b = requireArguments();

        int year = b.getInt(KEY_YEAR);
        int month = b.getInt(KEY_MONTH);
        int day = b.getInt(KEY_DAY);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int setYear, int setMonth, int setDay)
    {
        // when the date is selected, immediately send it to the activity via 
        // its callback interface method

        listener.onDatePicked(setYear, setMonth, setDay);
    }

}
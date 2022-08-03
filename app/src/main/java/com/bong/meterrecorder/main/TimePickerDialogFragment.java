package com.bong.meterrecorder.main;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{

    private static final String KEY_HOUR = "KEY_HOUR";
    private static final String KEY_MIN = "KEY_MIN";

    // This interface is implemented by this fragment's container activity.
    // In your case, it will be implemented by MainActivity.
    // When the container activity creates an instance of this fragment, it passes in a reference to this
    // method.  This allows the current fragment to use that reference to pass the entered date values back 
    // to that container activity as soon as they are entered by the user.
    public interface TimePickedListener
    {
        void onTimePicked(int hour, int minute);
    }

    private TimePickedListener listener;

    public static TimePickerDialogFragment newInstance(int hour, int min) {

        Bundle args = new Bundle();
        args.putInt(KEY_HOUR, hour);
        args.putInt(KEY_MIN, min);

        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        // when the fragment is initially attached to the activity, 
        // cast the activity to the callback interface type
        super.onAttach(context);

        if(context instanceof DatePickerDialogFragment.DatePickedListener) {
            listener = (TimePickedListener) context;
        } else {
            listener = (TimePickedListener) getParentFragment();
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

        int hour = b.getInt(KEY_HOUR);
        int min = b.getInt(KEY_MIN);

        return new TimePickerDialog(requireActivity(), this, hour, min, true);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        listener.onTimePicked(hourOfDay, minute);
    }

}
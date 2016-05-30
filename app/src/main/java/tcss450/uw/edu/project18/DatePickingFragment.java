package tcss450.uw.edu.project18;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickingFragment extends DialogFragment {

    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String LISTEN = "listener";


    public DatePickingFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year,month,day;
        android.app.DatePickerDialog.OnDateSetListener listener = null;
        Log.i("DatePicking", "Arg: " + getArguments());
        if (getArguments() != null) {
            year = getArguments().getInt(DatePickingFragment.YEAR);
            month = getArguments().getInt(DatePickingFragment.MONTH);
            day = getArguments().getInt(DatePickingFragment.DAY);
             listener = (android.app.DatePickerDialog.OnDateSetListener)
                     getArguments().getSerializable(LISTEN);
        } else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        Log.i("DatePicking", "Bundle: " + savedInstanceState);
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), listener, year, month, day);
        dpd.setMessage("Enter date...");
        return dpd;
    }
}

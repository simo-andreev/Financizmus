package bg.o.sim.finansizmus.date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private DateTime initialDate;

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, DateTime initialDate) {
        DatePickerFragment fragment = new DatePickerFragment();

        if (initialDate == null) initialDate = DateTime.now();
        if (listener == null)
            listener = (DatePickerDialog.OnDateSetListener) fragment.getActivity();

        fragment.initialDate = initialDate;
        fragment.listener = listener;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int year = initialDate.getYear();
        int month = initialDate.getMonthOfYear() - 1; //the '-1' is there because JodaTime lib starts counting Months at 1 *scoff-scoff*, and Java starts moths at 0.//
        int day = initialDate.getDayOfMonth();

        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}

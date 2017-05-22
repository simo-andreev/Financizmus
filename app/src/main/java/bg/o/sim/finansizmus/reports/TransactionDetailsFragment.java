package bg.o.sim.finansizmus.reports;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.model.Category;
import bg.o.sim.finansizmus.model.Transaction;


public class TransactionDetailsFragment extends DialogFragment {

    private Transaction t;

    public static TransactionDetailsFragment newInstance(Transaction transaction) {
        TransactionDetailsFragment fragment = new TransactionDetailsFragment();
        fragment.t = transaction;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        TextView account = (TextView) rootView.findViewById(R.id.transaction_dialog_account);
        TextView category = (TextView) rootView.findViewById(R.id.transaction_dialog_category);

        ImageView directionIndicator = (ImageView) rootView.findViewById(R.id.transaction_dialog_direction_indicator);

        TextView noteView = (TextView) rootView.findViewById(R.id.transaction_dialog_note);

        TextView dateView = (TextView) rootView.findViewById(R.id.transaction_dialog_date);
        TextView sumView = (TextView) rootView.findViewById(R.id.transaction_dialog_sum);

        Button returnButton = (Button) rootView.findViewById(R.id.transaction_dialog_return_button);

        account.setText(t.getAccount().getName());
        account.setCompoundDrawablesWithIntrinsicBounds(t.getAccount().getIconId(), 0, 0 , 0);

        category.setText(t.getCategory().getName());
        category.setCompoundDrawablesWithIntrinsicBounds(0, 0, t.getCategory().getIconId(), 0);

        directionIndicator.setImageResource(t.getCategory().getType() == Category.Type.EXPENSE ? R.mipmap.arrow_out : R.mipmap.arrow_in);

        String note = t.getNote();
        if (note == null || note.isEmpty()) note = "No note entered for this transaction.";
        noteView.setText(note);

        dateView.setText("Date: " + t.getDate().toString("dd MM/YYYY"));

        sumView.setText("Sum: " + t.getSum() + " $");

        returnButton.setOnClickListener(v -> dismiss());

        return rootView;
    }

}

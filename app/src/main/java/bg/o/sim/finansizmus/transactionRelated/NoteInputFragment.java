package bg.o.sim.finansizmus.transactionRelated;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import bg.o.sim.finansizmus.R;

import static bg.o.sim.finansizmus.utils.Const.EXTRA_NOTE;


public class NoteInputFragment extends DialogFragment {

    public interface NoteCommunicator{
        void setNote(String note);
    }

    private EditText input;
    private NoteCommunicator communicator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            communicator = (NoteCommunicator) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activities containing a NoteInputFragment MUST implement the NoteCommunicator interface!!!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_input, container, false);

        input = rootView.findViewById(R.id.note_input_text);

        Bundle args = getArguments();
        if (args != null) input.setText(args.getString(EXTRA_NOTE,""));

        Button cancel = rootView.findViewById(R.id.note_input_cancel);
        Button submit = rootView.findViewById(R.id.note_input_submit);

        cancel.setOnClickListener(v -> dismiss());

        submit.setOnClickListener(v -> {
            communicator.setNote(input.getText().toString());
            dismiss();
        });
        
        return rootView;
    }
}


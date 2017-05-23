package bg.o.sim.finansizmus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import bg.o.sim.finansizmus.model.Cacher;


public class LogoutDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.logout));
        alertDialogBuilder.setMessage(getString(R.string.message_logout_confirm));

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        alertDialogBuilder.setPositiveButton(getString(R.string.logout), (dialog, which) -> {
            Cacher.clearCache();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });
        return alertDialogBuilder.create();
    }
}

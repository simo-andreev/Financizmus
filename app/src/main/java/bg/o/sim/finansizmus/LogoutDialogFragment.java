package bg.o.sim.finansizmus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import bg.o.sim.finansizmus.dataManagment.CacheManager;


public class LogoutDialogFragment extends DialogFragment {

    private Context context;
    private CacheManager cache;

    public LogoutDialogFragment() {
        context = getActivity();
    }

    public static LogoutDialogFragment newInstance() {
        LogoutDialogFragment fragment = new LogoutDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        cache = CacheManager.getInstance();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.logout));
        alertDialogBuilder.setMessage(getString(R.string.message_logout_confirm));

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cache.clearCache();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        return alertDialogBuilder.create();
    }
}

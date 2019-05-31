package bg.o.sim.finansizmus.accounts;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.model.Cacher;
import bg.o.sim.finansizmus.model.Account;
import bg.o.sim.finansizmus.model.RowDisplayable;
import bg.o.sim.finansizmus.reports.FilteredReportFragment;
import bg.o.sim.finansizmus.utils.Util;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountViewHolder> {

    private ArrayList<RowDisplayable> accounts;
    private Context context;
    private FragmentManager fm;

    AccountsAdapter(ArrayList<Account> accountsList, Context context, FragmentManager fm) {
        this.context = context;
        this.accounts = new ArrayList<>(accountsList);
        this.fm = fm;
    }

    public List<RowDisplayable> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public Object getItem(int position) {
        return (position >= 0 && position < accounts.size()) ? accounts.get(position) : null;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.accounts_list_item, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AccountViewHolder holder, final int position) {
        final RowDisplayable account = accounts.get(position);
        holder.account.setCompoundDrawablesWithIntrinsicBounds(account.getIconId(),0, 0, 0);
        holder.account.setText(account.getName());
        //TODO                       v
        holder.accountSum.setText("TEMP");

        holder.accountDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setIcon(account.getIconId())
                .setTitle(account.getName())
                .setMessage("Are you sure you want to DELETE this account? \n This can't be undone!")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (!Cacher.getAllAccounts().isEmpty()) {
                        //TODO !!! DELETE QUERIES IN DAO !!! *note: check details on SQL cascading delete.
//                                  DAO.getInstance(context).deleteAccount(account);

                        //TODO - REDO - this \/ mess
                        Cacher.removeAccount((Account) account);
                        accounts.remove(account);
                        AccountsAdapter.this.notifyDataSetChanged();
                    } else {
                        Util.toastLong(context, "You can`t be without accounts!");
                    }

                })
                .setNegativeButton("No", null)
                .show());

        View.OnClickListener reportListener = v -> fm.beginTransaction()
                .replace(R.id.main_fragment_container, FilteredReportFragment.newInstance((Account) account), context.getString(R.string.tag_fragment_filtered_report))
                .addToBackStack(context.getString(R.string.tag_fragment_filtered_report))
                .commit();

        holder.account.setOnClickListener(reportListener);
        holder.accountSum.setOnClickListener(reportListener);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView account;
        TextView accountSum;
        ImageButton accountDelete;

        public AccountViewHolder(View itemView) {
            super(itemView);
            account = itemView.findViewById(R.id.account_name);
            accountSum = itemView.findViewById(R.id.account_sum);
            accountDelete = itemView.findViewById(R.id.account_delete);
        }
    }
}

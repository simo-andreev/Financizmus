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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.dataManagment.CacheManager;
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
        this.accounts = new ArrayList<RowDisplayable>(accountsList);
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
        holder.accountImage.setImageResource(account.getIconId());
        holder.accountName.setText(account.getName());
        //TODO                       v
        holder.accountSum.setText("TEMP");
        holder.accountDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(account.getIconId())
                        .setTitle(account.getName())
                        .setMessage("Are you sure you want to DELETE this account? \n This can't be undone!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!CacheManager.getInstance().getAllAccounts().isEmpty()) {
                                    //TODO !!! DELETE QUERIES IN DAO !!! *note: check details on SQL cascading delete.
//                                    DAO.getInstance(context).deleteAccount(account);
                                    accounts.remove(account);
                                    AccountsAdapter.this.notifyDataSetChanged();
                                } else {
                                    Util.toastLong(context, "You can`t be without accounts!");
                                }

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .replace(R.id.main_fragment_container, FilteredReportFragment.newInstance((Account) account), context.getString(R.string.filtered_report_fragment_tag))
                        .addToBackStack(context.getString(R.string.filtered_report_fragment_tag))
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        ImageView accountImage;
        TextView accountName;
        TextView accountSum;
        ImageButton accountDelete;

        public AccountViewHolder(View itemView) {
            super(itemView);
            accountImage = (ImageView) itemView.findViewById(R.id.account_image);
            accountName = (TextView) itemView.findViewById(R.id.account_name);
            accountSum = (TextView) itemView.findViewById(R.id.account_sum);
            accountDelete = (ImageButton) itemView.findViewById(R.id.account_delete);

            rootView = itemView.findViewById(R.id.account_root);
        }
    }
}

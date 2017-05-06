package bg.o.sim.finansizmus.accounts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.dataManagment.DBAdapter;
import bg.o.sim.finansizmus.favourites.AddCategoryDialogFragment;
import bg.o.sim.finansizmus.favourites.IconsAdapter;
import bg.o.sim.finansizmus.model.Manager;
import bg.o.sim.finansizmus.model.RowDisplayable;

import java.util.ArrayList;

public class AccountsFragment extends Fragment {

    private RecyclerView accountsList;
    private RecyclerView moreAccountIconsList;

    private AccountsAdapter accountsAdapter;
    private IconsAdapter iconsAdapter;

    private DBAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        adapter = DBAdapter.getInstance(getActivity());
        context = getActivity();

        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        final ArrayList<RowDisplayable> accounts = new ArrayList<>();
        accounts.addAll(adapter.getCachedAccounts().values());

        accountsAdapter = new AccountsAdapter(accounts, getActivity(), getFragmentManager());
        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        accountsList.setAdapter(accountsAdapter);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        iconsAdapter = new IconsAdapter(Manager.getInstance().getAllAccountIcons(), getActivity());
        moreAccountIconsList = (RecyclerView) view.findViewById(R.id.accounts_icons_list);
        moreAccountIconsList.setAdapter(iconsAdapter);
        moreAccountIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        moreAccountIconsList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, moreAccountIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override public void onItemClick(View view, int position) {

                        AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                        Bundle arguments = new Bundle();
                        int iconId = Manager.getInstance().getAllAccountIcons().get(position);

                        arguments.putInt(getString(R.string.EXTRA_ICON), iconId);
                        arguments.putString("ROW_DISPLAYABLE_TYPE", "ACCOUNT");

                        dialog.setArguments(arguments);
                        dialog.show(getFragmentManager(), String.valueOf(R.string.add_category_dialog));

                    }
                })
        );
        return view;
    }
}

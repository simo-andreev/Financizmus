package bg.o.sim.finansizmus.accounts;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bg.o.sim.finansizmus.MainActivity;
import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.model.CacheManager;
import bg.o.sim.finansizmus.model.DAO;
import bg.o.sim.finansizmus.favourites.AddCategoryDialogFragment;
import bg.o.sim.finansizmus.favourites.IconsAdapter;

public class AccountsFragment extends Fragment {

    private DAO dao;
    private CacheManager cache;
    private Context context;

    private RecyclerView accountsList;
    private RecyclerView moreAccountIconsList;

    private AccountsAdapter accountsAdapter;
    private IconsAdapter iconsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        context = getActivity();
        cache = CacheManager.getInstance();
        dao = DAO.getInstance(context);

        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);

        accountsAdapter = new AccountsAdapter(cache.getAllAccounts(), context, getFragmentManager());
        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        accountsList.setAdapter(accountsAdapter);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        iconsAdapter = new IconsAdapter(cache.getAccountIcons(), context);
        moreAccountIconsList = (RecyclerView) view.findViewById(R.id.accounts_icons_list);
        moreAccountIconsList.setAdapter(iconsAdapter);
        moreAccountIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        moreAccountIconsList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, moreAccountIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override public void onItemClick(View view, int position) {

                        AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                        Bundle arguments = new Bundle();
                        int iconId = cache.getAccountIcons()[position];

                        arguments.putInt(getString(R.string.EXTRA_ICON), iconId);
                        arguments.putString("ROW_DISPLAYABLE_TYPE", "ACCOUNT");

                        dialog.setArguments(arguments);
                        dialog.show(getFragmentManager(), String.valueOf(R.string.tag_dialog_add_category));

                    }
                })
        );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setDrawerCheck(R.id.nav_accounts);
    }
}

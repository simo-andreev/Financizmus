package bg.o.sim.finansizmus;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import bg.o.sim.finansizmus.accounts.AccountsFragment;
import bg.o.sim.finansizmus.dataManagment.CacheManager;
import bg.o.sim.finansizmus.dataManagment.DAO;
import bg.o.sim.finansizmus.favourites.FavouritesFragment;
import bg.o.sim.finansizmus.model.Category;
import bg.o.sim.finansizmus.transactionRelated.TransactionFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DAO dao;
    private CacheManager cache;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private TextView userProfile;
    private View headerView;
    private View toolbarTitle;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = DAO.getInstance(this);
        cache = CacheManager.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_fragment_frame, new MainFragment(), getString(R.string.diagram_fragment_tag)).commit();


        headerView = navigationView.getHeaderView(0);
        userProfile = (TextView) headerView.findViewById(R.id.user_profile_link);
        userProfile.setText("Hi, " + cache.getLoggedUser().getEmail());

        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        toolbarTitle = findViewById(R.id.diagram_fragment_link);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_frame, new MainFragment(), getString(R.string.diagram_fragment_tag))
                        .commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                LogoutDialogFragment dialog = new LogoutDialogFragment();
                dialog.show(getSupportFragmentManager(), getString(R.string.logout_button));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_accounts:
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_frame, new AccountsFragment(), getString(R.string.accounts_fragment_tag))
                        .addToBackStack(getString(R.string.accounts_fragment_tag))
                        .commit();

                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_favourites:
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_frame, new FavouritesFragment(), getString(R.string.favourites_fragment_tag))
                        .addToBackStack(getString(R.string.transaction_fragment_tag))
                        .commit();

                drawer.closeDrawer(GravityCompat.START);
                return false;

            case R.id.nav_income:
                TransactionFragment fragment0 = TransactionFragment.getNewInstance(Category.Type.INCOME);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_frame, fragment0, getString(R.string.transaction_fragment_tag))
                        .addToBackStack(getString(R.string.transaction_fragment_tag))
                        .commit();

                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_expense:
                TransactionFragment fragment1 = TransactionFragment.getNewInstance(Category.Type.EXPENSE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_frame, fragment1, getString(R.string.transaction_fragment_tag))
                        .addToBackStack(getString(R.string.transaction_fragment_tag))
                        .commit();

                drawer.closeDrawer(GravityCompat.START);
                return false;

            case R.id.nav_calendar:
                drawer.closeDrawer(GravityCompat.START);
                return false;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
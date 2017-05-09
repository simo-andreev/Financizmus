package bg.o.sim.finansizmus;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bg.o.sim.finansizmus.dataManagment.CacheManager;
import bg.o.sim.finansizmus.dataManagment.DAO;
import bg.o.sim.finansizmus.model.Category;

//TODO get pie-chart working

public class PieChartFragment extends Fragment {

    private static final byte MENU_BUTTON_COUNT = 12;

    private DAO dao;
    private CacheManager cache;

    private PieChart chart;
    private ImageButton[] menu;

    private ArrayList<Category> categoriesBySum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        dao = DAO.getInstance(getActivity());
        cache = CacheManager.getInstance();

        chart = (PieChart) rootView.findViewById(R.id.pie_chart_chart);

        categoriesBySum = new ArrayList<>(cache.getAllExpenseCategories());

        Collections.sort(categoriesBySum, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                //This might mess up on near-equal sums, but that loss of precision should be acceptable here.
                //Cane be negated by casting to float and checking if diff == 0 || diff < 0
                return (int) (o1.getSum() - o2.getSum());
            }
        });

        menu = new ImageButton[MENU_BUTTON_COUNT];
        for (byte i = 0; i < MENU_BUTTON_COUNT; i++) {
            menu[i] = (ImageButton) rootView.findViewById(getResources().getIdentifier("pie_chart_menu_" + i, "id", getActivity().getPackageName()));
            menu[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            menu[i].setBackgroundColor(Color.argb(0, 0, 0, 0));
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Collections.sort(categoriesBySum, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                //This might mess up on near-equal sums, but that loss of precision should be acceptable here.
                //Cane be negated by casting to float and checking if diff == 0 || diff < 0
                return (int) (o1.getSum() - o2.getSum());
            }
        });

        for (byte i = 0; i < MENU_BUTTON_COUNT; i++) {
            Category c;
            if ( i < categoriesBySum.size()){
                menu[i].setVisibility(View.VISIBLE);
                menu[i].setClickable(true);
                c = categoriesBySum.get(i);
                menu[i].setImageResource(c.getIconId());
            } else {
                menu[i].setVisibility(View.INVISIBLE);
                menu[i].setClickable(false);
            }
        }
    }
}

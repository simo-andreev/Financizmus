package bg.o.sim.finansizmus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;

import bg.o.sim.finansizmus.model.CacheManager;
import bg.o.sim.finansizmus.reports.ReportFragment;

public class HomeFragment extends Fragment {

//    private HashSet<CategoryExpense> displayedCategories;
    private HashMap<Integer, Integer> colors;

    private PieChart pieChart;
    private ArrayList<PieEntry> entries;
    private PieData pieData;
    private PieDataSet pieDataSet;
    private Button totalSumButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.findFragmentById(R.id.home_chart_container) == null)
            ft.add(R.id.home_chart_container, new PieChartFragment(), getString(R.string.tag_fragment_home_chart));
        if (fm.findFragmentById(R.id.home_report_container) == null)
            ft.add(R.id.home_report_container, new ReportFragment(), getString(R.string.tag_fragment_home_report));
        ft.commit();

        totalSumButton = (Button) rootView.findViewById(R.id.total_sum_btn);

        /* Animator for the Report Drawer */
        final LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.report_layout);
        final float[] startY = new float[1];
        final float[] translationY = new float[1];
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startY[0] = event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float newY = event.getY();
                        float deltaY = startY[0] - newY;
                        translationY[0] = v.getTranslationY();
                        translationY[0] -= deltaY;
                        if (translationY[0] < 0)
                            translationY[0] = 0;
                        if (translationY[0] >= v.getHeight()-150)
                            translationY[0] = v.getHeight()-150;
                        v.setTranslationY(translationY[0]);
                        return true;
                    default:
                        Interpolator interpolator = new AccelerateDecelerateInterpolator();
                        v.animate().setInterpolator(interpolator).translationY(translationY[0] < v.getHeight()/3 ? 0 : v.getHeight()-totalSumButton.getHeight());
                        return v.onTouchEvent(event);
                }
            }
        });

        totalSumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getTranslationY() != 0){
                    Interpolator interpolator = new AccelerateDecelerateInterpolator();
                    layout.animate().setInterpolator(interpolator).translationY(0);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setDrawerCheck(R.id.nav_unchecker);

        totalSumButton.setText("$ " + CacheManager.getInstance().getCurrentTotal());
    }
}
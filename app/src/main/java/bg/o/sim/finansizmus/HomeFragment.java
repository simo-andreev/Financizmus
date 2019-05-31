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

import java.util.HashMap;

import bg.o.sim.finansizmus.model.Cacher;
import bg.o.sim.finansizmus.reports.ReportFragment;

public class HomeFragment extends Fragment {

    private HashMap<Integer, Integer> colors;

    private Button totalSumButton;
    private LinearLayout reportLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        reportLayout = rootView.findViewById(R.id.report_layout);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.findFragmentById(R.id.home_chart_container) == null)
            ft.add(R.id.home_chart_container, new PieChartFragment(), getString(R.string.tag_fragment_home_chart));
        if (fm.findFragmentById(R.id.home_report_container) == null)
            ft.add(R.id.home_report_container, new ReportFragment(), getString(R.string.tag_fragment_home_report));
        ft.commit();

        totalSumButton = rootView.findViewById(R.id.total_sum_btn);


        reportLayout.setOnTouchListener(new layoutDragListener());

        totalSumButton.setOnClickListener(v -> {
            if (reportLayout.getTranslationY() == reportLayout.getHeight() - totalSumButton.getHeight()){
                Interpolator interpolator = new AccelerateDecelerateInterpolator();
                reportLayout.animate().setInterpolator(interpolator).translationY(0);
            } else {
                Interpolator interpolator = new AccelerateDecelerateInterpolator();
                reportLayout.animate().setInterpolator(interpolator).translationY(reportLayout.getHeight() - totalSumButton.getHeight());
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setDrawerCheck(R.id.nav_unchecker);
        totalSumButton.setText("$ " + Cacher.getCurrentTotal());
    }

    private class layoutDragListener implements View.OnTouchListener {
        /* Animator for the Report Drawer */
        final float[] startY = new float[1];
        final float[] translationY = new float[1];
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
    }
}
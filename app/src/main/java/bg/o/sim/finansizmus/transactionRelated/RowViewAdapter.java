package bg.o.sim.finansizmus.transactionRelated;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.model.RowDisplayable;

/**
 * Takes a RowDisplayable (meant for Category and Account) instance and
 * creates a view with a corresponding icon and title.
 */
public class RowViewAdapter<T extends RowDisplayable> extends BaseAdapter {

    private ArrayList<T> dataSet;
    private LayoutInflater inflater;

    public RowViewAdapter(LayoutInflater inflater, Collection<T> dataSet) {
        this.dataSet = new ArrayList<>(dataSet);
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.spinner_transaction_category, parent, false);

        T item = dataSet.get(position);

        TextView t = convertView.findViewById(R.id.spinner_transaction_title);
        t.setText(item.getName());

        ImageView i = convertView.findViewById(R.id.spinner_transaction_icon);
        i.setImageResource(item.getIconId());

        return convertView;
    }
}



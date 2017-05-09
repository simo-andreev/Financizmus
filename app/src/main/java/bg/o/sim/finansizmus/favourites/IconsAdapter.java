package bg.o.sim.finansizmus.favourites;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.util.ArrayList;

import bg.o.sim.finansizmus.R;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconViewHolder>{

    private int[] additionalIcons;
    private Context context;

    public IconsAdapter(int[] allExpenseIcons, Context context) {
        this.context = context;
        additionalIcons = allExpenseIcons;
    }

    @Override
    public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IconViewHolder holder, final int position) {
        final Integer icon = additionalIcons[position];
        holder.image.setImageResource(icon);
        holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.unselected_icon_background));
    }

    @Override
    public int getItemCount() {
        return additionalIcons.length;
    }

    static class IconViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageButton addButton;
        View viewGroup;

        IconViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            addButton = (ImageButton) itemView.findViewById(R.id.add_icon_btn);
            this.viewGroup = itemView.findViewById(R.id.viewGroup);
        }
    }
}

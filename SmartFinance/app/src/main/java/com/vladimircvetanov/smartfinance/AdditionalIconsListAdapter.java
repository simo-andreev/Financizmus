package com.vladimircvetanov.smartfinance;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;

public class AdditionalIconsListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.IconViewHolder>{

    private ArrayList<Integer> additionalIcons;
    private Activity activity;

    AdditionalIconsListAdapter(HashSet<Integer> allExpenseIcons, Activity activity) {
        this.activity = activity;
        additionalIcons = new ArrayList<Integer> (allExpenseIcons);
    }

    @Override
    public FavouritesListAdapter.IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
        return new FavouritesListAdapter.IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavouritesListAdapter.IconViewHolder holder, final int position) {
        final Integer icon = additionalIcons.get(position);
        holder.image.setImageResource(icon);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.image.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorGrey));
                holder.addButton.setVisibility(View.VISIBLE);

                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return additionalIcons.size();
    }
}
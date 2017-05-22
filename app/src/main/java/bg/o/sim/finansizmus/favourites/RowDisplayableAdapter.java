package bg.o.sim.finansizmus.favourites;

//TODO - !!! CONSIDER IF FAV_CATEGORY WILL REMAIN AS A FEATURE AND REMOVE OR FIX THIS CLASS !!!

public class RowDisplayableAdapter /*extends RecyclerView.Adapter<RowDisplayableAdapter.IconViewHolder>*/{

//    private ArrayList<RowDisplayable> categories;
//    private Context context;
//
//
//    RowDisplayableAdapter(ArrayList<RowDisplayable> favouriteCategories, Context context) {
//        this.context = context;
//        categories = favouriteCategories;
//        adapter = DBAdapter.getInstance(context);
//    }
//
//    @Override
//    public RowDisplayableAdapter.IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
//        return new IconViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final RowDisplayableAdapter.IconViewHolder holder, final int position) {
//        final RowDisplayable categoryExpense = categories.get(position);
//        holder.image.setImageResource(categoryExpense.getIconId());
//        holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.fav_icon_backgroud));
//
//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.image.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
//                holder.removeButton.setVisibility(View.VISIBLE);
//
//                if(!categoryExpense.getIsFavourite()){
//                    if(!categories.contains(categoryExpense)) {
//                        notifyItemRemoved(position);
//                    }
//                }
//                holder.removeButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if(categories.size() > 1) {
//                            adapter.deleteFavCategory(categoryExpense);
//                            categories.remove(holder.getAdapterPosition());
//                            notifyItemRemoved(position);
//
//                        } else{
//                            Util.toastLong(context, "You can`t be without favourite categories!");
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return categories.size();
//    }
//
//    static class IconViewHolder extends RecyclerView.ViewHolder{
//
//        ImageView image;
//        ImageButton removeButton;
//        ImageButton addButton;
//        View viewGroup;
//
//        IconViewHolder(View itemView) {
//            super(itemView);
//
//            image = (ImageView) itemView.findViewById(R.id.image);
//            removeButton = (ImageButton) itemView.findViewById(R.id.remove_icon_btn);
//            addButton = (ImageButton) itemView.findViewById(R.id.add_icon_btn);
//            this.viewGroup = itemView.findViewById(R.id.viewGroup);
//        }
//    }
}

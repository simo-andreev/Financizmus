package bg.o.sim.finansizmus.favourites;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.dataManagment.CacheManager;
import bg.o.sim.finansizmus.dataManagment.DAO;
import bg.o.sim.finansizmus.model.Category;
import bg.o.sim.finansizmus.utils.Util;

//TODO - !!! CONSIDER IF FAV_CATEGORY WILL REMAIN AS A FEATURE AND REMOVE OR FIX THIS CLASS !!!

public class AddToFavDeleteDialogFragment extends DialogFragment {

//    private TextView dialogTitle;
//    private Button deleteCategory;
//    private Button addToFav;
//    private Button cancel;
//
//    private DAO dao;
//    private CacheManager cache;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_dialog_add_to_fav, container, false);
//
//        dialogTitle = (TextView) view.findViewById(R.id.add_to_fav_title);
//        cancel = (Button) view.findViewById(R.id.cancel_category_button);
//        deleteCategory = (Button) view.findViewById(R.id.delete_category_button);
//        addToFav = (Button) view.findViewById(R.id.add_to_fav_button);
//
//        dao = DAO.getInstance(getActivity());
//        cache = CacheManager.getInstance();
//
//        Bundle b = getArguments();
//        String iconKey = getText(R.string.EXTRA_ICON).toString();
//        String listKey = "ROW_DISPLAYABLE_TYPE";
//        final int iconId = b.getInt(iconKey);
//        final Category cat = (Category) b.getSerializable(listKey);
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//        addToFav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!dao.getCachedFavCategories().containsValue(cat) && adapter.getCachedFavCategories().size() < 10) {
//                    if (adapter.getCachedExpenseCategories().size() > 1) {
//                        adapter.moveToFav(cat);
//                        Toast.makeText(getActivity(), "Category added to favorites!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Util.toastLong(getActivity(), "You can`t be without categories!");
//                    }
//                } else {
//                    Util.toastLong(getActivity(), "This category is already in your favorites, or there is no more place!");
//                }
//                dismiss();
//            }
//        });
//
//        deleteCategory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (adapter.getCachedExpenseCategories().size() > 1) {
//                    adapter.deleteExpenseCategory(cat);
//
//                    Util.toastLong(getActivity(), "Category deleted!");
//                } else {
//                    Util.toastLong(getActivity(), "You can`t be without categories!");
//                }
//
//                dismiss();
//            }
//        });
//        return view;
//    }

}



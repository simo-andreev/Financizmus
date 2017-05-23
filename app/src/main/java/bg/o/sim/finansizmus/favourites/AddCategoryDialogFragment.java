package bg.o.sim.finansizmus.favourites;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.model.DAO;
import bg.o.sim.finansizmus.model.RowDisplayable;


//TODO - extract Strings
public class AddCategoryDialogFragment extends DialogFragment {

    private ImageView icon;

    private EditText categoryName;

    private Button addCategory;
    private Button cancel;

    private DAO dao;
    private int iconId;

    private Class type;

    public static AddCategoryDialogFragment getInstance(@IdRes int iconId, Class<? extends RowDisplayable> c) {
        AddCategoryDialogFragment f = new AddCategoryDialogFragment();
        f.iconId = iconId;
        f.type = c;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_category, container, false);

        dao = DAO.getInstance(getActivity());

        icon = (ImageView) view.findViewById(R.id.add_category_icon);
        categoryName = (EditText) view.findViewById(R.id.add_category_name);
        cancel = (Button) view.findViewById(R.id.cancel_addition);
        addCategory = (Button) view.findViewById(R.id.start_addition);

        icon.setImageResource(iconId);

        cancel.setOnClickListener(v -> dismiss());

        addCategory.setOnClickListener(v -> {
//                if (categoryName.getText().length() == 0) {
//                    categoryName.requestFocus();
//                    categoryName.setError("Name cannot be empty!");
//                } else {
//                    String name = categoryName.getText().toString();
//                    if (type == Category.class)
//                        dao.insertCategory(name, iconId, Cacher.getInstance(), );
//                    dismiss();
//                }
        });
        return view;
    }
}
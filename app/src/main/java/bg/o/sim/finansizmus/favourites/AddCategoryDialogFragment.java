package bg.o.sim.finansizmus.favourites;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.model.DAO;


public class AddCategoryDialogFragment extends DialogFragment {

    private TextView dialogTitle;
    private ImageView icon;
    private EditText categoryName;
    private Button addCategory;
    private Button cancel;

    private DAO dao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_category, container, false);

        dialogTitle = (TextView) view.findViewById(R.id.add_category_title);
        icon = (ImageView) view.findViewById(R.id.add_category_icon);
        categoryName = (EditText) view.findViewById(R.id.add_category_name);
        cancel = (Button) view.findViewById(R.id.cancel_addition);
        addCategory = (Button) view.findViewById(R.id.start_addition);

        dao = DAO.getInstance(getActivity());
        Bundle b = getArguments();
        String iconKey = "KEY_ICON";
        String listKey = "ROW_DISPLAYABLE_TYPE";
        String list = "";
        int iconId = 0;


        if (b != null && !b.isEmpty() && b.containsKey(iconKey) && b.containsKey(listKey)) {
            iconId = b.getInt(iconKey);
            list = b.getString(listKey);

            assert list != null;
            switch (list) {
                case "ACCOUNT":
                    dialogTitle.setText(R.string.add_new_account);
                    break;
                case "CATEGORY":
                    dialogTitle.setText(R.string.add_new_category);
                    break;
            }
        }
        icon.setImageResource(iconId);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final String tempList = list;
        final int finalIconId = iconId;
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = categoryName.getText().toString();
                if (nameStr.isEmpty()) {
                    categoryName.setError("Name cannot be empty!");
                    categoryName.requestFocus();

                } else {
                    switch (tempList) {
                        //TODO - this bit needs re-modeling to fit new model (tbh, I'm not sure it really fits the old model either);
//                        case "CATEGORY":
//                            CategoryExpense cat = new CategoryExpense(nameStr, false, finalIconId);
//                            if(!dao.getCachedExpenseCategories().containsValue(cat) && !dao.getCachedFavCategories().containsValue(cat)) {
//                                dao.addExpenseCategory(cat, Manager.getLoggedUser().getId());
//                                Toast.makeText(getActivity(), "Category created!", Toast.LENGTH_SHORT).show();
//                            }else{
//                                Util.toastLong(getActivity(),"This category already exists,please choose another name!");
//                            }
//                            break;
//                        case "ACCOUNT":
//                            Account ac = new Account(nameStr, finalIconId);
//                            if(!dao.getCachedAccounts().containsValue(ac)) {
//                                dao.addAccount(ac, Manager.getLoggedUser().getId());
//                                Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
//                            }else{
//                                Util.toastLong(getActivity(),"This account already exists,please choose another name!");
//                            }
//                            break;
                    }
                    dismiss();
                }
            }
        });
        return view;
    }
}
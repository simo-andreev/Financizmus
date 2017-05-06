package bg.o.sim.finansizmus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import bg.o.sim.finansizmus.dataManagment.DBAdapter;
import bg.o.sim.finansizmus.model.Manager;
import bg.o.sim.finansizmus.utils.Util;

public class ProfileActivity extends AppCompatActivity {


    private ImageView userPic;
    private EditText changeEmail;
    private EditText changePass;
    private Button editChanges;
    private Button cancel;
    DBAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPic = (ImageView) findViewById(R.id.profile_user_pic);
        changeEmail = (EditText) findViewById(R.id.profile_email_change);
        changePass = (EditText) findViewById(R.id.profile_pass_change);
        editChanges = (Button) findViewById(R.id.profile_edit_button);
        cancel = (Button) findViewById(R.id.profile_cancel_button);

        adapter = DBAdapter.getInstance(this);


        changeEmail.setText(Manager.getLoggedUser().getEmail());
        changePass.setText(Manager.getLoggedUser().getPassword());

        final String oldData = changeEmail.getText().toString();
        final String oldPass = changePass.getText().toString();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String newEmail = changeEmail.getText().toString();
               String newPass = changePass.getText().toString();


                if (newEmail.isEmpty()) {
                    changeEmail.setError("Empty email");
                    changeEmail.requestFocus();
                  return ;
                }
                //if(!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                if (!newEmail.matches("^(.+)@(.+)$")) {
                    changeEmail.setError("enter a valid email address");
                    changeEmail.setText("");
                    changeEmail.requestFocus();
                   return;
                }
                if (newPass.isEmpty()) {
                    changePass.setError("Empty password");
                    changePass.requestFocus();
                    return;

                }
                if (!newPass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,30}$")) {
                    changePass.setError("Password should contain at least one digit," +
                            "one special symbol,one small letter,and should be between 8 and 30 symbols. ");
                    return;
                }
                if(adapter.existsUser(newEmail)){
                    Util.toastLong(ProfileActivity.this, "This username is taken by someone else.");
                    return;
                }

                adapter.updateUser(oldData,oldPass,newEmail,newPass);
                Manager.getLoggedUser().setEmail(newEmail);
                Manager.getLoggedUser().setPassword(newPass);
                finish();

            }
        });
    }




}




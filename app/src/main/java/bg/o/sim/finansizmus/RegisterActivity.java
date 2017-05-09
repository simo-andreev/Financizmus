package bg.o.sim.finansizmus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bg.o.sim.finansizmus.model.DAO;
import bg.o.sim.finansizmus.utils.Util;

public class RegisterActivity extends AppCompatActivity {

    //Input UI fields
    private EditText userName, userEmail, userPass, confirmPass;
    private Button signUp, cancel;

    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dao = DAO.getInstance(this);

        userName = (EditText) findViewById(R.id.activity_register_name);
        userEmail = (EditText) findViewById(R.id.activity_register_email);
        userPass = (EditText) findViewById(R.id.activity_register_pass0);
        confirmPass = (EditText) findViewById(R.id.activity_register_pass1);

        signUp = (Button) findViewById(R.id.activity_register_button_register);
        cancel = (Button) findViewById(R.id.activity_register_button_cancel);

        onNewIntent(getIntent());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);

                //Save input when transitioning between Register and LoIn activities
                if (userEmail.getText().length() > 1)
                    i.putExtra(getString(R.string.EXTRA_USERMAIL), userEmail.getText().toString().trim());

                startActivity(i);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Save input when transitioning between Register and LoIn activities
        Bundle extras = intent.getExtras();
        final String extraKey = getString(R.string.EXTRA_USERMAIL);
        if (extras != null && extras.containsKey(extraKey))
            userEmail.setText(extras.getString(extraKey));
    }


    private void signUp() {

        final String name = userName.getText().toString().trim();
        userName.setText(name);
        final String mail = userEmail.getText().toString().trim();
        userEmail.setText(mail);
        final String pass = userPass.getText().toString().trim();
        final String confirm = confirmPass.getText().toString().trim();

        //TODO - SET LENGTHS ACCORDING TO CONSTS

        if (name.isEmpty()) {
            userName.requestFocus();
            userName.setError(getString(R.string.error_empty_name));
            return;
        }
        if (mail.isEmpty()) {
            userEmail.requestFocus();
            userEmail.setError(getString(R.string.error_empty_mail));
            return;
        }
        if (!Util.validEmail(mail)) {
            userEmail.requestFocus();
            userEmail.setError(getString(R.string.error_invalid_email));
            userEmail.setText("");
            return;
        }
        if (pass.isEmpty()) {
            userPass.requestFocus();
            userPass.setError(getString(R.string.error_empty_pass));
            return;
        }
        if (!pass.matches("")) {
            userPass.requestFocus();
            userPass.setError(getString(R.string.error_invalid_pass));
            return;
        }
        if (!pass.equals(confirm)) {
            confirmPass.requestFocus();
            userPass.setError(getString(R.string.error_mismatch_pass));
            confirmPass.setError(getString(R.string.error_mismatch_pass));
            confirmPass.setText("");
            return;
        }

        dao.registerUser(name, mail, pass, this);
    }

}

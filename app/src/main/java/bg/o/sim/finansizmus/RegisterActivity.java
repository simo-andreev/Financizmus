package bg.o.sim.finansizmus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bg.o.sim.finansizmus.model.DAO;

//TODO - DOCUMENTATION
//TODO - add 'personal' name input.
//TODO - extract strings

public class RegisterActivity extends AppCompatActivity {


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

                if (userEmail.getText().length() > 1)
                    i.putExtra(getString(R.string.EXTRA_USERMAIL), userEmail.getText().toString());

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

        final String name = userName.getText().toString();
        final String mail = userEmail.getText().toString();
        final String pass = userPass.getText().toString();
        final String confirm = confirmPass.getText().toString();

        //TODO - extract strings
        //TODO - CHANGE REGEX TO ACCEPT SECOND LEVEL DOMAINS
        //TODO - SET LENGTHS ACCORDING TO CONSTS

        if (name.isEmpty()) {
            userName.requestFocus();
            userName.setError("Empty name");
            return;
        }
        if (mail.isEmpty()) {
            userEmail.requestFocus();
            userEmail.setError("Empty email");
            return;
        }
        if (!mail.matches("^(.+)@(.+)$")) {
            userEmail.requestFocus();
            userEmail.setError("enter a valid email address");
            userEmail.setText("");
            return;
        }
        if (pass.isEmpty()) {
            userPass.requestFocus();
            userPass.setError("Empty password");
            return;
        }
        if (!pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,30}$")) {
            userPass.requestFocus();
            userPass.setError("Password should contain at least one digit," +
                    "one special symbol,one small letter,and should be between 8 and 30 symbols. ");
            return;
        }
        if (confirm.isEmpty()) {
            confirmPass.requestFocus();
            confirmPass.setError("Empty confirmation");
            return;
        }
        if (!pass.equals(confirm)) {
            confirmPass.requestFocus();
            confirmPass.setError("Different passwords");
            confirmPass.setText("");
            return;

        }

        dao.registerUser(name, mail, pass, this);
    }

}

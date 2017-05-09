package bg.o.sim.finansizmus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bg.o.sim.finansizmus.model.DAO;

//TODO - DOCUMENTATION

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail, userPass;
    private Button logIn, signUp;

    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dao = DAO.getInstance(this);

        userEmail = (EditText) findViewById(R.id.activity_login_email);
        userPass = (EditText) findViewById(R.id.activity_login_pass);
        logIn = (Button) findViewById(R.id.activity_login_login_button);
        signUp = (Button) findViewById(R.id.activity_login_register_button);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(getString(R.string.EXTRA_USERNAME))) {
                //TODO
            }
            if (extras.containsKey(getString(R.string.EXTRA_USERMAIL))) {
                userEmail.setText(extras.getString(getString(R.string.EXTRA_USERMAIL), ""));
            }
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_login_login:
                        if (validateForm())
                            dao.logInUser(userEmail.getText().toString(), userPass.getText().toString(), LoginActivity.this);
                        break;
                    case R.id.activity_login_register:
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        break;
                }
            }
        };

        logIn.setOnClickListener(listener);
        signUp.setOnClickListener(listener);
    }

    private boolean validateForm() {
        if (userEmail.getText().length() < 4 || userEmail.getText().length() > 40) {
            userEmail.requestFocus();
            userEmail.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (userPass.getText().length() < 4 || userPass.getText().length() > 40) {
            userPass.requestFocus();
            userPass.setError(getString(R.string.error_password_length));
            return false;
        }

        return true;
    }
}

package bg.o.sim.finansizmus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bg.o.sim.finansizmus.model.DAO;
import bg.o.sim.finansizmus.utils.Util;

//TODO - DOCUMENTATION

public class LoginActivity extends AppCompatActivity {

    //Back-button flag/counter
    private static boolean doubleBackToExitPressedOnce = false;

    //Input fields and UI buttons
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


        onNewIntent(getIntent());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.activity_login_login_button:
                        if (validateForm()) {
                            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Authenticating...");
                            progressDialog.show();

                            dao.logInUser(userEmail.getText().toString(), userPass.getText().toString(), LoginActivity.this);

                            progressDialog.hide();
                        }

                        break;

                    case R.id.activity_login_register_button:
                        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                        //TODO - [Y/n] pass 'this' to RegisterActivity to allow saving e-mail onBackPressed?
                        if (userEmail.getText().length() > 1)
                            i.putExtra(getString(R.string.EXTRA_USERMAIL), userEmail.getText().toString());

                        startActivity(i);
                        break;
                }
            }
        };

        logIn.setOnClickListener(listener);
        signUp.setOnClickListener(listener);
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Util.toastLong(this, getString(R.string.message_double_back));

        Runnable delay = new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        };
        new Handler().postDelayed(delay, 2000);
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

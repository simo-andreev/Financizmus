package bg.o.sim.finansizmus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bg.o.sim.finansizmus.model.DAO;
import bg.o.sim.finansizmus.model.LoaderService;
import bg.o.sim.finansizmus.model.User;
import bg.o.sim.finansizmus.utils.Util;

import static bg.o.sim.finansizmus.utils.Const.EXTRA_USERMAIL;

//TODO - DOCUMENTATION
//TODO - Store some sort of identifier in shPrefs after logIn, to allow automatic log-in on start-up. I'd like to avoid full user credentials, just feels wrong.

public class LoginActivity extends AppCompatActivity {

    //Back-button flag/counter
    private static boolean doubleBackToExitPressedOnce = false;

    //Input fields and UI buttons
    private EditText userEmail, userPass;

    private ProgressDialog progressDialog;
    private LoaderReceiver loaderReceiver;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerReceiver(loaderReceiver = new LoaderReceiver(), new IntentFilter(Util.BROADCAST_LOADING_FINISH));

        dao = DAO.getInstance(this);

        userEmail = findViewById(R.id.activity_login_email);
        userPass = findViewById(R.id.activity_login_pass);
        Button logIn = findViewById(R.id.activity_login_login_button);
        Button signUp = findViewById(R.id.activity_login_register_button);

        onNewIntent(getIntent());

        View.OnClickListener listener = v -> {
            switch (v.getId()) {

                /* If login pressed- display an "authenticating" dialog while waiting for db queries to finish*/
                case R.id.activity_login_login_button:
                    if (validateForm()) {
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Authenticating...");
                        progressDialog.show();

                        User u = dao.logInUser(userEmail.getText().toString().trim(), userPass.getText().toString().trim());

                        startService(new Intent(LoginActivity.this, LoaderService.class));

                        if (u == null){
                            progressDialog.hide();
                            userPass.setText("");
                            userEmail.setText("");
                            userEmail.requestFocus();
                            userEmail.setError(getString(R.string.error_invalid_login));
                        }
                    }

                    break;

                /* If register pressed- start RegisterActivity with the e-mail input*/
                case R.id.activity_login_register_button:
                    Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                    //TODO - [Y/n] pass 'this' to RegisterActivity to allow saving e-mail onBackPressed?
                    if (userEmail.getText().length() > 1)
                        i.putExtra(EXTRA_USERMAIL, userEmail.getText().toString().trim());

                    startActivity(i);
                    break;
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
        if (extras != null && extras.containsKey(EXTRA_USERMAIL))
            userEmail.setText(extras.getString(EXTRA_USERMAIL));
    }

    /**Require 2 consecutive presses of the back-button within 2 sec of each-other in order to close app.*/
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce)
            this.finishAffinity();
        else {
            doubleBackToExitPressedOnce = true;
            Util.toastLong(this, getString(R.string.message_double_back));

            Runnable delay = () -> doubleBackToExitPressedOnce = false;
            new Handler().postDelayed(delay, 2000);
        }
    }


    /**
     * Confirm if input conforms to min/max length and pass a regex-ing
     * @return <code>true</code> if the input fields' data is in a valid format.
     */
    private boolean validateForm() {
        if (!Util.validEmail(userEmail.getText().toString().trim())) {
            userEmail.requestFocus();
            userEmail.setError(getString(R.string.error_invalid_email));
            return false;
        }
        if (!Util.validPassword(userPass.getText().toString())) {
            userPass.requestFocus();
            userPass.setError(getString(R.string.error_password_length));
            return false;
        }

        return true;
    }

    private class LoaderReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.hide();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(loaderReceiver);
        super.onDestroy();
    }
}

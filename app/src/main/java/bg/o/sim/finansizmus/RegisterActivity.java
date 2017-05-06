package bg.o.sim.finansizmus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bg.o.sim.finansizmus.dataManagment.DAO;

//TODO - DOCUMENTATION
//TODO - add 'personal' name input.
//TODO - extract strings

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPass;
    private EditText confirmPass;
    private Button signUp;
    private Button cancel;

    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = (EditText)findViewById(R.id.register_email_insert);
        userPass = (EditText)findViewById(R.id.register_pass_insert);
        confirmPass = (EditText)findViewById(R.id.register_confirm_insert);

        signUp = (Button)findViewById(R.id.register_reg_button);
        cancel = (Button)findViewById(R.id.register_cancel_button);

        dao = DAO.getInstance(this);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });
    }

    private void signUp() {

        final String username = userEmail.getText().toString();
        final String pass = userPass.getText().toString();
        final String confirm = confirmPass.getText().toString();

        if (username.isEmpty()) {
            userEmail.requestFocus();
            userEmail.setError("Empty email");
            return;
        }
        if (!username.matches("^(.+)@(.+)$")) {
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

        dao.registerUser(username, username, pass, this);
    }

}

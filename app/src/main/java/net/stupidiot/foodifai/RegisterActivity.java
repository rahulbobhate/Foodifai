package net.stupidiot.foodifai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;
    EditText registerEmail;
    EditText registerPassword;
    EditText registerName;
    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       /* toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);*/

        registerName = (EditText)findViewById(R.id.nameTxt);
        registerEmail = (EditText)findViewById(R.id.emailTxt);
        registerPassword = (EditText) findViewById(R.id.passwordTxt);
        registerButton = (Button) findViewById(R.id.registerBtn);

        registerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                String emailTxt = registerEmail.getText().toString();
                String passwordTxt = registerPassword.getText().toString();
                String nameTxt = registerName.getText().toString();

                // Force user to fill up the form
                if (emailTxt.equals("") && passwordTxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Save new user data into Parse.com Data Storage
                    ParseUser user = new ParseUser();

                    user.setUsername(emailTxt);
                    user.setPassword(passwordTxt);
                    user.setEmail(emailTxt);
                    user.put("Name", nameTxt);
                    user.put("Points", 0);


                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Show a simple Toast message upon successful registration
                                Intent intent = new Intent(RegisterActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up, please try logging in now.",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {

                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error"+e, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

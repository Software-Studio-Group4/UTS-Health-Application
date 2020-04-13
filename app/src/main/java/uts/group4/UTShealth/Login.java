package uts.group4.UTShealth;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText username, password;
    private Button userLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        username = (EditText) findViewById(R.id.username);//links to user input
        password = (EditText) findViewById(R.id.password);//links to user input
        userLoginBtn = (findViewById(R.id.userLoginBtn));//links to login btn

        userLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(username.getText().toString(), password.getText().toString());//takes user input and turns into string
            }
        });
    }

    private void validate(String user, String pass) {
        if ((user.equals("name")) && (pass.equals("pass123"))) {//checks user and password (hardcoded)
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);//opens dashboard
            startActivity(intent);
        } else {
            Toast.makeText(this, "username or password are invalid", Toast.LENGTH_LONG).show();//error message
        }
    }
}
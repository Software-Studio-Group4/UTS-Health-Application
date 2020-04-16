package uts.group4.UTShealth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText emailTf, passwordTf;
    private Button userLoginBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        emailTf = (EditText) findViewById(R.id.emailTf);
        passwordTf = (EditText) findViewById(R.id.passwordTf);
        userLoginBtn = (Button) findViewById(R.id.userLoginBtn);
        fAuth = FirebaseAuth.getInstance();

        userLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTf.getText().toString().trim();
                String password = passwordTf.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {
                    passwordTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailTf.setError("Cannot have Empty Field");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            } else {
                                Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    ;


                });


            }
        });

    /*private void validate(String user, String pass) {
        if ((user.equals("name")) && (pass.equals("pass123"))) {//checks user and password (hardcoded)
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);//opens dashboard
            startActivity(intent);
        } else {
            Toast.makeText(this, "username or password are invalid", Toast.LENGTH_LONG).show();//error message
        }

     */

    }

}
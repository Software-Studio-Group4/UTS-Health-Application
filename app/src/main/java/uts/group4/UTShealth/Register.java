package uts.group4.UTShealth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    EditText emailTf, passwordTf, firstNameTf, lastNameTf, ageTf, medicareNumberTf, streetAddressTf,
             cityTf, stateTf, postCodeTf, confirmPasswordTf, phoneNumberTf;
    Button registerBtn;
    DatabaseReference dbRef;
    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // GET ALL THE OBJECTS FROM THE VIEW TO MANIPULATE
        registerBtn = (Button) findViewById(R.id.registerBtn);

        emailTf = (EditText)findViewById(R.id.emailTf);
        passwordTf = (EditText)findViewById(R.id.passwordTf);
        firstNameTf = (EditText)findViewById(R.id.firstNameTf);
        lastNameTf = (EditText) findViewById(R.id.lastNameTf);
        confirmPasswordTf = (EditText) findViewById((R.id.confirmPasswordTf));
        ageTf = (EditText) findViewById(R.id.ageTf);
        medicareNumberTf = (EditText) findViewById(R.id.medicareNumberTf);
        streetAddressTf = (EditText) findViewById(R.id.streetAddressTf);
        cityTf = (EditText) findViewById(R.id.cityTf);
        stateTf = (EditText) findViewById(R.id.stateTf);
        postCodeTf = (EditText) findViewById(R.id.postCodeTf);
        phoneNumberTf = (EditText) findViewById(R.id.phoneNumberTf);


        patient = new Patient();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Patient");


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patient.setEmail(emailTf.getText().toString().trim());
                patient.setAge(Integer.parseInt(ageTf.getText().toString().trim()));
                patient.setCity(cityTf.getText().toString().trim());
                patient.setFirstName(firstNameTf.getText().toString().trim());
                patient.setLastName(lastNameTf.getText().toString().trim());
                patient.setMedicareNumber(medicareNumberTf.getText().toString().trim());
                patient.setPhoneNumber(phoneNumberTf.getText().toString().trim());
                patient.setPassword(passwordTf.getText().toString().trim());
                patient.setPostCode(passwordTf.getText().toString().trim());
                patient.setState(stateTf.getText().toString().trim());
                patient.setStreetAddress(streetAddressTf.getText().toString().trim());

                String patientEmail = patient.getEmail();

               dbRef.child(patientEmail).setValue(patient);
                Toast.makeText(Register.this, "data inserted successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*To Do : add limiters for phone numbers, medicare numbers, postcode length, etc. ****/
}
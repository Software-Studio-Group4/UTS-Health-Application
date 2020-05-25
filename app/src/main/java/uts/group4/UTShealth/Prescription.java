package uts.group4.UTShealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.AppointmentModel;

public class Prescription extends AppCompatActivity {
    EditText docNameTf, patNameTf, dateTf, recipeTf, medInsTf, dispInsTf;
    Button doneBtn;
    private static String docName;
    private static String patName;
    private static String date;
    private static String recipe;
    private static String medIns;
    private static String dispIns;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    CollectionReference appointmentRef = fStore.collection("Appointment");
    Intent intent = getIntent();
    //code to receive appointmentID all the way from BookAppointment class
    String appid = intent.getStringExtra("APPOINTMENT_ID");
    String id = appointmentRef.document().getId();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        docNameTf = findViewById(R.id.docNameTf);
        patNameTf = findViewById(R.id.patNameTf);
        dateTf = findViewById(R.id.dateTf);
        recipeTf = findViewById(R.id.recipeTf);
        medInsTf = findViewById(R.id.medInsTf);
        dispInsTf = findViewById(R.id.dispInsTf);
        doneBtn = findViewById(R.id.doneBtn);

        appointmentRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String doctorName = document.getString("DoctorFullName");
                        docNameTf.setText(doctorName);
                        String patientName = document.getString("PatientFullName");
                        patNameTf.setText(patientName);
                        String date = document.getString("Date");
                        dateTf.setText(date);
                    }
                }
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docName = docNameTf.getText().toString().trim();
                patName = patNameTf.getText().toString().trim();
                date = dateTf.getText().toString().trim();
                recipe = recipeTf.getText().toString().trim();
                medIns = medInsTf.getText().toString().trim();
                dispIns = dispInsTf.getText().toString().trim();

                if (TextUtils.isEmpty(docName)) {
                    docNameTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(patName)) {
                    patNameTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(date)) {
                    dateTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(recipe)) {
                    recipeTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(medIns)) {
                    medInsTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(dispIns)) {
                    dispInsTf.setError("Cannot have Empty Field");
                    return;
                }

                DocumentReference documentReference = fStore.collection("Appointment").document(appid).collection("Prescription").document(userID);
                Map<String, Object> prescriptionData = new HashMap<>(); //
                prescriptionData.put("DoctorFullName", docName);
                prescriptionData.put("PatientFullName", patName);
                prescriptionData.put("Date", date);
                prescriptionData.put("Recipe", recipe);
                prescriptionData.put("MedicalInstruction", medIns);
                prescriptionData.put("DispensingInstruction", dispIns);
                documentReference.set(prescriptionData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Prescription.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Prescription.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });


                startActivity(new Intent(getApplicationContext(), Notes.class));
                CustomIntent.customType(Prescription.this, "fadein-to-fadeout");
            }
        });
    }
}

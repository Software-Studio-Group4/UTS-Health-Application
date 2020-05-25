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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import maes.tech.intentanim.CustomIntent;

public class Prescription extends AppCompatActivity {
    EditText  recipeTf, medInsTf, dispInsTf;
    Button doneBtn;
    private static String recipe;
    private static String medIns;
    private static String dispIns;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    final int min = 1;
    final int max = 9999;
    final int random = new Random().nextInt((max - min) + 1) + min;
    String presId = userID + random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        recipeTf = findViewById(R.id.recipeTf);
        medInsTf = findViewById(R.id.medInsTf);
        dispInsTf = findViewById(R.id.dispInsTf);
        doneBtn = findViewById(R.id.doneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe = recipeTf.getText().toString().trim();
                medIns = medInsTf.getText().toString().trim();
                dispIns = dispInsTf.getText().toString().trim();

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
                //code to get the chat code
                Bundle extras = getIntent().getExtras();
                assert extras != null;
                String chatCode = extras.getString("Chatroomcode");
                fStore.collection("Appointment")
                        .whereEqualTo("ChatCode", chatCode)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String id = document.getId();
                                        DocumentReference documentReference = fStore.collection("Appointment").document(id).collection("Prescription").document(presId);
                                        Map<String, Object> prescriptionData = new HashMap<>();
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
                                    }
                                } else {
                                    Toast.makeText(Prescription.this, "Can't retrieve document", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                Intent i = new Intent(getApplicationContext(), Notes.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatroomcode1", chatCode);
                i.putExtras(bundle);
                startActivity(i);
                CustomIntent.customType(Prescription.this, "fadein-to-fadeout");
            }
        });
    }
}

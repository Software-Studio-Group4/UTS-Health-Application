package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class PrescriptionNotes extends AppCompatActivity {
    EditText medicationTf, directionsTf, notesTf;
    Button nextBtn;
    public String medication;
    public String directions;
    public String notes;
    String chatCode;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescription_layout);

        medicationTf = findViewById(R.id.medicationTf);
        directionsTf = findViewById(R.id.directionsTf);
        notesTf = findViewById(R.id.notesTf);
        nextBtn = findViewById(R.id.nextBtn);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        chatCode = extras.getString("Chatroomcode");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medication = medicationTf.getText().toString().trim();
                directions = directionsTf.getText().toString().trim();
                notes = notesTf.getText().toString().trim();

                if (TextUtils.isEmpty(medication)) {
                    medicationTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(directions)) {
                    directionsTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(notes)) {
                    notesTf.setError("Cannot have Empty Field");
                    return;
                }

                //code to get the chat code
                fStore.collection("Appointment")
                        .whereEqualTo("ChatCode", chatCode)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String id = document.getId();
                                        DocumentReference prescriptionRef = fStore.collection("Appointment").document(id).collection("PostAppointment").document("Prescription");
                                        Map<String, Object> prescriptionData = new HashMap<>();
                                        prescriptionData.put("Medication", medication);
                                        prescriptionData.put("Instructions", directions);
                                        prescriptionRef.set(prescriptionData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) { // Stores prescription in Firestore
                                                Toast.makeText(PrescriptionNotes.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(PrescriptionNotes.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        DocumentReference notesRef = fStore.collection("Appointment").document(id).collection("PostAppointment").document("Notes");
                                        Map<String, Object> notesData = new HashMap<>();
                                        notesData.put("Notes", notes);
                                        notesRef.set(notesData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) { // Stores Notes in Firestore
                                                Toast.makeText(PrescriptionNotes.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(PrescriptionNotes.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(PrescriptionNotes.this, "Can't retrieve document", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Intent i = new Intent(getApplicationContext(), Confirmation.class);
                Bundle bundle = new Bundle();
                bundle.putString("Chatroomcode", chatCode);
                bundle.putString("Medication", medicationTf.getText().toString());
                bundle.putString("Instructions", directionsTf.getText().toString());
                bundle.putString("Notes", notesTf.getText().toString());
                i.putExtras(bundle);
                startActivity(i);
                CustomIntent.customType(PrescriptionNotes.this, "left-to-right");
                                    }
        });
    }

    public void backBtnPressed(View view) {
        Intent i = new Intent(getApplicationContext(), Chat.class);
        Bundle bundle = new Bundle();
        bundle.putString("chatroomcode", chatCode);
        i.putExtras(bundle);
        startActivity(i);
        CustomIntent.customType(PrescriptionNotes.this, "right-to-left");
    }
}

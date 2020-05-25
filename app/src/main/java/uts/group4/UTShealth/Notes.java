package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class Notes extends AppCompatActivity {

    EditText notesTf;
    Button sendBtn, prescriptionBtn;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        notesTf = findViewById(R.id.notesTf1);
        sendBtn = findViewById(R.id.sendBtn1);
        prescriptionBtn = findViewById(R.id.prescriptionBtn);
        Bundle extras = getIntent().getExtras();
        final String chatCode = extras.getString("Chatroomcode");

        prescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code to send chatid to Prescription class
                Intent i = new Intent(getApplicationContext(), Prescription.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatroomcode1", chatCode);
                i.putExtras(bundle);
                startActivity(i);
                CustomIntent.customType(Notes.this, "fadein-to-fadeout");
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String notes = notesTf.getText().toString().trim();
                if (TextUtils.isEmpty(notes)) {
                    notesTf.setError("Cannot have Empty Field");
                    return;
                }
                fStore.collection("Appointment")
                        .whereEqualTo("ChatCode", chatCode)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String id = document.getId();
                                        DocumentReference documentReference = fStore.collection("Appointment").document(id);
                                        Map<String, Object> notesData = new HashMap<>(); //
                                        notesData.put("Notes", notes);
                                        documentReference.update(notesData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Notes.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Notes.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(Notes.this, "Can't retrieve document", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
                CustomIntent.customType(Notes.this, "fadein-to-fadeout");
            }
        });

    }
}

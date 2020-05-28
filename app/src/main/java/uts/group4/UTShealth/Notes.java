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

public class Notes extends AppCompatActivity{

    EditText notesTf;
    Button saveBtn, backBtn;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        notesTf = findViewById(R.id.notesTf1);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                String chatCode = extras.getString("chatroomcode1");
                String stbmp = extras.getString("bitmap");
                fStore.collection("Appointment")
                        .whereEqualTo("ChatCode", chatCode)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String notes = notesTf.getText().toString().trim();
                                        if (TextUtils.isEmpty(notes)) {
                                            notesTf.setError("Cannot have Empty Field");
                                            return;
                                        }
                                        String id = document.getId();
                                        DocumentReference documentReference = fStore.collection("Appointment").document(id).collection("Notes").document(userID);
                                        Map<String, Object> notesData = new HashMap<>(); //
                                        notesData.put("Notes", notes);
                                        documentReference.set(notesData).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                Intent i = new Intent(getApplicationContext(), Confirmation.class);
                Bundle bundle = new Bundle();
                bundle.putString("Chatroomcode1", chatCode);
                bundle.putString("Bitmap", stbmp);
                bundle.putString("Notes", notesTf.getText().toString().trim());
                i.putExtras(bundle);
                startActivity(i);
                CustomIntent.customType(Notes.this, "fadein-to-fadeout");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Prescription.class));
            }
        });
    }


}


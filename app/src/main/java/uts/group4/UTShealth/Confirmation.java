package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
<<<<<<<< HEAD:app/src/main/java/uts/group4/UTShealth/Confirmation.java
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
========
import android.text.TextUtils;
>>>>>>>> origin/Angela:app/src/main/java/uts/group4/UTShealth/Notes.java
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<<< HEAD:app/src/main/java/uts/group4/UTShealth/Confirmation.java
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import maes.tech.intentanim.CustomIntent;

public class Confirmation extends AppCompatActivity implements Runnable  {

    Button backBtn, closeBtn;
    private Intent mShareIntent;
    private OutputStream os;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
========
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
>>>>>>>> origin/Angela:app/src/main/java/uts/group4/UTShealth/Notes.java
        backBtn = findViewById(R.id.backBtn);
        closeBtn = findViewById(R.id.closeBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
<<<<<<<< HEAD:app/src/main/java/uts/group4/UTShealth/Confirmation.java
                startActivity(new Intent(getApplicationContext(), PrescriptionNotes.class));
                CustomIntent.customType(Confirmation.this, "right-to-left");
========
                Bundle extras = getIntent().getExtras();
                String chatCode = extras.getString("chatroomcode1");
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
                i.putExtras(bundle);
                startActivity(i);
                CustomIntent.customType(Notes.this, "fadein-to-fadeout");
>>>>>>>> origin/Angela:app/src/main/java/uts/group4/UTShealth/Notes.java
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StaffDashboard.class));
                CustomIntent.customType(Confirmation.this, "left-to-right");
            }
        });
    }
<<<<<<<< HEAD:app/src/main/java/uts/group4/UTShealth/Confirmation.java

    public void makeAndSharePDF(View buttonSource) {
        new Thread(this).start();
    }

    public void run() {

        // Create a new PDF document in memory
        // We want it to be printable, so add PrintAttributes
        PrintAttributes printAttrs = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printAttrs = new PrintAttributes.Builder().
                    setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                    setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                    setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 300, 300)).
                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                    build();
        }
        PdfDocument document = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document = new PrintedPdfDocument(this, printAttrs);
        }

        // crate a page description
        PdfDocument.PageInfo pageInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pageInfo = new PdfDocument.PageInfo.Builder(300, 300, 1).create();
        }

        // create a new page from the PageInfo
        PdfDocument.Page page = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            page = document.startPage(pageInfo);
        }

        // test to create something in the page
        Canvas canvas = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            canvas = page.getCanvas();
        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);
========
>>>>>>>> origin/Angela:app/src/main/java/uts/group4/UTShealth/Notes.java



}

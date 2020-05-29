package uts.group4.UTShealth;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import maes.tech.intentanim.CustomIntent;

public class Confirmation extends AppCompatActivity implements Runnable  {

    Button backBtn, closeBtn;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private Intent mShareIntent;
    private OutputStream os;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        backBtn = findViewById(R.id.backBtn);
        closeBtn = findViewById(R.id.closeBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PrescriptionNotes.class));
                CustomIntent.customType(Confirmation.this, "right-to-left");
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
            pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        }

        // create a new page from the PageInfo
        PdfDocument.Page page = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            page = document.startPage(pageInfo);
        }
        Bundle extras = getIntent().getExtras();
        String chatCode = extras.getString("chatroomcode1");
//        String stbmps = extras.getString("Bitmap");
//        Bitmap bits = StringToBitMap(stbmps);
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bits,595, 842, false);

//        scaledBitmap.prepareToDraw();

        // test to create something in the page
        Canvas canvas = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            canvas = page.getCanvas();

        }

        final Paint paint = new Paint();
//        canvas.drawBitmap(scaledBitmap,0,0, paint);
        canvas.drawText("Prescription", 200,50, paint);
        canvas.drawText("Medication", 40, 100, paint);
        canvas.drawText("Instructions", 40, 130, paint);
        canvas.drawText("Notes", 40, 160, paint);
        final Canvas finalCanvas = canvas;
        fStore.collection("Appointment")
                .whereEqualTo("ChatCode", chatCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                fStore.collection("Appointment").document(id).collection("PostAppointment").document("Prescription")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document1 = task.getResult();
                                                    String medication = document1.get("Medication").toString();
                                                    finalCanvas.drawText(medication, 80, 100, paint);
                                                    String instructions = document1.get("Instructions").toString();
                                                    finalCanvas.drawText(instructions, 80, 130, paint);
                                                }
                                                else {
                                                    Toast.makeText(Confirmation.this, "Can't retrieve document", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                fStore.collection("Appointment").document(id).collection("PostAppointment").document("Notes")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document2 = task.getResult();
                                                    String notes = document2.get("Notes").toString();
                                                    finalCanvas.drawText(notes,80,160,paint);
                                                }
                                                else {
                                                    Toast.makeText(Confirmation.this, "Can't retrieve document", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(Confirmation.this, "Can't retrieve document", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        // do final processing of the page
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.finishPage(page);
        }


        // Write the PDF document to a file
        try {
            File pdfDirPath = new File(getFilesDir(), "pdfs");
            boolean wasSuccessful = pdfDirPath.mkdirs();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }
            File file = new File(pdfDirPath, "pdfsend.pdf");
            Uri contentUri = FileProvider.getUriForFile(this, "uts.group4.UTShealth.fileprovider", file);
            os = new FileOutputStream(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                document.writeTo(os);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                document.close();
            }
            os.close();

            shareDocument(contentUri);
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }
/*    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    */

    private void shareDocument(Uri uri) {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("application/pdf");
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Here is a PDF from UTS Health Application");
        // Attach the PDf as a Uri.
        mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(mShareIntent, "Send email..."));
    }


}

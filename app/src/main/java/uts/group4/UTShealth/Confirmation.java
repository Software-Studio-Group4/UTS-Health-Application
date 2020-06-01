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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;
import uts.group4.UTShealth.Model.ChatMessage;

public class Confirmation extends AppCompatActivity implements Runnable {

    TextView patientText, emailText;
    private Intent mShareIntent;
    private OutputStream os;
    String chatCode;
    String appointmentID;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private static final String TAG = "Confirmation";
    ArrayList<ChatMessage> chatMessages = new ArrayList<>();
    String doctorName;
    String patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        patientText = findViewById(R.id.patientText);
        emailText = findViewById(R.id.emailText);
        //code to get the chat code
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        chatCode = extras.getString("Chatroomcode");
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats/" + chatCode);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot messageSnapshot : dataSnapshot.getChildren()){
                    Log.i("LOGGER", messageSnapshot.getValue().toString());
                    chatMessages.add(messageSnapshot.getValue(ChatMessage.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DocumentReference apptRef = FirebaseFirestore.getInstance().collection("Appointment").document(chatCode.substring(4));
        apptRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                doctorName = documentSnapshot.getString("DoctorFullName");
                patientName = documentSnapshot.getString("PatientFullName");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // this code block displays the patients name & email
        appointmentID = chatCode.substring(4);
        DocumentReference docRef = fStore.collection("Appointment").document(appointmentID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            private String ID;
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    ID = documentSnapshot.getString("patientID");
                    Log.d("PatientID", ID);
                    DocumentReference patientRef = fStore.collection("Patients").document(ID);
                    patientRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String firstName = documentSnapshot.getString("First Name");
                            String lastName = documentSnapshot.getString("Last Name");
                            String email = documentSnapshot.getString("Email");
                            patientText.setText("Patient: " + firstName + " " + lastName);
                            emailText.setText("Email: " + email);
                        }
                    });
                } else {
                    Toast.makeText(Confirmation.this, "Cannot retrieve details", Toast.LENGTH_SHORT).show();
                }
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
        PdfDocument.PageInfo pageInfo1 = null;
        PdfDocument.PageInfo pageInfo2 = null;
        PdfDocument.PageInfo pageInfo3 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pageInfo1 = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            pageInfo2 = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
            pageInfo3 = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
        }

        // create a new page from the PageInfo
        PdfDocument.Page prescriptionPage = null;
        PdfDocument.Page ChatPage1 = null;
        PdfDocument.Page ChatPage2 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            prescriptionPage = document.startPage(pageInfo1);
        }

        Bundle extras = getIntent().getExtras();
        String chatCode = extras.getString("chatroomcode1");
        String med = extras.getString("Medication");
        String ins = extras.getString("Instructions");
        String note = extras.getString("Notes");
        String screenshot = extras.getString("bitmap");

        // test to create something in the page
        // test to create something in the page

        /**********************************make the prescription page***********************************/
        Canvas canvas1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            canvas1 = prescriptionPage.getCanvas();
        }
        Paint paint1 = new Paint();
        canvas1.drawText("Prescription", 230, 50, paint1);
        canvas1.drawText("Medication: ", 40, 100, paint1);
        canvas1.drawText("Instructions: ", 40, 130, paint1);
        canvas1.drawText("Notes: ", 40, 160, paint1);
        canvas1.drawText(med, 120, 100, paint1);
        canvas1.drawText(ins, 120, 130, paint1);
        canvas1.drawText(note, 120, 160, paint1);


        // do final processing of the page
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.finishPage(prescriptionPage);
        }

        /**************************************chat messages p1***********************************************/
        int numMessage = chatMessages.size();
        int numPages = numMessage/9;
        Canvas canvas2 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            ChatPage1 = document.startPage(pageInfo2);
            canvas2 = ChatPage1.getCanvas();
        }
        Paint paint2 = new Paint();
        canvas2.drawText("Chat Transcript", 230, 50, paint1);
        ArrayList<String> multiLineString = new ArrayList();
        int y = 100;
        int lineCount  = 0;
        for(int j = 0; j < 9; j++){
        //for(ChatMessage message : chatMessages){
            if(!chatMessages.get(j).hasImageUrl()) {
                //canvas2.drawText(chatMessages.get(j).getDateAndTime(), 475, y, paint2);
                canvas2.drawText(chatMessages.get(j).getName(), 30, y, paint2);
                if(chatMessages.get(j).getText().length() <= 55 && chatMessages.get(j).getText().indexOf('\n') == -1){
                    canvas2.drawText(chatMessages.get(j).getText(), 120, y, paint2);
                    y+= 30; lineCount++;lineCount++;
                }
                else if(chatMessages.get(j).getText().length() > 55 && chatMessages.get(j).getText().indexOf('\n' ) == -1) {
                    String string = chatMessages.get(j).getText();
                    int length = chatMessages.get(j).getText().length();
                    int numlines = length / 55;
                    if (length % 55 != 0) {
                        numlines++;
                    }
                    int index = 0;
                    for (int i = 0; i < numlines; i++) {
                        if (i == (numlines - 1)) {
                            multiLineString.add(string.substring(index, string.length() - 1));
                            break;
                        }
                        multiLineString.add(string.substring(index, index + 55));
                        index += 55;
                    }
                    for (int i = 0; i < numlines; i++) {
                        if (i == (numlines - 1)) {
                            canvas2.drawText(multiLineString.get(i), 120, y, paint2);
                            y += 30; lineCount++;
                            break;
                        }
                        canvas2.drawText(multiLineString.get(i), 120, y, paint2);
                        y += 15; lineCount++;
                    }
                }
                multiLineString.clear();
            }
            else{
               // canvas2.drawText(chatMessages.get(j).getDateAndTime(), 475, y, paint2);
                canvas2.drawText("[Image not Shown]", 120, y, paint2);
                canvas2.drawText(chatMessages.get(j).getName(), 30, y, paint2);
                y+= 30; lineCount++;lineCount++;
            }

        }
        // do final processing of the page
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.finishPage(ChatPage1);
        }
        /**************************************chat messages p2***********************************************/
        Canvas canvas3 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            ChatPage2 = document.startPage(pageInfo3);
            canvas3 = ChatPage2.getCanvas();
        }
        Paint paint3 = new Paint();
        canvas3.drawText("Chat Transcript Page 2", 230, 50, paint2);
        //ArrayList<String> multiLineString = new ArrayList();
        y = 100;
        //int lineCount  = 0;
        for(int j = 9; j < chatMessages.size(); j++){
            //for(ChatMessage message : chatMessages){
            if(!chatMessages.get(j).hasImageUrl()) {
                canvas3.drawText(chatMessages.get(j).getName(), 30, y, paint3);
                if(chatMessages.get(j).getText().length() <= 55 && chatMessages.get(j).getText().indexOf('\n') == -1){
                    canvas3.drawText(chatMessages.get(j).getText(), 120, y, paint3);
                    y+= 30; lineCount++;lineCount++;
                }
                else if(chatMessages.get(j).getText().length() > 55 && chatMessages.get(j).getText().indexOf('\n' ) == -1) {
                    String string = chatMessages.get(j).getText();
                    int length = chatMessages.get(j).getText().length();
                    int numlines = length / 55;
                    if (length % 55 != 0) {
                        numlines++;
                    }
                    int index = 0;
                    for (int i = 0; i < numlines; i++) {
                        if (i == (numlines - 1)) {
                            multiLineString.add(string.substring(index, string.length() - 1));
                            break;
                        }
                        multiLineString.add(string.substring(index, index + 55));
                        index += 55;
                    }
                    for (int i = 0; i < numlines; i++) {
                        if (i == (numlines - 1)) {
                            canvas3.drawText(multiLineString.get(i), 120, y, paint3);
                            y += 30; lineCount++;
                            break;
                        }
                        canvas3.drawText(multiLineString.get(i), 120, y, paint3);
                        y += 15; lineCount++;
                    }
                }
                multiLineString.clear();
            }
            else{

                canvas3.drawText("[Image not Shown]", 120, y, paint3);
                canvas3.drawText(chatMessages.get(j).getName(), 30, y, paint3);
                y+= 30; lineCount++;lineCount++;
            }

        }
        // do final processing of the page
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.finishPage(ChatPage2);
        }

        /*************************************end chat transcript*********************************************/


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


    private void shareDocument(Uri uri) {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("application/pdf");
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, "UTS Health Application : Appointment Summary");
        mShareIntent.putExtra(Intent.EXTRA_EMAIL, "placeholder@email.com");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, "Hi, " + patientName + "!\nAttached is a summary of your appointment with Dr." + doctorName  + " from today!\n\n Kind Regards,\nUTS Health Application Group 4");
        // Attach the PDf as a Uri.
        mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(mShareIntent, "Send email..."));
    }

    public void endAppointment(View view) {
        // This code marks the CompletionStatus to true
        DocumentReference docRef = fStore.collection("Appointment").document(appointmentID);
        docRef.update("CompletionStatus", true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });
        startActivity(new Intent(getApplicationContext(), StaffDashboard.class));
        CustomIntent.customType(Confirmation.this, "left-to-right");
    }

    public void backBtnPressed(View view) {
        Intent i = new Intent(getApplicationContext(), PrescriptionNotes.class);
        Bundle bundle = new Bundle();
        bundle.putString("Chatroomcode", chatCode);
        i.putExtras(bundle);
        startActivity(i);
        CustomIntent.customType(Confirmation.this, "right-to-left");
    }
}

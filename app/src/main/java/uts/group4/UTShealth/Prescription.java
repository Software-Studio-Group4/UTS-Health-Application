package uts.group4.UTShealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import maes.tech.intentanim.CustomIntent;

public class Prescription extends AppCompatActivity {
    EditText docNameTf, docSpecialisationTf, patNameTf, dateTf, recipeTf, medInsTf, dispInsTf;
    Button doneBtn;
    private static String docName;
    private static String docSpe;
    private static String patName;
    private static String date;
    private static String recipe;
    private static String medIns;
    private static String dispIns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        docNameTf = findViewById(R.id.docNameTf);
        docSpecialisationTf = findViewById(R.id.docSpecialisationTf);
        patNameTf = findViewById(R.id.patNameTf);
        dateTf = findViewById(R.id.dateTf);
        recipeTf = findViewById(R.id.recipeTf);
        medInsTf = findViewById(R.id.medInsTf);
        dispInsTf = findViewById(R.id.dispInsTf);
        doneBtn = findViewById(R.id.doneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docName = docNameTf.getText().toString().trim();
                docSpe = docSpecialisationTf.getText().toString().trim();
                patName = patNameTf.getText().toString().trim();
                date = dateTf.getText().toString().trim();
                recipe = recipeTf.getText().toString().trim();
                medIns = medInsTf.getText().toString().trim();
                dispIns = dispInsTf.getText().toString().trim();

                if (TextUtils.isEmpty(docName)) {
                    docNameTf.setError("Cannot have Empty Field");
                    return;
                }
                if (TextUtils.isEmpty(docSpe)) {
                    docSpecialisationTf.setError("Cannot have Empty Field");
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
                startActivity(new Intent(getApplicationContext(), Notes.class));
                CustomIntent.customType(Prescription.this, "fadein-to-fadeout");
            }
        });
    }
}

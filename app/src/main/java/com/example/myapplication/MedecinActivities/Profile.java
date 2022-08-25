package com.example.myapplication.MedecinActivities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.example.myapplication.FormMedecin;
import com.example.myapplication.R;

public class Profile  extends AppCompatActivity {

    private FormMedecin infoProfile;

    private TextView nomMed,email,numTel,service,grade,nbPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        infoProfile = (FormMedecin)getIntent().getSerializableExtra("infoMedecin");
        initVariable();

        nomMed.setText(""+infoProfile.getNom()+"-"+infoProfile.getPrenom());
        email.setText(infoProfile.getMail());
        numTel.setText(""+infoProfile.getNumTel());
        grade.setText(infoProfile.getGrade());
        service.setText(infoProfile.getService());
        nbPatient.setText(""+infoProfile.getNbPatient());

    }

    private void initVariable()
    {
        nomMed = findViewById(R.id.nomMed);
        email = findViewById(R.id.MailMedecin);
        numTel = findViewById(R.id.numTelMed);
        service = findViewById(R.id.serviceMed);
        grade = findViewById(R.id.gradeMed);
        nbPatient = findViewById(R.id.nombrePat);
    }
}
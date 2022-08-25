package com.example.myapplication.MedecinActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import androidx.cardview.widget.CardView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.myapplication.FormLogin;
import com.example.myapplication.FormulairePatient;
import com.example.myapplication.MiBand3.ScanActivity;
import com.example.myapplication.R;
import com.example.myapplication.ObjectPaquet;
import com.example.myapplication.SSl_Socket.SocketManage;


public class AjoutPatienActivity extends AppCompatActivity {


    private CardView Valider ;
    private boolean response = false;
    private  Intent intent;
    private Context context;

    //variables des champs du formulaire

    private TextView idPatient,passWord,confirmPass;
    private AwesomeValidation awesomeValidation3;
    private FormulairePatient newFormulairePatient;
    private FormLogin loginP = new FormLogin();
    private long IdMedecin;
    private ObjectPaquet paquet = new ObjectPaquet();
    private SocketManage socketManage = new SocketManage();

    private boolean connexion ;
    private long tempsDebut, tempsFin;
    private double seconds;

    //_______________SSL Parametre_________________


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout_activity);

        //initialisation des varibales
        initVariables();
        IdMedecin = (long)getIntent().getSerializableExtra("idMedecin");
        Log.e("Id Medecin",""+IdMedecin);

        context = this;
        initAwsomeValidation();

        //verification des champs

        String ConfirmregexPassword = passWord.getText().toString().trim();
        if (!(confirmPass.getText().toString().trim()).equals(passWord.getText().toString().trim())  ) {
            awesomeValidation3.addValidation(this, R.id.confirmPass, ConfirmregexPassword, R.string.passwordConfirmation);
        }






        intent = new Intent(this, ScanActivity.class);

        Valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(awesomeValidation3.validate()){
                    if(traitementFormulaire(v) && connexion) {
                        Log.e("Lance le scan capteur"," ok ");
                        intent.putExtra("idPatient", Long.parseLong(idPatient.getText().toString()));
                        intent.putExtra("idMedecin",IdMedecin);
                        startActivityForResult(intent,1);
                    }
                    else if(!traitementFormulaire(v) && connexion){
                        alertPatientExiste();
                    }
               }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initAwsomeValidation(){

        String regexIdPatient= "[0-9]{12}";
        String regexPass= ".{8,}";

        awesomeValidation3 = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation3.addValidation(this, R.id.idPatient, RegexTemplate.NOT_EMPTY, R.string.emailValidationVide);
        awesomeValidation3.addValidation(this, R.id.idPatient, regexIdPatient, R.string.NinCorect);

        awesomeValidation3.addValidation(this, R.id.passWord, RegexTemplate.NOT_EMPTY, R.string.emailValidationVide);
        awesomeValidation3.addValidation(this, R.id.passWord, regexPass, R.string.passwordCorrect);

    }


    private void initVariables() {


        Valider = findViewById(R.id.Valider);


        idPatient = findViewById(R.id.idPatient);
        passWord = findViewById(R.id.passWord);
        confirmPass = findViewById(R.id.confirmPass);
    }

    private boolean traitementFormulaire(View v) {

            loginP.setIdConnexion( Long.parseLong(idPatient.getText().toString()));
            loginP.setPassWord(passWord.getText().toString());



            newFormulairePatient = new FormulairePatient();

            newFormulairePatient.setIdPatient(Long.parseLong(idPatient.getText().toString()));
            newFormulairePatient.setLogin(loginP);
            newFormulairePatient.setIdMedecin(IdMedecin);

            paquet.setService(2);
            paquet.setFormulairePatient(newFormulairePatient);


        try {
            tempsDebut = System.currentTimeMillis();

            socketManage.ConnexionSocket(v, null);
            connexion = true;
            boolean find = socketManage.chat(paquet);
            tempsFin = System.currentTimeMillis();
            seconds = (tempsFin - tempsDebut) / 1000F;
            Log.e("temps Execute"," = "+seconds);
            Toast.makeText(this,"Time = "+seconds,Toast.LENGTH_LONG);

            return find;


        } catch (Exception e) {
            e.printStackTrace();
            alertConnexion();
            connexion = false;
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            alertEroor();
        }
        return response;
    }

    private void alertEroor() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Quitter La Page");

        // set dialog message
        alertDialogBuilder
                .setMessage("Voulez vous vraiment quitter cette page")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        response = true;
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        response = false;
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void alertPatientExiste() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Patient Existe");

        // set dialog message
        alertDialogBuilder
                .setMessage("Cette Idantifiant est deja utiliser pour un autre patient")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void alertConnexion() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Impossible de Se Connecter");

        // set dialog message
        alertDialogBuilder
                .setMessage("Merci de verifier votre Connetion ... réessayer ulterierment !!!   ")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

}

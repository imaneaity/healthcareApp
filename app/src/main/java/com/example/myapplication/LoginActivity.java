package com.example.myapplication;


import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.myapplication.MedecinActivities.Serveur_Service;
import com.example.myapplication.MiBand3.ScanActivity;
import com.example.myapplication.PatientActivities.patientHomeService;
import com.example.myapplication.SSl_Socket.SocketManage;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    private ImageView bookIconImageView;
    private TextView bookITextView;

    private TextView idUsers;
    private TextView password;


    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    private Button login;
    private boolean connexion;
    private boolean blm =false;
    private AwesomeValidation awesomeValidation;
    private FormLogin  formLogin = new FormLogin();
    private ObjectPaquet paquet = new ObjectPaquet();
    private long idUser ;
    private ArrayList<FormCapteur> listCapteur = new ArrayList<>();

    private int nbTentative;
    private com.example.myapplication.session session;
    private long tempsDebut, tempsFin;
    private double seconds;

    //_______________SSL Parametre_________________

    private SocketManage socketManage = new SocketManage();
    private FormMedecin medeicinInfo ;
    private Intent mIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new session(this);
        if(!session.isLoggedIn()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.login_activity);
            initViews();


            String regexIdf = "[0-9]{10,}";
            awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            awesomeValidation.addValidation(this, R.id.emailEditText, regexIdf, R.string.emailValidation);
            awesomeValidation.addValidation(this, R.id.emailEditText, RegexTemplate.NOT_EMPTY, R.string.emailValidationVide);
            String regexPassword = ".{8,}";
            awesomeValidation.addValidation(this, R.id.passwordEditText, regexPassword, R.string.passwordValidation);


            new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    loadingProgressBar.setVisibility(VISIBLE);
                }

                @Override
                public void onFinish() {
                    bookITextView.setVisibility(GONE);
                    loadingProgressBar.setVisibility(GONE);
                    rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorSplashText));
                    bookIconImageView.setImageResource(R.drawable.logo);
                    startAnimation();
                }
            }.start();


            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (awesomeValidation.validate()) {
                        LoginSslValidation(view);
                        if (connexion && !blm) {
                            HomePage(view);
                        } else if (!connexion && !blm) {
                            if (nbTentative < 3) {
                                alertEroor();
                            } else {
                                alertBlock();
                            }
                        }
                    }
                }
            });
        }else{
            String user = String.valueOf(session.getUserNames());
           if(user.length() > 0 && user.length()<12){
               Intent mIntent = new Intent(this, Serveur_Service.class);
                this.startService(mIntent);

           }else{
               Intent mIntent = new Intent(this, patientHomeService.class);
               this.startService(mIntent);
           }

        }
    }


    private void initViews() {
        bookIconImageView = findViewById(R.id.bookIconImageView);
        bookITextView = findViewById(R.id.bookITextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);
        login = findViewById(R.id.loginButton);
        idUsers = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);


        nbTentative = 0;
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private void HomePage(View v) {

        String orientaation = idUsers.getText().toString();
        if(orientaation.length() < 12) {
            Intent mIntent = new Intent(this, Serveur_Service.class);
            mIntent.putExtra("idMedecin", idUser);
            this.startService(mIntent);
            this.finish();
        }
        else {
            Intent mIntent = new Intent(this, ScanActivity.class);
            paquet.setIdUser(idUser);
            paquet.setService(8);
            socketManage.ConnexionSocket(v,null);

            mIntent.putExtra("patient", socketManage.getPatient(paquet));
            startActivityForResult(mIntent,1);
        }
    }

    private void LoginSslValidation(View v) {
        if(awesomeValidation.validate()) {
            tempsDebut = System.currentTimeMillis();

            idUser = Long.parseLong(idUsers.getText().toString());
            formLogin.setIdConnexion(idUser);
            //Log.e("idConnexion :",""+Long.parseLong(idUsers.getText().toString()));
            formLogin.setPassWord(password.getText().toString());

            paquet.setService(1);
            paquet.setFormLogin(formLogin);

            try {
                socketManage.ConnexionSocket(v, null);
                connexion = socketManage.chat(paquet);
                blm = false;
            } catch (Exception e) {
                e.printStackTrace();
                alertConnexion();
                blm = true;
            }

            tempsFin = System.currentTimeMillis();
            seconds = (tempsFin - tempsDebut) / 1000F;

            Toast message = Toast.makeText(this,"Time = "+seconds,Toast.LENGTH_LONG);
            message.show();
        }

    }



    private void alertEroor() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Impossible de Connecter");

        // set dialog message
        alertDialogBuilder
                .setMessage("Votre Identifiant ou le mot de passe est incorrecte")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        idUsers.setText(String.valueOf(idUser));
        idUsers.requestFocus();
        password.setText("");
        nbTentative++;
        // show it
        alertDialog.show();
    }

    private void alertBlock() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Impossible de Connecter");

        // set dialog message
        alertDialogBuilder
                .setMessage("Votre Identifiant ou le mot de passe est incorrecte")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        idUsers.setText(String.valueOf(idUser));
        idUsers.requestFocus();
        password.setText("");
        nbTentative = 0;
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
        idUsers.setText(String.valueOf(idUser));
        idUsers.requestFocus();
        password.setText("");
        nbTentative = 0;
        // show it
        alertDialog.show();
    }


}

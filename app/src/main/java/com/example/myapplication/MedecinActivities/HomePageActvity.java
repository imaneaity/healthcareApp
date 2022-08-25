package com.example.myapplication.MedecinActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.AdapterNotif;
import com.example.myapplication.FormMedecin;
import com.example.myapplication.FormulairePatient;
import com.example.myapplication.FromNotification;
import com.example.myapplication.R;
import com.example.myapplication.ObjectPaquet;
import com.example.myapplication.SSl_Socket.SocketManage;
import com.example.myapplication.session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class HomePageActvity extends AppCompatActivity implements View.OnClickListener {

    private CardView ajoutCard;
    private CardView suivitCard;
    private CardView statistiqueCard;
    private CardView profileCard ;
    private CardView notificationCard;
    private TextView badgeNotification;
    private ArrayList<FormulairePatient> patients = new ArrayList<FormulairePatient>();
    private ArrayList<FromNotification> notifications = new ArrayList<FromNotification>();
    private FromNotification notifInfo ;
    private  static FormMedecin medeicinInfo ;


    private ObjectPaquet paquet = new ObjectPaquet();
    private SocketManage socketManage = new SocketManage();


    private com.example.myapplication.session session;
    private BottomSheetDialog mBottomSheetDialogNotif;
    private BottomSheetDialog mBottomSheetDialogNotifInfo;


    private RecyclerView mRycycleView;
    private AdapterNotif recylAdapter;
    private RecyclerView.LayoutManager mLayoutManger;
    private Context contex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_actvity);
        //init les CardView

        ajoutCard = findViewById(R.id.Ajout);
        suivitCard = findViewById(R.id.Suivit);
        statistiqueCard = findViewById(R.id.Statistique);
        profileCard = findViewById(R.id.Profile);
        notificationCard = findViewById(R.id.Notification);
        contex = this;
        badgeNotification = findViewById(R.id.badge_notification_4);
        badgeNotification.setVisibility(GONE);

        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_notif, null);
        (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialogNotif.dismiss();
            }
        });

        final View bottomSheetLayoutInfo = getLayoutInflater().inflate(R.layout.bottom_sheet_info_alert, null);
        (bottomSheetLayoutInfo.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialogNotifInfo.dismiss();
            }
        });
        mBottomSheetDialogNotifInfo = new BottomSheetDialog(this);
        mBottomSheetDialogNotifInfo.setContentView(bottomSheetLayoutInfo);

        (bottomSheetLayout.findViewById(R.id.button_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paquet.setIdUser(medeicinInfo.getIdMedecin());
                paquet.setService(17);
                socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                socketManage.chat(paquet);
                notifications = null;
                alertNotif();
                mBottomSheetDialogNotif.dismiss();

                badgeNotification.setVisibility(GONE);
                badgeNotification.setText(""+0);
            }
        });

        mBottomSheetDialogNotif = new BottomSheetDialog(this);
        mBottomSheetDialogNotif.setContentView(bottomSheetLayout);

        mRycycleView = mBottomSheetDialogNotif.findViewById(R.id.listNotification);
        mRycycleView.setHasFixedSize(true);
        mLayoutManger = new LinearLayoutManager(this);


        session = new session(this);
        if(!session.isLoggedIn()){

            medeicinInfo = (FormMedecin) getIntent().getSerializableExtra("Medecin");
                Log.e("Session user HommePage"," "+medeicinInfo.getIdMedecin());
            session.createLoginSession(medeicinInfo.getIdMedecin(),2);
            session.valideLoginSession();


        }
        notifications = (ArrayList<FromNotification>)getIntent().getSerializableExtra("notif");

        if (notifications == null){
             Log.e("notification ","on creat found");
            paquet.setIdUser(medeicinInfo.getIdMedecin());
            paquet.setService(15);
            socketManage.ConnexionSocket(null, this.getResources().openRawResource(R.raw.keystorepk));
            notifications = new ArrayList<FromNotification>();
            notifications = socketManage.ListNotification(paquet);
            recylAdapter = new AdapterNotif(notifications);
            mRycycleView.setLayoutManager(mLayoutManger);
            mRycycleView.setAdapter(recylAdapter);
            recylAdapter.setOnItemClickListener(new AdapterNotif.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    notifInfo = notifications.get(position);
                    paquet.setIdUser(notifInfo.getIdPatient());
                    paquet.setService(8);
                    socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                    FormulairePatient patient = socketManage.getPatient(paquet);

                    paquet.setFormNotification(notifInfo);
                    paquet.setService(18);
                    socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                    int HR = socketManage.getHR(paquet);

                    TextView nomPop = mBottomSheetDialogNotifInfo.findViewById(R.id.NomPatAlert);
                    nomPop.setText( " Nom : "+patient.getNom());

                    TextView prenomPop = mBottomSheetDialogNotifInfo.findViewById(R.id.PrenomPatAlert);
                    prenomPop.setText(" Prenom : "+patient.getPrenom());

                    TextView age = mBottomSheetDialogNotifInfo.findViewById(R.id.dateAlert);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = sdf.format(notifInfo.getDate());
                    age.setText( " Date : "+currentTime);

                    TextView HRAlert = mBottomSheetDialogNotifInfo.findViewById(R.id.HRAlert);
                    HRAlert.setText(HR+" BPM");

                    mBottomSheetDialogNotifInfo.show();
                }

                @Override
                public void onDeleteClick(int position) {
                    notifInfo = notifications.get(position);
                    paquet.setService(21);
                    paquet.setFormNotification(notifInfo);
                    socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                    try{
                        socketManage.chat(paquet);
                    } catch (Exception e) {
                        e.printStackTrace();
                        alertConnexion();
                    }
                    paquet.setIdUser(medeicinInfo.getIdMedecin());
                    paquet.setService(15);
                    socketManage.ConnexionSocket(null, contex.getResources().openRawResource(R.raw.keystorepk));
                    notifications = new ArrayList<FromNotification>();
                    notifications = socketManage.ListNotification(paquet);
                    if (notifications.size()>0) {
                        recylAdapter = new AdapterNotif(notifications);
                        mRycycleView.setLayoutManager(mLayoutManger);
                        mRycycleView.setAdapter(recylAdapter);
                        badgeNotification.setVisibility(VISIBLE);
                        badgeNotification.setText(""+notifications.size());

                    }
                    else {
                        alertNotif();
                        mBottomSheetDialogNotif.dismiss();
                    }
                }


            });
            if (notifications.size() > 0) {
                badgeNotification.setVisibility(VISIBLE);
                badgeNotification.setText(""+notifications.size());
                mBottomSheetDialogNotif.show();
            }
        }
        if (notifications != null){
            Log.e("notification ","on creat not found");

            if(notifications.size() > 0) {
                paquet.setIdUser(medeicinInfo.getIdMedecin());
                paquet.setService(16);
                socketManage.ConnexionSocket(null, this.getResources().openRawResource(R.raw.keystorepk));
                socketManage.chat(paquet);
                recylAdapter = new AdapterNotif(notifications);
                mRycycleView.setLayoutManager(mLayoutManger);
                mRycycleView.setAdapter(recylAdapter);
                recylAdapter.setOnItemClickListener(new AdapterNotif.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        notifInfo = notifications.get(position);
                        paquet.setIdUser(notifInfo.getIdPatient());
                        paquet.setService(8);
                        socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                        FormulairePatient patient = socketManage.getPatient(paquet);

                        paquet.setFormNotification(notifInfo);
                        paquet.setService(18);
                        socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                        int HR = socketManage.getHR(paquet);

                        TextView nomPop = mBottomSheetDialogNotifInfo.findViewById(R.id.NomPatAlert);
                        nomPop.setText( " Nom : "+patient.getNom());

                        TextView prenomPop = mBottomSheetDialogNotifInfo.findViewById(R.id.PrenomPatAlert);
                        prenomPop.setText(" Prenom : "+patient.getPrenom());

                        TextView age = mBottomSheetDialogNotifInfo.findViewById(R.id.dateAlert);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentTime = sdf.format(notifInfo.getDate());
                        age.setText( " Date : "+currentTime);

                        TextView HRAlert = mBottomSheetDialogNotifInfo.findViewById(R.id.HRAlert);
                        HRAlert.setText(HR+" BPM");

                        mBottomSheetDialogNotifInfo.show();
                    }

                    @Override
                    public void onDeleteClick(int position) {
                        notifInfo = notifications.get(position);
                        paquet.setService(21);
                        paquet.setFormNotification(notifInfo);
                        socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                        try{
                            socketManage.chat(paquet);
                        } catch (Exception e) {
                            e.printStackTrace();
                            alertConnexion();
                        }
                        paquet.setIdUser(medeicinInfo.getIdMedecin());
                        paquet.setService(15);
                        socketManage.ConnexionSocket(null, contex.getResources().openRawResource(R.raw.keystorepk));
                        notifications = new ArrayList<FromNotification>();
                        notifications = socketManage.ListNotification(paquet);
                        if (notifications.size()>0) {

                            recylAdapter = new AdapterNotif(notifications);
                            mRycycleView.setLayoutManager(mLayoutManger);
                            mRycycleView.setAdapter(recylAdapter);
                        }
                        else {
                            alertNotif();
                            badgeNotification.setVisibility(GONE);
                            badgeNotification.setText("");
                            mBottomSheetDialogNotif.dismiss();
                        }
                    }


                });
                badgeNotification.setVisibility(VISIBLE);
                badgeNotification.setText(""+notifications.size());
                mBottomSheetDialogNotif.show();
            }

        }
        //Click Lister pour les CardView
        Log.e("notification "," "+notifications);

        ajoutCard.setOnClickListener(this);
        suivitCard.setOnClickListener(this);
        statistiqueCard.setOnClickListener(this);
        profileCard.setOnClickListener(this);
        notificationCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent;



        switch (v.getId()){
            case R.id.Ajout : intent = new Intent(this, AjoutPatienActivity.class);

                Log.e("Id Medecin",""+session.getUserNames());

                intent.putExtra("idMedecin",session.getUserNames());
                startActivity(intent);
                break;
            case R.id.Statistique : intent = new Intent(this, StatistiqueNationale.class);
                startActivity(intent);
                break;
            case R.id.Suivit : intent = new Intent(this, SuivitPatients.class);
                paquet.setIdUser(session.getUserNames());
                paquet.setService(4);
                try{
                    socketManage.ConnexionSocket(v,null);
                     patients = socketManage.patients(paquet);
                    intent.putExtra("ListePatient",patients);
                    intent.putExtra("idMedecin",session.getUserNames());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    alertConnexion();
                }

                break;
            case R.id.Profile : intent = new Intent(this, Profile.class);
                paquet.setIdUser(medeicinInfo.getIdMedecin());
                paquet.setService(7);
                socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                medeicinInfo = socketManage.getMedecin(paquet);
                medeicinInfo.setIdMedecin(medeicinInfo.getIdMedecin());
                intent.putExtra("infoMedecin",medeicinInfo);
                startActivity(intent);
                break;
            case R.id.Notification :
                if(notifications != null) {
                    if (notifications.size() > 0) {
                        Log.e("Notification","on click exist ");

                        mBottomSheetDialogNotif.show();
                    } else {
                        Log.e("NOtification","on click not exist ");

                        alertNotif();
                    }
                }else{
                    Log.e("Notification","on click searche ");
                    paquet.setIdUser(medeicinInfo.getIdMedecin());
                    paquet.setService(15);
                    socketManage.ConnexionSocket(null, this.getResources().openRawResource(R.raw.keystorepk));
                    notifications = new ArrayList<FromNotification>();
                    notifications = socketManage.ListNotification(paquet);
                    if (notifications.size() > 0) {
                        Log.e("NOtification","on click found ");

                        recylAdapter = new AdapterNotif(notifications);
                        mRycycleView.setLayoutManager(mLayoutManger);
                        mRycycleView.setAdapter(recylAdapter);
                        recylAdapter.setOnItemClickListener(new AdapterNotif.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                notifInfo = notifications.get(position);
                                paquet.setIdUser(notifInfo.getIdPatient());
                                paquet.setService(8);
                                socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                                FormulairePatient patient = socketManage.getPatient(paquet);

                                paquet.setFormNotification(notifInfo);
                                paquet.setService(18);
                                socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                                int HR = socketManage.getHR(paquet);

                                TextView nomPop = mBottomSheetDialogNotifInfo.findViewById(R.id.NomPatAlert);
                                nomPop.setText( " Nom : "+patient.getNom());

                                TextView prenomPop = mBottomSheetDialogNotifInfo.findViewById(R.id.PrenomPatAlert);
                                prenomPop.setText(" Prenom : "+patient.getPrenom());

                                TextView age = mBottomSheetDialogNotifInfo.findViewById(R.id.dateAlert);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentTime = sdf.format(notifInfo.getDate());
                                age.setText( " Date : "+currentTime);

                                TextView HRAlert = mBottomSheetDialogNotifInfo.findViewById(R.id.HRAlert);
                                HRAlert.setText(HR+" BPM");

                                mBottomSheetDialogNotifInfo.show();
                            }

                            @Override
                            public void onDeleteClick(int position) {
                                notifInfo = notifications.get(position);
                                paquet.setService(21);
                                paquet.setFormNotification(notifInfo);
                                socketManage.ConnexionSocket(null, HomePageActvity.this.getResources().openRawResource(R.raw.keystorepk));
                                try{
                                    socketManage.chat(paquet);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    alertConnexion();
                                }
                                paquet.setIdUser(medeicinInfo.getIdMedecin());
                                paquet.setService(15);
                                socketManage.ConnexionSocket(null, contex.getResources().openRawResource(R.raw.keystorepk));
                                notifications = new ArrayList<FromNotification>();
                                notifications = socketManage.ListNotification(paquet);
                                if (notifications.size()>0) {
                                    recylAdapter = new AdapterNotif(notifications);
                                    mRycycleView.setLayoutManager(mLayoutManger);
                                    mRycycleView.setAdapter(recylAdapter);


                                }
                                else {
                                    alertNotif();
                                    mBottomSheetDialogNotif.dismiss();
                                }
                            }


                        });
                        badgeNotification.setVisibility(VISIBLE);
                        badgeNotification.setText(""+notifications.size());
                        mBottomSheetDialogNotif.show();
                    } else {
                        alertNotif();
                    }


                }
                break;

            default: break;
        }

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


    private void alertNotif() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Pas De Notification!!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Aucune Notification Pour Vous ...... Merci !!!   ")
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

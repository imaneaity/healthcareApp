package com.example.myapplication.MedecinActivities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.example.myapplication.FormMedecin;
import com.example.myapplication.FromNotification;
import com.example.myapplication.ObjectPaquet;
import com.example.myapplication.session;
import com.example.myapplication.R;
import com.example.myapplication.SSl_Socket.SocketManage;

import java.util.ArrayList;

import static com.example.myapplication.NotificationService.CHANNEL_ID_Alert;
import static com.example.myapplication.NotificationService.CHANNEL_ID_Service;

public class Serveur_Service extends Service {

    private long idMedecin ;
    private Intent mIntent;
    private com.example.myapplication.session session;
    private final static String Titre_notification = "Iot E_Sante";
    private final static String Titre_Alerte = "Alert !!!!!!!!!!!!";
    private final static String Descreption_notification = "L'application Fonctionne";
    private android.app.Notification notification;
    private Notification alert;
    private NotificationManager mNotificationManager;
    private PendingIntent pendingIntent;
    private Intent homePageIntent;
    private SocketManage socketManage = new SocketManage();
    private ObjectPaquet paquet = new ObjectPaquet();
    private FormMedecin medeicinInfo = new FormMedecin();
    private Context context;
    private int nbNotification;
    private int nbNotificationTemp;
    private ArrayList<FromNotification> notifcations;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            if (medeicinInfo != null) {
                Log.e("requeste serveur", "sTART");
                lancerServeurRequest();
            }
        }
    };

    public void lancerServeurRequest() {

        paquet.setIdUser(idMedecin);
        paquet.setService(15);
        socketManage.ConnexionSocket(null, context.getResources().openRawResource(R.raw.keystorepk));
        notifcations = new ArrayList<FromNotification>();
        notifcations = socketManage.ListNotification(paquet);
        nbNotificationTemp = notifcations.size();
        Log.e("Servuer Notification : ","Medecin id = "+idMedecin);
        Log.e("In Notif : ","nbnotif = "+nbNotification+" nbNotiftemp = "+nbNotificationTemp);

        if(nbNotificationTemp < nbNotification){
            if (nbNotificationTemp == 0){
                nbNotification = 0;
            }else {
                nbNotification --;
            }
        }

        if (nbNotificationTemp > nbNotification) {

            mIntent = new Intent(this, HomePageActvity.class);

            alert = new NotificationCompat.Builder(context, CHANNEL_ID_Alert)
                    .setContentTitle(Titre_Alerte)
                    .setContentText("Vous Avez  " + nbNotificationTemp + " Notification")
                    .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
                    .setColor(getColor(R.color.red))
                    .setAutoCancel(true)

                    .build();
            mNotificationManager.notify(2, alert);
            nbNotification = nbNotificationTemp;

            mIntent.putExtra("Medecin", medeicinInfo);
            mIntent.putExtra("notiftrue",1);
            mIntent.putExtra("notif",notifcations);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);

        }


        handler.postDelayed(runnable, 60000);
    }

    public Serveur_Service() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = new Intent(this, HomePageActvity.class);
        Log.e("Servuer Service : ","On Start Command");

        if(!session.isLoggedIn() ) {
            idMedecin = (Long) intent.getExtras().get("idMedecin");
            Log.e("Servuer Service : ","Medecin id = "+idMedecin);

            paquet.setIdUser(idMedecin);
            paquet.setService(7);
            socketManage.ConnexionSocket(null, context.getResources().openRawResource(R.raw.keystorepk));
            medeicinInfo = socketManage.getMedecin(paquet);
            medeicinInfo.setIdMedecin(idMedecin);
            mIntent.putExtra("notiftrue",1);
            mIntent.putExtra("Medecin", medeicinInfo);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            runnable.run();
            startActivity(mIntent);
        }
        else {
            Log.e("Session"," Cree deja");
            Log.e("Servuer Service : ","Medecin id = "+idMedecin);
            mIntent.putExtra("notiftrue",1);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);

        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Service !","onCreate");
        session = new session(getApplicationContext());
        context  = this;
        nbNotification = 0;

        homePageIntent = new Intent(this, HomePageActvity.class);
        pendingIntent = PendingIntent.getActivity(this,
                0, homePageIntent, 0);



        notification = new NotificationCompat.Builder(this, CHANNEL_ID_Service)
                .setContentTitle(Titre_notification)
                .setContentText(Descreption_notification)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nbNotification = 0;
        nbNotificationTemp = 0;
        context = null;
        session.logoutUser();
    }


}

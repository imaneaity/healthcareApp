package com.example.myapplication.PatientActivities;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.example.myapplication.CallPhoneActivity;
import com.example.myapplication.FormCapteur;
import com.example.myapplication.FormSuivitPatient;
import com.example.myapplication.FormulairePatient;
import com.example.myapplication.MiBand3.ActionCallback;
import com.example.myapplication.MiBand3.HeartRateNotifyListener;
import com.example.myapplication.MiBand3.MiBand;
import com.example.myapplication.MiBand3.NotifyListener;
import com.example.myapplication.R;
import com.example.myapplication.ObjectPaquet;
import com.example.myapplication.SSl_Socket.SocketManage;
import com.example.myapplication.session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.myapplication.NotificationService.CHANNEL_ID_Alert;
import static com.example.myapplication.NotificationService.CHANNEL_ID_Service;


public class patientHomeService extends Service {

    private FormulairePatient patient;
    private Intent mIntent;
    private Intent mIntentCall;
    private com.example.myapplication.session session;
    private final static String Titre_notification = "Iot E_Sante";
    private final static String Titre_Alerte = "Alert !!!!!!!!!!!!";
    private final static String Descreption_notification = "L'application Capte Les Données";
    private MiBand miband;
    private Notification notification;
    private Notification alert;
    private NotificationManager mNotificationManager;
    private PendingIntent pendingIntent;
    private Intent notificationIntent;
    private Context context;
    private SocketManage socketManage = new SocketManage();
    private ObjectPaquet paquet = new ObjectPaquet();
    private FormSuivitPatient suivit = new FormSuivitPatient();
    private BluetoothDevice device;
    private int nbScanCretique;
    private ArrayList<FormCapteur> capteurs = new ArrayList<FormCapteur>();
    private Date lastDateData;
    private int heartRateAsyn;
    private boolean first = true;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            if (patient != null) {
                Log.e("Run Heart rate scan", "sTART");
                lanceHeartRateScane();
               /* if(first){
                    runnable2.run();
                    first = false;
                }*/

            } else {
                Log.e("Run Heart rate scan", "Stop patient  = null");

            }
        }
    };

    Handler handler2 = new Handler();
    Runnable runnable2 = new Runnable() {
        public void run() {
            if (patient != null) {
                Log.e("AsynchTask", "START");
                AsyncTask();

            }
        }
    };

    public void lanceHeartRateScane() {

        Log.e("Heart rate Service", "Start  ");


        miband.startHeartRateScan();

        handler.postDelayed(runnable, 60000);

    }

    public void AsyncTask() {



        new MyAsyncTask(heartRateAsyn,socketManage,paquet).execute();


       // handler2.postDelayed(runnable2, 70000);

    }
    public patientHomeService() {
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = new Intent(this, PatientHomePage.class);
        mIntentCall = new Intent(this, CallPhoneActivity.class);


        if(!session.isLoggedIn() ) {
            miband = new MiBand(this);
            Log.e("Session"," Creation de la session");

            patient = (FormulairePatient) intent.getExtras().get("patient");
            device= (BluetoothDevice) intent.getExtras().get("device");
            capteurs= (ArrayList<FormCapteur>) intent.getExtras().get("listeCapteur");
            session.createLoginSession(patient.getIdPatient(),1);
            suivit.setIdPatient(patient.getIdPatient());

            miband.connect(device, new ActionCallback() {

                @Override
                public void onSuccess(Object data) {
                    Log.d("Success",
                            "Connexion réussie!!!");

                    miband.setDisconnectedListener(new NotifyListener() {
                        @Override
                        public void onNotify(byte[] data) {
                            Log.d("Set DEconnexion state",
                                    "Connexion déconnectée!!!");
                        }
                    });

                    miband.setHeartRateScanListener(new HeartRateNotifyListener() {
                        @Override
                        public void onNotify(int heartRate) {
                            Log.e("Heart rate Service", "heart rate: " + heartRate);
                            Log.e("Mi band"," : "+device.getAddress());
                            Log.e("Time"," : "+Calendar.getInstance().getTime());

                            notification = new NotificationCompat.Builder(context, CHANNEL_ID_Service)
                                    .setContentTitle(Titre_notification)
                                    .setContentText("Rythme cardiaque : "+ heartRate)
                                    .setSmallIcon(R.drawable.ic_check_black_24dp)
                                    .setContentIntent(pendingIntent)
                                    .build();
                            mNotificationManager.notify(1, notification);

                            if(heartRate <40 || heartRate> 180 ) {
                                nbScanCretique++;
                                alert = new NotificationCompat.Builder(context, CHANNEL_ID_Alert)
                                        .setContentTitle(Titre_Alerte)
                                        .setContentText("Patient En danger   : " + heartRate + " BPM")
                                        .setSmallIcon(R.drawable.ic_check_black_24dp)
                                        .setContentIntent(pendingIntent)
                                        .setColor(getColor(R.color.red))
                                        .build();
                                mNotificationManager.notify(2, alert);
                                runnable.run();
                                heartRate = 20;
                            }else {
                                nbScanCretique = 0;
                            }
                            suivit.setData(heartRate);
                            suivit.setIdCapteur(miband.getDevice().getAddress());
                            EnvoieData(suivit);

                            if(nbScanCretique>3) {
                                mIntentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mIntentCall);
                                nbScanCretique = 0;
                            }
                            heartRateAsyn = heartRate;

                        }
                    });

                    runnable.run();

                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.d("Fail", "connect fail, code:" + errorCode + ",mgs:" + msg);
                }

            });

            mIntent.putExtra("patient", patient);
            mIntent.putExtra("listeCapteur", capteurs);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
        }
        else {
            Log.e("Session"," Cree deja²");

            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);

        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        session = new session(getApplicationContext());
        context  = this;
        nbScanCretique = 0;
        lastDateData =  new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        notificationIntent = new Intent(this, PatientHomePage.class);
        pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID_Service)
                .setContentTitle(Titre_notification)
                .setContentText(Descreption_notification)
                .setSmallIcon(R.drawable.ic_check_black_24dp)
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
        miband = null;
        context = null;
        session.logoutUser();
    }

    public void EnvoieData(FormSuivitPatient suivit) {

        socketManage.ConnexionSocket(null,context.getResources().openRawResource(R.raw.keystorepk));

        Date dt = new Date();
        if(dt.after(lastDateData)) {


            suivit.setDate(dt);
            paquet.setFormSuivitPatient(suivit);
            paquet.setService(10);
            socketManage.chat(paquet);
            lastDateData = dt;
        }

    }


    class MyAsyncTask extends AsyncTask<Void, Integer, String> {
        /**     * Un compteur */
        int heartRateAsyn;
        private FormSuivitPatient suivitAsyn = new FormSuivitPatient();
        private SocketManage socketManageAsyn ;
        private ObjectPaquet paquetAsyn ;
        private Long idPatientAsyn = Long.parseLong("369852147214");
        private String MacAdd = "FE:0F:40:F6:24:23";

        public MyAsyncTask(int heartRate,SocketManage socketManageAsyn,ObjectPaquet paquetAsyn) {
            this.heartRateAsyn = heartRate;
            this.socketManageAsyn= socketManageAsyn;
            this.paquetAsyn =paquetAsyn ;

        }
        //Surcharge de la méthode doInBackground (Celle qui s'exécute dans une Thread à part)
        @Override
        protected String doInBackground(Void... unused) {
            Log.e("Heart rate Asynch", "doInBackground  ");

            socketManageAsyn.ConnexionSocket(null,context.getResources().openRawResource(R.raw.keystorepk));
            Date dt = new Date();

            if(heartRateAsyn < 20){
                heartRateAsyn = 80;
            }
            suivitAsyn.setIdPatient(idPatientAsyn);
            suivitAsyn.setData(heartRateAsyn);
            suivitAsyn.setIdCapteur(MacAdd);
            suivitAsyn.setDate(dt);
            paquetAsyn.setFormSuivitPatient(suivitAsyn);
            paquetAsyn.setService(10);
            socketManage.chat(paquetAsyn);

            return ("send donne ");
        }

        // Surcharge de la méthode onProgressUpdate (s'exécute dans la Thread de l'IHM)
        @Override
        protected void onProgressUpdate(Integer... diff) {

        }

        // Surcharge de la méthode onPostExecute (s'exécute dans la Thread de l'IHM)
        @Override
        protected void onPostExecute(String message) {

        }
    }

}

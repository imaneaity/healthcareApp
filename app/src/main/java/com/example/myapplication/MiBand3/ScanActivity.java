package com.example.myapplication.MiBand3;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.FormCapteur;
import com.example.myapplication.FormulairePatient;
import com.example.myapplication.ObjectPaquet;
import com.example.myapplication.R;
import com.example.myapplication.SSl_Socket.SocketManage;
import com.example.myapplication.PatientActivities.patientHomeService;

import java.util.ArrayList;
import java.util.HashMap;

public class ScanActivity extends Activity {

    private static final String TAG = "==[mibandtest]==";
    private MiBand miband;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    private HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();
    private ListView lv;

    private BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    private final static int REQUEST_ENABLE_BT = 1;
    private Context context;
    private int nbCapteur;



    private FormCapteur capteur = new FormCapteur();
    private long idPatient;
    private long idMedecin;
    private ObjectPaquet paquet = new ObjectPaquet();
    private SocketManage socketManage = new SocketManage();


    private ArrayList<FormCapteur> listCapteur = new ArrayList<>();
    private FormulairePatient patient;
    private Intent mIntent;

    private ComponentName componentName;

    private BluetoothDevice deviceSend;
    private int nbliste;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        initialissatioDesObjet_Variable();

        ActiveBt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }


        final ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.item, new ArrayList<String>());

        final ScanCallback scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    BluetoothDevice device = result.getDevice();
                    Log.d(TAG,
                            "Trouver des appareils Bluetooth à proximité\n: name:" + device.getName() + ",uuid:"
                                    + device.getUuids() + ",add:"
                                    + device.getAddress() + ",type:"
                                    + device.getType() + ",bondState:"
                                    + device.getBondState() + ",rssi:" + result.getRssi());

                    boolean find = false;
                    String item = device.getName() + "  " + device.getAddress()+"   "+result.getRssi();
                    if (!devices.containsKey(item)) {
                        if(componentName.getClassName().equals( "com.example.myapplication.MedecinActivities.AjoutPatienActivity")) {
                            for (int i = 0; i < listCapteur.size(); i++) {
                                if (device.getAddress().equals(listCapteur.get(i).getIdCapteur()))
                                    find = true;

                            }
                            if (!find) {
                                devices.put(item, device);
                                adapter.add(item);
                                find = false;
                            }
                        }else{
                            for (int i = 0; i < listCapteur.size(); i++) {
                                if (device.getAddress().equals(listCapteur.get(i).getIdCapteur())) {
                                    devices.put(item, device);
                                    adapter.add(item);
                                }
                            }

                        }
                    }
                }

        };


        (findViewById(R.id.ajoutCapteur)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Lancer la numérisation à proximité des appareils Bluetooth...");
                adapter.clear();

                MiBand.startScan(scanCallback);

            }
        });
        mIntent = new Intent(this, patientHomeService.class);

        ( findViewById(R.id.termineAssociation)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Arrêter la numérisation\n...");
                    MiBand.stopScan(scanCallback);
                    if(nbCapteur >= 1) {

                        if(componentName.getClassName().equals( "com.example.myapplication.MedecinActivities.AjoutPatienActivity")) {
                            alertPatientEnregister();
                        } else {
                            mIntent.putExtra("patient", patient);
                            mIntent.putExtra("listeCapteur", listCapteur);
                            mIntent.putExtra("device", miband.getDevice());
                            ScanActivity.this.startService(mIntent);
                            finish();

                        }
                    }
                    else
                        alertCapteurNonSaisir();

                }
        });

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();
                if (devices.containsKey(item)) {
                    Log.d(TAG, "Arrêter la numérisation...");
                    MiBand.stopScan(scanCallback);
                    TextView color = (TextView) view;
                    //color.setBackgroundColor(Color.rgb(255,0,0));
                    color.setBackgroundColor(getResources().getColor(R.color.circleBorder));
                    BluetoothDevice device = devices.get(item);

                    miband.connect(device, new ActionCallback() {

                        @Override
                        public void onSuccess(Object data) {
                            Log.d(TAG,
                                    "Connexion réussie!!!");
                            Toast toast = Toast.makeText(context, "Connexoin reussi au capteur", Toast.LENGTH_LONG);
                            toast.show();

                            miband.setDisconnectedListener(new NotifyListener() {
                                @Override
                                public void onNotify(byte[] data) {
                                    Log.d(TAG, "Connexion déconnectée!!!");
                                }
                            });

                            miband.pair(new ActionCallback() {
                                @Override
                                public void onSuccess(Object data) {
                                    Log.d(TAG,
                                            "pairing  réussie!!!");
                                }

                                @Override
                                public void onFail(int errorCode, String msg) {
                                    Log.d(TAG,
                                            "pairing faile!!!"+ errorCode + ",mgs:" + msg);
                                }
                            });
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
                        }
                    });


                    Log.e("compentName2",""+componentName.getClassName());


                    // Medecin Associer un capteur pour un patient
                    if(componentName.getClassName().equals( "com.example.myapplication.MedecinActivities.AjoutPatienActivity")) {
                        Log.e("compentName 3","if enter = "+componentName.getClassName());
                        miband.connect(device, new ActionCallback() {

                            @Override
                            public void onSuccess(Object data) {
                                Log.d(TAG,
                                        "Connexion réussie!!!");
                                Toast toast = Toast.makeText(context, "Connexoin reussi au capteur", Toast.LENGTH_LONG);
                                toast.show();

                                miband.setDisconnectedListener(new NotifyListener() {
                                    @Override
                                    public void onNotify(byte[] data) {
                                        Log.d(TAG, "Connexion déconnectée!!!");
                                    }
                                });

                                miband.pair(new ActionCallback() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        Log.d(TAG,
                                                "pairing  réussie!!!");
                                    }

                                    @Override
                                    public void onFail(int errorCode, String msg) {
                                        Log.d(TAG,
                                                "pairing faile!!!"+ errorCode + ",mgs:" + msg);
                                    }
                                });
                            }

                            @Override
                            public void onFail(int errorCode, String msg) {
                                Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
                            }
                        });

                        miband.startVibration(VibrationMode.VIBRATION_WITH_LED);

                        capteur.setIdCapteur(device.getAddress());
                        capteur.setFocntion("Hear Rate");
                        capteur.setIdPatient(idPatient);
                        capteur.setIdMedecin(idMedecin);
                        capteur.setEtat("Non");
                        Toast toast = Toast.makeText(context, "Captuer = "+device.getName(), Toast.LENGTH_LONG);
                        toast.show();

                        Log.e("capteur name",""+device.getName());

                        paquet.setService(3);
                        paquet.setFormCapteur(capteur);

                        try {
                            socketManage.ConnexionSocket(view, null);
                            if (!socketManage.chat(paquet)) {
                                alertCapteurExiste();
                            } else {
                                color.setBackgroundColor(getResources().getColor(R.color.green));
                                nbCapteur++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            alertConnexion();
                        }
                    }
                    else {
                        for (FormCapteur capteur:listCapteur) {

                            if(capteur.getIdCapteur().equals(device.getAddress())){
                                miband.connect(device, new ActionCallback() {

                                    @Override
                                    public void onSuccess(Object data) {
                                        Log.d(TAG,
                                                "Connexion réussie!!!");
                                        Toast toast = Toast.makeText(context, "Connexoin reussi au capteur", Toast.LENGTH_LONG);
                                        toast.show();

                                        miband.setDisconnectedListener(new NotifyListener() {
                                            @Override
                                            public void onNotify(byte[] data) {
                                                Log.d(TAG, "Connexion déconnectée!!!");
                                            }
                                        });

                                        miband.pair(new ActionCallback() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                Log.d(TAG,
                                                        "pairing  réussie!!!");
                                            }

                                            @Override
                                            public void onFail(int errorCode, String msg) {
                                                Log.d(TAG,
                                                        "pairing faile!!!"+ errorCode + ",mgs:" + msg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(int errorCode, String msg) {
                                        Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
                                    }
                                });
                                miband.startVibration(VibrationMode.VIBRATION_WITH_LED);

                                capteur.setEtat("Oui");
                                try {
                                    socketManage.ConnexionSocket(view, null);

                                    paquet.setService(9);
                                    paquet.setFormCapteur(capteur);

                                    if (socketManage.update(paquet)) {
                                        nbCapteur++;
                                        CapteurConnecté();
                                        color.setBackgroundColor(getResources().getColor(R.color.green));
                                        //UserInfo userInfo = new UserInfo(20271234, 1, patient.getAge(), patient.getDocs().getTaille(), patient.getDocs().getPoid(), patient.getNom()+" "+patient.getPrenom(), 0);
                                        // miband.setUserInfo(userInfo);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    alertConnexion();
                                }
                            }
                        }
                    }
                }
            }
        });

    }


    private void initialissatioDesObjet_Variable(){

        componentName = this.getCallingActivity();
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        context = this;
        miband = new MiBand(this);
        nbCapteur= 0;
        nbliste=0;
        lv= (ListView) findViewById(R.id.lvNewDevices);

        if(componentName.getClassName().equals( "com.example.myapplication.MedecinActivities.AjoutPatienActivity")) {
            idPatient = (long)getIntent().getSerializableExtra("idPatient");
            idMedecin = (long)getIntent().getSerializableExtra("idMedecin");
            Log.e(TAG, "taille list capteur medecin 1 = "+listCapteur.size());
            Log.e(TAG, "id Patient = "+idPatient);

            socketManage.ConnexionSocket(null,context.getResources().openRawResource(R.raw.keystorepk));
            paquet.setService(13);
            listCapteur = socketManage.capteurs(paquet);
            Log.e(TAG, "taille list capteur medecin 2= "+listCapteur.size());

        }
        else {
            patient = (FormulairePatient) getIntent().getSerializableExtra("patient");
            idPatient = patient.getIdPatient();
            Log.e(TAG, "taille list capteur patient = "+listCapteur.size());
            Log.e(TAG, "id Patient = "+idPatient);

            socketManage.ConnexionSocket(null,context.getResources().openRawResource(R.raw.keystorepk));
            paquet.setService(6);
            paquet.setIdUser(idPatient);
            listCapteur = socketManage.capteurs(paquet);
            Log.e(TAG, "taille list capteur patient= "+listCapteur.size());

        }



    }


    public void ActiveBt() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Se Mobile ne contient pas de bluetooth ");
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }
    }

    private void alertCapteurExiste() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Alerte Capteur Existe");

        // set dialog message
        alertDialogBuilder
                .setMessage("Ce Capteur est deja associer a un autre patient")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_OK, null);

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    private void alertCapteurNonSaisir() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Alerte Capteur Non choisir");

        // set dialog message
        alertDialogBuilder
                .setMessage("Veuiller s'il vous plait choisir au moin un capteur Merci !!")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_OK, null);

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void alertPatientEnregister() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Le Patient a été enregistré");

        // set dialog message
        alertDialogBuilder
                .setMessage("Votre Patient a été enregistré et associé à son  capteur!!")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_OK, null);
                        finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    private void CapteurConnecté() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Capteur Est Connecté");

        // set dialog message
        alertDialogBuilder
                .setMessage("Votre Capteur Est Bien Conncté")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_OK, null);

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            alertEroor();
        }
        return false;
    }

    private void alertEroor() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Attention");

        // set dialog message
        alertDialogBuilder
                .setMessage("Merci de choisir un capteur pour votre patient")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}

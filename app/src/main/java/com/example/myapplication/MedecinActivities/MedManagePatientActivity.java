package com.example.myapplication.MedecinActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.example.myapplication.AdapterCapteur;
import com.example.myapplication.AdapterDocumments;
import com.example.myapplication.AdapterInfoMed;
import com.example.myapplication.AdapterSuivitData;
import com.example.myapplication.Documents;
import com.example.myapplication.FormCapteur;
import com.example.myapplication.FormMoreInfo;
import com.example.myapplication.FormSuivitPatient;
import com.example.myapplication.FormulairePatient;
import com.example.myapplication.R;
import com.example.myapplication.ObjectPaquet;
import com.example.myapplication.SSl_Socket.SocketManage;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MedManagePatientActivity  extends AppCompatActivity   {

    private FormulairePatient patient;
    private CardView infoPersoPat;
    private CardView infoPersoMed;
    private CardView Documents;
    private CardView capteurCard;
    private CardView dateDebut;
    private CardView dateFin;
    private Context context;

    private TextView dateDebutText;
    private TextView dateFinText;
    private Calendar dateDebutPiker,dateFinPiker;




    private ArrayList<Documents> docs = new ArrayList<Documents>();
    private Documents document;
    private RecyclerView mRycycleViewDoc;
    private AdapterDocumments recylAdapterDoc;
    private RecyclerView.LayoutManager mLayoutMangerDoc;


    private RecyclerView mRycycleViewCapteur;
    private AdapterCapteur recylAdapterCapteur;
    private RecyclerView.LayoutManager mLayoutMangerCaptuer;
    private ArrayList<FormCapteur> capteurs = new ArrayList<FormCapteur>();
    private FormCapteur capteur;




    private SocketManage socketManage = new SocketManage();
    private ObjectPaquet paquet = new ObjectPaquet();

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetDialog mBottomSheetDialogInfo;
    private BottomSheetDialog mBottomSheetDialogInfoMed;
    private BottomSheetDialog mBottomSheetDialogDocs;
    private BottomSheetDialog mBottomSheetDialogSuivit;
    private BottomSheetDialog mBottomSheetDialoginfoDocs;
    private BottomSheetDialog mBottomSheetDialoginfoGraphe;

    private RecyclerView mRycycleViewSuivit;
    private AdapterSuivitData recylAdapterSuivit;
    private RecyclerView.LayoutManager mLayoutMangerSuivit;
    private  ArrayList<FormSuivitPatient> suivits;

    private RecyclerView mRycycleViewInfoMed;
    private RecyclerView.LayoutManager mLayoutMangerInfoMed;

    private AdapterInfoMed recylAdapterinfoMed;
    private LineChart mChart;
    private boolean error;
    private Calendar mCalendar;
    private Spinner typeDocs;

    private long tempsDebut, tempsFin;
    private double seconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_manage);
        patient = (FormulairePatient)getIntent().getSerializableExtra("patient");

        initVariable();


        infoPersoPat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView nomPop = mBottomSheetDialogInfo.findViewById(R.id.NomPatPop);
                nomPop.setText( " Nom : "+patient.getNom());

                TextView prenomPop = mBottomSheetDialogInfo.findViewById(R.id.PrenomPatPop);
                prenomPop.setText(" Prenom : "+patient.getPrenom());

                TextView dateNaissPop = mBottomSheetDialogInfo.findViewById(R.id.DAtNaissPatPop);
                dateNaissPop.setText(" Date de naissance : "+patient.getDateNaissance());

                TextView lieuxNaisPop = mBottomSheetDialogInfo.findViewById(R.id.LieuxNaissPatPop);
                lieuxNaisPop.setText(" lieu de naissance: "+patient.getWilaya()+"-"+patient.getVille());

                TextView numTelPop = mBottomSheetDialogInfo.findViewById(R.id.NumTelPatPop);
                numTelPop.setText(" Numero de Telephone : 0"+patient.getNumTel());

                mBottomSheetDialogInfo.show();
            }
        });


        Documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    tempsDebut = System.currentTimeMillis();

                    socketManage.ConnexionSocket(v,null);
                    paquet.setService(5);
                    paquet.setIdUser(patient.getIdPatient());

                    docs = socketManage.documents(paquet);
                    tempsFin = System.currentTimeMillis();
                    seconds = (tempsFin - tempsDebut) / 1000F;

                    Toast message = Toast.makeText(context,"Time = "+seconds,Toast.LENGTH_LONG);
                    message.show();


                    if(docs.size()>0) {

                        recylAdapterDoc = new AdapterDocumments(docs);
                        mRycycleViewDoc.setLayoutManager(mLayoutMangerDoc);
                        mRycycleViewDoc.setAdapter(recylAdapterDoc);
                        recylAdapterDoc.setOnItemClickListener(new AdapterDocumments.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                tempsDebut = System.currentTimeMillis();

                                socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));

                                paquet.setIdUser(patient.getIdPatient());
                                paquet.setIdDocs(docs.get(position).getIdDoccs());
                                paquet.setService(24);
                                File fichier = socketManage.getFile(paquet);
                                AssetManager assetManager = getAssets();
                                tempsFin = System.currentTimeMillis();
                                seconds = (tempsFin - tempsDebut) / 1000F;

                                Toast message = Toast.makeText(context,"Time = "+seconds,Toast.LENGTH_LONG);
                                message.show();

                                InputStream in = null;
                                OutputStream out = null;
                                File file = new File(getFilesDir(), "fichier.pdf");
                                try
                                {
                                    in = assetManager.open("fichier.pdf");
                                    out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

                                    copyFile(in, out);
                                    in.close();
                                    in = null;
                                    out.flush();
                                    out.close();
                                    out = null;
                                } catch (Exception e)
                                {
                                    Log.e("tag", e.getMessage());
                                }

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(
                                        Uri.parse("file://" + getFilesDir() + "/fichier.pdf"),
                                        "application/pdf");

                                startActivity(intent);

                            }
                        });

                        mBottomSheetDialogDocs.show();
                    }else {
                        alertDataDocs();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    alertConnexion();
                }
            }
        });

        capteurCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tempsDebut = System.currentTimeMillis();

                    socketManage.ConnexionSocket(v, null);
                    paquet.setService(6);
                    paquet.setIdUser(patient.getIdPatient());

                    capteurs = socketManage.capteurs(paquet);


                    tempsFin = System.currentTimeMillis();
                    seconds = (tempsFin - tempsDebut) / 1000F;

                    Toast message = Toast.makeText(context,"Time = "+seconds,Toast.LENGTH_LONG);
                    message.show();

                    recylAdapterCapteur = new AdapterCapteur(capteurs);
                    mRycycleViewCapteur.setLayoutManager(mLayoutMangerCaptuer);
                    mRycycleViewCapteur.setAdapter(recylAdapterCapteur);
                    recylAdapterCapteur.setOnItemClickListener(new AdapterCapteur.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            capteur = capteurs.get(position);
                            try {
                                tempsDebut = System.currentTimeMillis();

                                socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));
                                paquet.setService(14);
                                paquet.setFormCapteur(capteur);

                                suivits = new ArrayList<FormSuivitPatient>();
                                suivits = socketManage.getSuivit(paquet);
                                tempsFin = System.currentTimeMillis();
                                seconds = (tempsFin - tempsDebut) / 1000F;

                                Toast message = Toast.makeText(context,"Time = "+seconds,Toast.LENGTH_LONG);
                                message.show();
                                if(suivits.size()> 0) {
                                    recylAdapterSuivit = new AdapterSuivitData(suivits);
                                    mRycycleViewSuivit.setLayoutManager(mLayoutMangerSuivit);
                                    mRycycleViewSuivit.setAdapter(recylAdapterSuivit);
                                    recylAdapterSuivit.setOnItemClickListener(new AdapterSuivitData.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            if (suivits.size() > 0) {
                                                mChart = mBottomSheetDialoginfoGraphe.findViewById(R.id.lineChart);

                                                lineChartConfig(suivits);
                                                mBottomSheetDialoginfoGraphe.show();
                                            } else {
                                                alertData();
                                            }
                                        }
                                    });

                                    mBottomSheetDialogSuivit.show();

                                }else
                                {
                                    alertData();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                alertConnexion();
                            }
                        }

                        @Override
                        public void onDeleteClick(int position) {
                            FormCapteur capteur = capteurs.get(position);

                            alertDelete(capteur);

                        }
                    });
                    if (capteurs.size() == 0) {
                        alertCapteur();
                    } else {
                        mBottomSheetDialog.show();

                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    alertConnexion();
                }
            }
        });

        infoPersoMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    socketManage.ConnexionSocket(v,null);
                    paquet.setService(5);
                    paquet.setIdUser(patient.getIdPatient());

                    docs = socketManage.documents(paquet);
                    Log.e("taille liste docs = ",""+docs.size());
                    if(docs.size()>0) {
                        recylAdapterinfoMed = new AdapterInfoMed(docs);
                        mRycycleViewInfoMed.setLayoutManager(mLayoutMangerInfoMed);
                        mRycycleViewInfoMed.setAdapter(recylAdapterinfoMed);
                        recylAdapterinfoMed.setOnItemClickListener(new AdapterInfoMed.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                document = docs.get(position);
                                TextView medecin = mBottomSheetDialoginfoDocs.findViewById(R.id.medcinDocPAts);
                                medecin.setText(" Medecin : " + document.getIdMedecin());

                                TextView date = mBottomSheetDialoginfoDocs.findViewById(R.id.dateDocPats);
                                date.setText(" Date : " + document.getDate());

                                TextView taille = mBottomSheetDialoginfoDocs.findViewById(R.id.taillePatDocs);
                                taille.setText(" taille : " + document.getTaille()+" cm");

                                TextView poid = mBottomSheetDialoginfoDocs.findViewById(R.id.poidPatDocs);
                                poid.setText(" poid : " + document.getPoid()+" Kg");

                                TextView heartRate = mBottomSheetDialoginfoDocs.findViewById(R.id.heartRatePatDocs);
                                heartRate.setText("Rythme Cadriaque : " + document.getHeartRate() +" BPM ");

                                TextView tension = mBottomSheetDialoginfoDocs.findViewById(R.id.tensionPatDocs);
                                tension.setText(" tension: " + document.getPresur());

                                TextView commente = mBottomSheetDialoginfoDocs.findViewById(R.id.commentPatDocs);
                                commente.setText(" commentaire : " + document.getComment());

                                mBottomSheetDialoginfoDocs.show();
                            }
                        });


                        mBottomSheetDialogInfoMed.show();
                    }else {
                        alertDataDocs();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    alertConnexion();
                }
            }
        });




    }
    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }


    private void initVariable() {


        infoPersoPat = findViewById(R.id.infoPersoPat);
        infoPersoMed = findViewById(R.id.infoMedPat);
        Documents = findViewById(R.id.Documents);
        capteurCard = findViewById(R.id.CapteurPatient);
        context = this;
        mCalendar = Calendar.getInstance();



        View bottomSheet = findViewById(R.id.framelayout_bottom_sheet);

        //------------- Capteur bottom

        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mRycycleViewCapteur = mBottomSheetDialog.findViewById(R.id.listCapteurPatients);
        mRycycleViewCapteur.setHasFixedSize(true);
        mLayoutMangerCaptuer = new LinearLayoutManager(this);

        //----------iinfo bottom
        final View bottomSheetLayoutinfo = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_info, null);
        (bottomSheetLayoutinfo.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialogInfo.dismiss();
            }
        });

        mBottomSheetDialogInfo = new BottomSheetDialog(this);
        mBottomSheetDialogInfo.setContentView(bottomSheetLayoutinfo);

        //----------docs bottom
        final View bottomSheetLayoutdocs = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_docs, null);
        (bottomSheetLayoutdocs.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialogDocs.dismiss();
            }
        });
        (bottomSheetLayoutdocs.findViewById(R.id.plusDocs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusDocsAlert();
            }
        });

        mBottomSheetDialogDocs = new BottomSheetDialog(this);
        mBottomSheetDialogDocs.setContentView(bottomSheetLayoutdocs);

        mRycycleViewDoc = mBottomSheetDialogDocs.findViewById(R.id.listDocsPatients);
        mRycycleViewDoc.setHasFixedSize(true);
        mLayoutMangerDoc = new LinearLayoutManager(this);


        //----------suivitdata bottom
        final View bottomSheetLayoutsuivit = getLayoutInflater().inflate(R.layout.bottom_sheet_suivit, null);
        (bottomSheetLayoutsuivit.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialogSuivit.dismiss();
            }
        });
        (bottomSheetLayoutsuivit.findViewById(R.id.plusData)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeData = capteur.getFocntion();
                Log.e("Fonction Plus ",typeData);
                plusAlert();
            }
        });
        (bottomSheetLayoutsuivit.findViewById(R.id.button_graphe)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suivits.size()> 0) {
                    mChart = mBottomSheetDialoginfoGraphe.findViewById(R.id.lineChart);

                    lineChartConfig(suivits);
                    mBottomSheetDialoginfoGraphe.show();
                }else{
                    alertData();
                }

            }
        });
        (bottomSheetLayoutsuivit.findViewById(R.id.actualisersuivit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    socketManage.ConnexionSocket(view, null);
                    paquet.setService(14);
                    paquet.setFormCapteur(capteur);

                    suivits = new ArrayList<FormSuivitPatient>();
                    suivits = socketManage.getSuivit(paquet);

                    if(suivits.size()> 0) {
                        recylAdapterSuivit = new AdapterSuivitData(suivits);
                        mRycycleViewSuivit.setLayoutManager(mLayoutMangerSuivit);
                        mRycycleViewSuivit.setAdapter(recylAdapterSuivit);
                        recylAdapterSuivit.setOnItemClickListener(new AdapterSuivitData.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                if(suivits.size()> 2) {
                                    mChart = mBottomSheetDialoginfoGraphe.findViewById(R.id.lineChart);

                                    lineChartConfig(suivits);
                                    mBottomSheetDialoginfoGraphe.show();
                                } else {
                                    alertData();
                                }
                            }
                        });
                        mBottomSheetDialogSuivit.show();

                    }else
                    {
                        alertData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    alertConnexion();
                }
            }
        });

        mBottomSheetDialogSuivit = new BottomSheetDialog(this);
        mBottomSheetDialogSuivit.setContentView(bottomSheetLayoutsuivit);


        mRycycleViewSuivit = mBottomSheetDialogSuivit.findViewById(R.id.lisSuivitPat);
        mRycycleViewSuivit.setHasFixedSize(true);
        mLayoutMangerSuivit= new LinearLayoutManager(this);


        //----------docinfo bottom
        final View bottomSheetLayoutinfoDocs = getLayoutInflater().inflate(R.layout.bottom_sheet_ifodocs, null);
        (bottomSheetLayoutinfoDocs.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialoginfoDocs.dismiss();
            }
        });

        mBottomSheetDialoginfoDocs = new BottomSheetDialog(this);
        mBottomSheetDialoginfoDocs.setContentView(bottomSheetLayoutinfoDocs);

        //----------graphe bottom
        final View bottomSheetLayoutinfoGraphe = getLayoutInflater().inflate(R.layout.bottom_sheet_graphe, null);
        (bottomSheetLayoutinfoGraphe.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialoginfoGraphe.dismiss();
            }
        });
        (bottomSheetLayoutinfoGraphe.findViewById(R.id.actualisergraphe)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socketManage.ConnexionSocket(v, null);
                    paquet.setService(14);
                    paquet.setFormCapteur(capteur);

                    suivits = new ArrayList<FormSuivitPatient>();
                    suivits = socketManage.getSuivit(paquet);

                    if(suivits.size()> 0) {
                        recylAdapterSuivit = new AdapterSuivitData(suivits);
                        mRycycleViewSuivit.setLayoutManager(mLayoutMangerSuivit);
                        mRycycleViewSuivit.setAdapter(recylAdapterSuivit);
                        recylAdapterSuivit.setOnItemClickListener(new AdapterSuivitData.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                if(suivits.size()> 2) {
                                    mChart = mBottomSheetDialoginfoGraphe.findViewById(R.id.lineChart);

                                    lineChartConfig(suivits);
                                    mBottomSheetDialoginfoGraphe.show();
                                } else {
                                    alertData();
                                }
                            }
                        });
                        if(suivits.size()> 2) {
                            mChart = mBottomSheetDialoginfoGraphe.findViewById(R.id.lineChart);

                            lineChartConfig(suivits);
                            mBottomSheetDialoginfoGraphe.show();
                        }else{
                            alertData();
                        }

                    }else
                    {
                        alertData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    alertConnexion();
                }


            }
        });

        mBottomSheetDialoginfoGraphe = new BottomSheetDialog(this);
        mBottomSheetDialoginfoGraphe.setContentView(bottomSheetLayoutinfoGraphe);

        //----------info med
        final View bottomSheetLayoutinfoMed = getLayoutInflater().inflate(R.layout.bottom_sheet_listmed, null);
        (bottomSheetLayoutinfoMed.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialogInfoMed.dismiss();
            }
        });

        mBottomSheetDialogInfoMed = new BottomSheetDialog(this);
        mBottomSheetDialogInfoMed.setContentView(bottomSheetLayoutinfoMed);

        mRycycleViewInfoMed = mBottomSheetDialogInfoMed.findViewById(R.id.lisInfoMed);
        mRycycleViewInfoMed.setHasFixedSize(true);
        mLayoutMangerInfoMed = new LinearLayoutManager(this);

    }


    private void lineChartConfig(ArrayList<FormSuivitPatient> suivits) {


        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry>  yValues= new ArrayList<>();
        YAxis AxesY = mChart.getAxisLeft();
        String[] Xvalues = new String[100];
        LimitLine topLimite = new LimitLine(180,"");
        LimitLine botLimite = new LimitLine(30,"");

        topLimite.setLineWidth(5f);
        topLimite.setLineColor(Color.RED);
        topLimite.enableDashedLine(10f,10f,0f);
        topLimite.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        topLimite.setTextSize(15f);
        topLimite.setTextColor(Color.RED);
        topLimite.setLabel("Danger");



        botLimite.setLineWidth(5f);
        botLimite.setLineColor(Color.RED);
        botLimite.enableDashedLine(10f,10f,0f);
        botLimite.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        botLimite.setTextSize(15f);
        botLimite
                .setTextColor(Color.RED);
        botLimite.setLabel("Danger");

        AxesY.removeAllLimitLines();
        AxesY.addLimitLine(topLimite);
        AxesY.addLimitLine(botLimite);
        AxesY.setAxisMaximum(250);
        AxesY.enableGridDashedLine(10f,10f,0f);
        AxesY.setDrawLimitLinesBehindData(true);

        Legend legend = mChart.getLegend();
        legend.setTextSize(18f);
        legend.setForm(Legend.LegendForm.CIRCLE);

       for(int i = 0; i < suivits.size(); i++){
            Log.e("data "," = "+suivits.get(i).getData());
                yValues.add(new Entry(i,suivits.get(i).getData()));

        }



        LineDataSet setOne = new LineDataSet(yValues,"Rythme Cardiaque");
        setOne.setColor(Color.BLUE);
        setOne.setCircleColor(Color.YELLOW);
        setOne.setLineWidth(3f);
        setOne.setValueTextSize(15f);
        setOne.setValueTextColor(Color.RED);
        setOne.setDrawValues(false);



        setOne.setFillAlpha(200);
        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        dataSet.add(setOne);

        LineData datas = new LineData(dataSet);

        mChart.getXAxis().setLabelCount(yValues.size());
        mChart.getXAxis().setDrawLabels(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.setData(datas);
        mChart.invalidate();


    }


    private void alertCapteur() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Aucun capteur !!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Ce patient n'as aucun capteur Associer")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                })
                .setNegativeButton("Recherche", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        plusAlert();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    private void alertData() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Données de suivi Vide");

        // set dialog message
        alertDialogBuilder
                .setMessage("Pas de données de suivi pour ce capteur ......Merci !!!   ")
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }
    private void alertDataDocs() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Vide ..........!!!!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Pas de documents pour ce patient ......Merci !!!   ")
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    private void alertDelete(FormCapteur capteur) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));
        paquet.setService(19);
        paquet.setFormCapteur(capteur);
        alertDialogBuilder.setTitle("Attention");

        // set dialog message
        alertDialogBuilder
                .setMessage("Voulez Vous vraiment dissolution de ce capteur ........!!!   ")
                .setCancelable(false)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(!socketManage.chat(paquet)){
                            alertConnexion();
                        }
                        paquet.setService(6);
                        paquet.setIdUser(patient.getIdPatient());

                        capteurs = socketManage.capteurs(paquet);

                        if(capteurs.size()>0) {
                            recylAdapterCapteur = new AdapterCapteur(capteurs);
                            mRycycleViewCapteur.setLayoutManager(mLayoutMangerCaptuer);
                            mRycycleViewCapteur.setAdapter(recylAdapterCapteur);
                        }else {
                            alertCapteur();
                        }
                    }
                })
                .setNegativeButton("Non ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    private void plusAlert() {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.plus_data_dialog, null);
        final Date currentDate = new Date();


        dateDebut = (CardView)dialogView.findViewById(R.id.dateDebut);
        dateFin = (CardView)dialogView.findViewById(R.id.dateFin);
        dateDebutText = dialogView.findViewById(R.id.dateDebutText);
        dateFinText = dialogView.findViewById(R.id.dateFinText);
        Button valider = dialogView.findViewById(R.id.button_ok);
        error = false;

        dateDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment.OnDateSetListener dateSetListener = new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        // Set date from user input.
                        dateDebutPiker = Calendar.getInstance();
                        dateDebutPiker.set(Calendar.HOUR_OF_DAY, 0);
                        dateDebutPiker.set(Calendar.MINUTE, 0);
                        dateDebutPiker.set(Calendar.SECOND,0);
                        dateDebutPiker.set(Calendar.YEAR, year);
                        dateDebutPiker.set(Calendar.MONTH, monthOfYear);
                        dateDebutPiker.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if(currentDate.after(dateDebutPiker.getTime())) {
                            if(dateFinPiker != null) {
                                Log.e("Date ", "" + dateDebutPiker.getTime());
                                SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentTime = sdf.format(dateDebutPiker.getTime());

                                dateDebutText.setText(currentTime);
                            }else {
                                if (dateFinPiker != null) {
                                    if (dateFinPiker.getTime().before(dateDebutPiker.getTime())) {
                                        Log.e("Date ", "" + dateDebutPiker.getTime());
                                        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String currentTime = sdf.format(dateDebutPiker.getTime());

                                        dateDebutText.setText(currentTime);
                                    } else {
                                        error = true;
                                        alertDate();
                                    }
                                }else {
                                    Log.e("Date ", "" + dateDebutPiker.getTime());
                                    SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String currentTime = sdf.format(dateDebutPiker.getTime());
                                    Log.e("Date ", "" + currentTime);

                                    dateDebutText.setText(currentTime);

                                }
                            }
                        }else {
                            error = true;
                            alertDate();
                        }
                        // Do as you please with the date.
                    }
                };

                // Show date picker dialog.
                CalendarDatePickerDialogFragment dialog = new CalendarDatePickerDialogFragment();
                dialog.setOnDateSetListener(dateSetListener);
                dialog.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
            }
        });

        dateFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment.OnDateSetListener dateSetListener = new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        // Set date from user input.
                        dateFinPiker =Calendar.getInstance();
                        dateFinPiker.set(Calendar.HOUR_OF_DAY, 23);
                        dateFinPiker.set(Calendar.MINUTE, 59);
                        dateFinPiker.set(Calendar.SECOND,0);

                        dateFinPiker.set(Calendar.YEAR, year);
                        dateFinPiker.set(Calendar.MONTH, monthOfYear);
                        dateFinPiker.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if(dateFinPiker.getTime().after(dateDebutPiker.getTime()) || dateFinPiker.getTime().equals(dateDebutPiker.getTime())) {
                            Log.e("Date ", " Debut" + dateDebutPiker.getTime()+"  Date fin"+dateFinPiker.getTime()+" curenteDAte "+currentDate);
                            if(currentDate.after(dateDebutPiker.getTime())) {
                            Log.e("Date ", "" + dateFinPiker.getTime());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = sdf.format(dateFinPiker.getTime());

                            dateFinText.setText(currentTime);
                            }else {
                                error = true;
                                alertDate();
                            }
                        }else {
                            error = true;
                            alertDate();
                        }
                    }
                };

                // Show date picker dialog.
                CalendarDatePickerDialogFragment dialog = new CalendarDatePickerDialogFragment();
                dialog.setOnDateSetListener(dateSetListener);
                dialog.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
            }
        });
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!error && dateFinPiker != null && dateDebutPiker != null ){
                    socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));
                    paquet.setService(22);
                    FormMoreInfo plus = new FormMoreInfo();
                    plus.setDebut(dateDebutPiker.getTime());
                    plus.setFin(dateFinPiker.getTime());
                    plus.setIdPatient(patient.getIdPatient());
                    plus.setTypeData(capteur.getFocntion());
                    paquet.setPlusInfo(plus);

                    ArrayList<FormSuivitPatient> list = socketManage.getSuivit(paquet);


                    if(list != null) {
                        if (list.size() > 2) {
                            mChart = mBottomSheetDialoginfoGraphe.findViewById(R.id.lineChart);

                            lineChartConfig(list);
                            mBottomSheetDialoginfoGraphe.show();
                            dialogBuilder.dismiss();
                        } else {
                            alertPlusInfo();
                        }
                    }
                    else{
                        alertPlusInfo();
                    }


                }
            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void alertDate() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Date Erronée");

        // set dialog message
        alertDialogBuilder
                .setMessage("Merci de saisir une Date Valide ........!!!   ")
                .setCancelable(false)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    private void alertPlusInfo() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Pas De Données HR");

        // set dialog message
        alertDialogBuilder
                .setMessage("Pas De Donnée Pour cette periode  ........!!!   ")
                .setCancelable(false)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    private void plusDocsAlert() {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.plus_docs_dialog, null);

        typeDocs = dialogView.findViewById(R.id.spinnerDocsType);
        Button valider = dialogView.findViewById(R.id.button_ok);


        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type  = typeDocs.getSelectedItem().toString();
                socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));
                paquet.setService(23);
                paquet.setTypeDocs(type);
                paquet.setIdUser(patient.getIdPatient());
                ArrayList<Documents> list = socketManage.documents(paquet);

                if (list.size()> 0){

                    mBottomSheetDialogDocs.dismiss();

                    recylAdapterDoc = new AdapterDocumments(list);
                    mRycycleViewDoc.setLayoutManager(mLayoutMangerDoc);
                    mRycycleViewDoc.setAdapter(recylAdapterDoc);

                    mBottomSheetDialogDocs.show();
                    dialogBuilder.dismiss();
                }
                else{
                    alertPlusDocs(type);
                }


            }
        });





        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void alertPlusDocs(String type) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Pas De Documents");

        // set dialog message
        alertDialogBuilder
                .setMessage("Ce patient n'a pas de Document de Type"+type+" ........!!!   ")
                .setCancelable(false)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

}




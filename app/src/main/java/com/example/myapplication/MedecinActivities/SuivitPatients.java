package com.example.myapplication.MedecinActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import androidx.appcompat.widget.SearchView;

import com.example.myapplication.FormulairePatient;
import com.example.myapplication.ObjectPaquet;
import com.example.myapplication.PatientRecylAdapter;
import com.example.myapplication.R;
import com.example.myapplication.SSl_Socket.SocketManage;
import com.example.myapplication.ViewDialog;


import java.util.ArrayList;


public class SuivitPatients extends AppCompatActivity implements  AdapterView.OnItemClickListener{

    private RecyclerView mRycycleView;
    private PatientRecylAdapter recylAdapter;
    private RecyclerView.LayoutManager mLayoutManger;

//--------------------------------------------------
    private long IdMedecin;
    private ArrayList<FormulairePatient> patients = new ArrayList<FormulairePatient>();
    private Context context;
    private FormulairePatient patient;

    final SocketManage socketManage = new SocketManage();
    final ObjectPaquet paquet = new ObjectPaquet();
    private     ViewDialog viewDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suivit_activity);
        IdMedecin = (long)getIntent().getSerializableExtra("idMedecin");
        patients = (ArrayList<FormulairePatient>)getIntent().getSerializableExtra("ListePatient");
        mRycycleView = findViewById(R.id.lisPatient);

        viewDialog = new ViewDialog(this);

        Log.e("Id Medecin suivit = ",""+IdMedecin);
        Log.e("TAille Liste patient = ", " "+patients.size());
        context = this;
        if(patients.size()>0) {


            mRycycleView.setHasFixedSize(true);
            mLayoutManger = new LinearLayoutManager(this);
            recylAdapter = new PatientRecylAdapter(patients);

            mRycycleView.setLayoutManager(mLayoutManger);
            mRycycleView.setAdapter(recylAdapter);
            final Intent intent = new Intent(this, MedManagePatientActivity.class);



            recylAdapter.setOnItemClickListener(new PatientRecylAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    patient = patients.get(position);
                    Log.e("nom :", "" + patient.getNom());
                    intent.putExtra("patient", patient);
                    startActivity(intent);
                }

                @Override
                public void onDeleteClick(int position) {
                    patient = patients.get(position);
                    alertDelete(patient);
                }

                @Override
                public void onTransferClick(int position) {
                    patient = patients.get(position);
                    alertTRensfer(patient);
                }
            });
        }else{
            alertVide();

        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

            patient = patients.get(i);
            Log.e("nom :",""+patient.getNom());
            Intent intent= new Intent(this, MedManagePatientActivity.class);
            intent.putExtra("patient",patient);
            startActivity(intent);
    }

    private  void alertDelete(FormulairePatient patient) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));
        paquet.setService(20);
        paquet.setFormulairePatient(patient);
        alertDialogBuilder.setTitle("Alerte !!!!!!!!!!!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Voulez vous vraiment supprimer cet patient !!!   ")
                .setCancelable(false)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!socketManage.chat(paquet)){
                            alertData();
                        }
                        else{

                            paquet.setIdUser(IdMedecin);
                            paquet.setService(4);
                            try{

                                socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));
                                patients = socketManage.patients(paquet);
                                recylAdapter = new PatientRecylAdapter(patients);
                                mRycycleView.setLayoutManager(mLayoutManger);
                                mRycycleView.setAdapter(recylAdapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                                alertConnexion();
                            }

                        }
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    private  void alertData() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Erreur");

        // set dialog message
        alertDialogBuilder
                .setMessage("OPS Erreu Lors de la suppression...... !!!   ")
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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        MenuItem  srearchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView)srearchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recylAdapter.getFilter().filter(newText);

                return false;
            }
        });

        return  true;
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

     private void alertTRensfer(final FormulairePatient patient) {

         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Transfere ........!!!!!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Voulez vous vraiment transferez cet patient    ")
                .setCancelable(false)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {
                            viewDialog.showDialog();

                            socketManage.ConnexionSocket(null,  context.getResources().openRawResource(R.raw.keystorepk));
                            paquet.setService(25);
                            paquet.setIdUser(patient.getIdPatient());
                            socketManage.chat(paquet);
                            viewDialog.hideDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                            alertConnexion();
                        }

                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    private void alertVide() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Vide ........!!!!!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Vous n'avez aucun patient a suivir!!!!    ")
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

package com.example.myapplication.SSl_Socket;

import android.util.Log;
import android.view.View;

import com.example.myapplication.Documents;
import com.example.myapplication.FormCapteur;
import com.example.myapplication.FormMedecin;
import com.example.myapplication.FormSuivitPatient;
import com.example.myapplication.FormulairePatient;
import com.example.myapplication.FromNotification;
import com.example.myapplication.R;
import com.example.myapplication.ObjectPaquet;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class SocketManage {
    private String ip_address = "192.168.1.10";
    private int port = 12346;
    private SSLSocket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private final String TAG = "TAG";
    private char keystorepass[] = "imane123".toCharArray(); //the error might be this mdp



    public void ConnexionSocket(View v,InputStream keyins) {

        try {


            KeyStore ks = KeyStore.getInstance("PKCS12");
            if(v!= null){
                InputStream keyin = v.getResources().openRawResource(R.raw.keystorepk);
                Log.e("certifs :","key    "+keyin);
                  ks.load(keyin, keystorepass);
                // getting the list of aliases
                // using aliases() method
                Enumeration<String> e = ks.aliases();

                // display the result
                System.out.println("List of all the alias present");
                while (e.hasMoreElements()) {
                    System.out.print(e.nextElement() + "");
                    System.out.println();
                }
                Log.e("certifs :","end of certifs");
                SSLSocketFactory socketFactory = new SSLSocketFactory(ks);
                socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                socket = (SSLSocket)
                        socketFactory.createSocket(new Socket(ip_address, port), ip_address, port, false);
                socket.setNeedClientAuth(true);
                socket.startHandshake();

            }
            else{
                InputStream keyin= keyins;
                ks.load(keyin, keystorepass);
                SSLSocketFactory socketFactory = new SSLSocketFactory(ks);
                socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                socket = (SSLSocket)
                        socketFactory.createSocket(new Socket(ip_address, port), ip_address, port, false);
                socket.setNeedClientAuth(true);
                socket.startHandshake();

            }



            printServerCertificate(socket);
            printSocketInfo(socket);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void printServerCertificate(SSLSocket socket) {
        try {
            Certificate[] serverCerts =
                    socket.getSession().getPeerCertificates();
            for (int i = 0; i < serverCerts.length; i++) {
                Certificate myCert = serverCerts[i];
                Log.i(TAG, "====Certificate:" + (i + 1) + "====");
                Log.i(TAG, "-Public Key-\n" + myCert.getPublicKey());
                Log.i(TAG, "-Certificate Type-  " + myCert.getType());
                Log.i(TAG, "-Certificate Encoded-  " + myCert.getEncoded());

                System.out.println();
            }
        } catch (SSLPeerUnverifiedException | CertificateEncodingException e) {
            Log.i(TAG, "Could not verify peer");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void printSocketInfo(SSLSocket s) {
        Log.i(TAG, "Socket class: " + s.getClass());
        Log.i(TAG, "   Remote address = "
                + s.getInetAddress().toString());
        Log.i(TAG, "   Remote port = " + s.getPort());
        Log.i(TAG, "   Local socket address = "
                + s.getLocalSocketAddress().toString());
        Log.i(TAG, "   Local address = "
                + s.getLocalAddress().toString());
        Log.i(TAG, "   Local port = " + s.getLocalPort());
        Log.i(TAG, "   Need client authentication = "
                + s.getNeedClientAuth() );
        SSLSession ss = s.getSession();
        Log.i(TAG, "   Cipher suite = " + ss.getCipherSuite());
        Log.i(TAG, "   Protocol = " + ss.getProtocol());
        Log.i(TAG, "   Session Contex  = " + ss.getSessionContext());
    }

    public boolean chat(ObjectPaquet paquet) {

        boolean line = false;
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (boolean) in.readObject();
            Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }


    public boolean update(ObjectPaquet paquet) {

        boolean line = false;
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (boolean) in.readObject();
            Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }

    public ArrayList<FormulairePatient> patients(ObjectPaquet paquet) {

        ArrayList<FormulairePatient> line =  new ArrayList<FormulairePatient>();
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (ArrayList<FormulairePatient>) in.readObject();
           // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }



    public ArrayList<Documents> documents(ObjectPaquet paquet) {

        ArrayList<Documents> line =  new ArrayList<Documents>();
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (ArrayList<Documents>) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }

    public ArrayList<FormCapteur> capteurs(ObjectPaquet paquet) {

        ArrayList<FormCapteur> line =  new ArrayList<FormCapteur>();
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (ArrayList<FormCapteur>) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }


    public FormMedecin getMedecin(ObjectPaquet paquet) {

        FormMedecin line =  new FormMedecin();
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (FormMedecin) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }

    public FormulairePatient getPatient(ObjectPaquet paquet) {

        FormulairePatient line = new FormulairePatient();
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (FormulairePatient) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }


    public int getNbNotification(ObjectPaquet paquet) {

        int line = 0;
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (int) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }

    public ArrayList<FormSuivitPatient> getSuivit(ObjectPaquet paquet) {

        ArrayList<FormSuivitPatient> line =  new ArrayList<FormSuivitPatient>();
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (ArrayList<FormSuivitPatient>) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }

    public ArrayList<FromNotification>  ListNotification(ObjectPaquet paquet) {
        ArrayList<FromNotification> line =  new ArrayList<FromNotification>();
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (ArrayList<FromNotification>) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }

    public int getHR(ObjectPaquet paquet) {

        int line = 0;
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (int) in.readObject();
            // Log.e("Message Serveur : ", "" + line);

            //Log.i(TAG,line);
        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }

    public File getFile(ObjectPaquet paquet) {

        File line = null;
        // send id of the device to match with the image
        try {
            out.writeObject(paquet);
            out.flush();
        } catch (IOException e2) {
            Log.i(TAG, "Write failed Out");
            System.exit(1);
        }
        // receive a ready command from the server
        try {
            line = (File)in.readObject();

        } catch (Exception e1) {
            Log.i(TAG, "Read failed IN");
            System.exit(1);
        }

        return line;
    }
}

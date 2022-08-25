package com.example.myapplication;

import java.io.Serializable;

public class FormulairePatient implements Serializable {
    private static final long serialVersionUID = 1L;


    private String nom,prenom,dateNaissance;
    private int age,numTel;
    private long idMedecin,idPatient;
    private FormLogin login;
    private String wilaya,ville;
    private Documents docs;
    private int nbDocs;

    public int getNbDocs() {
        return nbDocs;
    }

    public void setNbDocs(int nbDocs) {
        this.nbDocs = nbDocs;
    }

    public Documents getDocs() {
        return docs;
    }

    public void setDocs(Documents docs) {
        this.docs = docs;
    }

    public long getIdMedecin() {
        return idMedecin;
    }

    public void setIdMedecin(long idMedecin) {
        this.idMedecin = idMedecin;
    }

    public FormLogin getLogin() {
        return login;
    }

    public void setLogin(FormLogin login) {
        this.login = login;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(long idPatient) {
        this.idPatient = idPatient;
    }

    public int getNumTel() {
        return numTel;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }



}

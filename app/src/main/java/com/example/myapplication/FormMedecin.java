package com.example.myapplication;

import java.io.Serializable;

public class FormMedecin implements Serializable {
    private static final long serialVersionUID = 1L;


    private long idMedecin,numTel,nbPatient;

    private String nom,prenom,mail,grade,service;

    public long getIdMedecin() {
        return idMedecin;
    }

    public void setIdMedecin(long idMedecin) {
        this.idMedecin = idMedecin;
    }

    public long getNumTel() {
        return numTel;
    }

    public void setNumTel(long numTel) {
        this.numTel = numTel;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public long getNbPatient() {
        return nbPatient;
    }

    public void setNbPatient(long nbPatient) {
        this.nbPatient = nbPatient;
    }

    public static class FromNotification implements Serializable {
        private static final long serialVersionUID = 1L;

        private  Long  idMedecin;
        private Long idPatient;
        private String idcapteur;
        private String date;

        public Long getIdMedecin() {
            return idMedecin;
        }

        public void setIdMedecin(Long idMedecin) {
            this.idMedecin = idMedecin;
        }

        public Long getIdPatient() {
            return idPatient;
        }

        public void setIdPatient(Long idPatient) {
            this.idPatient = idPatient;
        }

        public String getIdcapteur() {
            return idcapteur;
        }

        public void setIdcapteur(String idcapteur) {
            this.idcapteur = idcapteur;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }


    }
}

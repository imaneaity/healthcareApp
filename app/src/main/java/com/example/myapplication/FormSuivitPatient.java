package com.example.myapplication;


import java.io.Serializable;
import java.util.Date;

public class FormSuivitPatient implements Serializable {

    private static final long serialVersionUID = 1L;


    private String idCapteur;
    private Long idPatient;
    private int data;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIdCapteur() {
        return idCapteur;
    }

    public void setIdCapteur(String idCapteur) {
        this.idCapteur = idCapteur;
    }

    public Long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Long idPatient) {
        this.idPatient = idPatient;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}

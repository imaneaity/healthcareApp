package com.example.myapplication;

import java.io.Serializable;

public class ObjectPaquet implements Serializable {

    private static final long serialVersionUID = 1L;

    private int service;
    private FormulairePatient formulairePatient;
    private FormLogin formLogin ;
    private FormCapteur formCapteur;
    private FormMedecin formMedecin;
    private FromNotification formNotification;
    private long idUser;
    private FormSuivitPatient formSuivitPatient;
    private String wilaya;
    private FormMoreInfo plusInfo;
    private String typeDocs;
    private int idDocs;

    public int getIdDocs() {
        return idDocs;
    }

    public void setIdDocs(int idDocs) {
        this.idDocs = idDocs;
    }

    public String getTypeDocs() {
        return typeDocs;
    }

    public void setTypeDocs(String typeDocs) {
        this.typeDocs = typeDocs;
    }

    public FormMoreInfo getPlusInfo() {
        return plusInfo;
    }

    public void setPlusInfo(FormMoreInfo plusInfo) {
        this.plusInfo = plusInfo;
    }

    public FromNotification getFormNotification() {
        return formNotification;
    }

    public void setFormNotification(FromNotification formNotification) {
        this.formNotification = formNotification;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }

    public FormMedecin getFormMedecin() {
        return formMedecin;
    }

    public void setFormMedecin(FormMedecin formMedecin) {
        this.formMedecin = formMedecin;
    }


    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public FormCapteur getFormCapteur() {
        return formCapteur;
    }

    public void setFormCapteur(FormCapteur formCapteur) {
        this.formCapteur = formCapteur;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public FormLogin getFormLogin() {
        return formLogin;
    }

    public void setFormLogin(FormLogin formLogin) {
        this.formLogin = formLogin;
    }

    public FormulairePatient getFormulairePatient() {
        return formulairePatient;
    }

    public void setFormulairePatient(FormulairePatient formulairePatient) {
        this.formulairePatient = formulairePatient;
    }

    public FormSuivitPatient getFormSuivitPatient() {
        return formSuivitPatient;
    }

    public void setFormSuivitPatient(FormSuivitPatient formSuivitPatient) {
        this.formSuivitPatient = formSuivitPatient;
    }

	
}

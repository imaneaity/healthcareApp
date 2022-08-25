package com.example.myapplication;

import java.io.Serializable;

public class FormCapteur implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idCapteur;
    private String focntion;
    private long idPatient;
    private Long idMedecin;
    private String etat;


    
    public Long getIdMedecin() {
		return idMedecin;
	}

	public void setIdMedecin(Long idMedecin) {
		this.idMedecin = idMedecin;
	}

	public String getIdCapteur() {
        return idCapteur;
    }

    public void setIdCapteur(String idCapteur) {
        this.idCapteur = idCapteur;
    }

    public String getFocntion() {
        return focntion;
    }

    public void setFocntion(String focntion) {
        this.focntion = focntion;
    }

    public long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(long idPatient) {
        this.idPatient = idPatient;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}

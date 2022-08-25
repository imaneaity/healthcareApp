package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;

public class FromNotification implements Serializable {
    private static final long serialVersionUID = 1L;

    private  Long  idMedecin;
    private Long idPatient;
    private String idcapteur;
    private Date date;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
    
    
}

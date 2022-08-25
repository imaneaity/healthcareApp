package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;

public class FormMoreInfo implements Serializable {
    private static final long serialVersionUID = 1L;


    private Date debut;
    private Date fin;
    private Long idPatient;
    private String typeData;

    public Long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Long idPatient) {
        this.idPatient = idPatient;
    }

    public String getTypeData() {
        return typeData;
    }

    public void setTypeData(String typeData) {
        this.typeData = typeData;
    }

    public Date getDebut() {
        return debut;
    }

    public void setDebut(Date debut) {
        this.debut = debut;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }
}

package com.example.myapplication;

import java.io.Serializable;

public class Documents implements Serializable {
	private static final long serialVersionUID = 1L;

	private int taille;
	private int heartRate;
	private int presur;
	private int poid;
	private int idDoccs;
	private String comment;
	private String date;
	private long idPatient;
	private String designation;
	private Long idMedecin;
	



	public Long getIdMedecin() {
		return idMedecin;
	}

	public void setIdMedecin(Long idMedecin) {
		this.idMedecin = idMedecin;
	}

	public Documents(int taille, int heartRate, int presur, int poid, String comment, long idPatient) {
		super();
		this.taille = taille;
		this.heartRate = heartRate;
		this.presur = presur;
		this.poid = poid;
		this.comment = comment;
		this.idPatient = idPatient;
	}

	public long getIdPatient() {
		return idPatient;
	}

	public void setIdPatient(long idPatient) {
		this.idPatient = idPatient;
	}

	public int getTaille() {
		return taille;
	}

	public void setTaille(int taille) {
		this.taille = taille;
	}

	public int getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}

	public int getPresur() {
		return presur;
	}

	public void setPresur(int presur) {
		this.presur = presur;
	}

	public int getPoid() {
		return poid;
	}

	public void setPoid(int poid) {
		this.poid = poid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getIdDoccs() {
		return idDoccs;
	}

	public void setIdDoccs(int idDoccs) {
		this.idDoccs = idDoccs;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}
}

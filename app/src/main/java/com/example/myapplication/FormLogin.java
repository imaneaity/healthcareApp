package com.example.myapplication;

import java.io.Serializable;

public class FormLogin implements Serializable {
    private static final long serialVersionUID = 1L;


    private long IdConnexion;
    private String PassWord;

    public long getIdConnexion() {
        return IdConnexion;
    }

    public void setIdConnexion(long idConnexion) {
        IdConnexion = idConnexion;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }
}

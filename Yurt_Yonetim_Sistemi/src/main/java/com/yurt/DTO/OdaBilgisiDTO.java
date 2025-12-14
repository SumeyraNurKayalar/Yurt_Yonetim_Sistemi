package com.yurt.DTO;

import com.yurt.model.User;

import java.util.List;

public class OdaBilgisiDTO {
    private String odaNumarasi;
    private List<User> odaArkadaslari;

    public OdaBilgisiDTO(String odaNumarasi, List<User> odaArkadaslari) {
        this.odaNumarasi = odaNumarasi;
        this.odaArkadaslari = odaArkadaslari;
    }

    public String getOdaNumarasi() {
        return odaNumarasi;
    }

    public List<User> getOdaArkadaslari() {
        return odaArkadaslari;
    }
}
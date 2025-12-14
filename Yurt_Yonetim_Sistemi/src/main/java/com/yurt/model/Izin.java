package com.yurt.model;

import java.time.LocalDate;

public class Izin {

    private int izinId;
    private int izinAlanId;
    private LocalDate baslangicTarihi;
    private LocalDate bitisTarihi;
    private String neden;
    private String durum;
    private String ogrenciAdi;
    private String odaNumarasi;
    private LocalDate talepTarihi;

    public Izin() {
    }

    private Izin(IzinBuilder builder) {
        this.izinId = builder.izinId;
        this.izinAlanId = builder.izinAlanId;
        this.baslangicTarihi = builder.baslangicTarihi;
        this.bitisTarihi = builder.bitisTarihi;
        this.neden = builder.neden;
        this.durum = builder.durum;
        this.ogrenciAdi = builder.ogrenciAdi;
        this.odaNumarasi = builder.odaNumarasi;
        this.talepTarihi = builder.talepTarihi;
    }

    public int getIzinId() { return izinId; }
    public int getIzinAlanId() { return izinAlanId; }
    public LocalDate getBaslangicTarihi() { return baslangicTarihi; }
    public LocalDate getBitisTarihi() { return bitisTarihi; }
    public String getNeden() { return neden; }
    public String getDurum() { return durum; }
    public String getOgrenciAdi() { return ogrenciAdi; }
    public String getOdaNumarasi() { return odaNumarasi; }
    public LocalDate getTalepTarihi() { return talepTarihi; }

    public void setIzinId(int izinId) { this.izinId = izinId; }
    public void setIzinAlanId(int izinAlanId) { this.izinAlanId = izinAlanId; }
    public void setBaslangicTarihi(LocalDate baslangicTarihi) { this.baslangicTarihi = baslangicTarihi; }
    public void setBitisTarihi(LocalDate bitisTarihi) { this.bitisTarihi = bitisTarihi; }
    public void setNeden(String neden) { this.neden = neden; }
    public void setDurum(String durum) { this.durum = durum; }
    public void setOgrenciAdi(String ogrenciAdi) { this.ogrenciAdi = ogrenciAdi; }
    public void setOdaNumarasi(String odaNumarasi) { this.odaNumarasi = odaNumarasi; }
    public void setTalepTarihi(LocalDate talepTarihi) { this.talepTarihi = talepTarihi; }

    public static class IzinBuilder {
        private int izinId = 0;
        private int izinAlanId = 0;
        private LocalDate baslangicTarihi;
        private LocalDate bitisTarihi;
        private String neden;
        private String durum = "Beklemede";
        private String ogrenciAdi = "";
        private String odaNumarasi = "";
        private LocalDate talepTarihi = LocalDate.now();

        public IzinBuilder() {}

        public IzinBuilder izinId(int izinId) {
            this.izinId = izinId;
            return this;
        }

        public IzinBuilder izinAlanId(int izinAlanId) {
            this.izinAlanId = izinAlanId;
            return this;
        }

        public IzinBuilder baslangicTarihi(LocalDate baslangicTarihi) {
            this.baslangicTarihi = baslangicTarihi;
            return this;
        }

        public IzinBuilder bitisTarihi(LocalDate bitisTarihi) {
            this.bitisTarihi = bitisTarihi;
            return this;
        }

        public IzinBuilder neden(String neden) {
            this.neden = neden;
            return this;
        }

        public IzinBuilder durum(String durum) {
            this.durum = durum;
            return this;
        }

        public IzinBuilder ogrenciAdi(String ogrenciAdi) {
            this.ogrenciAdi = ogrenciAdi;
            return this;
        }

        public IzinBuilder odaNumarasi(String odaNumarasi) {
            this.odaNumarasi = odaNumarasi;
            return this;
        }

        public IzinBuilder talepTarihi(LocalDate talepTarihi) {
            this.talepTarihi = talepTarihi;
            return this;
        }

        public Izin build() {
            if (izinAlanId == 0 || baslangicTarihi == null || bitisTarihi == null || neden == null) {
                throw new IllegalStateException("Zorunlu alanlar (izinAlanId, Tarihler, Neden) doldurulmalıdır.");
            }
            return new Izin(this);
        }
    }
}
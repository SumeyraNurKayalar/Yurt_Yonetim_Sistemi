package com.yurt.repository;

import com.yurt.model.User;
import com.yurt.DTO.OdaBilgisiDTO;
import java.util.List;

public interface StudentRepository {

    User kullaniciGirisYap(String giris, String plainPassword);
    User getUserById(int id);
    boolean profilGuncelle(int kullaniciId, String ad, String soyad, String email,
                           String telefon, String adres, String yeniSifre);
    boolean kayitEkle(String ad, String soyad, String tc, String email, String telefon,
                      String adres, String sifre, String rol);
    List<User> findAll();
    User findByEmail(String email);
    User findByTc(String tc);
    List<User> findByOdaId(int odaId);
    void createUser(String ad, String soyad, String tc, String kullaniciAdi, String telefon, String adres, String rol, String email, String passwordHash);
    boolean isStudentCurrentlyPlaced(long userId);
    OdaBilgisiDTO getOdaVeArkadasBilgileri(int ogrenciId);
}
package com.yurt.service;

import com.yurt.model.Izin;
import com.yurt.model.User;
import com.yurt.design.observer.PermissionSubject;
import com.yurt.repository.jdbc.JdbcPermissionRepository;
import com.yurt.repository.jdbc.JdbcStudentRepository;

import java.util.List;

public class PermissionService {

    private final JdbcPermissionRepository permissionRepository;
    private final JdbcStudentRepository studentRepository;
    private final PermissionSubject permissionSubject;

    public PermissionService() {
        this.permissionRepository = JdbcPermissionRepository.getInstance();
        this.studentRepository = JdbcStudentRepository.getInstance();
        this.permissionSubject = PermissionSubject.getInstance();
    }

    public List<Izin> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<User> getAllUsers() {
        return studentRepository.findAll();
    }

    public User getUserById(int id) {
        return studentRepository.getUserById(id);
    }

    public Izin savePermissionRequest(Izin izin) {
        Izin saved = permissionRepository.save(izin);

        if (saved != null) {
            System.out.println("✔ Yeni izin oluşturuldu: " + saved.getIzinId());
            permissionSubject.notifyNewPermissionCreated(saved);
        } else {
            System.err.println("❌ İzin kaydedilemedi.");
        }

        return saved;
    }

    public Izin updatePermissionStatus(int izinId, String newStatus, int personelId) {
        Izin updated = permissionRepository.updateStatus(izinId, newStatus, personelId);

        if (updated != null) {
            System.out.println("✔ İzin durumu güncellendi: " + updated.getIzinId() + " -> " + updated.getDurum());
            permissionSubject.notifyObservers(updated);
        } else {
            System.err.println("❌ İzin durumu güncellenemedi: " + izinId);
        }

        return updated;
    }
}
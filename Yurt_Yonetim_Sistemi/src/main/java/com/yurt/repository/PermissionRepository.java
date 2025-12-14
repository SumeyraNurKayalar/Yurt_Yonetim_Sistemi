package com.yurt.repository;

import com.yurt.model.Izin;
import java.util.List;

public interface PermissionRepository {

    List<Izin> findAll();
    Izin findById(int izinId);
    List<Izin> findByOgrenciId(int ogrenciId);
    Izin save(Izin izin);
    Izin updateStatus(int izinId, String newStatus, int personelId);
    List<Izin> findPendingPermissions();
    List<Izin> findApprovedPermissions();
    List<Izin> findRejectedPermissions();
}
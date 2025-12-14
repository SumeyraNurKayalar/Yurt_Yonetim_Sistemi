package com.yurt.design.observer;

import com.yurt.model.Izin;

import java.util.ArrayList;
import java.util.List;

public class PermissionSubject {

    private static PermissionSubject instance;

    private final List<StudentObserver> studentObservers = new ArrayList<>();
    private StaffObserver staffObserver;

    private PermissionSubject() {}

    public static PermissionSubject getInstance() {
        if (instance == null) {
            instance = new PermissionSubject();
        }
        return instance;
    }

    public void addStudentObserver(StudentObserver observer) {
        studentObservers.add(observer);
    }

    public void removeStudentObserver(StudentObserver observer) {
        studentObservers.remove(observer);
    }

    public void setStaffObserver(StaffObserver observer) {
        this.staffObserver = observer;
    }

    public void notifyObservers(Izin izin) {
        for (StudentObserver observer : studentObservers) {
            observer.update(izin);
        }
    }

    public void notifyNewPermissionCreated(Izin izin) {
        if (staffObserver != null) {
            staffObserver.refreshPermissions();
        }
    }
}
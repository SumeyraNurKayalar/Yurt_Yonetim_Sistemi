package com.yurt.repository;

import com.yurt.model.Room;
import java.util.List;

public interface RoomRepository {
    Room save(Room room);
    Room findByRoomNumber(String roomNumber);
    List<Room> findAll();
    void update(Room room);
    int countOccupantsByRoomNumber(String roomNumber);
    void recordStudentPlacement(int studentId, String roomNumber);
}
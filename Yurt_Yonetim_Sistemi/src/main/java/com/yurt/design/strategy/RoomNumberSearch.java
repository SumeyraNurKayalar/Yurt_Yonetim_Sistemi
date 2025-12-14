package com.yurt.design.strategy;

import com.yurt.model.User;
import com.yurt.model.Student;
import java.util.List;
import java.util.stream.Collectors;

public class RoomNumberSearch implements SearchStrategy {

    @Override
    public List<User> search(List<User> allUsers, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return allUsers.stream()
                    .filter(u -> "Ogrenci".equalsIgnoreCase(u.getRole()))
                    .toList();
        }

        String lower = searchTerm.toLowerCase();

        return allUsers.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .filter(s -> s.getCurrentRoomNumber() != null && s.getCurrentRoomNumber().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
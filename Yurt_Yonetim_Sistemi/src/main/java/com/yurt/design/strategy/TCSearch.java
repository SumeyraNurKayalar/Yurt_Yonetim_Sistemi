package com.yurt.design.strategy;

import com.yurt.model.User;
import com.yurt.model.Student;
import java.util.List;
import java.util.stream.Collectors;

public class TCSearch implements SearchStrategy {

    @Override
    public List<User> search(List<User> allUsers, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return allUsers.stream()
                    .filter(u -> "Ogrenci".equalsIgnoreCase(u.getRole()))
                    .toList();
        }

        String trimmedTerm = searchTerm.trim();

        return allUsers.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .filter(s -> s.getTcNumber() != null && s.getTcNumber().contains(trimmedTerm))
                .collect(Collectors.toList());
    }
}
package com.yurt.design.strategy;

import com.yurt.model.User;
import com.yurt.model.Student;
import java.util.List;
import java.util.stream.Collectors;

public class NameSearch implements SearchStrategy {

    @Override
    public List<User> search(List<User> allUsers, String searchTerm) {
        if (searchTerm == null) {
            return allUsers;
        }

        String lower = searchTerm.toLowerCase();

        return allUsers.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .filter(s ->
                        (s.getFirstName() != null && s.getFirstName().toLowerCase().contains(lower)) ||
                                (s.getLastName() != null && s.getLastName().toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());
    }
}
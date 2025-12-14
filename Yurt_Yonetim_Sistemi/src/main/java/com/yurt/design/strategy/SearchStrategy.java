package com.yurt.design.strategy;

import com.yurt.model.User;
import java.util.List;

public interface SearchStrategy {

    List<User> search(List<User> allUsers, String searchTerm);
}
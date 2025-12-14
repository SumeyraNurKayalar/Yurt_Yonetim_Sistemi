package com.yurt.design.strategy;

import com.yurt.model.User;
import java.util.List;

public class SearchContext {
    private SearchStrategy strategy;

    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<User> executeSearch(List<User> userList, String searchTerm) {
        if (strategy == null) {

            return userList;
        }

        return strategy.search(userList, searchTerm);
    }
}
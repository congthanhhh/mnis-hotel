package com.thanh.mnishotel.service;

import com.thanh.mnishotel.model.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);
}

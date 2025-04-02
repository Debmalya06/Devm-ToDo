package com.services2;

import com.user_entity.User;

public interface UserService {
    boolean AddUser(User user);
    User login_user(String email, String password);
}
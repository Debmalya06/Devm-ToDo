package com.services2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.repo.UserRepo;
import com.user_entity.User;

@Service
public class ImplUserService implements UserService {
    @Autowired
   private UserRepo urepo;

    @Override
    public boolean AddUser(User user) {
        try {
            urepo.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User login_user(String email, String password) {
        User user = urepo.findByEmail(email);
            if (user != null && user.getSetPassword().equals(password)) {
                return user;
            }
            
       return null;       
    }
}
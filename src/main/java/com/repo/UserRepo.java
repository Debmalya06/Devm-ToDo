package com.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.user_entity.User;
import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
User findByEmail(String email);
}
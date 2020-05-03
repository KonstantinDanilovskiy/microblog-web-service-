package com.letscode.sweater.repository;

import com.letscode.sweater.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    List<User> findAll();

    User findByActivationCode(String code);
}


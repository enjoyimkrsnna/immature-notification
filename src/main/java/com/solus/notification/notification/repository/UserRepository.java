package com.solus.notification.notification.repository;

import com.solus.notification.notification.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
}

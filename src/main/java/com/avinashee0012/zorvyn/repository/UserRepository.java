package com.avinashee0012.zorvyn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avinashee0012.zorvyn.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);
}

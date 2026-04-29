
package com.user_service.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.user_service.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByUsername(String username);
}
package com.house.hunter.repository;

import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserRole;
import com.house.hunter.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
    Optional<List<User>> findByRole(UserRole role);
    Optional<List<User>> findByAccountStatusAndCreatedAtBefore(UserAccountStatus accountStatus, LocalDateTime date);
    Optional<List<User>> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page findAll(Pageable pageable);
    long countByRole(UserRole role);
}

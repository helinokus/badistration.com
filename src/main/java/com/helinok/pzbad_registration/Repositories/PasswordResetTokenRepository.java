package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @Query("select p from PasswordResetToken p where p.email = ?1 and p.code = ?2 and p.used = false")
    Optional<PasswordResetToken> findByEmailAndCodeAndUsedFalse(String email, String code);
    @Query("select p from PasswordResetToken p where p.email = ?1 and p.used = false")
    Optional<PasswordResetToken> findByEmailAndUsedFalse(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now OR p.used = true")
    int deleteExpiredAndUsedTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.email = :email AND (p.expiryDate < :now OR p.used = true)")
    int deleteExpiredTokensForEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) FROM PasswordResetToken p WHERE p.expiryDate < :now AND p.used = false")
    long countExpiredTokens(@Param("now") LocalDateTime now);
}

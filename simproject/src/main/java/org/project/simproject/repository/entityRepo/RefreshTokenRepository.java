package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.RefreshToken;
import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(User user);
    RefreshToken findByRefreshToken(String refreshToken);
}

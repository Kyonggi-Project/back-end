package org.project.simproject.repository.entityRepo;

import org.project.simproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByNicknameContains(String author);
    Optional<User> findByNickname(String nickname);     // DM Room 생성 시 필요성
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}

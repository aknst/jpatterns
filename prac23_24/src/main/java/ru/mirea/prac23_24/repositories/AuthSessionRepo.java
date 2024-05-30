package ru.mirea.prac23_24.repositories;

import ru.mirea.prac23_24.entities.AuthSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepo extends JpaRepository<AuthSession, UUID> {

  Optional<AuthSession> findAuthSessionByRefreshToken(String refreshToken);
}

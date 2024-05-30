package ru.mirea.prac23_24.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "auth_sessions")
@Data
public class AuthSession {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "idempotency_key", nullable = false, unique = true)
  private UUID idempotencyKey;

  @Column(name = "refresh_token", columnDefinition = "TEXT")
  private String refreshToken;
}

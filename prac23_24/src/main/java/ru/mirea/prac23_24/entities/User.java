package ru.mirea.prac23_24.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "users")
public class User {

  @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

  @Column(nullable = false, unique = true)
  @Size(max = 100)
  private String nickname;

  @Column(nullable = false, unique = true)
  @Size(max = 100)
  private String phone;

  @Column(nullable = false) @Size(max = 100) private String password;

  @Column(name = "idempotency_key", nullable = false, unique = true)
  private UUID idempotencyKey;

  @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
  private AuthSession authSession;
}

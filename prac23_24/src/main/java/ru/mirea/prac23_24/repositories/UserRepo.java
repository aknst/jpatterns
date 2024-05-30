package ru.mirea.prac23_24.repositories;

import ru.mirea.prac23_24.entities.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

  Optional<User> findByPhone(String phone);
}

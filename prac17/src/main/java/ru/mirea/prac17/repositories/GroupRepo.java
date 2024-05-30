package ru.mirea.prac17.repositories;

import ru.mirea.prac17.entities.Group;
import ru.mirea.prac17.entities.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupRepo extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group>{
  List<Group> findAll(Specification<Group> spec);
  Optional<Group> findGroupByName(String name);
}

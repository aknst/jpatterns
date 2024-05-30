package ru.mirea.prac17.repositories;

import ru.mirea.prac17.entities.Student;
import ru.mirea.prac17.specifications.StudentSpecifications;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepo extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {
  List<Student> findAll(Specification<Student> spec);
}

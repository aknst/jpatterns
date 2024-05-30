package ru.mirea.prac20.repositories;

import ru.mirea.prac20.entities.Group;
import ru.mirea.prac20.entities.Student;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepo extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

  List<Student> findAllByFirstNameAndLastNameAndMiddleNameAndGroup(String firstname, String lastname, String middleName, Group group);
}

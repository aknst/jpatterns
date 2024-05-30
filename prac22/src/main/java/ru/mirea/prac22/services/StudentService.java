package ru.mirea.prac22.services;

import ru.mirea.prac22.dtos.FilterStudentsDto;
import ru.mirea.prac22.dtos.StudentDto;
import ru.mirea.prac22.entities.Student;
import ru.mirea.prac22.exceptions.ResourceNotFound;
import ru.mirea.prac22.repositories.GroupRepo;
import ru.mirea.prac22.repositories.StudentRepo;
import ru.mirea.prac22.specifications.StudentSpecifications;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class StudentService {

  private StudentRepo studentRepo;
  private GroupRepo groupRepo;

  public UUID createStudent(StudentDto studentDto) {
    var student = new Student(studentDto);
    studentRepo.save(student);
    return student.getId();
  }

  public StudentDto getStudent(UUID id) {
    var optionalStudent = studentRepo.findById(id);
    if (optionalStudent.isEmpty()) {
      throw new ResourceNotFound();
    }
    var student = optionalStudent.get();
    return StudentDto.builder().firstName(student.getFirstName()).lastName(student.getLastName())
        .middleName(student.getMiddleName()).groupName(student.getGroup().getName()).build();
  }

  public void deleteStudent(UUID id) {
    studentRepo.deleteById(id);
  }

  public void updateStudent(UUID id, StudentDto studentDto) {
    var optionalStudent = studentRepo.findById(id);
    if (optionalStudent.isEmpty()) {
      throw new ResourceNotFound();
    }

    var student = optionalStudent.get();
    student.setData(studentDto);

    studentRepo.save(student);
  }

  public List<StudentDto> getFilteredStudents(FilterStudentsDto filterStudentsDto) {
    Specification<Student> spec = Specification.where(null);

    if (!filterStudentsDto.getFirstName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasFirstName(filterStudentsDto.getFirstName()));
    }else{
      log.debug("No filtering by firstname");
    }
    if (!filterStudentsDto.getMiddleName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasMiddleName(filterStudentsDto.getMiddleName()));
    }else{
      log.debug("No filtering by middleName");
    }
    if (!filterStudentsDto.getLastName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasLastName(filterStudentsDto.getLastName()));
    }else{
      log.debug("No filtering by lastname");
    }
    if (!filterStudentsDto.getGroupName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasGroupName(filterStudentsDto.getGroupName()));
    }else{
      log.debug("No filtering by groupName");
    }
    if (!filterStudentsDto.getGroupNamePrefix().isEmpty()) {
      spec = spec.and(
          StudentSpecifications.hasGroupNamePrefix(filterStudentsDto.getGroupNamePrefix()));
    }else{
      log.debug("No filtering by groupNamePrefix");
    }

    var rawStudents = studentRepo.findAll(spec);
    if (rawStudents.isEmpty()) {
      throw new ResourceNotFound();
    }
    return rawStudents.stream().map(
            student -> StudentDto.builder().firstName(student.getFirstName())
                .lastName(student.getLastName())
                .middleName(student.getMiddleName()).groupName(student.getGroup().getName()).build())
        .collect(Collectors.toList());
  }
}

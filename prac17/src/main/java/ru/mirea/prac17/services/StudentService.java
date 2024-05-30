package ru.mirea.prac17.services;

import ru.mirea.prac17.dtos.FilterStudentsDto;
import ru.mirea.prac17.dtos.StudentDto;
import ru.mirea.prac17.entities.Student;
import ru.mirea.prac17.exceptions.ResourceNotFound;
import ru.mirea.prac17.repositories.GroupRepo;
import ru.mirea.prac17.repositories.StudentRepo;
import ru.mirea.prac17.specifications.StudentSpecifications;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class StudentService {

  private StudentRepo studentRepo;
  private GroupRepo groupRepo;

  public UUID createStudent(StudentDto studentDto) {
    var student = new Student(studentDto);
    var groupOpt = groupRepo.findGroupByName(studentDto.getGroupName());
    if (groupOpt.isEmpty()) {
      throw new ResourceNotFound();
    }

    student.setGroup(groupOpt.get());
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

    if (filterStudentsDto.getFirstName() != null && !filterStudentsDto.getFirstName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasFirstName(filterStudentsDto.getFirstName()));
    }
    if (filterStudentsDto.getMiddleName() != null && !filterStudentsDto.getMiddleName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasMiddleName(filterStudentsDto.getMiddleName()));
    }
    if (filterStudentsDto.getLastName() != null && !filterStudentsDto.getLastName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasLastName(filterStudentsDto.getLastName()));
    }
    if (filterStudentsDto.getGroupName() != null && !filterStudentsDto.getGroupName().isEmpty()) {
      spec = spec.and(StudentSpecifications.hasGroupName(filterStudentsDto.getGroupName()));
    }
    if (filterStudentsDto.getGroupNamePrefix() != null && !filterStudentsDto.getGroupNamePrefix().isEmpty()) {
      spec = spec.and(
          StudentSpecifications.hasGroupNamePrefix(filterStudentsDto.getGroupNamePrefix()));
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

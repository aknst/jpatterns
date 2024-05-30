package ru.mirea.prac20.specifications;

import ru.mirea.prac20.entities.Student;
import org.springframework.data.jpa.domain.Specification;

public interface StudentSpecifications {

  static Specification<Student> hasFirstName(String firstName) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.like(
        criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
  }

  static Specification<Student> hasLastName(String lastName) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.like(
        criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
  }

  static Specification<Student> hasMiddleName(String middleName) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.like(
        criteriaBuilder.lower(root.get("middleName")), "%" + middleName.toLowerCase() + "%"));
  }

  static Specification<Student> hasGroupName(String groupName) {
    return (root, query, cb) -> groupName != null ? root.join("group").get("name").in(groupName)
        : null;
  }

  static Specification<Student> hasGroupNamePrefix(String groupNamePrefix) {
    return (root, query, cb) -> groupNamePrefix != null ? cb.like(root.join("group").get("name"),
        groupNamePrefix + "%") : null;
  }
}

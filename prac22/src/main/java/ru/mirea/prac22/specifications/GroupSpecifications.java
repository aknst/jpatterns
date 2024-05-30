package ru.mirea.prac22.specifications;

import ru.mirea.prac22.entities.Group;
import org.springframework.data.jpa.domain.Specification;

public interface GroupSpecifications {

  static Specification<Group> startsWithPrefix(String prefix) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), prefix + "%"));
  }
}

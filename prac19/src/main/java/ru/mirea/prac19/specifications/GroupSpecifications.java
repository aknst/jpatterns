package ru.mirea.prac19.specifications;

import ru.mirea.prac19.entities.Group;
import org.springframework.data.jpa.domain.Specification;

public interface GroupSpecifications {

  static Specification<Group> startsWithPrefix(String prefix) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), prefix + "%"));
  }
}

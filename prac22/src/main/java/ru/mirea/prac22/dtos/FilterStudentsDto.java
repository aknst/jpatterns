package ru.mirea.prac22.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterStudentsDto {
  private String firstName;
  private String lastName;
  private String middleName;
  private String groupName;
  private String groupNamePrefix;
}

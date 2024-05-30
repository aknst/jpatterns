package ru.mirea.prac19.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterStudentsDto {
  private String firstName;
  private String lastName;
  private String middleName;
  private String groupName;
  private String groupNamePrefix;
}

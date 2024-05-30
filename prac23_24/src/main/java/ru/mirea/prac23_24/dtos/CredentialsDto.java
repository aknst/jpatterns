package ru.mirea.prac23_24.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredentialsDto {

  private String phone;
  private char[] password;
}

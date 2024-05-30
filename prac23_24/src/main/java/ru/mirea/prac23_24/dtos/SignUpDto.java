package ru.mirea.prac23_24.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignUpDto {

  private String nickname;
  private String phone;
  private char[] password;
}

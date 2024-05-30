package ru.mirea.prac23_24.mappers;

import ru.mirea.prac23_24.dtos.SignUpDto;
import ru.mirea.prac23_24.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toUser(SignUpDto user) {
    return User.builder().nickname(user.getNickname()).phone(user.getPhone()).build();
  }

}

package ru.mirea.prac23_24.exceptions;

public class UserAlreadySignUpException extends RuntimeException {

  public UserAlreadySignUpException(String message) {
    super(message);
  }
}

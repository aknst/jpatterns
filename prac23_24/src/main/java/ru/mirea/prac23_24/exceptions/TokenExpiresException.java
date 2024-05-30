package ru.mirea.prac23_24.exceptions;

public class TokenExpiresException extends RuntimeException {

  public TokenExpiresException(String message) {
    super(message);
  }
}

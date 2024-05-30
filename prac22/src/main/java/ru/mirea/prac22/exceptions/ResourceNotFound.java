package ru.mirea.prac22.exceptions;

public class ResourceNotFound extends RuntimeException{

  public ResourceNotFound(){
    super("Resource not found");
  }

}

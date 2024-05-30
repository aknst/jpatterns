package ru.mirea.prac19.exceptions;

public class ResourceNotFound extends RuntimeException{

  public ResourceNotFound(){
    super("Resource not found");
  }

}

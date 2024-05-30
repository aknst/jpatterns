package ru.mirea.prac20.exceptions;

public class ResourceNotFound extends RuntimeException{

  public ResourceNotFound(){
    super("Resource not found");
  }

}

package ru.mirea.prac17.exceptions;

public class ResourceNotFound extends RuntimeException{

  public ResourceNotFound(){
    super("Resource not found");
  }

}

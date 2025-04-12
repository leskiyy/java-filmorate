package ru.yandex.practicum.filmorate.exceptions;

public class DeletedUserException extends RuntimeException {
  public DeletedUserException() {
    super();
  }

  public DeletedUserException(String message) {
    super(message);
  }
}

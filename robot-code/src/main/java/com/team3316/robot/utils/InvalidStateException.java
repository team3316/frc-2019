package com.team3316.robot.utils;

 public class InvalidStateException extends Exception {
  private static final long serialVersionUID = 3316L;

  public InvalidStateException() {
    super();
  }

   public InvalidStateException(String message) {
    super(message);
  }

   public InvalidStateException(String message, Throwable cause) {
    super(message, cause);
  }

   public InvalidStateException(Throwable cause) {
    super(cause);
  }
}

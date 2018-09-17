package com.huawei.spring.exceptions;

public class AlreadyExistingException extends Exception {
  public AlreadyExistingException(Throwable cause) {
    super(cause);
  }

  public AlreadyExistingException(String message) {
    super(message);
  }

  public AlreadyExistingException(String message, Throwable cause) {
    super(message, cause);
  }
}

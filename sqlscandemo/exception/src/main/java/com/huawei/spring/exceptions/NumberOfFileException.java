package com.huawei.spring.exceptions;

public class NumberOfFileException extends Exception {
  public NumberOfFileException(String message) {
    super(message);
  }

  public NumberOfFileException(String message, Throwable cause) {
    super(message, cause);
  }

  public NumberOfFileException(Throwable cause) {
    super(cause);
  }
}

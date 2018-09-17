package com.huawei.spring.exceptions;

public class SQLFormatException extends Exception{

  public SQLFormatException(String message) {
    super(message);
  }

  public SQLFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public SQLFormatException(Throwable cause) {
    super(cause);
  }
}

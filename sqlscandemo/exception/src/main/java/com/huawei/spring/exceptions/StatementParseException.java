package com.huawei.spring.exceptions;

public class StatementParseException extends Exception{

  public StatementParseException(String message) {
    super(message);
  }

  public StatementParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public StatementParseException(Throwable cause) {
    super(cause);
  }
}

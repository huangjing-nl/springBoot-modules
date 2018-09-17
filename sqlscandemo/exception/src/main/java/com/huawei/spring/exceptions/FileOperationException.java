package com.huawei.spring.exceptions;

import java.io.IOException;

public class FileOperationException extends IOException {
  public FileOperationException(String message) {
    super(message);
  }

  public FileOperationException(Throwable cause) {
    super(cause);
  }
}

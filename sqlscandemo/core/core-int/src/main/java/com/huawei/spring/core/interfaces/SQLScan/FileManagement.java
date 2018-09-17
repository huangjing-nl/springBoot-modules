package com.huawei.spring.core.interfaces.SQLScan;

import com.huawei.spring.catalogue.SQLScan.ScanTask;
import com.huawei.spring.catalogue.SQLScan.SqlPackage;
import com.huawei.spring.catalogue.SQLScan.SqlScript;
import com.huawei.spring.exceptions.AlreadyExistingException;
import com.huawei.spring.exceptions.FileOperationException;
import com.huawei.spring.exceptions.NotAllowedException;
import com.huawei.spring.exceptions.NotFoundException;

import java.io.BufferedInputStream;
import java.util.List;

public interface FileManagement {

  ScanTask addFileToTask(BufferedInputStream bis, String fileName, long size, ScanTask task) throws FileOperationException, NotAllowedException;
  List<SqlScript> parseTaskFile(ScanTask task, long size) throws NotFoundException;
  SqlScript addSqlOnly(BufferedInputStream bis, String fileName, long size, ScanTask task) throws FileOperationException, AlreadyExistingException, NotFoundException;
  List<SqlPackage> findPackagesByTaskId(String taskId);
  List<SqlScript> findScriptsByTaskId(String taskId);
}

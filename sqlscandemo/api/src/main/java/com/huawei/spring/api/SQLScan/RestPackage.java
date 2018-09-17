package com.huawei.spring.api.SQLScan;

import com.huawei.spring.catalogue.SQLScan.ScanTask;
import com.huawei.spring.catalogue.SQLScan.SqlScript;
import com.huawei.spring.catalogue.SQLScan.UploadResult;
import com.huawei.spring.core.implement.SQLScan.util.FileUtil;
import com.huawei.spring.core.implement.SQLScan.util.ScanTaskUtil;
import com.huawei.spring.core.interfaces.SQLScan.FileManagement;
import com.huawei.spring.core.interfaces.SQLScan.ScanTaskManagement;
import com.huawei.spring.exceptions.AlreadyExistingException;
import com.huawei.spring.exceptions.NotAllowedException;
import com.huawei.spring.exceptions.NotFoundException;
import com.huawei.spring.exceptions.NumberOfFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/package")
public class RestPackage {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanTaskManagement taskManagement;
  @Autowired private FileManagement fileManagement;
  @Value("${sqlscan.upload.baseDir:/temp/sqlscan/}")
  private String baseDir;
  @Value("${task.file.max-number:1}")
  private int maxNumber;

  @RequestMapping(value = "upload/{taskId}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public UploadResult uploadPackage(
      @PathVariable("taskId") String taskId,
      @RequestParam("package") MultipartFile file) throws IOException, NotFoundException, NotAllowedException, NumberOfFileException {
    ScanTask task = taskManagement.findById(taskId);
    if (null == task) {
      //throw new NotFoundException("This task with id : " + taskId + " was not found!");
      String message = "This task with id : " + taskId + " was not found!";
      return new UploadResult("Failed", message);
    }
    int fileNumber = FileUtil.getFileNumber(task.getTaskPath());
    if (fileNumber >= maxNumber) {
      //throw new NumberOfFileException("File upload exceeds the maximum limit!");
      String message = "File upload exceeds the maximum limit!";
      return new UploadResult("Failed", message);
    }
    String filename = file.getOriginalFilename();
    long sizeKB = (long) Math.ceil((double) file.getSize() / 1024);
    BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
    ScanTask toTask = fileManagement.addFileToTask(bis, filename, sizeKB, task);
    List<SqlScript> sqlScripts = fileManagement.parseTaskFile(toTask, sizeKB);
    String message = filename + " upload success!";
    return new UploadResult("Success", message, sqlScripts);
  }


  @RequestMapping(value = "upload/onlySqlScript", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public SqlScript uploadSqlOnly(
      @RequestParam("file") MultipartFile file,
      @RequestParam String baseType,
      @RequestParam String business
  ) throws IOException, AlreadyExistingException, NotFoundException {
    if ((null == business || "".equals(business)) || (null == baseType || "".equals(baseType))) {
      throw new NotFoundException("Please double check the following parameters (business / baseType) is empty!");
    }
    if (null == baseDir || baseDir.equals("")) {
      baseDir = "/temp/sqlscan/";
    }
    String filename = file.getOriginalFilename();
    ScanTask task = ScanTaskUtil.getTask(baseType, business, baseDir, filename);
    long sizeKB = (long) Math.ceil((double) file.getSize() / 1024);
    BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
    return fileManagement.addSqlOnly(bis, filename, sizeKB, task);
  }

  @RequestMapping(value = "upload/taskFile/version", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ScanTask uploadTaskFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam String baseType,
      @RequestParam String business,
      @RequestParam String version
  ) throws IOException, AlreadyExistingException, NotFoundException, NotAllowedException {
    if ((null == business || "".equals(business)) || (null == baseType || "".equals(baseType))) {
      throw new NotFoundException("Please double check the following parameters (business / baseType) is empty!");
    }
    if (null == baseDir || baseDir.equals("")) {
      baseDir = "/temp/sqlscan/";
    }
    String filename = file.getOriginalFilename();
    ScanTask task = ScanTaskUtil.getTask(baseType, business, baseDir, filename);
    task.setVersion(version);
    task = taskManagement.addScanTask(task);
    long sizeKB = (long) Math.ceil((double) file.getSize() / 1024);
    BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
    ScanTask toTask = fileManagement.addFileToTask(bis, filename, sizeKB, task);
    fileManagement.parseTaskFile(toTask, sizeKB);
    return toTask;
  }

}

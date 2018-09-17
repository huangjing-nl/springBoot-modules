package com.huawei.spring.api.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.core.implement.SQLScan.util.ScanTaskUtil;
import com.huawei.spring.core.interfaces.SQLScan.FileManagement;
import com.huawei.spring.core.interfaces.SQLScan.SQLScanManagement;
import com.huawei.spring.core.interfaces.SQLScan.ScanTaskManagement;
import com.huawei.spring.exceptions.*;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@RestController
@RequestMapping(value = "api/v1/scan")
public class RestSQLScanner {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired
  private SQLScanManagement scanManagement;
  @Autowired
  private FileManagement fileManagement;
  @Autowired
  private ScanTaskManagement taskManagement;
  @Value("${sqlscan.upload.baseDir:/temp/sqlscan/}")
  private String baseDir;

  @RequestMapping(
      value = "/launch/{taskId}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ScanResult> getResultsByTaskId(@PathVariable("taskId") String taskId) throws NotFoundException, NotAllowedException, FileOperationException {
    List<SqlScript> scriptList = fileManagement.findScriptsByTaskId(taskId);
    return scanManagement.getResultByFile(scriptList);
  }

  @RequestMapping(
      value = "/onlyScript/parse",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ScanResult> getResultsByOnlySql(@RequestBody @Valid SqlScript script) throws NotFoundException, NotAllowedException, FileOperationException {
    //List<SQLBlock> sqlBlocks = scanManagement.cutBlockByFile(sqlScript);
    List<SqlScript> scripts = new ArrayList<>();
    log.info("script:-->" + script.toString());
    scripts.add(script);
    return scanManagement.getResultByFile(scripts);
  }

  @RequestMapping(
      value = "/string/parse",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ScanResult> getResultsByStringSql(
      @RequestParam String business,
      @RequestParam String baseType,
      @RequestParam String sqlString
  ) throws IOException, NotFoundException, AlreadyExistingException, StatementParseException, NotAllowedException {

    //List<SQLBlock> sqlBlocks = scanManagement.cutBlockByString(business, baseType, sqlString);
    if ((null == business || "".equals(business)) || (null == baseType || "".equals(baseType)) || (null == sqlString || "".equals(sqlString))) {
      throw new NotFoundException("Please double check the following parameters (business / baseType / sqlString) is empty!");
    }
    log.info("before decode sqlString--> " + sqlString + "   before decode business--> " + business);
    sqlString = URLDecoder.decode(new String(Base64.getDecoder().decode(sqlString.getBytes())), "UTF-8");
    business = URLDecoder.decode(new String(Base64.getDecoder().decode(business.getBytes())), "UTF-8");
    log.info("after decode sqlString--> " + sqlString + "   after decode business--> " + business);
    ScanTask task = ScanTaskUtil.getTask(baseType, business, baseDir, "string");
    task.setSqlString(sqlString);
    return scanManagement.getResultByString(task);
  }

  @RequestMapping(
      value = "/string/parse/version",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ScanResult> getResultsByStringSql(
      @RequestParam String business,
      @RequestParam String baseType,
      @RequestParam String version,
      @RequestParam String sqlString
  ) throws IOException, NotFoundException, AlreadyExistingException, StatementParseException, NotAllowedException {
    if ((null == business || "".equals(business)) || (null == baseType || "".equals(baseType)) || (null == sqlString || "".equals(sqlString))) {
      throw new NotFoundException("Please double check the following parameters (business / baseType / sqlString) is empty!");
    }
    log.info("before decode sqlString--> " + sqlString);
    sqlString = URLDecoder.decode(new String(Base64.getDecoder().decode(sqlString.getBytes())), "UTF-8");
    log.info("after decode sqlString--> " + sqlString);
    ScanTask task = ScanTaskUtil.getTask(baseType, business, baseDir, "string");
    task.setSqlString(sqlString);
    task.setVersion(version);
    return scanManagement.getResultByString(task);
  }

  @RequestMapping(value = "/upload/launch", method = RequestMethod.POST)
  public List<ScanResult> getResultsByUpload(
      @RequestParam("file") MultipartFile file,
      @RequestParam String business,
      @RequestParam String baseType
  ) throws IOException, NotFoundException, AlreadyExistingException, NotAllowedException {
    String filename = file.getOriginalFilename();
    long sizeKB = (long) Math.ceil((double) file.getSize() / 1024);
    BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
    ScanTask task = ScanTaskUtil.getTask(baseType, business, baseDir, filename);
    ScanTask savedTask = taskManagement.addScanTask(task);
    if (null == savedTask) {
      throw new NotFoundException("This task with id : " + task.getId() + " was not found!");
    }
    ScanTask toTask = fileManagement.addFileToTask(bis, filename, sizeKB, task);
    List<SqlScript> sqlScripts = fileManagement.parseTaskFile(toTask, sizeKB);
    return scanManagement.getResultByFile(sqlScripts);
  }

  @RequestMapping(value = "/jenkins/{taskId}", method = RequestMethod.POST)
  public List<ScanResult> getResultsByUpload(
      @PathVariable("taskId") String taskId,
      @RequestParam String filename,
      @RequestParam String business,
      @RequestParam String baseType
  ) throws IOException, NotFoundException, AlreadyExistingException, NotAllowedException {
    if ((null == taskId || "".equals(taskId))
        || (null == filename || "".equals(filename))
        || (null == business || "".equals(business))
        || (null == baseType || "".equals(baseType))) {
      throw new NotFoundException("Incorrect parameter transfer, please check carefully!");
    }
    business = URLDecoder.decode(business, "UTF-8");
    ScanTask task = ScanTaskUtil.getTask(baseType, business, baseDir, filename);
    task.setId(taskId);
    log.debug("business--->" + business);
    task.setTaskPath(baseDir + business + "/" + taskId);
    ScanTask savedTask = taskManagement.addScanTask(task);
    if (null == savedTask) {
      throw new NotFoundException("This task with id : " + task.getId() + " was not found!");
    }
    task.setFileName(filename);
    List<SqlScript> sqlScripts = fileManagement.parseTaskFile(task, 0);
    return scanManagement.getResultByFile(sqlScripts);
  }

}

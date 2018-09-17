package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.ScanTask;
import com.huawei.spring.catalogue.SQLScan.SqlPackage;
import com.huawei.spring.catalogue.SQLScan.SqlScript;
import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.core.implement.SQLScan.util.FileUtil;
import com.huawei.spring.core.implement.SQLScan.util.ScanTaskUtil;
import com.huawei.spring.core.interfaces.SQLScan.FileManagement;
import com.huawei.spring.core.interfaces.SQLScan.ScanTaskManagement;
import com.huawei.spring.exceptions.AlreadyExistingException;
import com.huawei.spring.exceptions.FileOperationException;
import com.huawei.spring.exceptions.NotAllowedException;
import com.huawei.spring.exceptions.NotFoundException;
import com.huawei.spring.mappers.SQLScanMapper.SqlPackageMapper;
import com.huawei.spring.mappers.SQLScanMapper.SqlScriptMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileManager implements FileManagement {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanTaskManagement taskManagement;
  @Autowired private SqlPackageMapper sqlPackageMapper;
  @Autowired private SqlScriptMapper sqlScriptMapper;
  @Value("${sqlscan.upload.baseDir:/temp/sqlscan/}")
  private String baseDir;

  @Override
  public ScanTask addFileToTask(BufferedInputStream bis, String fileName, long size, ScanTask task) throws FileOperationException, NotAllowedException {
    if (null == baseDir || baseDir.equals("")) {
      baseDir = "/temp/sqlscan/";
    }
    String taskFolder = task.getTaskPath();
    File fileFolder = new File(taskFolder);
    if (!fileFolder.exists()) {
      if (!fileFolder.mkdirs()) {
        log.error("fileFolder create failed!");
        throw new FileOperationException("File directory creation failed!");
      }
    }
    String filePath = taskFolder + File.separator + fileName;
    this.addFile(bis, filePath);
    log.info(fileName + "upload success!");
    //TODO 前台为了显示文件名，后期可能不需要
    task.setFileName(fileName);
    return taskManagement.updateTask(task);
  }

  @Override
  public List<SqlScript> parseTaskFile(ScanTask task, long size) throws NotFoundException {
    List<SqlScript> sqlScripts = new ArrayList<>();
    String fileName = task.getFileName();
    String filePath = task.getTaskPath() + File.separator + fileName;
    log.debug("TaskPath--->" + task.getTaskPath());
    log.debug("filePath--->" + filePath);
    if (fileName.endsWith(".sql")) {
      SqlScript sqlScript = new SqlScript();
      sqlScript.setId(IdGenerator.createUUID());
      sqlScript.setName(fileName);
      sqlScript.setVersion(task.getVersion());
      sqlScript.setSize(size);
      sqlScript.setTaskId(task.getId());
      sqlScript.setBaseType("TODO");
      sqlScript.setFilePath(filePath);
      sqlScript.setUploaded(new Date());
      sqlScriptMapper.addSqlScript(sqlScript);
      //TODO findById
      sqlScripts.add(sqlScript);
    } else if (fileName.endsWith(".zip")) {
      SqlPackage sqlPackage = getSqlPackage(fileName, size, filePath, "ZIP", task);
      sqlPackageMapper.addSqlPackage(sqlPackage);
      String targetPath = filePath.substring(0, filePath.indexOf(".zip"));
      FileUtil.unzip(filePath, targetPath);
      List<SqlScript> scripts = FileUtil.getAllSqlScripts(FileUtil.getAllSqlScriptFiles(targetPath, new ArrayList<>()), sqlPackage);
      if (null == scripts || scripts.isEmpty()) {
        throw new NotFoundException("Compression package sql script not found, please check your compressed package!");
      }
      sqlScriptMapper.addSqlScriptByBatch(scripts);
      // TODO findByTaskId
      sqlScripts.addAll(scripts);
    } else if (fileName.endsWith(".tar")) {
      SqlPackage sqlPackage = getSqlPackage(fileName, size, filePath, "TAR", task);
      sqlPackageMapper.addSqlPackage(sqlPackage);
      String targetPath = filePath.substring(0, filePath.indexOf(".tar"));
      FileUtil.untar(filePath, targetPath);
      List<SqlScript> scripts = FileUtil.getAllSqlScripts(FileUtil.getAllSqlScriptFiles(targetPath, new ArrayList<>()), sqlPackage);
      if (null == scripts || scripts.isEmpty()) {
        throw new NotFoundException("Compression package sql script not found, please check your compressed package!");
      }
      sqlScriptMapper.addSqlScriptByBatch(scripts);
      // TODO findByTaskId
      sqlScripts.addAll(scripts);
    } else if (fileName.endsWith(".rar")) {
      SqlPackage sqlPackage = getSqlPackage(fileName, size, filePath,"RAR", task);
      sqlPackageMapper.addSqlPackage(sqlPackage);
      String targetPath = filePath.substring(0, filePath.indexOf(".rar"));
      FileUtil.unrar(filePath, targetPath);
      List<SqlScript> scripts = FileUtil.getAllSqlScripts(FileUtil.getAllSqlScriptFiles(targetPath, new ArrayList<>()), sqlPackage);
      if (null == scripts || scripts.isEmpty()) {
        throw new NotFoundException("Compression package sql script not found, please check your compressed package!");
      }
      sqlScriptMapper.addSqlScriptByBatch(scripts);
      // TODO findByTaskId
      sqlScripts.addAll(scripts);
    } else {
      throw new NotFoundException("No matching file format was found!");
    }
    return sqlScripts;
  }

  @Override
  public SqlScript addSqlOnly(BufferedInputStream bis, String fileName, long size, ScanTask task) throws FileOperationException, AlreadyExistingException, NotFoundException {
    File fileFolder = new File(task.getTaskPath());
    if (!fileFolder.exists()) {
      if (!fileFolder.mkdirs()) {
        log.error("fileFolder create failed!");
        throw new FileOperationException("File directory creation failed!");
      }
    }
    String filePath = task.getTaskPath() + "/" + fileName;
    this.addFile(bis, filePath);
    log.info(fileName + " upload success!");
    task.setFileName(fileName);
    ScanTask savedTask = taskManagement.addScanTask(task);
    if (null == savedTask) {
      throw new NotFoundException("Task creation failed!");
    }
    SqlScript sqlScript = new SqlScript(fileName, savedTask.getVersion(), size, filePath, task.getBaseType(), savedTask.getId(), new Date());
    sqlScriptMapper.addSqlScript(sqlScript);
    return sqlScript;
  }

  @Override
  public List<SqlPackage> findPackagesByTaskId(String taskId) {
    return sqlPackageMapper.findByTaskId(taskId);
  }

  @Override
  public List<SqlScript> findScriptsByTaskId(String taskId) {
    return sqlScriptMapper.findBytaskId(taskId);
  }

  private void addFile(BufferedInputStream bis, String filePath) throws FileOperationException {
    //TODO Check if the file exits according to the product/sqlPackage/sqlScript conditions
    BufferedOutputStream bos = null;
    OutputStream os = null;
    try {
      File file = new File(filePath);
      os = new FileOutputStream(file);
      bos = new BufferedOutputStream(os);
      byte[] b = new byte[4096];
      int n = 0;
      while ((n = bis.read(b)) != -1) {
        bos.write(b, 0, n);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (bos != null) {
        try {
          bos.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          bos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      try {
        bis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private SqlPackage getSqlPackage(String fileName, long size, String filePath, String type, ScanTask task) {
    SqlPackage sqlPackage = new SqlPackage();
    sqlPackage.setId(IdGenerator.createUUID());
    sqlPackage.setName(fileName);
    sqlPackage.setVersion(task.getVersion());
    sqlPackage.setSize(size);
    sqlPackage.setPath(filePath);
    sqlPackage.setBaseType(task.getBaseType());
    sqlPackage.setType(type);
    sqlPackage.setTaskId(task.getId());
    sqlPackage.setCreated(new Date());
    return sqlPackage;
  }

}

package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.PageResult;
import com.huawei.spring.catalogue.SQLScan.Pagination;
import com.huawei.spring.catalogue.SQLScan.ProductInfo;
import com.huawei.spring.catalogue.SQLScan.ScanTask;
import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.core.implement.SQLScan.util.FileUtil;
import com.huawei.spring.core.interfaces.SQLScan.ScanTaskManagement;
import com.huawei.spring.exceptions.AlreadyExistingException;
import com.huawei.spring.exceptions.FileOperationException;
import com.huawei.spring.exceptions.NotAllowedException;
import com.huawei.spring.mappers.SQLScanMapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class ScanTaskManager implements ScanTaskManagement {

  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanTaskMapper scanTaskMapper;
  @Autowired private SqlPackageMapper sqlPackageMapper;
  @Autowired private SqlScriptMapper sqlScriptMapper;
  @Autowired private ScanResultMapper scanResultMapper;

  @Override
  public ScanTask addScanTask(ScanTask task) throws AlreadyExistingException {
    if (null != scanTaskMapper.findById(task.getId()) && null != scanTaskMapper.findByNameAndBusinessAndBaseType(task)) {
      throw new AlreadyExistingException("This task already exists!");
    }
    scanTaskMapper.addTask(task);
    return scanTaskMapper.findById(task.getId());
  }

  @Override
  public ScanTask findById(String id) {
    return scanTaskMapper.findById(id);
  }

  @Override
  public ScanTask updateTask(ScanTask task) throws NotAllowedException {
    String original = scanTaskMapper.findById(task.getId()).getStatus();
    if (original.equals("NULL")) {
      scanTaskMapper.update(task);
    } else {
      throw new NotAllowedException("The task has been executed, it is not allowed to modify it!");
    }
    return scanTaskMapper.findById(task.getId());
  }

  @Override
  public List<ScanTask> findAll() {
    return scanTaskMapper.findTasks();
  }

  @Override
  public PageResult findByPage(Pagination page) {
    PageResult pageResult = new PageResult();
    int total = 0;
    List<ScanTask> tasks = null;
    if (page.getSearch() == null || "".equals(page.getSearch())) {
      total = scanTaskMapper.getCounts();
      tasks = scanTaskMapper.findByPage(page);
    } else {
      page.setSearch("%" + page.getSearch() + "%");
      total = scanTaskMapper.getCountsBySearch(page);
      tasks = scanTaskMapper.findByPageAndSearch(page);
    }
    pageResult.setTotal(total);
    pageResult.setRows(tasks);
    return pageResult;
  }

  @Override
  public void delete(String id) throws FileOperationException {
    ScanTask task = scanTaskMapper.findById(id);
    if (null != task) {
      File taskPath = new File(task.getTaskPath());
      if (taskPath.exists()) {
        boolean isRemove = FileUtil.deleteDir(taskPath);
        if (!isRemove) {
          throw new FileOperationException("Task directory deletion failed!");
        }
        this.cleanTask(id);
      } else {
        this.cleanTask(id);
      }
    }
  }

  private void cleanTask(String taskId) {
    scanResultMapper.deleteByTaskId(taskId);
    sqlScriptMapper.deleteByTaskId(taskId);
    sqlPackageMapper.deleteByTaskId(taskId);
    scanTaskMapper.deleteById(taskId);
  }
}

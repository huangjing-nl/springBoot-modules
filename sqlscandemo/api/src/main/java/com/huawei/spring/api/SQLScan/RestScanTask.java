package com.huawei.spring.api.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.core.implement.SQLScan.util.ExcelUtil;
import com.huawei.spring.core.interfaces.SQLScan.ScanResultManagement;
import com.huawei.spring.core.interfaces.SQLScan.ScanTaskManagement;
import com.huawei.spring.exceptions.AlreadyExistingException;
import com.huawei.spring.exceptions.FileOperationException;
import com.huawei.spring.exceptions.NotAllowedException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/task")
public class RestScanTask {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanTaskManagement scanTaskManagement;
  @Autowired private ScanResultManagement resultManagement;
  @Value("${sqlscan.upload.baseDir:/temp/sqlscan/}")
  private String basePath;
  @Value("${sqlscan.result.excel.prefix:scan_result}")
  private String excelPrefix;

  @RequestMapping(
      value = "/add",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ScanTask createTask(@RequestBody @Valid ScanTask task) throws AlreadyExistingException {
    task.setId(IdGenerator.createUUID());
    task.setTaskPath(basePath + task.getBusiness() + "/" + task.getId());
    task.setCreated(new Date());
    return scanTaskManagement.addScanTask(task);
  }

  @RequestMapping(
      value = "/find/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ScanTask findById(@PathVariable("id") String id) {
    return scanTaskManagement.findById(id);
  }

  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public List<ScanTask> findAllTasks() {
    return scanTaskManagement.findAll();
  }

  @RequestMapping(
      value = "/find/page",
      method = RequestMethod.POST,
      /*consumes = MediaType.APPLICATION_JSON_VALUE,*/
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public PageResult findByPage(/*@RequestBody */@Valid Pagination pagination) {
    log.info(pagination.toString());
    return scanTaskManagement.findByPage(pagination);
  }

  @RequestMapping(value = "/edit",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ScanTask update(@RequestBody @Valid ScanTask task) throws NotAllowedException {
    return scanTaskManagement.updateTask(task);
  }

  @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
  public void deleteById(@PathVariable("id") String id) throws FileOperationException {
    scanTaskManagement.delete(id);
  }

  @RequestMapping(
      value = "result/{taskId}",
      method = RequestMethod.GET,
      /*consumes = MediaType.APPLICATION_JSON_VALUE,*/
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ScanResult> findByPageAndTaskId(
      @PathVariable("taskId") String taskId/*,
      @RequestBody @Valid Pagination page*/) {
    //page.setTaskId(taskId);
    //return resultManagement.findByPageAndTaskId(page);
    log.info("------------taskId--->" + taskId);
    return resultManagement.findByTaskId(taskId);
  }

  @RequestMapping(
      value = "result/excel/{taskId}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public void export(HttpServletResponse response, @PathVariable("taskId") String taskId) throws UnsupportedEncodingException {
    List<ScanResult> results = resultManagement.findByTaskId(taskId);
    if (null == results || results.isEmpty()) {
      log.error("No data can be directed to the target Excel!");
    } else {
      SXSSFWorkbook workBook = ExcelUtil.getWorkBook(results);
      String format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
      String name = excelPrefix + "_" + format + ".xlsx";
      // response.setHeader("Accept-Ranges","bytes");
      // response.setHeader("ETag", "");
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition","attachment;filename=" + new String(name.getBytes("UTF-8"), "iso-8859-1"));
      OutputStream os = null;
      try {
        os = response.getOutputStream();
        workBook.write(os);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (null != os) {
          try {
            os.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

}

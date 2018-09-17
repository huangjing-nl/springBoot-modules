package com.huawei.spring.api.SQLScan;

import com.huawei.spring.catalogue.SQLScan.ResultReport;
import com.huawei.spring.core.interfaces.SQLScan.ReportManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/vi/report")
public class RestSQLScanReport {

  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ReportManagement reportManagement;
  @RequestMapping(
      value = "produce",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResultReport produceReport(@RequestBody @Valid ResultReport report) {
    return reportManagement.backReport(report);
  }
}

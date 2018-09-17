package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.core.interfaces.SQLScan.RelationManagement;
import com.huawei.spring.core.interfaces.SQLScan.ReportManagement;
import com.huawei.spring.mappers.SQLScanMapper.ScanResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportManager implements ReportManagement {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanResultMapper resultMapper;
  @Autowired private RelationManagement relationManagement;

  @Override
  public ResultReport backReport(ResultReport report) {
    List<Business> businesses = relationManagement.findAllBusiness();
    if (businesses.size() > 0) {
      List<String> list = new ArrayList<>();
      for (Business b : businesses) {
        list.add(b.getName());
      }
      report.setBusinesses(list);
      if (null == report.getBusiness() || "".equals(report.getBusiness())) {  //初始化
        report.setBusiness(list.get(0));
        report = getReport(report);
      } else {  //非初始化
        report = getReport(report);
      }
    } else {
      log.warn("初始化失败，没有查询到业务！");
    }
    return report;
  }

  private List<ReportMap> combine(List<ReportMap> reportMaps, Long countByTime) {
    for (ReportMap reportMap : reportMaps) {
      reportMap.calculate(countByTime);
    }
    return reportMaps;
  }

  private ResultReport getReport(ResultReport report) {
    long countByTime = resultMapper.getCountByTime(report);
    List<ReportMap> businessGroup = this.combine(resultMapper.getBusinessGroup(report), countByTime);
    BusinessReport businessReport = new BusinessReport("业务类型SQL问题统计", "业务", businessGroup);
    businessReport.setMap();
    report.setBusinessReport(businessReport);
    long countByTimeAndBusiness = resultMapper.getCountByTimeAndBusiness(report);
    List<ReportMap> ruleGroup = this.combine(resultMapper.getRuleGroup(report), countByTimeAndBusiness);
    RuleReport ruleReport = new RuleReport("SQL问题统计", ruleGroup);
    RuleReport.XAxis xAxis = ruleReport.new XAxis("规则");
    xAxis.calculate();
    ruleReport.setxAxis(xAxis);
    RuleReport.YAxis yAxis = ruleReport.new YAxis("问题数");
    yAxis.calculate();
    ruleReport.setyAxis(yAxis);
    report.setRuleReport(ruleReport);
    List<ReportMap> topGroup = this.combine(resultMapper.getTopGroup(report), countByTime);
    ResultTop resultTop = new ResultTop("排行榜", topGroup);
    ResultTop.XAxisTop xAxisTop = resultTop.new XAxisTop("问题数");
    ResultTop.YAxisTop yAxisTop = resultTop.new YAxisTop("规则");
    xAxisTop.calculate();
    yAxisTop.calculate();
    resultTop.setxAxis(xAxisTop);
    resultTop.setyAxis(yAxisTop);
    report.setResultTop(resultTop);
    return report;
  }

}

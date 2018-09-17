package com.huawei.spring.core.interfaces.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.exceptions.AlreadyExistingException;

import java.util.List;

public interface RuleManagement {

  ScanRule findById(String id);
  List<ScanRule> findAll();
  ScanRule createRule(ScanRule rule);
  ScanRule updateRule(ScanRule rule);
  void deleteRule(String id);
  PageResult findByPage(Pagination page);
  Oper_Rule_Level_Business addOrlb(Oper_Rule_Level_Business orlb);
  List<ScanRule> findByCondition(Oper_Rule_Level_Business operRuleLevelBusiness);
  List<ScanRule> findByUnConfig(Oper_Rule_Level_Business operRuleLevelBusiness);
  List<RuleConfigResult> configRule(List<Oper_Rule_Level_Business> operRuleLevelBusinesses) throws AlreadyExistingException;
}

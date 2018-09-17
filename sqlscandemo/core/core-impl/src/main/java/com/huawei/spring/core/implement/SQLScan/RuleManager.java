package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.core.interfaces.SQLScan.RelationManagement;
import com.huawei.spring.core.interfaces.SQLScan.RuleManagement;
import com.huawei.spring.exceptions.AlreadyExistingException;
import com.huawei.spring.mappers.SQLScanMapper.OperTypeMapper;
import com.huawei.spring.mappers.SQLScanMapper.RelationMapper;
import com.huawei.spring.mappers.SQLScanMapper.ScanRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RuleManager implements RuleManagement {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanRuleMapper scanRuleMapper;
  @Autowired private OperTypeMapper operTypeMapper;
  @Autowired private RelationManagement relationManagement;

  @Override
  public ScanRule findById(String id) {
    return scanRuleMapper.findById(id);
  }

  @Override
  public List<ScanRule> findAll() {
    return scanRuleMapper.findAll();
  }

  @Override
  public ScanRule createRule(ScanRule rule) {
    rule.setId(IdGenerator.createUUID());
    scanRuleMapper.addRule(rule);
    return scanRuleMapper.findById(rule.getId());
  }

  @Override
  public ScanRule updateRule(ScanRule rule) {
    scanRuleMapper.update(rule);
    return scanRuleMapper.findById(rule.getId());
  }

  @Override
  public void deleteRule(String id) {
    relationManagement.deleteORLBByRuleId(id);
    scanRuleMapper.delete(id);
  }

  @Override
  public PageResult findByPage(Pagination page) {
    PageResult pageResult = new PageResult();
    int total = 0;
    List<ScanRule> rules = null;
    if (page.getSearch() == null || "".equals(page.getSearch())) {
      total = scanRuleMapper.getCounts();
      rules = scanRuleMapper.findByPage(page);
    } else {
      page.setSearch("%" + page.getSearch() + "%");
      total = scanRuleMapper.getCountsBySearch(page);
      rules = scanRuleMapper.findByPageAndSearch(page);
    }
    pageResult.setTotal(total);
    pageResult.setRows(rules);
    return pageResult;
  }

  @Override
  public Oper_Rule_Level_Business addOrlb(Oper_Rule_Level_Business orlb) {
    return relationManagement.addOrlb(orlb);
  }

  @Override
  public List<ScanRule> findByCondition(Oper_Rule_Level_Business operRuleLevelBusiness) {
    //OperType operType = operTypeMapper.findByName(operRuleLevelBusiness.getOperName());
    Business business = relationManagement.findBusinessById(operRuleLevelBusiness.getBusiness().getId());
    return scanRuleMapper.findByOperAndBusiness(operRuleLevelBusiness.getOperType().getName(), business.getName());
  }

  @Override
  public List<ScanRule> findByUnConfig(Oper_Rule_Level_Business operRuleLevelBusiness) {
    Business business = relationManagement.findBusinessById(operRuleLevelBusiness.getBusiness().getId());
    List<ScanRule> configedRules = scanRuleMapper.findByOperAndBusiness(operRuleLevelBusiness.getOperType().getName(), business.getId());
    //stream().collect(Collectors.toMap(OperType::getName, Function.identity()));
    Map<String, ScanRule> configedMap = configedRules.stream().collect(Collectors.toMap(ScanRule::getId, Function.identity()));
    List<ScanRule> allRules = this.findAll();
    Map<String, ScanRule> allMap = allRules.stream().collect(Collectors.toMap(ScanRule::getId, Function.identity()));
    for (String key : configedMap.keySet()) {
      allMap.remove(key);
    }
    Collection<ScanRule> values = allMap.values();
    return new ArrayList<>(values);
  }


  @Override
  public List<RuleConfigResult> configRule(List<Oper_Rule_Level_Business> operRuleLevelBusinesses) throws AlreadyExistingException {
    List<RuleConfigResult> results = new ArrayList<>();
    if (!operRuleLevelBusinesses.isEmpty()) {
      for (Oper_Rule_Level_Business orlb : operRuleLevelBusinesses) {
        if (null != relationManagement.findOrlbById(orlb.getId())) {
          //throw new AlreadyExistingException("This rule configuration already exists!");
          String message = "This rule with id : " + orlb.getRule().getId() + " configuration already exists!";
          RuleConfigResult failedResult = new RuleConfigResult("Failed", message, orlb);
          results.add(failedResult);
        } else {
          Oper_Rule_Level_Business savedOrlb = relationManagement.addOrlb(orlb);
          String message = "This rule with id : " + orlb.getRule().getId() + " is configured successfully!";
          RuleConfigResult successResult = new RuleConfigResult("Success", message, savedOrlb);
          results.add(successResult);
        }
      }
    }
    return results;
  }
}

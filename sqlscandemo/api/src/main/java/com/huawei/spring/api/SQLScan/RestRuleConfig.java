package com.huawei.spring.api.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.core.interfaces.SQLScan.RelationManagement;
import com.huawei.spring.core.interfaces.SQLScan.RuleManagement;
import com.huawei.spring.exceptions.AlreadyExistingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/rule")
public class RestRuleConfig {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private RuleManagement ruleManagement;
  @Autowired private RelationManagement relationManagement;

  @RequestMapping(
      value = "/find/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ScanRule findById(@PathVariable("id") String id) {
    return ruleManagement.findById(id);
  }

  @RequestMapping(
      value = "/find/all",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ScanRule> findRules() {
    return ruleManagement.findAll();
  }

  @RequestMapping(
      value = "/find/page",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public PageResult findRulesByPage(@Valid Pagination page) {
    return ruleManagement.findByPage(page);
  }

  @RequestMapping(
      value = "/find/condition",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ScanRule> findRulesByCondition(@RequestBody @Valid Oper_Rule_Level_Business operRuleLevelBusiness) {
    return ruleManagement.findByCondition(operRuleLevelBusiness);
  }

  @RequestMapping(
      value = "/find/unused",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ScanRule> findUnusedRules(@RequestBody @Valid Oper_Rule_Level_Business operRuleLevelBusiness) {
    log.info("operRuleLevelBusiness---> " + operRuleLevelBusiness.toString());
    return ruleManagement.findByUnConfig(operRuleLevelBusiness);
  }

  @RequestMapping(
      value = "/create",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ScanRule addRule(@RequestBody @Valid ScanRule scanRule) {
    return ruleManagement.createRule(scanRule);
  }

  @RequestMapping(
      value = "/update",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ScanRule updateRule(@RequestBody @Valid ScanRule scanRule) {
    return ruleManagement.updateRule(scanRule);
  }

  @RequestMapping(
      value = "/delete/{id}",
      method = RequestMethod.GET
  )
  public void updateRule(@PathVariable("id") String id) {
    ruleManagement.deleteRule(id);
  }

  @RequestMapping(
      value = "/config",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<RuleConfigResult> ruleConfig(@RequestBody @Valid RuleConfig ruleConfig) throws AlreadyExistingException {
    List<Oper_Rule_Level_Business> orlbs = new ArrayList<>();
    Set<RuleRelationLevel> ruleRelationLevels = ruleConfig.getRuleRelationLevels();
    if (null != ruleRelationLevels && !ruleRelationLevels.isEmpty()) {
      for (RuleRelationLevel rrl : ruleRelationLevels) {
        Oper_Rule_Level_Business orlb = new Oper_Rule_Level_Business(new Business(ruleConfig.getBusinessId(),null), new OperType(null, ruleConfig.getOperName()), new ScanRule(rrl.getRuleId(), null), new RuleLevel(rrl.getLevelId(), null));
        orlbs.add(orlb);
      }
    }
    return ruleManagement.configRule(orlbs);
  }

  @RequestMapping(
      value = "/config/edit",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Oper_Rule_Level_Business ruleConfigEdit(@RequestBody @Valid Oper_Rule_Level_Business operRuleLevelBusiness) throws AlreadyExistingException {
    return relationManagement.updateOrlb(operRuleLevelBusiness);
  }

  @RequestMapping(
      value = "/find/config/page",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public PageResult findOrlbsByPage(/*@RequestBody */@Valid Pagination page) {
    if ((page.getBusiness() == null || "".equals(page.getBusiness())) && (page.getOperType() == null || "".equals(page.getOperType()))) {
      return relationManagement.findOrlbsByPage(page);
    } else if ((page.getBusiness() != null && !"".equals(page.getBusiness())) && (page.getOperType() == null || "".equals(page.getOperType()))) {
      page.setBusiness("%" + page.getBusiness() + "%");
      return relationManagement.findOrlbsByPageAndBusiness(page);
    } else if ((page.getBusiness() == null || "".equals(page.getBusiness())) && (page.getOperType() != null && !"".equals(page.getOperType()))) {
      page.setOperType("%" + page.getOperType() + "%");
      return relationManagement.findOrlbsByPageAndOperType(page);
    } else if ((page.getBusiness() != null && !"".equals(page.getBusiness())) && (page.getOperType() != null && !"".equals(page.getOperType()))) {
      page.setBusiness("%" + page.getBusiness() + "%");
      page.setOperType("%" + page.getOperType() + "%");
      return relationManagement.findOrlbsByPageAndBusinessAndOperType(page);
    }
    return new PageResult();
  }

  @RequestMapping(
      value = "/config/delete",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public void ruleConfigDelete(@RequestBody @Valid List<String> ids) {
    //TODO mybaties batch delete
    for (String id : ids) {
      relationManagement.deleteORLBById(id);
    }
  }
}

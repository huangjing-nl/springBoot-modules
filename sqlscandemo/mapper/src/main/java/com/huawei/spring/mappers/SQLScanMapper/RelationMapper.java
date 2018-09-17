package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RelationMapper {
    List<RuleLevel> findAllLevel();
    List<Business> findAllBusiness();
    List<BaseType> findAllBaseType();
    RuleLevel findLevelById(String id);
    Business findBusinessById(String id);
    BaseType findBaseTypeById(String id);
    Oper_Rule_Level_Business findORLBById(String id);
    int getOrlbCounts();
    int getOrlbCountsByBusiness(Pagination page);
    int getOrlbCountsByOperType(Pagination page);
    int getOrlbCountsByBusinessAndOper(Pagination page);
    List<Oper_Rule_Level_Business> findOrlbsByPage(Pagination page);
    List<Oper_Rule_Level_Business> findOrlbsByPageAndBusiness(Pagination page);
    List<Oper_Rule_Level_Business> findOrlbsByPageAndOperType(Pagination page);
    List<Oper_Rule_Level_Business> findOrlbsByPageAndBusinessAndOperType(Pagination page);
    int addORLB(Oper_Rule_Level_Business operRuleLevelBusiness);
    void updateOrlb(Oper_Rule_Level_Business operRuleLevelBusiness);
    void deleteORLBById(String id);
    void deleteORLBByRuleId(String ruleId);
}

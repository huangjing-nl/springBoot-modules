package com.huawei.spring.core.interfaces.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;

import java.util.List;
import java.util.Set;

/**
 * Created by wWX511772 on 2017/12/8.
 */
public interface RelationManagement {
    List<RuleLevel> findAllLevel();
    List<Business> findAllBusiness();
    List<BaseType> findAllBaseType();
    RuleLevel findLevelById(String id);
    Business findBusinessById(String id);
    BaseType findBaseTypeById(String id);
    Set<OperType> findOperTypes();
    Oper_Rule_Level_Business addOrlb(Oper_Rule_Level_Business orlb);
    Oper_Rule_Level_Business updateOrlb(Oper_Rule_Level_Business orlb);
    void deleteORLBById(String id);
    void deleteORLBByRuleId(String ruleId);
    Oper_Rule_Level_Business findOrlbById(String id);
    PageResult findOrlbsByPage(Pagination page);
    PageResult findOrlbsByPageAndBusiness(Pagination page);
    PageResult findOrlbsByPageAndOperType(Pagination page);
    PageResult findOrlbsByPageAndBusinessAndOperType(Pagination page);
}

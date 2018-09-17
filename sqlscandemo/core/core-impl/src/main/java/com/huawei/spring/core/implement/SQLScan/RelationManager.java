package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.core.interfaces.SQLScan.RelationManagement;
import com.huawei.spring.mappers.SQLScanMapper.OperTypeMapper;
import com.huawei.spring.mappers.SQLScanMapper.RelationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by wWX511772 on 2017/12/8.
 */
@Service
public class RelationManager implements RelationManagement {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired private RelationMapper relationMapper;
    @Autowired private OperTypeMapper operTypeMapper;

    @Override
    public List<RuleLevel> findAllLevel() {
        return relationMapper.findAllLevel();
    }

    @Override
    public List<Business> findAllBusiness() {
        return relationMapper.findAllBusiness();
    }

    @Override
    public List<BaseType> findAllBaseType() {
        return relationMapper.findAllBaseType();
    }

    @Override
    public RuleLevel findLevelById(String id) {
        return relationMapper.findLevelById(id);
    }

    @Override
    public Business findBusinessById(String id) {
        return relationMapper.findBusinessById(id);
    }

    @Override
    public BaseType findBaseTypeById(String id) {
        return relationMapper.findBaseTypeById(id);
    }

    @Override
    public Set<OperType> findOperTypes() {
        return operTypeMapper.findOpers();
    }

    @Override
    public Oper_Rule_Level_Business addOrlb(Oper_Rule_Level_Business orlb) {
        relationMapper.addORLB(orlb);
        return relationMapper.findORLBById(orlb.getId());
    }

    @Override
    public Oper_Rule_Level_Business updateOrlb(Oper_Rule_Level_Business orlb) {
        relationMapper.updateOrlb(orlb);
        return relationMapper.findORLBById(orlb.getId());
    }

    @Override
    public void deleteORLBById(String id) {
        relationMapper.deleteORLBById(id);
    }

    @Override
    public void deleteORLBByRuleId(String ruleId) {
        relationMapper.deleteORLBByRuleId(ruleId);
    }
    @Override
    public Oper_Rule_Level_Business findOrlbById(String id) {
        return relationMapper.findORLBById(id);
    }

    @Override
    public PageResult findOrlbsByPage(Pagination page) {
        page.setTotal(relationMapper.getOrlbCounts());
        page.setOffset(page.getOffset());
        List<Oper_Rule_Level_Business> orlbs = relationMapper.findOrlbsByPage(page);
        return this.getPageResult(page, orlbs);
    }

    @Override
    public PageResult findOrlbsByPageAndBusiness(Pagination page) {
        page.setTotal(relationMapper.getOrlbCountsByBusiness(page));
        page.setOffset(page.getOffset());
        List<Oper_Rule_Level_Business> orlbs = relationMapper.findOrlbsByPageAndBusiness(page);
        return this.getPageResult(page, orlbs);
    }

    @Override
    public PageResult findOrlbsByPageAndOperType(Pagination page) {
        page.setTotal(relationMapper.getOrlbCountsByOperType(page));
        page.setOffset(page.getOffset());
        List<Oper_Rule_Level_Business> orlbs = relationMapper.findOrlbsByPageAndOperType(page);
        return this.getPageResult(page, orlbs);
    }

    @Override
    public PageResult findOrlbsByPageAndBusinessAndOperType(Pagination page) {
        page.setTotal(relationMapper.getOrlbCountsByBusinessAndOper(page));
        page.setOffset(page.getOffset());
        List<Oper_Rule_Level_Business> orlbs = relationMapper.findOrlbsByPageAndBusinessAndOperType(page);
        return this.getPageResult(page, orlbs);
    }

    private PageResult getPageResult(Pagination page, List<Oper_Rule_Level_Business> orlbs) {
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRows(orlbs);
        return pageResult;
    }
}

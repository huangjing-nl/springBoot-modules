package com.huawei.spring.api.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.core.interfaces.SQLScan.RelationManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Created by wWX511772 on 2017/12/8.
 */
@RestController
@RequestMapping(value = "/api/v1/relation")
public class RestRelation {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired private RelationManagement relationManagement;

    @RequestMapping(
            value = "/find/allLevel",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<RuleLevel> findAllLevel(){
        return relationManagement.findAllLevel();
    }

    @RequestMapping(
            value = "/find/allBaseType",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<BaseType> findAllBaseType(){
        return relationManagement.findAllBaseType();
    }

    @RequestMapping(
            value = "/find/allBusiness",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Business> findAllBusiness(){
        return relationManagement.findAllBusiness();
    }

    @RequestMapping(
        value = "/find/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Business findBusinessById(@PathVariable("id") String id){
        return relationManagement.findBusinessById(id);
    }

    @RequestMapping(
        value = "/find/opers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Set<OperType> findOperTypes(){
        return relationManagement.findOperTypes();
    }

//    @RequestMapping(
//        value = "/find/orlb/{id}",
//        method = RequestMethod.GET,
//        produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public Oper_Rule_Level_Business findByRrlbId(@PathVariable("id") String id){
//        return relationManagement.findOrlbById(id);
//    }
}

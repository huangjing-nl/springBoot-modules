package com.huawei.spring.api.SQLScan;

import com.huawei.spring.catalogue.SQLScan.ScanResult;
import com.huawei.spring.core.interfaces.SQLScan.ScanResultManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/jenkins")
public class RestJenkinsController {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ScanResultManagement resultManagement;

  @RequestMapping(
      value = "result/{taskId}",
      method = RequestMethod.GET
  )
  public String findByPageAndTaskId(
      @PathVariable("taskId") String taskId, Model model) {
    List<ScanResult> results = resultManagement.findByTaskId(taskId);
    model.addAttribute("results", results);
    model.addAttribute("taskId", taskId);
    return "jenkins";
  }

  @RequestMapping(
      value = "error",
      method = RequestMethod.GET)
  public ModelAndView errorHandle() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("error");
    // return "error";
    return modelAndView;
  }
}

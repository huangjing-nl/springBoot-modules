package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.core.implement.SQLScan.custom.CustomFunctions;
import com.huawei.spring.core.implement.SQLScan.util.FileUtil;
import com.huawei.spring.core.implement.SQLScan.util.ScanTaskUtil;
import com.huawei.spring.core.implement.SQLScan.util.SqlScanUtil;
import com.huawei.spring.core.interfaces.SQLScan.BigTableManagement;
import com.huawei.spring.core.interfaces.SQLScan.SQLScanManagement;
import com.huawei.spring.core.interfaces.SQLScan.ScanTaskManagement;
import com.huawei.spring.exceptions.*;
import com.huawei.spring.mappers.SQLScanMapper.OperTypeMapper;
import com.huawei.spring.mappers.SQLScanMapper.ScanResultMapper;
import com.huawei.spring.mappers.SQLScanMapper.ScanTaskMapper;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Service
public class SQLScanManager implements SQLScanManagement {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanTaskManagement taskManagement;
  @Autowired private OperTypeMapper operTypeMapper;
  @Autowired private ScanResultMapper scanResultMapper;
  @Autowired private BigTableManagement bigTableManagement;
  @Value("${sqlscan.upload.baseDir:/temp/sqlscan/}")
  private String baseDir;
  @Value("${sqlscan.bigTable.partition.day:3}")
  private int bDays;
  @Value("${sqlscan.other.partition.day:30}")
  private int sDays;
  @Value("${sqlscan.bigTable.partition.hour:1}")
  private int daysOfHour;

  //TODO Careful analysis, the future is very important here
  @Override
  public SqlScript cutBlockByFile(SqlScript sqlScript, String business) {
    //Map<List<ScanResult>, List<SQLBlock>> listMap = null;
    LineNumberReader lineNumberReader = null;
    // FileReader fileReader = null;
    FileInputStream in = null;
    InputStreamReader fileReader = null;
    BufferedReader bufferedReader = null;
    List<SQLBlock> sqlBlocks = null;
    OutputComment outputComment = null;
    List<String> builders = new ArrayList<>();
    List<String> repoBuilders = new ArrayList<>();
    boolean isBeeline = false;
    int lineNumber = 0;
    Set<OperType> operTypes = operTypeMapper.findOpers();
    // boolean checkSemicolon = false;
    AtomicReference<StringBuilder> atomicReference = new AtomicReference<>(new StringBuilder());
    // SQLBlock onlyBlock = new SQLBlock();
    try {
      // fileReader = new FileReader(sqlScript.getFilePath());
      in = new FileInputStream(sqlScript.getFilePath());
      fileReader = new InputStreamReader(in, "utf-8");
      bufferedReader = new BufferedReader(fileReader);
      lineNumberReader = new LineNumberReader(bufferedReader);
      String line = null;
      StringBuilder blockLine = new StringBuilder();
      StringBuilder stringBuilder = atomicReference.get();
      while ((line = lineNumberReader.readLine()) != null) {
        String trim = line.trim();
        if (Pattern.compile("output:").matcher(trim.toLowerCase()).find()) {
          outputComment = new OutputComment();
          outputComment.setOutput(line);
          outputComment.setMapByOutput();
        }
        if (trim.toLowerCase().startsWith("beeline")) {
          isBeeline = true;
        }
        lineNumber = lineNumberReader.getLineNumber();
        if (trim.endsWith(";")) {
          String sub = trim.substring(0, trim.lastIndexOf(";"));
          stringBuilder.append(lineNumber).append("\n").append(sub).append(" ").append("\n");
          if (isBeeline) {
            builders.add(stringBuilder.toString());
          } else {
            repoBuilders.add(stringBuilder.toString());
          }
          // atomicReference.set(new StringBuilder());
          stringBuilder = new StringBuilder();
        } else {
          if ("".equals(line)) {
            stringBuilder.append(lineNumber).append("\n").append(line).append(" ").append("\n");
          } else {
            stringBuilder.append(lineNumber).append("\n").append(line).append("\n");
          }
        }
      }
      if (stringBuilder.length() > 0) {
        if (isBeeline) {
          builders.add(stringBuilder.insert(stringBuilder.length() - 1, " ").toString());
          // builders.add(stringBuilder.toString());
        } else {
          repoBuilders.add(stringBuilder.insert(stringBuilder.length() - 1, " ").toString());
          // repoBuilders.add(stringBuilder.toString());
        }
      }
      if (isBeeline) {
        if (null != outputComment) sqlScript.setOutputComment(outputComment);
        if (builders.size() > 0) {
          sqlBlocks = SqlScanUtil.parseBuilder(builders, operTypes, sqlScript, business);
          sqlScript.setBlocks(sqlBlocks);
        }
      } else {
        if (null != outputComment) sqlScript.setOutputComment(outputComment);
        if (repoBuilders.size() > 0) {
          sqlBlocks = SqlScanUtil.parseBuilder(repoBuilders, operTypes, sqlScript, business);
          sqlScript.setBlocks(sqlBlocks);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != in) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (null != fileReader) {
        try {
          fileReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (null != bufferedReader) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (null != lineNumberReader) {
        try {
          lineNumberReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sqlScript;
  }

  @Override
  public List<SQLBlock> cutBlockByString(ScanTask task) throws AlreadyExistingException, NotFoundException {
    // List<SQLBlock> sqlBlocks = new ArrayList<SQLBlock>();
    Set<OperType> operTypes = operTypeMapper.findOpers();
    AtomicReference<StringBuilder> atomicReference = new AtomicReference<>(new StringBuilder());
    List<String> builders = new ArrayList<>();
    String[] split = task.getSqlString().split("\n");
    StringBuilder stringBuilder = atomicReference.get();
    for (int i = 0; i < split.length; i++) {
      String trim = split[i].trim();
      if (trim.endsWith(";")) {
        String sub = trim.substring(0, trim.lastIndexOf(";"));
        stringBuilder.append(i + 1).append("\n").append(sub).append(" ").append("\n");
        builders.add(stringBuilder.toString());
        // atomicReference.set(new StringBuilder());
        stringBuilder = new StringBuilder();
      } else {
        if ("".equals(split[i])) {
          stringBuilder.append(i + 1).append("\n").append(split[i]).append(" ").append("\n");
        } else {
          stringBuilder.append(i + 1).append("\n").append(split[i]).append("\n");
        }
      }
    }
    if (stringBuilder.length() > 0) {
      builders.add(stringBuilder.insert(stringBuilder.length() - 1, " ").toString());
      // builders.add(stringBuilder.toString());
    }
    //    String[] splits = sqlStr.trim().toLowerCase().split(";");
//    for (String str : splits) {
//      //String blockContent = str.trim();
//      //TODO 解决注释问题
//      SQLBlock block = SqlScanUtil.removeComment(str, operTypes);
//      if (null != block) {
//        block.setBaseType(baseType);
//        block.setBusiness(business);
//        block.setTaskId(savedTask.getId());
//        sqlBlocks.add(block);
//      }
//    }
    SqlScript script = new SqlScript(task.getVersion(), task.getBaseType(), task.getId());
    return SqlScanUtil.parseBuilder(builders, operTypes, script, task.getBusiness());
  }

  @Override
  public List<ScanResult> getResultByFile(List<SqlScript> scripts) throws NotFoundException, NotAllowedException, FileOperationException {
    //TODO Use thread pool management thread to improve performance
    if (null == scripts || scripts.isEmpty()) {
      throw new NotFoundException("No executable found in this task!");
    }
    ArrayList<SqlScript> sqlScripts = new ArrayList<>();
    List<SQLBlock> blocks = new ArrayList<>();
    List<ScanResult> results = new ArrayList<>();
    List<BigTable> bigTables = bigTableManagement.findAll();
    ScanTask task = taskManagement.findById(scripts.get(0).getTaskId());
    if (null == task) {
      throw new NotFoundException("The task with id: " + scripts.get(0).getTaskId() + " not found!");
    }
    for (SqlScript script : scripts) {
      script = this.cutBlockByFile(script, task.getBusiness());
      sqlScripts.add(script);
//      if (null != listMap) {
//        for (Map.Entry<List<ScanResult>, List<SQLBlock>> entry : listMap.entrySet()) {
//          blocks.addAll(entry.getValue());
//          results.addAll(entry.getKey());
//        }
//      }
      if (null != script.getBlocks() && !script.getBlocks().isEmpty()) {
        blocks.addAll(script.getBlocks());
        //todo
      }
    }
    //TODO custom
    Map<String, OperType> typeMap = operTypeMapper.findByBusiness(task.getBusiness()).stream().collect(Collectors.toMap(OperType::getName, Function.identity()));
    CustomParameter customParameter = SqlScanUtil.configParam(blocks, sqlScripts, bigTables, bDays, sDays, daysOfHour, typeMap);
    List<ScanResult> scanResults = SqlScanUtil.sqlScanner(customParameter);
//    List<ScanResult> createAndDropTableResults = CustomFunctions.createAndDropTable(customParameter);  //todo combine
//    if (null != createAndDropTableResults && !createAndDropTableResults.isEmpty()) {
//      results.addAll(createAndDropTableResults);
//    }
    results.addAll(scanResults);
//    if (!blocks.isEmpty()) {
//      for (SQLBlock block : blocks) {
//        List<ScanResult> scanResults = SqlScanUtil.blockScan(block, typeMap);
//        //TODO Here can only be gradually added custom methods, need to consider the late intelligent selection of custom functions
//        // List<ScanResult> resultList = CustomFunctions.checkTableAndComment(block);
//        if (null != scanResults && !scanResults.isEmpty()) {
//          results.addAll(scanResults);
//        }
//        if (null != scanResults && !scanResults.isEmpty()) results.addAll(resultList);
//      }
//    }
    if (!results.isEmpty()) {
      scanResultMapper.addSqlResultByBatch(results);
    }
    //TODO find by taskId
    task.setStatus("ACTIVE");
    taskManagement.updateTask(task);
    // 删除目录
    File taskPath = new File(task.getTaskPath());
    if (taskPath.exists()) {
      boolean isRemove = FileUtil.deleteDir(taskPath);
      if (!isRemove) {
        throw new FileOperationException("Task directory deletion failed!");
      }
    }
    return results;
  }

  @Override
  public List<ScanResult> getResultByString(ScanTask task) throws NotFoundException, AlreadyExistingException, StatementParseException, NotAllowedException, UnsupportedEncodingException {
    List<BigTable> bigTables = bigTableManagement.findAll();
    ScanTask savedTask = taskManagement.addScanTask(task);
    if (null == savedTask) {
      throw new NotFoundException("The task with id: " + task.getId() + " failed to be created");
    }
    List<ScanResult> results = new ArrayList<>();
    List<SQLBlock> blocks = this.cutBlockByString(savedTask);
    Map<String, OperType> typeMap = operTypeMapper.findByBusiness(savedTask.getBusiness()).stream().collect(Collectors.toMap(OperType::getName, Function.identity()));
    //TODO custom
    CustomParameter customParameter = SqlScanUtil.configParam(blocks, null, bigTables, bDays, sDays, daysOfHour, typeMap);
    List<ScanResult> scanResults = SqlScanUtil.sqlScanner(customParameter);
    results.addAll(scanResults);
    // todo combine
    // List<ScanResult> resultList = CustomFunctions.checkTableAndComment(customParameter);
    // if (null != resultList && !resultList.isEmpty()) results.addAll(resultList);
    //TODO Here can only be gradually added custom methods, need to consider the late intelligent selection of custom functions
    // List<ScanResult> btrs = CustomFunctions.bigTableCustom(customParameter);
    // if (null != scanResults && !scanResults.isEmpty()) results.addAll(scanResults);
    // if (null != btrs && !btrs.isEmpty()) results.addAll(btrs);
    if (!results.isEmpty()) {
      scanResultMapper.addSqlResultByBatch(results);
    }
    savedTask.setStatus("ACTIVE");
    taskManagement.updateTask(savedTask);
    return results;
  }

  @Override
  public Set<OperType> opers() {
    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    Set<OperType> opers = operTypeMapper.findOpers();
    System.out.println("-------------------------------------");
    return opers;
  }

}

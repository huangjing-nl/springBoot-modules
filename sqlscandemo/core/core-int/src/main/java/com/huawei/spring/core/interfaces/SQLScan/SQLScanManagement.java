package com.huawei.spring.core.interfaces.SQLScan;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.exceptions.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xWX522916 on 2017/11/22.
 */
public interface SQLScanManagement {

  /** dump the *.sql file according to the type of operation to generate SQLBlock collection */
  SqlScript cutBlockByFile(SqlScript sqlScript, String business);

  List<SQLBlock> cutBlockByString(ScanTask task) throws AlreadyExistingException, NotFoundException;

  List<ScanResult> getResultByFile(List<SqlScript> scripts) throws NotFoundException, NotAllowedException, FileOperationException;
  List<ScanResult> getResultByString(ScanTask task) throws NotFoundException, AlreadyExistingException, StatementParseException, NotAllowedException, UnsupportedEncodingException;

  Set<OperType> opers();
}

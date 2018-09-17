package com.huawei.spring.catalogue.SQLScan;

public enum CustomMethod {
  // mustBePartitioned("mustBePartitioned", 0),
  tableWithPt("tableWithPt", 0),
  bigTableCustom("bigTableCustom", 1),
  createAndDropTable("createAndDropTable", 2),
  checkTableAndComment("checkTableAndComment", 3),
  // checkBigTableNumbers("checkBigTableNumbers", 4),
  sameTableTimes("sameTableTimes", 4),
  checkColumnComment("checkColumnComment", 5),
  checkColumnRoot("checkColumnRoot", 6),
  checkOutputTnames("checkOutputTnames", 7),
  checkRepeatTname("checkRepeatTname", 8),
  checkGroupByOfSubQuery("checkGroupByOfSubQuery", 9),
  removeGroupByOfSubQueryForUnion("removeGroupByOfSubQueryForUnion", 10);

  CustomMethod() {
  }

  private static final CustomMethod[] a = new CustomMethod[]{bigTableCustom};

  private CustomMethod(String var1, int var2) {}
}

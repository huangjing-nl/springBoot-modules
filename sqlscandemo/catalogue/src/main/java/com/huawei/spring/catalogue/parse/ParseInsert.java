package com.huawei.spring.catalogue.parse;


public class ParseInsert {

  public InsertBean parseInsertSql(String sql) {
    InsertBean insertBean = new InsertBean();
    insertBean.setOriSqlContent(sql);
    sql = ParseHiveSQL.removeMulSpace(sql);
    sql = sql.toLowerCase();
    int index = sql.indexOf(" select ") > 0 ? sql.indexOf(" select ") : 0;
    String head = sql.substring(0, index);
    if (head.contains("insert into table")) {
      insertBean.setOperType("1");
      //tableName=head.matches("")[1];


    } else {
      String[] arrays = head.split(" ");
      if (arrays.length >= 4) {
        insertBean.setTabName(arrays[3]);
        if (arrays.length > 5) {
          StringBuilder partitions = new StringBuilder();
          for (int i = 5; i < arrays.length; i++) {
            partitions.append(arrays[i]);
          }
          insertBean.setTabPartCols(partitions.toString());
        }
      }
      insertBean.setOperType("2");
    }
    String query = sql.substring(index + 1, sql.length());
    System.out.println(head);
    System.out.println(query);
    QueryBean qBean = new ParseQuery().itratorQuery(query);
    insertBean.setQueryBean(qBean);
    return insertBean;
  }


  public static void main(String[] args) {
    String sql1 = "INSERT OVERWRITE TABLE bicoredata.dwd_onl_fans_forum_sect_ds           \n" +
        "PARTITION (pt_d = '$date')                                             \n" +
        "SELECT                                                                 \n" +
        "   sect_id                                                             \n" +
        "  ,super_sect_id                                                       \n" +
        "  ,sect_type_cd                                                        \n" +
        "  ,sect_name                                                           \n" +
        "  ,disp_status_cd                                                      \n" +
        "  ,disp_seq                                                            \n" +
        "  ,style_id                                                            \n" +
        "  ,thread_cnt                                                          \n" +
        "  ,post_cnt                                                            \n" +
        "  ,today_post_cnt                                                      \n" +
        "  ,ytday_post_cnt                                                      \n" +
        "  ,rank                                                                \n" +
        "  ,day_bf_ytday_rank                                                   \n" +
        "  ,final_publish                                                       \n" +
        "  ,second_domain                                                       \n" +
        "  ,group_rank                                                          \n" +
        "  ,group_pub_point                                                     \n" +
        "  ,arch_tbl_flg                                                        \n" +
        "  ,recommend_sect                                                      \n" +
        "  ,collect_cnt                                                         \n" +
        "  ,share_cnt                                                           \n" +
        "  ,etl_time                                                            \n" +
        "FROM temp.tmp_dwd_onl_fans_forum_sect_ds T1                            \n";

    String sql2 = "";

    new ParseInsert().parseInsertSql(sql1);
  }

}

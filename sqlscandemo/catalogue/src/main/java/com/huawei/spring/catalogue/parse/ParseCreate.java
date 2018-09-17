package com.huawei.spring.catalogue.parse;

import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.exceptions.SQLFormatException;

import java.util.*;


public class ParseCreate {


  /**
   * 检查Create的类型
   * 1.create
   * 2.create ... as
   */
  public CreateBean parseCreateSql(String sql) throws SQLFormatException {
    CreateBean createBean = new CreateBean();
    createBean.setSqlContent(sql);
    sql = ParseHiveSQL.removeMulSpace(sql);
    sql = sql.toLowerCase();
    String type = "";
    if (sql.startsWith("create ")) {
      type = ParseHiveSQL.checkCreateType(sql);
      createBean.setOperType(type);
    }
    if ("1".equals(type)) {
      //根据AS得出的  as前的内容（格式化后的数据）
      int a = sql.indexOf(" as ");
      String head = sql.substring(0, a + 1);
      String sql1 = createBean.getSqlContent().toLowerCase();

      //根据查找第一个select 得出 as之后的内容
      int b = sql1.indexOf("select");
      //System.out.println(head);
      String query = sql.substring(a + 4, sql.length());
      String query1 = sql1.substring(b, sql1.length());
      //如果两种方式得出的结果一样则取格式化之前的数据，反则去格式化后的数据
      if (query.equals(ParseHiveSQL.removeMulSpace(query1))) {
        QueryBean qb = new ParseQuery().itratorQuery(query1);
        createBean.setQueryBean(qb);
      } else {
        QueryBean qb = new ParseQuery().itratorQuery(query);
        createBean.setQueryBean(qb);
      }
      createBean = parseHeadOfCreateTable(createBean, head);
    } else {
      //切块
      String tempSql = sql;
      String head = "";
      String center = "";
      String tail = "";
      int startflag = 0;
      int endflag = 0;
      int start = 0;
      for (int i = 0; i < tempSql.length(); i++) {
        if (tempSql.charAt(i) == '(') {
          startflag++;
          if (startflag == endflag + 1) {
            start = i;
          }
        } else if (tempSql.charAt(i) == ')') {
          endflag++;
          if (startflag == endflag) {
            head = tempSql.substring(0, start);
            center = tempSql.substring(start, i + 1);
            tail = tempSql.substring(i + 1, tempSql.length());
            break;
          }
        }
      }
      //解析头
      createBean = parseHeadOfCreateTable(createBean, head);
      //解析字段
      createBean = parseColsOfCreateTable(createBean, center);
      //解析属性
      createBean = parseAttrOfCreateTable(createBean, tail);
    }
    return createBean;
  }

  /**
   * 解析建表语句的头
   * ---CREATE EXTERNAL TABLE IF NOT EXISTS bicoredata.dwd_onl_fans_forum_sect_ds
   *
   * @param createBean
   * @param head
   * @return
   */
  public CreateBean parseHeadOfCreateTable(CreateBean createBean, String head) {
    if (head != null && head.length() > 0) {
      if (head.contains(" if not exists ")) {
        createBean.setTabIfNotExists(" if not exists ");
      }
      if (head.contains(" external ")) {
        createBean.setTabExternal(" external ");
      }
      String[] arrays = head.split(" ");
      if (arrays.length > 0) {
        createBean.setTabName(arrays[arrays.length - 1]);
      }
    }
    return createBean;
  }
  /**
   * 解析建表语句中的各个字段
   *
   * @param createBean
   * @param center
   * @return
   */
  public CreateBean parseColsOfCreateTable(CreateBean createBean, String center) throws SQLFormatException {
    //去除两边的括号以及空格
    center = ParseHiveSQL.removeMulSpace(center.substring(1, center.length() - 1));
    if (center != null && center.length() > 0) {
      ArrayList<Column> colunmList = new ArrayList<Column>();
      center = center.trim().replaceAll("[\\s]+", " ").replaceAll("\"", "'").replaceAll("[']+", "'"); //对于同时出现"'"多个单引号，而语法不报错, 则可以将其做格式化

      Map<Character, Character> charMap = getMap();
      StringBuilder builder = new StringBuilder();
      String code1 = IdGenerator.createUUID();
      String code2 = IdGenerator.createUUID();
      recursiveBuild(charMap, builder, center, code1, code2);

      String[] arrays = builder.toString().split(",");
      for (int i = 0; i < arrays.length; i++) {
        String[] tempArray = arrays[i].trim().split(" ");
        Column col = new Column();
        col.setColumn(arrays[i]);
        if (tempArray.length == 1) {
          col.setColName(tempArray[0]);
        } else if (tempArray.length == 2) {
          col.setColName(tempArray[0]);
          String colType = tempArray[1];
          String s = colType.replaceAll(code1, ",").replaceAll(code2, " ");
          if (!s.toLowerCase().startsWith("comment")) {
            col.setColType(s);
          } else {
            col.setColComment(s.substring(7, s.length()));
          }
        } else if (tempArray.length == 3) {
          col.setColName(tempArray[0]);
          col.setColType(tempArray[1].replaceAll(code1, ",").replaceAll(code2, " "));
          String comment = tempArray[2];
          if (comment.toLowerCase().startsWith("comment") && comment.length() > 7) {
            String substring = comment.substring(7, comment.length());
            substring = substring.replaceAll(code1, ",").replaceAll(code2, " ");
            col.setColComment(substring);
          }
        } else if (tempArray.length == 4) {
          col.setColName(tempArray[0]);
          col.setColType(tempArray[1].replaceAll(code1, ",").replaceAll(code2, " "));
          col.setColComment(tempArray[3].replaceAll(code1, ",").replaceAll(code2, " "));
        }
        colunmList.add(col);
      }
      createBean.setTabColList(colunmList);
    }
    return createBean;
  }

  private void recursiveBuild(Map<Character, Character> charMap, StringBuilder builder, String s, String code1, String code2) throws SQLFormatException {
    String val2 = null;
    int index = (-1);
    Character key = null;
    Character value = null;
    Character escape = null; //处理转义符 "\"
    for (int i = 0; i < s.length(); i++) {
      key = s.charAt(i);
      value = charMap.get(key);
      if (i > 0) escape = s.charAt(i - 1);
      if (null != value && (escape == null || escape != '\\')) {
        index = i;
        break;
      }
    }
    if (index < 0) {
      builder.append(s);
    } else {
      builder.append(s.substring(0, index)); //截取到出现的第一个闭合的括号符的前面的字符串
      String end = s.substring(index, s.length()); // 截取出现的第一个闭合的括号符(包含此符号)的后面的所有字符的字符串

      Map<Integer, String> formatMap = getFormatMap(end, key, value, code1, code2);
      builder.append(formatMap.get(0));
      val2 = formatMap.get(1);
    }
    if (null != val2 && val2.length() > 0) {
      recursiveBuild(charMap, builder, val2, code1, code2);
    }
  }

  private Map<Integer, String> getFormatMap(String end, Character key, Character value, String code1, String code2) throws SQLFormatException {
    Map<Integer, Character> map = new HashMap<Integer, Character>();
    Map<Integer, String> formatMap = new HashMap<Integer, String>();
    int leftCount = 0; // 统计[(<']的数量 表示可能在字段中出现的几种括号情况
    int endRight = 0;  //记录最后一个右括号的下标
    Character escape = null; //处理转义符 "\"
    for (int i = 0; i < end.length(); i++) {
      if (i > 0) escape = end.charAt(i - 1);
      if (end.charAt(i) == key && (escape == null || escape != '\\')) {  // 如果key为"'" 则左右一样，会出现无法找到对称的情况, 故特殊处理 &&后条件处理转义字符的
        leftCount += 1;
        map.put(leftCount, key);
        if (key == value && leftCount == 2) {
          map.clear();
          leftCount = 0;
        }
      } else if (end.charAt(i) == value && (escape == null || escape != '\\')) { // key 和value一样 那么这个条件将无法进去
        map.remove(leftCount);
        leftCount -= 1;
      }
      if (i != 0 && map.size() == 0) { // 这边通过map的size等于0或者leftCount等于0来判断 起始的(左括号有没有被最后一个右括号)消除掉   所以s的起始字符必须是(
        endRight = i;  // 如果一直走不进 map.size() == 0  则说明少一个或多个右括号)  要么在前面的语法解析过不去，要么这里再处理
        break;
      }
    }
    if (endRight != 0) {
      String sub = end.substring(0, endRight + 1);
      if (sub.contains(",") || sub.contains(" ")) {
        String format = sub.replaceAll(",", code1); //code随机生成保证唯一性
        format = format.replaceAll(" ", code2); //code随机生成保证唯一性
        formatMap.put(0, format);
      }else {
        formatMap.put(0, sub);
      }
      formatMap.put(1, end.substring(endRight + 1, end.length()));
    } else {
      throw new SQLFormatException("The syntax of the SQL statement is malformed (for example: {(,<} caused by such characters. Please check carefully!!"); // endRight == 0 代表 这里的map的长度没有清空,也就是左括号没有得到闭合,应该格式抛异常
    }
    return formatMap;
  }

  private Map<Character, Character> getMap() {
    Map<Character, Character> map = new HashMap<>();
    map.put('(', ')');
    map.put('\'', '\'');
    map.put('<', '>');
    return map;
  }

  /**
   * 解析建表语句的属性
   *
   * @param createBean
   * @param tail
   * @return
   */
  public CreateBean parseAttrOfCreateTable(CreateBean createBean, String tail) {
    List<String> list = new ArrayList<>();
    if (tail != null && tail.length() > 0) {
      String s = tail.trim();
      String[] keyWords = keyWords();
      String[] split = s.split(" ");
      boolean isPartitioned = false;
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < split.length;) {
        int count = 0;
        String keyWord = "";
        for (int j = 0; j < keyWords.length; j++) {
          String[] keySplit = keyWords[j].split(" ");
          if (split[i].startsWith(keySplit[0])) {
            if (!isPartitioned || !split[i].startsWith("comment")) {
              keyWord = keyWords[j];
              count = keySplit.length;
              break;
            }
          }
        }
        if (count > 0) {
          if (split[i].equals("partitioned")) {
            isPartitioned = true;
          } else {
            isPartitioned = false;
          }
          if (count == 1) {
            if (builder.toString().length() > 0) {
              list.add(builder.toString());
            }
            builder = new StringBuilder();
            builder.append(split[i]).append(" ");
            i = i + 1;
          } else if (count == 2) {
            if ((i + 1) < split.length) {
              if ((split[i] + " " + split[i + 1]).equals(keyWord)) {
                if (builder.toString().length() > 0) {
                  list.add(builder.toString());
                }
                builder = new StringBuilder();
                if ((split[i] + " " + split[i + 1]).equals("partitioned by")) {
                  isPartitioned = true;
                } else {
                  isPartitioned = false;
                }
                builder.append(split[i]).append(" ").append(split[i + 1]).append(" ");
                i = i + 2;
              } else {
                builder.append(split[i]).append(" ").append(split[i + 1]).append(" ");
                i = i + 2;
              }
            } else {
              builder.append(split[i]).append(" ");
              i = i + 1;
            }
          } else if (count == 3) {
            if ((i + 2) < split.length) {
              if ((split[i] + " " + split[i + 1] + " " + split[i + 2]).equals(keyWord)) {
                if (builder.toString().length() > 0) {
                  list.add(builder.toString());
                }
                builder = new StringBuilder();
                isPartitioned = false;
                builder.append(split[i]).append(" ").append(split[i + 1]).append(" ").append(split[i + 2]).append(" ");
                i = i + 3;
              } else {
                builder.append(split[i]).append(" ").append(split[i + 1]).append(" ").append(split[i + 2]).append(" ");
                i = i + 3;
              }
            } else {
              builder.append(split[i]).append(" ");
              i = i + 1;
            }
          }
        } else {
          builder.append(split[i]).append(" ");
          i = i + 1;
        }
      }
      if (builder.toString().length() > 0) {
        list.add(builder.toString());
      }
    }
    if (list.size() > 0) {
      for (String ss : list) {
        if (ss.trim().startsWith("comment")) {
          createBean.setTabComment(ss);
        } else if (ss.trim().startsWith("location")) {
          createBean.setTabLocation(ss);
        }
      }
    }
    return createBean;
  }

  private String[] keyWords() {
    String builder = "comment" + "\n" +
        "partitioned by" + "\n" +
        "row format delimited" + "\n" +
        "fields terminated by" + "\n" +
        "lines terminated by" + "\n" +
        "stored as" + "\n" +
        "location" + "\n" +
        "tblproperties";
    return builder.split("\n");
  }

//  public static void main(String[] args) {
//    String sql2 =
//        "CREATE EXTERNAL TABLE IF NOT EXISTS bicoredata.dwd_onl_fans_forum_sect_ds (      \n" +
//            "      sect_id              VARCHAR(128)  COMMENT '版块编号'                         \n" +
//            "     ,super_sect_id        VARCHAR(128)  COMMENT '上级版块编号'                     \n" +
//            "     ,sect_type_cd         VARCHAR(16)   COMMENT '版块类型代码'                     \n" +
//            "     ,sect_name            VARCHAR(256)  COMMENT '版块名称'                         \n" +
//            "     ,disp_status_cd       VARCHAR(16)   COMMENT '显示状态代码'                     \n" +
//            "     ,disp_seq             VARCHAR(256)  COMMENT '显示顺序'                         \n" +
//            "     ,style_id             VARCHAR(128)  COMMENT '风格编号'                         \n" +
//            "     ,thread_cnt           INT           COMMENT '主题帖数量'                       \n" +
//            "     ,post_cnt             INT           COMMENT '帖子数量'                         \n" +
//            "     ,today_post_cnt       INT           COMMENT '今日发帖数量'                     \n" +
//            "     ,ytday_post_cnt       INT           COMMENT '昨日发帖数量'                     \n" +
//            "     ,rank                 INT           COMMENT '排名'                             \n" +
//            "     ,day_bf_ytday_rank    INT           COMMENT '前天排名'                         \n" +
//            "     ,final_publish        VARCHAR(5000) COMMENT '最后发表'                         \n" +
//            "     ,second_domain        VARCHAR(256)  COMMENT '二级域名'                         \n" +
//            "     ,group_rank           INT           COMMENT '群组等级'                         \n" +
//            "     ,group_pub_point      INT           COMMENT '群组公共积分'                     \n" +
//            "     ,arch_tbl_flg         SMALLINT      COMMENT '存档表标志'                       \n" +
//            "     ,recommend_sect       VARCHAR(256)  COMMENT '推荐版块'                         \n" +
//            "     ,collect_cnt          INT           COMMENT '收藏次数'                         \n" +
//            "     ,share_cnt            INT           COMMENT '分享次数'                         \n" +
//            "     ,etl_time             VARCHAR(30)   COMMENT 'ETL时间'                          \n" +
//            ")                                                                                   \n" +
//            "COMMENT '花粉论坛版块'                                                              \n" +
//            "PARTITIONED BY (pt_d VARCHAR(8) COMMENT '天分区')                                   \n" +
//            "ROW FORMAT DELIMITED                                                                \n" +
//            "FIELDS TERMINATED BY '\001'                                                         \n" +
//            "LINES TERMINATED BY '\n'                                                            \n" +
//            "STORED AS ORC                                                                       \n" +
//            "LOCATION '/AppData/BIProd/SSS/ONL/dwd_onl_fans_forum_sect_ds'                       \n" +
//            "TBLPROPERTIES('orc.compress'='ZLIB')                                                \n";
//
//    String sql = "CREATE TABLE IF NOT EXISTS temp.tmp_ads_hwmovie_play_content_dm_1                                                                                                                                                               \n" +
//        "AS                                                                                                                                                                                                                              \n" +
//        "SELECT                                                                                                                                                                                                                          \r\n" +
//        "     t5.up_id                                                                                                            AS up_id                                                                                               \n" +
//        "    ,imei                                                                                                                AS imei                                                                                                \n" +
//        "    ,'movie'                                                                                                             AS service_id                                                                                          \n" +
//        "    ,t5.hwmovie_id                                                                                                       AS hwmovie_id                                                                                          \n" +
//        "    ,''                                                                                                                  AS video_src_resolution                                                                                \n" +
//        "    ,play_zone                                                                                                           AS play_zone                                                                                           \n" +
//        "    ,play_catalog_id                                                                                                     AS play_catalog_id                                                                                     \n" +
//        "    ,login_user_flg                                                                                                      AS login_user_flg                                                                                      \n" +
//        "    ,IF(IsEmpty(t2.up_id),0,1)                                                                                           AS vip_flg                                                                                             \n" +
//        "    ,play_cnt                                                                                                            AS play_cnt                                                                                            \n" +
//        "    ,play_duration                                                                                                       AS play_duration                                                                                       \n" +
//        "    ,0                                                                                                                   AS play_success_cnt                                                                                    \n" +
//        "    ,0                                                                                                                   AS play_fail_cnt                                                                                       \n" +
//        "    ,0                                                                                                                   AS play_break_cnt                                                                                      \n" +
//        "    ,0                                                                                                                   AS play_end_cnt                                                                                        \n" +
//        "    ,0                                                                                                                   AS stop_cnt                                                                                            \n" +
//        "    ,0                                                                                                                   AS stop_duration                                                                                       \n" +
//        "FROM                                                                                                                                                                                                                            \n" +
//        "(                                                                                                                                                                                                                               \n" +
//        "SELECT                                                                                                                                                                                                                          \n" +
//        "     t1.up_id                               AS up_id                                                                                                                                                                            \n" +
//        "    ,t1.imei                                AS imei                                                                                                                                                                             \n" +
//        "    ,login_user_flg                         AS login_user_flg                                                                                                                                                                   \n" +
//        "    ,t3.hwmovie_id                          AS hwmovie_id                                                                                                                                                                       \n" +
//        "    ,t4.catalog_id                          AS play_catalog_id                                                                                                                                                                  \n" +
//        "    ,t1.play_zone                           AS play_zone                                                                                                                                                                        \r\n" +
//        "    ,count(t1.imei)                         AS play_cnt                                                                                                                                                                         \n" +
//        "    ,sum(t1.play_duration)                  AS play_duration                                                                                                                                                                    \n" +
//        "FROM                                                                                                                                                                                                                            \n" +
//        "   (                                                                                                                                                                                                                            \n" +
//        "  SELECT                                                                                                                                                                                                                        \n" +
//        "         up_id                                                                                 AS up_id                                                                                                                         \n" +
//        "        ,imei                                                                                  AS imei                                                                                                                          \n" +
//        "        ,content_encode                                                                        AS content_encode                                                                                                                \n" +
//        "#guest为游客                                                                                                                                                                                                                    \n" +
//        "        ,CASE WHEN up_id <>SHA('guest') THEN '1' ELSE '0' END                                  AS login_user_flg                                                                                                                \n" +
//        "#判断ip是否为空,如果为空插入NULL值，不为空则进行解密拆分出城市地址                                                                                                                                                              \n" +
//        "        ,IF(IsEmpty(user_ip_addr),NULL,GETJSONOBJ(IP2AREAINFO(user_ip_addr),'$.city'))         AS play_zone                                                                                                                     \n" +
//        "        ,IF(IsEmpty(service_end_time) OR IsEmpty(service_start_time),0,Dateutil(service_end_time,'yyyy-MM-dd HH:mm:ss',0)-Dateutil(service_start_time,'yyyy-MM-dd HH:mm:ss',0)) AS play_duration                                \n" +
//        "#hwmovie_service_type_cd=4业务类型为VOD                                                                                                                                                                                         \n" +
//        "    FROM  bicoredata.dwd_evt_hwmovie_oper_dm                                                                                                                                                                                    \n" +
//        "    WHERE pt_d='$date'                                                                                                                                                                                                          \n" +
//        "    AND hwmovie_service_type_cd=4                                                                                                                                                                                               \n" +
//        "    )t1                                                                                                                                                                                                                         \n" +
//        "LEFT JOIN                                                                                                                                                                                                                       \n" +
//        "#获取hwmovie_id                                                                                                                                                                                                                 \n" +
//        "   (                                                                                                                                                                                                                            \n" +
//        "    SELECT                                                                                                                                                                                                                      \n" +
//        "         MAX(hwmovie_id) AS hwmovie_id                                                                                                                                                                                          \n" +
//        "        ,content_encode                                                                                                                                                                                                         \n" +
//        "    FROM bicoredata.dwd_con_hwmovie_ds                                                                                                                                                                                          \n" +
//        "    WHERE pt_d = '$date'                                                                                                                                                                                                        \n" +
//        "    GROUP BY content_encode                                                                                                                                                                                                     \n" +
//        "    )t3                                                                                                                                                                                                                         \n" +
//        "   ON t1.content_encode=t3.content_encode                                                                                                                                                                                       \n" +
//        "LEFT JOIN                                                                                                                                                                                                                       \n" +
//        "#获取catalog_id                                                                                                                                                                                                                 \n" +
//        "   (                                                                                                                                                                                                                            \n" +
//        "    SELECT                                                                                                                                                                                                                      \n" +
//        "         MAX(catalog_id) AS catalog_id                                                                                                                                                                                          \n" +
//        "        ,content_encode                                                                                                                                                                                                         \n" +
//        "    FROM bicoredata.dwd_con_hwmovie_catalog_rela_ds                                                                                                                                                                             \n" +
//        "    WHERE pt_d = '$date'                                                                                                                                                                                                        \n" +
//        "    GROUP BY content_encode                                                                                                                                                                                                     \n" +
//        "   )t4                                                                                                                                                                                                                          \n" +
//        "   ON t1.content_encode=t4.content_encode                                                                                                                                                                                       \n" +
//        "   GROUP BY t1.up_id,t1.imei,t1.login_user_flg,t1.play_zone,t3.hwmovie_id,t4.catalog_id                                                                                                                                         \n" +
//        ")t5                                                                                                                                                                                                                             \n" +
//        "LEFT JOIN                                                                                                                                                                                                                       \n" +
//        "#获取up_id来判断vip登录                                                                                                                                                                                                         \n" +
//        "(                                                                                                                                                                                                                               \n" +
//        "SELECT                                                                                                                                                                                                                          \n" +
//        "     up_id                                                                                                                                                                                                                      \n" +
//        "FROM bicoredata.dwd_sal_hwmovie_user_pay_ds                                                                                                                                                                                     \n" +
//        "WHERE pt_d = '$date' AND pt_d between '20170402' AND '20170806' AND DateUtil(valid_period_end_time,'yyyy-MM-dd HH:mm:ss','yyyyMMdd')>'$last_date'                                                                                                                         \n" +
//        "AND hwmovie_id = '-1'                                                                                                                                                                                                           \n" +
//        "#-1=会员                                                                                                                                                                                                                        \n" +
//        "GROUP BY up_id                                                                                                                                                                                                                  \n" +
//        ")t2 ON t5.up_id=t2.up_id                                                                                                                                                                                                        \n";
//
//    ParseCreate parseCreate = new ParseCreate();
//    CreateBean createBean = parseCreate.parseCreateSql(sql);
//    //QueryBean queryBean = createBean.getQueryBean();
//    List<QueryBean> queryBeans = new ArrayList<>();
//    queryBeans = BigTableUtil.backQueryBeans(createBean, queryBeans);
//    // System.out.println(createBean.toString());
//    System.out.println("size--->" + queryBeans.size());
//    ArrayList<String> list = new ArrayList<>();
//    for (QueryBean bean : queryBeans) {
//      if (!"${bl-ock}".equals(bean.getFromTableName())) {
//        //System.out.println(bean.getFromTableName() + "-->" + bean.getSqlContent());
//        List<String> strings = BigTableUtil.parseCondition(bean, 3);
//        list.addAll(strings);
//      }
//    }
//    for (String s : list) {
//      System.out.println(s);
//    }
//  }

}

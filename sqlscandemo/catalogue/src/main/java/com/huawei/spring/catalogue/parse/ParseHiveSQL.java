package com.huawei.spring.catalogue.parse;


public class ParseHiveSQL {
  /**
   * 去除字符串中的多空格以及换行
   *
   * @param str
   * @return
   */
  public static String removeMulSpace(String str) {
    // 去除两边的空格
//    str = str.trim();
//    // 去除字符串中的换行
//    str = str.replaceAll("\r\n+", " ");
//    str = str.replaceAll("\n+", " ");
//    // 去除字符串中间的多空格
//    str = str.replaceAll(" +", " ");

    return str.trim().replaceAll("[\\s]+", " ");
  }

  /**
   * 判断是否是有效的子查询块
   *
   * @param str
   * @return
   */
  public static String checkCreateType(String str) {
    str = str.toLowerCase();
    if (str.contains("select ") && str.contains(" from ") && str.contains(" as ")) {
      return "1";
    } else {
      return "0";
    }
  }

  /**
   * 去除注释
   * #开头的注释
   * --开头的注释
   * --'/*'开头的注释
   * --'//'开头的注释
   * '/'=47
   * '\'=92
   * '\n'=10
   * '*'=42
   *
   * @param str
   * @return
   */
  public static String removeComment(String str) {
    StringBuilder result = new StringBuilder();
    //1.去除/**/这类注释
    int start = 0;
    int startFlag = 0;
    int endFlag = 0;
    // 读取有多少块，并提取head的值
    boolean lineFlag = true;
    boolean charFlag = true;
    for (int i = 0; i < str.length(); i++) {
      //char c=str.charAt(i);
      //System.out.println(c);
      if (str.charAt(i) == '#') {
        lineFlag = false;

      } else if ((str.charAt(i) == 47) && (str.charAt(i + 1) == 47)) {
        lineFlag = false;
        i = i + 1;

      } else if ((str.charAt(i) == 47) && (str.charAt(i + 1) == '*')) {
        System.out.println(str.charAt(i) == 47);
        System.out.println(str.charAt(i + 1) == 42);
        System.out.println(str.charAt(i + 1) == '*');
        startFlag++;
        charFlag = false;
        if (startFlag == endFlag) {
          start = i;
        }
        i = i + 2;
      } else if (str.charAt(i) == 42 && str.charAt(i + 1) == 47) {
        endFlag++;
        if (endFlag == startFlag) {
          charFlag = true;
        }
        i = i + 2;
      } else if (str.charAt(i) == 10) {
        lineFlag = true;
      }
      if (lineFlag && charFlag) {
        result.append(str.charAt(i));
      }

    }
    return result.toString();
  }


  public static String containsRelationStr(String c) {
    if (c.equals(">=") || c.contains(">=")) {
      return ">=";
    } else if (c.equals("<=") || c.contains("<=")) {
      return "<=";
    } else if (c.equals("=") || c.contains("=")) {
      return "=";
    } else if (c.equals("<>") || c.contains("<>")) {
      return "<>";
    } else if (c.equals("<") || c.contains("<")) {
      return "<";
    } else if (c.equals(">") || c.contains(">")) {
      return ">";
    } else if (c.equals("in")) {
      return "in";
    } else if (c.equals("between")) {
      return "between";
    } else {
      return "";
    }
  }

  public static boolean containsRelationChar(String c) {
    if (c.equals("!") || c.contains("!")) {
      return true;
    } else if (c.equals("=") || c.contains("=")) {
      return true;
    } else if (c.equals("<") || c.contains("<")) {
      return true;
    } else if (c.equals(">") || c.contains(">")) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isConnStr(String str) {
    if ("and".equals(str) || "or".equals(str)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 检查是否是有效的条件
   *
   * @param str
   * @return
   */
  public static boolean isValidCondition(String str) {
    int startNum = 0;
    int endNum = 0;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == '(') {
        startNum++;
      } else if (str.charAt(i) == ')') {
        endNum++;
      }
    }
    if (startNum == endNum) {
      return true;
    } else {
      return false;
    }

  }

  public static String formatWhereStr(String str) {
    StringBuilder res = new StringBuilder();
    str = str.replace(" in(", " in (");
    str = str.replace(" between(", " between (");
    for (int i = 0; i < str.length(); i++) {
      String s1 = str.charAt(i) + "";
      String s2 = "";
      if (i < str.length() - 1) {
        s2 += str.charAt(i + 1);
      }
      if (containsRelationChar(s1)) {
        res.append(" ");
        if (containsRelationChar(s2)) {
          res.append(s1);
          res.append(s2);
          i++;
        } else {
          res.append(s1);
        }
        res.append(" ");
      } else {
        res.append(s1);
      }
    }
    return removeMulSpace(res.toString());
  }

  public static void main(String[] args) {

    String msg1 = "SELECT                                                                                                \n"
        + "     t1.up_id                                                         AS up_id                        \n"
        + "    ,t1.imei                                                                                          \n"
        + "    ,t2.up_type_cd                                                    AS up_type_cd                   \n"
        + "    ,FROM_UNIXTIME(UNIX_TIMESTAMP(),'yyyy-MM-dd HH:mm:ss')            AS etl_time                     \n"
        + "    FROM temp.tmp_ads_hwmovie_play_content_dm_2 t1                                                    \n"
        + "LEFT OUTER JOIN                                                                                       \n"
        + "(                                                                                                     \n"
        + "SELECT                                                                                                \n"
        + "     up_id                                                             AS up_id                       \n"
        + "    ,up_type_cd                                                        AS register_acct_type_cd       \n"
        + "FROM bicoredata.dwd_pty_up_ds_his                                                                     \n"
        + "WHERE end_date > '$date'                                                                              \n"
        + "AND start_date <= '$date'                                                                             \n"
        + ") t2                                                                                                  \n"
        + "ON t1.up_id = t2.up_id                                                                                \n"
        + "where t1.pt_d='20171215'                                                                              \n";

    String msg2 = "SELECT   //这是第一个注释 \n"
        + "     t1.up_id           /* 这是第二个注释*/                                              AS up_id                        \n"
        + "    ,t1.imei                                                                                          \n"
        + "    ,t2.up_type_cd                                                    AS up_type_cd #这是第三个注释                  \r\n"
        + "    ,FROM_UNIXTIME(UNIX_TIMESTAMP(),'yyyy-MM-dd HH:mm:ss')            AS etl_time                     \r\n"
        + "/*  啊啊\n"
        + "this is third comment\r\n"
        + "this is five comment\n"
        + "*/   \n"
        + "    ,t2.up_type                                                       AS up_type                      \n"
        + "    FROM temp.tmp_ads_hwmovie_play_content_dm_2 t1                                                    \n"
        + "where t1.pt_d='20171215'                                                                              \n";
    String msg3 = "select                                                        \n" +
        "                    t1.success_flg,                           \n" +
        "                    t1.err_code,                              \n" +
        "                    t1.err_desc,                              \n" +
        "                    t1.package_name                           \n" +
        "                from bicoredata.dwd_evt_up_oper_log_dm t1     \n" +
        "                left join                                      \n" +
        "                bicoredata.dwd_evt_up_oper_log_fail_dm t2      \n" +
        "                on                                             \n" +
        "                t1.package_name=t2.package_name and t1.col=t2.col               \n" +
        "                right join                                      \n" +
        "                bicoredata.dwd_evt_up_oper_log_fail1_dm t3      \n" +
        "                on                                             \n" +
        "                t3.package_name=t2.package_name                \n" +
        "                where t2.name='huawei'                         \n";


    String str4 = "select                                                                                                                                                                               \n" +
        "    d.cust_id_map as uid,                                                                                                                                                            \n" +
        "    a.*,                                                                                                                                                                             \n" +
        "    d.register_time,                                                                                                                                                                 \n" +
        "    d.register_ip                                                                                                                                                                    \n" +
        "from                                                                                                                                                                                 \n" +
        "    (                                                                                                                                                                                \n" +
        "        select                                                                                                                                                                       \n" +
        "            up_id as id,                                                                                                                                                             \n" +
        "            user_account,                                                                                                                                                            \n" +
        "            ip,                                                                                                                                                                      \n" +
        "            concat(regexp_extract(ip ,'([0-9]{1,3}[\\.]){3}',0),'*') as ip_segment,                                                                                                   \n" +
        "            up_oper_type_cd as oper_type,                                                                                                                                            \n" +
        "            oper_id,                                                                                                                                                                 \n" +
        "            server_name,                                                                                                                                                             \n" +
        "            req_client_type_cd as req_client_type,                                                                                                                                   \n" +
        "            interface_name,                                                                                                                                                          \n" +
        "            up_type_cd as account_type,                                                                                                                                              \n" +
        "            channel_id as channel,                                                                                                                                                   \n" +
        "            get_json_object(get_json_object(oper_detail, '$.deviceInfo'), '$.terminalType') as terminal_type,                                                                        \n" +
        "            imei,                                                                                                                                                                    \n" +
        "            substr(oper_req_time,0,19) as req_time,                                                                                                                                  \n" +
        "            substr(oper_time,0,19)as oper_time,                                                                                                                                      \n" +
        "            user_agent,                                                                                                                                                              \n" +
        "            if(success_flg=1,TRUE,FALSE) as success_flag,                                                                                                                            \n" +
        "            get_json_object(ip2areainfo(ip),'$.country') as ip_country,                                                                                                              \n" +
        "            get_json_object(ip2areainfo(ip),'$.province') as ip_prov,                                                                                                                \n" +
        "            get_json_object(ip2areainfo(ip),'$.city') as ip_city,                                                                                                                    \n" +
        "            err_code,                                                                                                                                                                \n" +
        "            err_desc,                                                                                                                                                                \n" +
        "            get_json_object(oper_detail, '$.verifyRiskFlag') as risk_flag,                                                                                                           \n" +
        "            get_json_object(oper_detail, '$.appID') as app_id,                                                                                                                       \n" +
        "            get_json_object(oper_detail, '$.version') as version,                                                                                                                    \n" +
        "            package_name,                                                                                                                                                            \n" +
        "            get_json_object(oper_detail, '$.transactionID') as transaction_id,                                                                                                       \n" +
        "            get_json_object(oper_detail, '$.flowID') as flow_id,                                                                                                                     \n" +
        "            get_json_object(oper_detail, '$.uuid') as uuid,                                                                                                                          \n" +
        "            oper_detail as other_params,                                                                                                                                             \n" +
        "            get_json_object(oper_detail, '$.twoStepVerifyCode') as ts_code,                                                                                                          \n" +
        "            get_json_object(oper_detail, '$.verifyAccountType') as ts_type                                                                                                           \n" +
        "        from                                                                                                                                                                         \n" +
        "            (                                                                                                                                                                        \n" +
        "                select                                                                                                                                                               \n" +
        "                    up_id,                                                                                                                                                           \n" +
        "                    aesdecrypt4ad(user_acct_id) as user_account,                                                                                                                     \n" +
        "                        CASE WHEN size(split(user_ip_addr,':'))=1 THEN UpDecryption(user_ip_addr)                                                                                    \n" +
        "                        WHEN size(split(user_ip_addr,':')) in(2,3) THEN AesCBCUpDecry(CONCAT(split(user_ip_addr,':')[0],':',split(user_ip_addr,':')[1]),'hota')                      \n" +
        "                        ELSE NULL END AS                                                                                                                                             \n" +
        "                    ip,                                                                                                                                                              \n" +
        "                    up_oper_type_cd,                                                                                                                                                 \n" +
        "                    oper_id,                                                                                                                                                         \n" +
        "                    server_name,                                                                                                                                                     \n" +
        "                    req_client_type_cd,                                                                                                                                              \n" +
        "                    interface_name,                                                                                                                                                  \n" +
        "                    up_type_cd,                                                                                                                                                      \n" +
        "                    channel_id,                                                                                                                                                      \n" +
        "                    imei,                                                                                                                                                            \n" +
        "                    oper_detail,                                                                                                                                                     \n" +
        "                    oper_req_time,                                                                                                                                                   \n" +
        "                    oper_time,                                                                                                                                                       \n" +
        "                    user_agent,                                                                                                                                                      \n" +
        "                    success_flg,                                                                                                                                                     \n" +
        "                    err_code,                                                                                                                                                        \n" +
        "                    err_desc,                                                                                                                                                        \n" +
        "                    package_name                                                                                                                                                     \n" +
        "                from bicoredata.dwd_evt_up_oper_log_dm                                                                                                                               \n" +
        "                where interface_name='userLoginAuth' and server_name='UP' and  pt_d='20171213'                                                                                       \n" +
        "            union all                                                                                                                                                                \n" +
        "                select                                                                                                                                                               \n" +
        "                    up_id,                                                                                                                                                           \n" +
        "                    aesdecrypt4ad(user_acct_id) as user_account,                                                                                                                     \n" +
        "                        CASE WHEN size(split(user_ip_addr,':'))=1 THEN UpDecryption(user_ip_addr)                                                                                    \n" +
        "                        WHEN size(split(user_ip_addr,':')) in(2,3) THEN AesCBCUpDecry(CONCAT(split(user_ip_addr,':')[0],':',split(user_ip_addr,':')[1]),'hota')                      \n" +
        "                        ELSE NULL END AS                                                                                                                                             \n" +
        "                    ip,                                                                                                                                                              \n" +
        "                    up_oper_type_cd,                                                                                                                                                 \n" +
        "                    oper_id,                                                                                                                                                         \n" +
        "                    server_name,                                                                                                                                                     \n" +
        "                    req_client_type_cd,                                                                                                                                              \n" +
        "                    interface_name,                                                                                                                                                  \n" +
        "                    up_type_cd,                                                                                                                                                      \n" +
        "                    channel_id,                                                                                                                                                      \n" +
        "                    imei,                                                                                                                                                            \n" +
        "                    oper_detail,                                                                                                                                                     \n" +
        "                    oper_req_time,                                                                                                                                                   \n" +
        "                    oper_time,                                                                                                                                                       \n" +
        "                    user_agent,                                                                                                                                                      \n" +
        "                    success_flg,                                                                                                                                                     \n" +
        "                    err_code,                                                                                                                                                        \n" +
        "                    err_desc,                                                                                                                                                        \n" +
        "                    package_name                                                                                                                                                     \n" +
        "                from bicoredata.dwd_evt_up_oper_log_fail_dm                                                                                                                          \n" +
        "                where interface_name='userLoginAuth' and server_name='UP' and  pt_d='20171213'                                                                                       \n" +
        "                                                                                                                                                                                     \n" +
        "			)t                                                                                                                                                                        \n" +
        "    )a                                                                                                                                                                               \n" +
        "left join                                                                                                                                                                            \n" +
        "    (                                                                                                                                                                                \n" +
        "        select * from                                                                                                                                                                \n" +
        "        (                                                                                                                                                                            \n" +
        "            select                                                                                                                                                                   \n" +
        "            register_time,                                                                                                                                                           \n" +
        "                IF(ISEMPTY(AESDecrypt4AD(register_ip_addr)),register_ip_addr, AESDecrypt4AD(register_ip_addr))                                                                       \n" +
        "            as                                                                                                                                                                       \n" +
        "            register_ip,                                                                                                                                                             \n" +
        "            up_id                                                                                                                                                                    \n" +
        "            from bicoredata.dwd_pty_up_ds_his where end_date='99991231'                                                                                                              \n" +
        "        )b                                                                                                                                                                           \n" +
        "        left join                                                                                                                                                                    \n" +
        "        (                                                                                                                                                                            \n" +
        "            select * from bicoredata.dwd_cust_mapping where pt_type='upid'                                                                                                           \n" +
        "        )c                                                                                                                                                                           \n" +
        "        on c.cust_id=b.up_id                                                                                                                                                         \n" +
        "    )d                                                                                                                                                                               \n" +
        "on a.id=d.cust_id                                                                                                                                                                    \n";
    String str5 = "SELECT                                                                  \n" +
        "   sect_id                                                              \n" +
        "  ,super_sect_id                                                        \n" +
        "  ,sect_type_cd                                                         \n" +
        "  ,sect_name                                                            \n" +
        "  ,disp_status_cd                                                       \n" +
        "  ,disp_seq                                                             \n" +
        "  ,style_id                                                             \n" +
        "  ,thread_cnt                                                           \n" +
        "  ,post_cnt                                                             \n" +
        "  ,today_post_cnt                                                       \n" +
        "  ,ytday_post_cnt                                                       \n" +
        "  ,rank                                                                 \n" +
        "  ,day_bf_ytday_rank                                                    \n" +
        "  ,final_publish                                                        \n" +
        "  ,second_domain                                                        \n" +
        "  ,group_rank                                                           \n" +
        "  ,group_pub_point                                                      \n" +
        "  ,arch_tbl_flg                                                         \n" +
        "  ,recommend_sect                                                       \n" +
        "  ,collect_cnt                                                          \n" +
        "  ,share_cnt                                                            \n" +
        "  ,etl_time                                                             \n" +
        "FROM bicoredata.dwd_onl_fans_forum_sect_ds T2                           \n" +
        "WHERE T2.pt_d = '$last_date'                                               \n" +
        "AND NOT EXISTS (                                                        \n" +
        "   SELECT 1 FROM temp.tmp_dwd_onl_fans_forum_sect_ds T3                 \n" +
        "   WHERE T2.sect_id = T3.sect_id and T2.sect_id = T3.sect_id                                   \n" +
        ")                                                                       \n" +
        "AND (t2.start_date <= '$date' or t2.start_date>'20171225' ) and t2.pt_d between '20171222' and '20171223' and t2.pt_h in('20171221','20171222','20171223')\n";

    //System.out.println(new ParseHiveSQL().removeComment(msg2));
    str5 = removeComment(str5);
    System.out.println(new ParseQuery().itratorQuery(str5));


  }

}

import com.huawei.spring.catalogue.SQLScan.DateParameter;
import com.huawei.spring.catalogue.parse.BtDateValue;
import com.huawei.spring.catalogue.parse.BtSWCNode;
import com.huawei.spring.catalogue.parse.BtSWNodeGroup;
import com.huawei.spring.core.implement.SQLScan.custom.BigTableUtil;
import com.huawei.spring.core.implement.SQLScan.util.DateUtil;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MyTest {

//  @Test
//  public void test001() throws Exception {
//    InputStream stream = MyTest.class.getResourceAsStream("colRoot.txt");
//    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//    StringBuilder builder = new StringBuilder();
//    StringBuilder builder2 = new StringBuilder();
////    if (null == stream) {
////      System.out.println("failed!");
////    } else {
////      System.out.println("success!");
////    }
//    String line = null;
//    while ((line = reader.readLine()) != null) {
//      builder.append(line).append("\n");
//    }
//    String[] splits = builder.toString().split("\n");
//    for (String s : splits) {
//      s = s.replaceAll(" +", "");
//      String[] split = s.split("=");
//      String str = split[split.length - 1];
//      String str1 = str.toLowerCase();
//      if (str1.startsWith("_")) {
//        str1 = str1.substring(1, str1.length());
//      }
//      builder2.append(str1).append("=").append(str).append("\n");
//    }
//    String string = builder2.toString();
//    System.out.println(string);
//  }


  @Test
  public void test002() throws Exception {
//    BtSWNodeGroup group = new BtSWNodeGroup();
//    List<BtSWCNode> btSWCNodes = new ArrayList<>();
//
//    BtSWCNode node1 = new BtSWCNode(true, "pt_d > 20180101");
//    node1.setPartition("pt_d > 20180101"); node1.setOrigin("pt_d > 20180101");
//    List<BtDateValue> values1 = new ArrayList<>();
//    BtDateValue value1 = new BtDateValue("20180101", false, 3);
//    values1.add(value1); node1.setValues(values1);
//
//    BtSWCNode node2 = new BtSWCNode(true, "pt_d < 20180104");
//    node2.setPartition("pt_d < 20180104"); node2.setOrigin("pt_d < 20180104");
//    List<BtDateValue> values2 = new ArrayList<>();
//    BtDateValue value2 = new BtDateValue("20180104", false, 1);
//    values2.add(value2); node2.setValues(values2);
//
//    btSWCNodes.add(node1); btSWCNodes.add(node2);
//    group.setBtSWCNodes(btSWCNodes);
//
//    BigTableUtil.parsePartitionNew(group, 3, 1);
  }

  @Test
  public void test003() throws Exception {
    // new ParseDriver().parse(getStr());
    // String s = "pt_d = 1 and pt_d > 1 and pt_d < 4 and pt_d <= 3 or pt_d >= 7";
    // String[] split = s.split("[=<>]");
    // System.out.println(split);
  }

  @Test
  public void test004() throws Exception {
//    String classPath = "com.huawei.spring.core.implement.SQLScan.util.DateUtil";
//    String date = null;
//    String key = "$date";
//    DateParameter dateParameter = new DateParameter();
//    String value = dateParameter.getDateMap().get(key);
//    Class<?> classDateUtil = Class.forName(classPath);
//    Method method = classDateUtil.getMethod(value, null);
//    date = (String) method.invoke(null, null);
//    System.out.println(date);
  }

  private String getStr() {
    return "INSERT OVERWRITE TABLE biads.ads_hispace_hms_payment_conversion_funnel_stat_dm\n" +
        "PARTITION(pt_d = '$date',pt_type = 'pay_login_fail')\n" +
        "SELECT \n" +
        "    t1.pay_flg\n" +
        "    ,t2.up_id\n" +
        "    ,t2.app_id\n" +
        "    ,t2.app_ver\n" +
        "    ,t2.type_value\n" +
        "FROM \n" +
        "    (\n" +
        "        SELECT \n" +
        "            pay_flg\n" +
        "            ,up_id\n" +
        "            ,app_id\n" +
        "            ,app_ver\n" +
        "        FROM \n" +
        "            biads.ads_hispace_hms_payment_conversion_funnel_stat_dm\n" +
        "        WHERE \n" +
        "            pt_d = '$date' \n" +
        "            AND pt_type = 'pay_login'\n" +
        "    )t1\n" +
        "JOIN\n" +
        "    (\n" +
        "        SELECT \n" +
        "            SHA256(SPLIT(non_stru_field,'\\\\\\\\|')[0]) AS up_id\n" +
        "            ,SUBSTR(GETHICLOUDAPPID(SPLIT(non_stru_field,'\\\\\\\\|')[1]),2) AS app_id\n" +
        "            ,SPLIT(non_stru_field,'\\\\\\\\|')[3] AS app_ver\n" +
        "            ,COUNT(*) AS type_value\n" +
        "        FROM \n" +
        "            dwd_evt_bisdk_customize_dm\n" +
        "        WHERE \n" +
        "            pt_d = '$date' \n" +
        "            AND pt_service='up'\n" +
        "            AND oper_id = 'PAY_PAYMENT_LOGIN' \n" +
        "            AND app_ver > '2.5.3.300'\n" +
        "            AND SPLIT(non_stru_field,'\\\\\\\\|')[4] = '2'\n" +
        "        GROUP BY SPLIT(non_stru_field,'\\\\\\\\|')[0]\n" +
        "                 ,SUBSTR(GETHICLOUDAPPID(SPLIT(non_stru_field,'\\\\\\\\|')[1]),2)\n" +
        "                 ,SPLIT(non_stru_field,'\\\\\\\\|')[3]\n" +
        "    )t2\n" +
        "ON \n" +
        "    (\n" +
        "        t1.up_id = t2.up_id\n" +
        "        AND t1.app_id = t2.app_id\n" +
        "        AND t1.app_ver = t2.app_ver\n" +
        "    )";
  }
}

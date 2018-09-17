//package catalogue;
//
//import com.huawei.spring.catalogue.parse.CreateBean;
//import com.huawei.spring.catalogue.parse.ParseCreate;
//import com.huawei.spring.catalogue.parse.ParseQuery;
//import com.huawei.spring.catalogue.parse.QueryBean;
//import org.junit.Test;
//
//public class MyTest {
//  @Test
//  public void test001() throws Exception {
////    ParseCreate parseCreate = new ParseCreate();
////    CreateBean createBean = parseCreate.parseCreateSql(getStr01());
//    QueryBean bean = new ParseQuery().itratorQuery(getStr02());
//  }
//
//  private String getStr02() {
//    return "SELECT bt1.pay_up_id, bt2.merch_name, bt1.dev_app_id " +
//        "FROM bt1 b1 left join bt2 b2 on bt1.id = bt2.id left join bt3 b3 on bt2.id = bt3.id " +
//        "WHERE bt1.pt_d = '20180207' AND bt2.pt_d = '20180207'";
//  }
//
//  private String getStr03() {
//    return "SELECT bt2.pay_up_id, bt2.merch_name, bt2.dev_app_id FROM bt1 bt2 WHERE bt1.pt_d = '20180207' AND bt2.pt_d = '20180207'";
//  }
//
//  private String getStr01() {
//    return "create table temp.tmp_ywx463765_20180313_t2 as\n" +
//        "SELECT \n" +
//        "    t.app_id,\n" +
//        "    merch_name,\n" +
//        "    dev_app_id,\n" +
//        "    pay_up_id,\n" +
//        "    imei,\n" +
//        "    SUM(t.fee_application)  AS fee_application\n" +
//        "FROM \n" +
//        "(\n" +
//        "    SELECT \n" +
//        "        a.app_id,\n" +
//        "        merch_name,\n" +
//        "        dev_app_id,\n" +
//        "        pay_up_id,\n" +
//        "        imei,\n" +
//        "        SUM(a.fee_application) AS fee_application\n" +
//        "    FROM\n" +
//        "    (\n" +
//        "        SELECT\n" +
//        "            pay_up_id,\n" +
//        "            merch_name,\n" +
//        "            dev_app_id,\n" +
//        "            pay_up_id,\n" +
//        "            imei,\n" +
//        "            IF(dev_app_id='10824308','C10829613',gethicloudappid(dev_app_id)) AS app_id,\n" +
//        "            SUM(pay_amt)                                                      AS fee_application \n" +
//        "        FROM bicoredata.dwd_sal_order_pay_ds as a left join  bicoredata.dwd_sal_order_pay_ds1 as b on a.id = b.id\n" +
//        "        WHERE pt_d = '20180207'\n" +
//        "            AND to_date(txn_finish_time)='2018-02-07'\n" +
//        "            and gethicloudappid(dev_app_id) in('C100156805','C100171805','C100207501')\n" +
//        "            AND order_type='PURCHASE'\n" +
//        "            AND pay_status_cd IN ('0','3') \n" +
//        "            AND (gethicloudappid(dev_app_id) IN ('C10847440','C10848063','C10848060','C10848046','C10847461','C10847455','C10846999','C10846997','C10848065',\n" +
//        "            'C10847444','C34509','C10336063','C10829513','C10824024','C10044781','C10829520','C10829523','C10827562','C10825307','C10824425',\n" +
//        "            'C10824434','C10825288','C10824429','C10824406','C10824419','C10825271','C10824375','C10824393','C10824112','C10824398','C10819808',\n" +
//        "            'C10825313','C10825299','C10829613','C10823057','C10827543','C10829192','C10827513','C10827529','C10608950')\n" +
//        "            OR (project_id ='9211135-2' AND dev_app_id NOT IN ( '10733025','10733029','10733021', '10049053','10614915','10316274','10575037','10399039','10481893','10333057','10557200','10026727','10537044')))     \n" +
//        "        GROUP BY pay_up_id,IF(dev_app_id='10824308','C10829613',gethicloudappid(dev_app_id)),merch_name,dev_app_id,pay_up_id,imei\n" +
//        "    )a\n" +
//        "    JOIN\n" +
//        "    (\n" +
//        "        SELECT \n" +
//        "            cust_id\n" +
//        "        FROM dwd_cust_mapping \n" +
//        "        WHERE pt_type='upid'\n" +
//        "    )t3\n" +
//        "    on (a.pay_up_id=t3.cust_id)\n" +
//        "    GROUP BY a.app_id,merch_name,\n" +
//        "            dev_app_id,\n" +
//        "            pay_up_id,\n" +
//        "            imei\n" +
//        ")t\n" +
//        "left join\n" +
//        "(\n" +
//        "    SELECT \n" +
//        "        app_id \n" +
//        "    from biads.ads_hispace_co_blacklist_all_ds\n" +
//        ")t4\n" +
//        "on t.app_id=t4.app_id\n" +
//        "where t4.app_id is null\n" +
//        "GROUP BY t.app_id,merch_name,\n" +
//        "        dev_app_id,\n" +
//        "        pay_up_id,\n" +
//        "        imei\n" +
//        "\n";
//  }
//}

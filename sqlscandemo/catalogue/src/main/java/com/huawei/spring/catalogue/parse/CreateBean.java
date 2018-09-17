package com.huawei.spring.catalogue.parse;

import java.util.ArrayList;

/**
 * Created by zWX450505 on 2017/12/13.
 */
public class CreateBean {

  /**
   * 原始的sql语句
   */
  private String sqlContent;
  /**
   * TOK_TABNAME
   * 表名称
   */
  private String tabName;
  /**
   * EXTERNAL
   * 外部表
   */
  private String tabExternal;
  /**
   * QUERY
   * 建立临时表时
   */
  private QueryBean queryBean;

  /**
   * operType
   * 0,create table
   * 1,create table as
   */
  private String operType;

  /**
   * TOK_IFNOTEXISTS
   * 表是否存在
   */
  private String tabIfNotExists;
  /**
   * TOK_TABCOLLIST
   * 表的列
   */
  private ArrayList<Column> tabColList;
  /**
   * TOK_TABLECOMMENT
   * 表注释
   */
  private String tabComment;
  /**
   * TOK_TABLEPARTCOLS
   * 表分区
   */
  private ArrayList<Column> tabPartCols;
  /**
   * 域分隔符
   * TOK_TABLEROWFORMATFIELD
   */
  private String tabRowFormatField;
  /**
   * 行分割符
   * TOK_TABLEROWFORMATLINES
   */
  private String tabRowFormatLines;
  /**
   * 文件压缩格式
   * TOK_FILEFORMAT_GENERIC
   */
  private String tabFileformatGeneric;
  /**
   * 表位置
   * TOK_TABLELOCATION
   */
  private String tabLocation;

  /**
   * 表属性
   * TBLPROPERTIES
   * TBLPROPERTIES('orc.compress'='ZLIB')
   */
  private String tabProperties;

  public CreateBean() {
  }

  public String getSqlContent() {
    return sqlContent;
  }

  public void setSqlContent(String sqlContent) {
    this.sqlContent = sqlContent;
  }

  public String getTabName() {
    return tabName;
  }

  public void setTabName(String tabName) {
    this.tabName = tabName;
  }

  public String getTabExternal() {
    return tabExternal;
  }

  public void setTabExternal(String tabExternal) {
    this.tabExternal = tabExternal;
  }

  public QueryBean getQueryBean() {
    return queryBean;
  }

  public void setQueryBean(QueryBean queryBean) {
    this.queryBean = queryBean;
  }

  public String getTabIfNotExists() {
    return tabIfNotExists;
  }

  public void setTabIfNotExists(String tabIfNotExists) {
    this.tabIfNotExists = tabIfNotExists;
  }

  public ArrayList<Column> getTabColList() {
    return tabColList;
  }

  public void setTabColList(ArrayList<Column> tabColList) {
    this.tabColList = tabColList;
  }

  public String getTabComment() {
    return tabComment;
  }

  public void setTabComment(String tabComment) {
    this.tabComment = tabComment;
  }

  public ArrayList<Column> getTabPartCols() {
    return tabPartCols;
  }

  public void setTabPartCols(ArrayList<Column> tabPartCols) {
    this.tabPartCols = tabPartCols;
  }

  public String getTabRowFormatField() {
    return tabRowFormatField;
  }

  public void setTabRowFormatField(String tabRowFormatField) {
    this.tabRowFormatField = tabRowFormatField;
  }

  public String getTabRowFormatLines() {
    return tabRowFormatLines;
  }

  public void setTabRowFormatLines(String tabRowFormatLines) {
    this.tabRowFormatLines = tabRowFormatLines;
  }

  public String getTabFileformatGeneric() {
    return tabFileformatGeneric;
  }

  public void setTabFileformatGeneric(String tabFileformatGeneric) {
    this.tabFileformatGeneric = tabFileformatGeneric;
  }

  public String getTabLocation() {
    return tabLocation;
  }

  public void setTabLocation(String tabLocation) {
    this.tabLocation = tabLocation;
  }

  public String getTabProperties() {
    return tabProperties;
  }

  public void setTabProperties(String tabProperties) {
    this.tabProperties = tabProperties;
  }

  public String getOperType() {
    return operType;
  }

  public void setOperType(String operType) {
    this.operType = operType;
  }

    

    /*
    (
    TOK_CREATETABLE --11个子节点
            (TOK_TABNAME--2个子节点
                    bicoredata
                    dws_device_hota_active_dm
            )
    EXTERNAL
            TOK_IFNOTEXISTS
    TOK_LIKETABLE
            (TOK_TABCOLLIST (TOK_TABCOL imei (TOK_VARCHAR 128) '设备编号') (TOK_TABCOL did (TOK_VARCHAR 128) '设备唯一号') (TOK_TABCOL region_cd (TOK_VARCHAR 128) '行政区划代码') (TOK_TABCOL series_name (TOK_VARCHAR 128) '系列名称') (TOK_TABCOL hw_device_type (TOK_VARCHAR 256) '华为设备类型') (TOK_TABCOL device_name (TOK_VARCHAR 128) '外部型号') (TOK_TABCOL hw_device_flg TOK_SMALLINT '华为设备标志') (TOK_TABCOL currt_emui_ver (TOK_VARCHAR 128) '当前EMUI版本') (TOK_TABCOL currt_rom_ver (TOK_VARCHAR 256) '当前ROM版本') (TOK_TABCOL upgrade_package_affil_catalog (TOK_VARCHAR 128) '每个业务局点对应的一个业务产品') (TOK_TABCOL first_usage_time (TOK_VARCHAR 30) '首次使用时间') (TOK_TABCOL last_usage_time (TOK_VARCHAR 30) '最近使用时间') (TOK_TABCOL etl_time (TOK_VARCHAR 30) 'ETL时间'))
            (TOK_TABLECOMMENT '设备业务HOTA局点活跃汇总日表')
            (TOK_TABLEPARTCOLS (TOK_TABCOLLIST (TOK_TABCOL pt_d (TOK_VARCHAR 8) '天分区') (TOK_TABCOL pt_commc_point (TOK_VARCHAR 128) '局点分区')))
            (TOK_TABLEROWFORMAT (TOK_SERDEPROPS (TOK_TABLEROWFORMATFIELD '') (TOK_TABLEROWFORMATLINES '')))
            (TOK_FILEFORMAT_GENERIC ORC)
            (TOK_TABLELOCATION '/AppData/BIProd/DWS/DEVICE/dws_device_hota_active_dm')
            (TOK_TABLEPROPERTIES (TOK_TABLEPROPLIST (TOK_TABLEPROPERTY 'orc.compress' 'ZLIB')))

            )
<EOF>
    */

/*
    "CREATE TABLE IF NOT EXISTS temp.tmp_ads_hwmovie_play_content_dm_2                    "+
            "AS                                                                                   "+
            "SELECT                                                                               "+
            "     up_id                                       AS up_id                            "+
            "    ,imei                                        AS imei                             "+
            "    ,sum(play_end_cnt)                           AS play_end_cnt                     "+
            "    ,sum(stop_cnt)                               AS stop_cnt                         "+
            "    ,sum(stop_duration)                          AS stop_duration                    "+
            " FROM temp.tmp_ads_hwmovie_play_content_dm_1                                         "+
            "GROUP BY up_id                                                                       "+
            ",imei                                                                                "+
            ",up_id                                                                           ";                                                                                     ";

            (TOK_CREATETABLE
            (TOK_TABNAME
                     temp
		tmp_ads_hwmovie_play_content_dm_2
            )
    TOK_IFNOTEXISTS
    TOK_LIKETABLE
            (TOK_QUERY (TOK_FROM (TOK_TABREF (TOK_TABNAME temp tmp_ads_hwmovie_play_content_dm_1)))
            (TOK_INSERT
            (TOK_DESTINATION (TOK_DIR TOK_TMP_FILE))
            (TOK_SELECT
            (TOK_SELEXPR (TOK_TABLE_OR_COL up_id) up_id)
            (TOK_SELEXPR (TOK_TABLE_OR_COL imei) imei)
            (TOK_SELEXPR (TOK_FUNCTION sum (TOK_TABLE_OR_COL play_end_cnt)) play_end_cnt)
            (TOK_SELEXPR (TOK_FUNCTION sum (TOK_TABLE_OR_COL stop_cnt)) stop_cnt)
            (TOK_SELEXPR (TOK_FUNCTION sum (TOK_TABLE_OR_COL stop_duration)) stop_duration)
            )
            (TOK_GROUPBY
            (TOK_TABLE_OR_COL up_id)
				(TOK_TABLE_OR_COL imei)
            (TOK_TABLE_OR_COL up_id)
            )
            )
            )
            )
	<EOF>
*/

  @Override
  public String toString() {
    return "CreateBean{" +
        "sqlContent='" + sqlContent + '\'' +
        ", tabName='" + tabName + '\'' +
        ", tabExternal='" + tabExternal + '\'' +
        ", queryBean=" + queryBean +
        ", operType='" + operType + '\'' +
        ", tabIfNotExists='" + tabIfNotExists + '\'' +
        ", tabColList=" + tabColList +
        ", tabComment='" + tabComment + '\'' +
        ", tabPartCols=" + tabPartCols +
        ", tabRowFormatField='" + tabRowFormatField + '\'' +
        ", tabRowFormatLines='" + tabRowFormatLines + '\'' +
        ", tabFileformatGeneric='" + tabFileformatGeneric + '\'' +
        ", tabLocation='" + tabLocation + '\'' +
        ", tabProperties='" + tabProperties + '\'' +
        '}';
  }
}

package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.Date;

/**
 * 检查结果
 * @author zWX450505
 *
 */
public class ScanResult implements Serializable {

	private String id;
	private String name;
	private String version;
	private Date created;
	private String status;
	private String head;
	private String level;
	private String message;
	private int startLineNum;
	private int endLineNum;
	private String sqlString;
	private String scriptName;
	private String operType;
	private String ruleId;
	private String ruleName;
	private String blockId;
	private String scriptId;
	private String taskId;
	private String business;
	private String operator;
	private ScanRule scanRule;

	public ScanResult() {
	}

	public ScanResult(String status, String head, String level, String message) {
		this.status = status;
		this.head = head;
		this.level = level;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStartLineNum() {
		return startLineNum;
	}

	public void setStartLineNum(int startLineNum) {
		this.startLineNum = startLineNum;
	}

	public int getEndLineNum() {
		return endLineNum;
	}

	public void setEndLineNum(int endLineNum) {
		this.endLineNum = endLineNum;
	}

	public String getSqlString() {
		return sqlString;
	}

	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

	public String getScriptId() {
		return scriptId;
	}

	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public ScanRule getScanRule() {
		return scanRule;
	}

	public void setScanRule(ScanRule scanRule) {
		this.scanRule = scanRule;
	}

	@Override
	public String toString() {
		return "ScanResult{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", version='" + version + '\'' +
				", created=" + created +
				", status='" + status + '\'' +
				", head='" + head + '\'' +
				", level='" + level + '\'' +
				", message='" + message + '\'' +
				", startLineNum=" + startLineNum +
				", endLineNum=" + endLineNum +
				", sqlString='" + sqlString + '\'' +
				", scriptName='" + scriptName + '\'' +
				", operType='" + operType + '\'' +
				", ruleId='" + ruleId + '\'' +
				", ruleName='" + ruleName + '\'' +
				", blockId='" + blockId + '\'' +
				", scriptId='" + scriptId + '\'' +
				", taskId='" + taskId + '\'' +
				", business='" + business + '\'' +
				", operator='" + operator + '\'' +
				", scanRule=" + scanRule +
				'}';
	}
}

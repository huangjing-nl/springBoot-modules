package com.huawei.spring.catalogue.SQLScan;

import com.huawei.spring.catalogue.util.IdGenerator;

import java.io.Serializable;
import java.util.Set;

/**
 * 脚本块信息
 * @author zWX450505
 *
 */
public class SQLBlock implements Serializable {

	private String id;
	private String name;
	private String version;
	private String blockType;
	private String blockContent;
	private int startLineNum;
	private int endLineNum;
	private String  baseType;
	private String business;
	private String scriptId;
	private String scriptName;
	private String taskId;
	private String operId;
	private Set<ScanRule> rules;
	private Set<ScanResult> results;

	//TODO 缺少一个sql解析驱动对象的属性

	public SQLBlock() {
	}

	public SQLBlock(String baseType, String business, String scriptId, String scriptName, String taskId) {
		this.baseType = baseType;
		this.business = business;
		this.scriptId = scriptId;
		this.scriptName = scriptName;
		this.taskId = taskId;
	}

	public SQLBlock(String name, int startLineNum, int endLineNum, String business, String scriptId, String scriptName, String taskId) {
		this.id = IdGenerator.createUUID();
		this.name = name;
		this.startLineNum = startLineNum;
		this.endLineNum = endLineNum;
		this.business = business;
		this.scriptId = scriptId;
		this.scriptName = scriptName;
		this.taskId = taskId;
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

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public String getBlockContent() {
		return blockContent;
	}

	public void setBlockContent(String blockContent) {
		this.blockContent = blockContent;
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

	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public String getScriptId() {
		return scriptId;
	}

	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getOperId() {
		return operId;
	}

	public void setOperId(String operId) {
		this.operId = operId;
	}

	public Set<ScanRule> getRules() {
		return rules;
	}

	public void setRules(Set<ScanRule> rules) {
		this.rules = rules;
	}

	public Set<ScanResult> getResults() {
		return results;
	}

	public void setResults(Set<ScanResult> results) {
		this.results = results;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	@Override
	public String toString() {
		return "SQLBlock{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", version='" + version + '\'' +
				", blockType='" + blockType + '\'' +
				", blockContent='" + blockContent + '\'' +
				", startLineNum=" + startLineNum +
				", endLineNum=" + endLineNum +
				", baseType='" + baseType + '\'' +
				", business='" + business + '\'' +
				", scriptId='" + scriptId + '\'' +
				", scriptName='" + scriptName + '\'' +
				", taskId='" + taskId + '\'' +
				", operId='" + operId + '\'' +
				", rules=" + rules +
				", results=" + results +
				'}';
	}
}

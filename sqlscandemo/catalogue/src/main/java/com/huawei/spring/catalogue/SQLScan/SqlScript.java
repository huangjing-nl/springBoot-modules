package com.huawei.spring.catalogue.SQLScan;

import com.huawei.spring.catalogue.util.IdGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 脚本信息
 * @author zWX450505
 *
 */
public class SqlScript implements Serializable {

	private String id;
	private String name;
	private String version;
	private long size;
	private String filePath;
	private String baseType;
	private String packageId;
	private String taskId;
	private Date uploaded;
	private OutputComment outputComment;
	private List<SQLBlock> blocks;
	public SqlScript() {
	}

	public SqlScript(String version, String baseType, String taskId) {
		this.version = version;
		this.baseType = baseType;
		this.taskId = taskId;
	}

	public SqlScript(String name, String version, long size, String filePath, String baseType, String taskId, Date uploaded) {
		this.id = IdGenerator.createUUID();
		this.name = name;
		this.version = version;
		this.size = size;
		this.filePath = filePath;
		this.baseType = baseType;
		this.taskId = taskId;
		this.uploaded = uploaded;
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Date getUploaded() {
		return uploaded;
	}

	public void setUploaded(Date uploaded) {
		this.uploaded = uploaded;
	}

	public OutputComment getOutputComment() {
		return outputComment;
	}

	public void setOutputComment(OutputComment outputComment) {
		this.outputComment = outputComment;
	}

	public List<SQLBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<SQLBlock> blocks) {
		this.blocks = blocks;
	}

	@Override
	public String toString() {
		return "SqlScript{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", version='" + version + '\'' +
				", size=" + size +
				", filePath='" + filePath + '\'' +
				", baseType='" + baseType + '\'' +
				", packageId='" + packageId + '\'' +
				", taskId='" + taskId + '\'' +
				", uploaded=" + uploaded +
				", outputComment=" + outputComment +
				", blocks=" + blocks +
				'}';
	}
}

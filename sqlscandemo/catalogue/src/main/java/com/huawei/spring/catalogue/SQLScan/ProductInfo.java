package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


/**
 * 产品信息
 * @author zWX450505
 *
 */
public class ProductInfo implements Serializable {
	
    /**
     * 产品ID
     */
	private String id;
	/**
	 * 产品名称
	 */
	private String name;
	/**
	 * 产品版本
	 */
	private String version;
	/**
	 * 产品迭代轮次
	 */
	private String turn;
	/**
	 * 脚本存放路径
	 */
	private String path;

	private Date created;

	private Date updated;

	private Set<SqlPackage> sqlPackages;

	/**
	 * 脚本文件信息
	 */
	private Set<SqlScript> sqlScripts;

	public ProductInfo() {
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

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Set<SqlPackage> getSqlPackages() {
		return sqlPackages;
	}

	public void setSqlPackages(Set<SqlPackage> sqlPackages) {
		this.sqlPackages = sqlPackages;
	}

	public Set<SqlScript> getSqlScripts() {
		return sqlScripts;
	}

	public void setSqlScripts(Set<SqlScript> sqlScripts) {
		this.sqlScripts = sqlScripts;
	}

	@Override
	public String toString() {
		return "ProductInfo{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", version='" + version + '\'' +
				", turn='" + turn + '\'' +
				", path='" + path + '\'' +
				", created=" + created +
				", updated=" + updated +
				", sqlPackages=" + sqlPackages +
				", sqlScripts=" + sqlScripts +
				'}';
	}
}

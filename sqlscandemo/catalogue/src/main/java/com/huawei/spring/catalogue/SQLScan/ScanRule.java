package com.huawei.spring.catalogue.SQLScan;


import java.io.Serializable;

/**
 * 检查规则
 * @author zWX450505
 *
 */
public class ScanRule implements Serializable {
	private String id;
	private String name;
	private String category;
	private String description;
	private String typeRegexp;
	private String conditionRegexp;
	private String ruleGroup;
	private RuleLevel level;
	private String defaultLevel;
	private String baseType;
	private Business business;
	private String method;
	private int ruleSwitch;

	public ScanRule() {
	}

	public ScanRule(String id, String name) {
		this.id = id;
		this.name = name;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypeRegexp() {
		return typeRegexp;
	}

	public void setTypeRegexp(String typeRegexp) {
		this.typeRegexp = typeRegexp;
	}

	public String getConditionRegexp() {
		return conditionRegexp;
	}

	public void setConditionRegexp(String conditionRegexp) {
		this.conditionRegexp = conditionRegexp;
	}

	public String getRuleGroup() {
		return ruleGroup;
	}

	public void setRuleGroup(String ruleGroup) {
		this.ruleGroup = ruleGroup;
	}

	public RuleLevel getLevel() {
		return level;
	}

	public void setLevel(RuleLevel level) {
		this.level = level;
	}

	public String getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(String defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getRuleSwitch() {
		return ruleSwitch;
	}

	public void setRuleSwitch(int ruleSwitch) {
		this.ruleSwitch = ruleSwitch;
	}

	@Override
	public String toString() {
		return "ScanRule{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", category='" + category + '\'' +
				", description='" + description + '\'' +
				", typeRegexp='" + typeRegexp + '\'' +
				", conditionRegexp='" + conditionRegexp + '\'' +
				", ruleGroup='" + ruleGroup + '\'' +
				", level=" + level +
				", defaultLevel='" + defaultLevel + '\'' +
				", baseType='" + baseType + '\'' +
				", business=" + business +
				", method='" + method + '\'' +
				", ruleSwitch=" + ruleSwitch +
				'}';
	}
}

/*
 * Copyright (c) 2017. ExtantFuture Inc. All Rights Reserved
 */

package com.extantfuture.mars.config.gray;

import java.util.List;

/**
 * 灰度策略
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:50
 */
public class GrayRule {

	// 配置项名 configFileName+key
	private String configKey;
	// 规则类型
	private RuleType type;
	// 名单列表
	private List<String> nameList;
	// 取模规则列表
	private List<Long> percentList;

	public GrayRule() {
		super();
	}

	public GrayRule(String configKey, RuleType type, List<String> nameList, List<Long> percentList) {
		super();
		this.configKey = configKey;
		this.type = type;
		this.nameList = nameList;
		this.percentList = percentList;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public RuleType getType() {
		return type;
	}

	public void setType(RuleType type) {
		this.type = type;
	}

	public List<String> getNameList() {
		return nameList;
	}

	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}

	public List<Long> getPercentList() {
		return percentList;
	}

	public void setPercentList(List<Long> percentList) {
		this.percentList = percentList;
	}

	@Override
	public String toString() {
		return "GrayRule [configKey=" + configKey + ", type=" + type + ", nameList=" + nameList + ", percentList=" + percentList
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configKey == null) ? 0 : configKey.hashCode());
		result = prime * result + ((nameList == null) ? 0 : nameList.hashCode());
		result = prime * result + ((percentList == null) ? 0 : percentList.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrayRule other = (GrayRule) obj;
		if (configKey == null) {
			if (other.configKey != null)
				return false;
		} else if (!configKey.equals(other.configKey))
			return false;
		if (nameList == null) {
			if (other.nameList != null)
				return false;
		} else if (!nameList.equals(other.nameList))
			return false;
		if (percentList == null) {
			if (other.percentList != null)
				return false;
		} else if (!percentList.equals(other.percentList))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}

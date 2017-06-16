package com.extantfuture.mars.config.gray;

import com.extantfuture.mars.util.StringUtil;

/**
 * 灰度规则类型(拒绝/允许)
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:46
 */
public enum RuleType {

	/**
	 * 拒绝
	 */
	DENY("deny"), /**
	 * 允许
	 */
	ALLOW("allow");

	private String value;

	private RuleType(String value) {
		this.value = value;
	}

	/**
	 * 解析类型
	 *
	 * @param value
	 * @return
	 */
	public static RuleType parse(String value) {
		if (StringUtil.isNotEmpty(value)) {
			for (RuleType type : values()) {
				if (type.value.equals(value)) {
					return type;
				}
			}
		}
		return null;
	}

	public String getValue() {
		return value;
	}
}

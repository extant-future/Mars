package com.extantfuture.mars.config.gray;

/**
 * 灰度类型
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:46
 */
public enum GrayType {

	/**
	 * 黑名单
	 */
	BLACK_LIST(0), /**
	 * 白名单
	 */
	WHILE_LIST(1), /**
	 * 灰度比例
	 */
	PERCENT(2);

	private int value;

	private GrayType(int value) {
		this.value = value;
	}

	/**
	 * 解析类型
	 *
	 * @param value
	 * @return
	 */
	public static GrayType parse(int value) {
		for (GrayType type : values()) {
			if (type.value == value) {
				return type;
			}
		}
		return null;
	}

	public int getValue() {
		return value;
	}

}

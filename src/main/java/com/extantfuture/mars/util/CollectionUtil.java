package com.extantfuture.mars.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * utils for collection
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:38
 */
public class CollectionUtil {

	public static <T> boolean isNotEmpty(List<T> list) {
		if (null != list && list.size() > 0) {
			return true;
		}
		return false;
	}

	public static <T> boolean isNotEmpty(Collection<T> list) {
		if (null != list && list.size() > 0) {
			return true;
		}
		return false;
	}

	public static <K, V> boolean isNotEmpty(Map<K, V> map) {
		if (null != map && map.size() > 0) {
			return true;
		}
		return false;
	}

	public static <T> boolean isNotEmpty(T... list) {
		if (null != list && list.length > 0) {
			return true;
		}
		return false;
	}
}

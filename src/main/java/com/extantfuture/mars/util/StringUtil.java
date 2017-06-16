package com.extantfuture.mars.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * utils for string
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:35
 */
public class StringUtil {

	/**
	 * UTF-8编码常量
	 */
	public static final String ENC_UTF8 = "UTF-8";

	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		return s.trim().isEmpty();
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	/**
	 * 字符串全量替换
	 *
	 * @param s    原始字符串
	 * @param src  要替换的字符串
	 * @param dest 替换目标
	 * @return 结果
	 */
	public static String replaceAll(String s, String src, String dest) {
		if (s == null || src == null || dest == null || src.length() == 0)
			return s;
		int pos = s.indexOf(src); // 查找第一个替换的位置
		if (pos < 0)
			return s;
		int capacity = dest.length() > src.length() ? s.length() * 2 : s.length();
		StringBuilder sb = new StringBuilder(capacity);
		int writen = 0;
		for (; pos >= 0; ) {
			sb.append(s, writen, pos); // append 原字符串不需替换部分
			sb.append(dest); // append 新字符串
			writen = pos + src.length(); // 忽略原字符串需要替换部分
			pos = s.indexOf(src, writen); // 查找下一个替换位置
		}
		sb.append(s, writen, s.length()); // 替换剩下的原字符串
		return sb.toString();
	}

	public static String removeAll(String s, String src) {
		return replaceAll(s, src, "");
	}

	/**
	 * 以第一个分割字符串分割
	 *
	 * @param line      原始字符串
	 * @param seperator 分隔符
	 * @return 分割结果
	 */
	public static String[] splitFirst(String line, String seperator) {
		if (line == null || seperator == null || seperator.length() == 0)
			return null;
		ArrayList<String> list = new ArrayList<String>(2);
		int pos1 = 0;
		int pos2 = line.indexOf(seperator, pos1);
		if (pos2 < 0) {
			list.add(line);
		} else {
			list.add(line.substring(pos1, pos2));
			list.add(line.substring(pos2 + 1, line.length()));
		}
		return list.toArray(new String[0]);
	}

	/**
	 * 分割字符串
	 *
	 * @param line      原始字符串
	 * @param seperator 分隔符
	 * @return 分割结果
	 */
	public static String[] split(String line, String seperator) {
		if (line == null || seperator == null || seperator.length() == 0)
			return null;
		ArrayList<String> list = new ArrayList<String>();
		int pos1 = 0;
		int pos2;
		for (; ; ) {
			pos2 = line.indexOf(seperator, pos1);
			if (pos2 < 0) {
				list.add(line.substring(pos1));
				break;
			}
			list.add(line.substring(pos1, pos2));
			pos1 = pos2 + seperator.length();
		}
		// 去掉末尾的空串，和String.split行为保持一致
		for (int i = list.size() - 1; i >= 0 && list.get(i).length() == 0; --i) {
			list.remove(i);
		}
		return list.toArray(new String[0]);
	}

	/**
	 * 拼接字符串
	 *
	 * @param o
	 * @return
	 */
	public static String concat(Object... o) {
		if (null != o && o.length > 0) {
			// 先计算长度
			int len = 0;
			// 收集str，避免重复toString
			List<String> list = new ArrayList<String>(o.length);
			for (Object obj : o) {
				if (null != obj) {
					String str = obj.toString();
					if (isNotEmpty(str)) {
						len += str.length();
						list.add(str);
					}
				}
			}
			if (len > 0) {
				// 一次性申请好容量
				StringBuilder sb = new StringBuilder(len);
				for (String str : list) {
					sb.append(str);
				}
				return sb.toString();
			}
		}
		return null;
	}

	/**
	 * 获取boolean型参数，若输入字符串为null或不能转为boolean，则返回设定的默认值
	 *
	 * @param str      输入字符串
	 * @param defaults 默认值
	 * @return boolean型参数
	 */
	public static boolean convertBoolean(String str, boolean defaults) {
		if (str == null) {
			return defaults;
		}
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			return defaults;
		}
	}

	/**
	 * 获取int参数，若输入字符串为null或不能转为int，则返回设定的默认值
	 *
	 * @param str      输入字符串
	 * @param defaults 默认值
	 * @return int参数
	 */
	public static int convertInt(String str, int defaults) {
		if (str == null) {
			return defaults;
		}
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaults;
		}
	}

	/**
	 * 获取long型参数，若输入字符串为null或不能转为long，则返回设定的默认值
	 *
	 * @param str      输入字符串
	 * @param defaults 默认值
	 * @return long参数
	 */
	public static long convertLong(String str, long defaults) {
		if (str == null) {
			return defaults;
		}
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return defaults;
		}
	}

	/**
	 * 获取double型参数，若输入字符串为null或不能转为double，则返回设定的默认值
	 *
	 * @param str      输入字符串
	 * @param defaults 默认值
	 * @return double型参数
	 */
	public static double convertDouble(String str, double defaults) {
		if (str == null) {
			return defaults;
		}
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return defaults;
		}
	}

	/**
	 * 获取short型参数，若输入字符串为null或不能转为short，则返回设定的默认值
	 *
	 * @param str      输入字符串
	 * @param defaults 默认值
	 * @return short型参数
	 */
	public static short convertShort(String str, short defaults) {
		if (str == null) {
			return defaults;
		}
		try {
			return Short.parseShort(str);
		} catch (Exception e) {
			return defaults;
		}
	}

	/**
	 * 获取float型参数，若输入字符串为null或不能转为float，则返回设定的默认值
	 *
	 * @param str      输入字符串
	 * @param defaults 默认值
	 * @return float型参数
	 */
	public static float convertFloat(String str, float defaults) {
		if (str == null) {
			return defaults;
		}
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return defaults;
		}
	}

	/**
	 * 获取字节数组的UTF-8编码字符串
	 *
	 * @param b
	 * @return
	 */
	public static String getUTF8String(byte[] b) {
		if (b != null) {
			try {
				return new String(b, ENC_UTF8);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}

	/**
	 * 获取字符串的UTF-8编码字节数组
	 *
	 * @param s
	 * @return
	 */
	public static byte[] getUTF8Bytes(String s) {
		if (s != null && s.length() >= 0) {
			try {
				return s.getBytes(ENC_UTF8);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}
}

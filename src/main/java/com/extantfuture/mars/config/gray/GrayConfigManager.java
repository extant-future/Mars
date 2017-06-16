package com.extantfuture.mars.config.gray;

import com.extantfuture.mars.config.ConfigManager;
import com.extantfuture.mars.util.CollectionUtil;
import com.extantfuture.mars.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * manager for gray rule config
 * 灰度配置管理类
 * <p>
 * config like:
 * config_key=allow;10000;%2;%5;	this means allows for default, but id=10000 or id%2==0 or id%5==0 is not allowed
 * config_key=deny;10000;2000;%2;%5;	this means not allows for default, but id=10000 or id=2000 or id%2==0 or id%5==0 is allowed
 * </p>
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:50
 */
public class GrayConfigManager {

	private static final Logger LOG = Logger.getLogger(GrayConfigManager.class.getSimpleName());
	// cache in memory
	private static final Map<String, GrayRule> grayConfigMap = new HashMap<String, GrayRule>();

	/**
	 * judge whether is allowed by config item and id in long
	 * 灰度判断是否允许
	 * most used in our scene, id like user_id
	 * 最常用的方法，比如判断某个用户是否开启某个功能或逻辑
	 *
	 * @param configKey <p>
	 *                  格式: configFileName.key
	 * @param id
	 * @return
	 */
	public static boolean isAllowed(String configKey, long id) {
		GrayRule grayConfig = parseConfig(configKey);
		return isAllowed(grayConfig, id);
	}

	/**
	 * judge whether is allowed by config item and id in long
	 * 灰度判断是否允许
	 *
	 * @param configFileName
	 * @param key
	 * @param id
	 * @return
	 */
	public static boolean isAllowed(String configFileName, String key, long id) {
		GrayRule grayConfig = parseConfig(configFileName, key);
		return isAllowed(grayConfig, id);
	}

	/**
	 * judge whether is allowed by config item and id in string
	 * 灰度判断是否允许
	 *
	 * @param configKey <p>
	 *                  格式: configFileName.key
	 * @param id
	 * @return
	 */
	public static boolean isAllowed(String configKey, String id) {
		GrayRule grayConfig = parseConfig(configKey);
		return isAllowed(grayConfig, id);
	}

	/**
	 * judge whether is allowed by config item and id in string
	 * 灰度判断是否允许
	 *
	 * @param configFileName
	 * @param key
	 * @param id
	 * @return
	 */
	public static boolean isAllowed(String configFileName, String key, String id) {
		GrayRule grayConfig = parseConfig(configFileName, key);
		return isAllowed(grayConfig, id);
	}

	/**
	 * fetch white list
	 * 获取白名单
	 *
	 * @param configKey
	 * @return
	 */
	public static List<String> getWhiteList(String configKey) {
		GrayRule grayConfig = parseConfig(configKey);
		if (Objects.nonNull(grayConfig) && grayConfig.getType().ordinal() == RuleType.DENY.ordinal()) {
			return grayConfig.getNameList();
		}
		return null;
	}

	/**
	 * 灰度判断是否允许
	 *
	 * @param grayConfig
	 * @param id
	 * @return
	 */
	private static boolean isAllowed(GrayRule grayConfig, String id) {
		if (null != grayConfig && null != grayConfig.getType()) {
			if (RuleType.ALLOW.ordinal() == grayConfig.getType().ordinal()) {
				// 允许
				// 再看看有没有黑名单
				if (CollectionUtil.isNotEmpty(grayConfig.getNameList())) {
					// 有黑名单
					if (grayConfig.getNameList().contains(id)) {
						// 被禁
						return false;
					}
				}
				// 再看看有没有百分比配置
				if (CollectionUtil.isNotEmpty(grayConfig.getPercentList())) {
					long hashId = hash(id);
					for (Long percent : grayConfig.getPercentList()) {
						if (null != percent && 0L == hashId % percent) {
							return false;
						}
					}
				}
				return true;
			} else {
				// 拒绝
				// 再看看有没有白名单
				if (CollectionUtil.isNotEmpty(grayConfig.getNameList())) {
					// 有白名单
					if (grayConfig.getNameList().contains(id)) {
						// 在白名单以内，允许
						return true;
					}
				}
				// 再看看有没有百分比配置
				if (CollectionUtil.isNotEmpty(grayConfig.getPercentList())) {
					long hashId = hash(id);
					for (Long percent : grayConfig.getPercentList()) {
						if (null != percent && 0L == hashId % percent) {
							return true;
						}
					}
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * 灰度判断是否允许
	 *
	 * @param grayConfig
	 * @param id
	 * @return
	 */
	private static boolean isAllowed(GrayRule grayConfig, long id) {
		if (null != grayConfig && null != grayConfig.getType()) {
			if (RuleType.ALLOW.ordinal() == grayConfig.getType().ordinal()) {
				// 允许
				// 再看看有没有黑名单
				if (CollectionUtil.isNotEmpty(grayConfig.getNameList())) {
					// 有黑名单
					String name = String.valueOf(id);
					if (grayConfig.getNameList().contains(name)) {
						// 被禁
						return false;
					}
				}
				// 再看看有没有百分比配置
				if (CollectionUtil.isNotEmpty(grayConfig.getPercentList())) {
					for (Long percent : grayConfig.getPercentList()) {
						if (null != percent && 0L == id % percent) {
							return false;
						}
					}
				}
				return true;
			} else {
				// 拒绝
				// 再看看有没有白名单
				if (CollectionUtil.isNotEmpty(grayConfig.getNameList())) {
					// 有白名单
					String name = String.valueOf(id);
					if (grayConfig.getNameList().contains(name)) {
						// 在白名单以内，允许
						return true;
					}
				}
				// 再看看有没有百分比配置
				if (CollectionUtil.isNotEmpty(grayConfig.getPercentList())) {
					for (Long percent : grayConfig.getPercentList()) {
						if (null != percent && 0L == id % percent) {
							return true;
						}
					}
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * 取hash值
	 *
	 * @param id
	 * @return
	 */
	private static long hash(String id) {
		if (StringUtil.isNotEmpty(id)) {
			return id.hashCode();
		}
		return -1L;
	}

	/**
	 * 查询并解析灰度规则
	 *
	 * @param configKey <p>
	 *                  格式: configFileName.key
	 * @return
	 */
	private static GrayRule parseConfig(String configKey) {
		GrayRule grayConfig = null;
		if (StringUtil.isNotEmpty(configKey)) {
			String[] params = StringUtil.splitFirst(configKey, ".");
			if (null != params && 2 == params.length) {
				grayConfig = parseConfig(params[0], params[1]);
			}
		}
		return grayConfig;
	}

	/**
	 * 查询并解析灰度规则
	 *
	 * @param configFileName
	 * @param key
	 * @return
	 */
	private static GrayRule parseConfig(String configFileName, String key) {
		GrayRule grayConfig = null;
		try {
			if (StringUtil.isNotEmpty(configFileName) && StringUtil.isNotEmpty(key)) {
				String configKey = StringUtil.concat(configFileName, ".", key);
				grayConfig = grayConfigMap.get(configKey);
				if (null == grayConfig) {// 未命中缓存
					String config = ConfigManager.getConfig(configFileName, key);
					if (StringUtil.isNotEmpty(config)) {
						String[] params = StringUtil.splitFirst(config, ";");
						if (CollectionUtil.isNotEmpty(params)) {
							RuleType ruleType = RuleType.parse(params[0]);
							if (null != ruleType) {
								List<Long> percentList = null;
								List<String> nameList = null;

								if (params.length > 1) {
									String grayContent = params[1];
									if (StringUtil.isNotEmpty(grayContent)) {
										String[] grayItems = StringUtil.split(grayContent, ";");
										if (CollectionUtil.isNotEmpty(grayItems)) {
											percentList = new ArrayList<Long>();
											nameList = new ArrayList<String>();
											for (String grayItem : grayItems) {
												if (StringUtil.isNotEmpty(grayItem)) {
													if (grayItem.startsWith("%")) {
														try {
															// 百分比
															String percent = StringUtil.removeAll(grayItem, "%");
															Long percentValue = new Long(percent);
															if (null != percentValue && !percentList.contains(percent)) {
																percentList.add(percentValue);
															}
														} catch (NumberFormatException e) {
														}
													} else {
														// 名单
														if (!nameList.contains(grayItem)) {
															nameList.add(grayItem);
														}
													}
												}
											}
										}
									}
								}

								// 生成灰度配置对象
								grayConfig = new GrayRule(configKey, ruleType, nameList, percentList);
								grayConfigMap.put(configKey, grayConfig);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("GrayConfigUtil#parseConfig exception, drawerName=" + configFileName + ", key=" + key, e);
		}
		return grayConfig;
	}

	/**
	 * clear Gray Config cache
	 */
	public static void resetGrayConfigCache() {
		grayConfigMap.clear();
		LOG.info("resetGrayConfigCache");
	}
}

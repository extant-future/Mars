package com.extantfuture.mars.config;

import com.extantfuture.mars.util.CollectionUtil;
import com.extantfuture.mars.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * manager for all config items
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:35
 */
public class MarsConfigManager {

	private static final Logger LOG = Logger.getLogger(MarsConfigManager.class.getSimpleName());

	// local memory cache to hold all config items
	private static final Map<String, Map<String, String>> configLocalMap = new HashMap<String, Map<String, String>>();

	/**
	 * get config item's string value by key in config file
	 * 查询配置(本地缓存)
	 *
	 * @param configFileName
	 * @param key            配置项名
	 * @return
	 */
	public static String getConfig(String configFileName, String key) {
		String value = null;
		if (StringUtil.isNotEmpty(configFileName) && StringUtil.isNotEmpty(key) && CollectionUtil.isNotEmpty(configLocalMap)) {
			Map<String, String> configMap = configLocalMap.get(configFileName);
			if (CollectionUtil.isNotEmpty(configMap)) {
				value = configMap.get(key);
			}
		}
		return value;
	}

	/**
	 * get config item's string value with default by key in config file
	 * 查询配置(本地缓存)
	 *
	 * @param configFileName
	 * @param key            配置项名
	 * @param defaultConfig  默认配置
	 * @return
	 */
	public static String getConfigWithDefault(String configFileName, String key, String defaultConfig) {
		String value = getConfig(configFileName, key);
		if (!StringUtil.isNotEmpty(value)) {
			value = defaultConfig;
		}
		return value;
	}

	/**
	 * get config item's string value with default by key in config file
	 * 查询配置(本地缓存)
	 *
	 * @param configName    格式： configFileName.key
	 *                      <p>
	 *                      如： league_type_category.league_config
	 * @param defaultConfig 默认配置
	 * @return
	 */
	public static String getConfigWithDefault(String configName, String defaultConfig) {
		String value = getConfig(configName);
		if (!StringUtil.isNotEmpty(value)) {
			value = defaultConfig;
		}
		return value;
	}

	/**
	 * get config item's boolean value with default by key in config file
	 *
	 * @param configName
	 * @param defaultConfig
	 * @return
	 */
	public static boolean getBooleanConfig(String configName, boolean defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertBoolean(value, defaultConfig);
		}
		return defaultConfig;
	}

	/**
	 * get config item's double value with default by key in config file
	 *
	 * @param configName
	 * @param defaultConfig
	 * @return
	 */
	public static double getDouebleConfig(String configName, double defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertDouble(value, defaultConfig);
		}
		return defaultConfig;
	}

	/**
	 * get config item's float value with default by key in config file
	 *
	 * @param configName
	 * @param defaultConfig
	 * @return
	 */
	public static float getFloatConfig(String configName, float defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertFloat(value, defaultConfig);
		}
		return defaultConfig;
	}

	/**
	 * get config item's int value with default by key in config file
	 *
	 * @param configName
	 * @param defaultConfig
	 * @return
	 */
	public static int getIntConfig(String configName, int defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertInt(value, defaultConfig);
		}
		return defaultConfig;
	}

	/**
	 * get config item's long value with default by key in config file
	 *
	 * @param configName
	 * @param defaultConfig
	 * @return
	 */
	public static long getLongConfig(String configName, long defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertLong(value, defaultConfig);
		}
		return defaultConfig;
	}

	/**
	 * read config file to get file content with return
	 * 读配置文件内容，带换行
	 *
	 * @param configFileName
	 * @return
	 */
	public static String getConfigFromFileWithReturn(String configFileName) {
		String content = null;
		if (StringUtil.isNotEmpty(configFileName)) {
			InputStream is = null;
			BufferedReader br = null;
			try {
				is = MarsConfigManager.class.getClassLoader().getResourceAsStream(configFileName);
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				StringBuilder sb = new StringBuilder();
				do {
					line = br.readLine();
					if (null == line) {
						break;
					}
					sb.append(line).append(System.getProperty("line.separator"));
				} while (true);
				content = sb.toString();
			} catch (Exception e) {
				LOG.error("getConfigFromFile exception, configFileName=" + configFileName, e);
			} finally {
				if (null != br) {
					try {
						br.close();
					} catch (IOException e) {
					}
				}
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return content;
	}

	/**
	 * read config file to get file content without return
	 * 读整个配置文件(不带换行)
	 *
	 * @param configFileName
	 * @return
	 */
	public static String getConfigFromFile(String configFileName) {
		String content = null;
		if (StringUtil.isNotEmpty(configFileName)) {
			InputStream is = null;
			BufferedReader br = null;
			try {
				is = MarsConfigManager.class.getClassLoader().getResourceAsStream(configFileName);
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				StringBuilder sb = new StringBuilder();
				do {
					line = br.readLine();
					if (null == line) {
						break;
					}
					sb.append(line);
				} while (true);
				content = sb.toString();
			} catch (Exception e) {
				LOG.error("getConfigFromFile exception, configFileName=" + configFileName, e);
			} finally {
				if (null != br) {
					try {
						br.close();
					} catch (IOException e) {
					}
				}
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return content;
	}

	/**
	 * get config item's string value with default NULL by key in config file
	 * 查询配置(本地缓存)
	 *
	 * @param configName 格式： configFileName.key
	 *                   <p>
	 *                   如： user_conf.system_user
	 *                   <p>
	 * @return
	 */
	public static String getConfig(String configName) {
		String value = null;
		if (StringUtil.isNotEmpty(configName)) {
			String configFileName = null;
			String key = null;
			String[] params = StringUtil.splitFirst(configName, ".");
			if (null != params && 2 == params.length) {
				configFileName = params[0];
				key = params[1];
				if (StringUtil.isNotEmpty(configFileName) && StringUtil.isNotEmpty(key) && CollectionUtil
						.isNotEmpty(configLocalMap)) {
					Map<String, String> configMap = configLocalMap.get(configFileName);
					if (CollectionUtil.isNotEmpty(configMap)) {
						value = configMap.get(key);
					}
				}
			}
		}
		return value;
	}

	/**
	 * reload local config files
	 * 加载本地多个配置
	 *
	 * @param configFileNames
	 */
	public static void reloadConfigs(String... configFileNames) {
		if (CollectionUtil.isNotEmpty(configFileNames)) {
			for (String configFileName : configFileNames) {
				if (StringUtil.isNotEmpty(configFileName)) {
					LOG.info("start to reload configFileName=" + configFileName);
					initConfig(configFileName);
				}
			}
		}
	}

	/**
	 * parse config file content into config map
	 *
	 * @param configContent
	 */
	public static void reloadConfigContent(String configContent) {
		if (StringUtil.isNotEmpty(configContent)) {
			String[] lines = StringUtil.split(configContent, "\n");
			if (CollectionUtil.isNotEmpty(lines)) {
				String configFileName = null;
				for (String line : lines) {
					if (StringUtil.isNotEmpty(line)) {
						if (line.startsWith("#")) {// comment
							continue;
						}
						if (line.startsWith("[") && line.endsWith("]")) {// config file name
							configFileName = line.substring(1, line.length() - 1);
						} else if (line.contains("=")) {
							parseConfigMap(configFileName, line);
						}
					}
				}
			}
		}
	}

	/**
	 * 初始化配置
	 *
	 * @param configFileName
	 */
	private static void initConfig(String configFileName) {
		if (StringUtil.isNotEmpty(configFileName)) {
			InputStream is = null;
			BufferedReader br = null;
			try {
				is = MarsConfigManager.class.getClassLoader().getResourceAsStream(configFileName);
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				do {
					line = br.readLine();
					if (null == line) {
						break;
					}
					parseConfigMap(configFileName, line);
				} while (true);
			} catch (Exception e) {
				LOG.error("initConfig exception, configFileName=" + configFileName, e);
			} finally {
				if (null != br) {
					try {
						br.close();
					} catch (IOException e) {
					}
				}
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * parse config from a line in config file
	 * 解析一行配置项
	 *
	 * @param configFileName
	 * @param line
	 */
	private static void parseConfigMap(String configFileName, String line) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("parse config, configFileName=" + configFileName + ", line=" + line);
		}
		if (StringUtil.isNotEmpty(configFileName) && StringUtil.isNotEmpty(line) && !line.startsWith("#")) {
			String[] params = StringUtil.splitFirst(line, "=");
			if (null != params && 2 == params.length && StringUtil.isNotEmpty(params[0]) && StringUtil.isNotEmpty(params[1])) {
				updateConfigMap(configFileName, params[0], params[1]);
			}
		}
	}

	/**
	 * update local config item
	 * 更新某一项配置
	 *
	 * @param configFileName
	 * @param key
	 * @param value
	 */
	private static void updateConfigMap(String configFileName, String key, String value) {
		if (StringUtil.isNotEmpty(configFileName) && StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
			Map<String, String> map = configLocalMap.get(configFileName);
			if (null == map) {
				map = new HashMap<>();
				configLocalMap.put(configFileName, map);
			}
			map.put(key, value);
			if (LOG.isDebugEnabled()) {
				LOG.debug("update config, fileName=" + configFileName + ", key=" + key + ", value=" + value);
			}
		}
	}

}

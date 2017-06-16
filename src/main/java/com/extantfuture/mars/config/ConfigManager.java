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
public class ConfigManager {

	private static final Logger LOG = Logger.getLogger(ConfigManager.class.getSimpleName());

	// 本地缓存的所有配置项
	private static final Map<String, Map<String, String>> configLocalMap = new HashMap<String, Map<String, String>>();

	/**
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
				is = ConfigManager.class.getClassLoader().getResourceAsStream(configFileName);
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
				is = ConfigManager.class.getClassLoader().getResourceAsStream(configFileName);
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
	 * 查询配置(本地缓存)
	 *
	 * @param configName 格式： configFileName.key
	 *                   <p>
	 *                   如： league_type_category.league_config
	 *                   <p>
	 * @return
	 */
	public static String getConfig(String configName) {
		String value = null;
		if (StringUtil.isNotEmpty(configName)) {
			String drawerName = null;
			String key = null;
			String[] params = StringUtil.splitFirst(configName, ".");
			if (null != params && 2 == params.length) {
				drawerName = params[0];
				key = params[1];
				if (StringUtil.isNotEmpty(drawerName) && StringUtil.isNotEmpty(key) && CollectionUtil.isNotEmpty(configLocalMap)) {
					Map<String, String> configMap = configLocalMap.get(drawerName);
					if (CollectionUtil.isNotEmpty(configMap)) {
						value = configMap.get(key);
					}
				}
			}
		}
		return value;
	}

	/**
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

	public static boolean getBooleanConfig(String configName, boolean defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertBoolean(value, defaultConfig);
		}
		return defaultConfig;
	}

	public static double getDouebleConfig(String configName, double defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertDouble(value, defaultConfig);
		}
		return defaultConfig;
	}

	public static float getFloatConfig(String configName, float defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertFloat(value, defaultConfig);
		}
		return defaultConfig;
	}

	public static int getIntConfig(String configName, int defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertInt(value, defaultConfig);
		}
		return defaultConfig;
	}

	public static long getLongConfig(String configName, long defaultConfig) {
		String value = getConfig(configName);
		if (StringUtil.isNotEmpty(value)) {
			return StringUtil.convertLong(value, defaultConfig);
		}
		return defaultConfig;
	}

	/**
	 * 加载多个配置
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
				is = ConfigManager.class.getClassLoader().getResourceAsStream(configFileName);
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
	 * 更新某一项配置
	 *
	 * @param drawerName
	 * @param key
	 * @param value
	 */
	private static void updateConfigMap(String drawerName, String key, String value) {
		if (StringUtil.isNotEmpty(drawerName) && StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
			Map<String, String> map = configLocalMap.get(drawerName);
			if (null == map) {
				map = new HashMap<String, String>();
				configLocalMap.put(drawerName, map);
			}
			map.put(key, value);
			if (LOG.isDebugEnabled()) {
				LOG.debug("update config, fileName=" + drawerName + ", key=" + key + ", value=" + value);
			}
		}
	}

	/**
	 * 更新配置文件某配置顶内容
	 *
	 * @param configFileName
	 * @param key
	 * @param value
	 */
	public static void updateConfig(String configFileName, String key, String value) {
		updateConfigMap(configFileName, key, value);
	}

	public static void main(String[] args) {
		String testConfigFileName1 = "test_config";
		String testConfigFileName2 = "uu_car";
		reloadConfigs(testConfigFileName1, testConfigFileName2);
		for (Map.Entry<String, Map<String, String>> entry : configLocalMap.entrySet()) {
			System.out.println(entry.getKey());
			for (Map.Entry<String, String> e : entry.getValue().entrySet()) {
				System.out.println(e.getKey() + "=" + e.getValue());
			}
		}
		System.out.println("uu_car.rambo=" + getConfig("uu_car.rambo"));
		System.out.println("test_config.key2=" + getConfig("test_config.key2"));
	}
}

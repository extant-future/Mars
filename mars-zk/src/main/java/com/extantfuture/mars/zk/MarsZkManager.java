package com.extantfuture.mars.zk;

import com.extantfuture.mars.config.MarsConfigManager;
import com.extantfuture.mars.config.MarsCallback;
import com.extantfuture.mars.config.gray.GrayConfigManager;
import com.extantfuture.mars.util.CollectionUtil;
import com.extantfuture.mars.util.EnvUtil;
import com.extantfuture.mars.util.StringUtil;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Config managed by ZooKeeper
 * 通过ZooKeeper管理配置
 * use ZooKeeper to store config data and watcher to watch the update of config to execute callbacks
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:36
 */
public class MarsZkManager {

	private static final Logger log = Logger.getLogger(MarsZkManager.class.getSimpleName());
	// prefix path for modules in ZooKeeper
	private static final String PRE_PATH = "/ef-config/";
	// timeout config for ZooKeeper connection session
	private static final int SESSION_TIMEOUT = 60000;
	// server address of ZooKeeper for formal deploy environment
	private static final String FORMAL_ZK_CONNECT_ADDRESS = "formal.zookeeper.extantfuture.ali:2181";// maybe multi zk node address
	// server address of ZooKeeper for preview deploy environment
	private static final String PREVIEW_ZK_CONNECT_ADDRESS = "preview.zookeeper.extantfuture.ali:2181";
	// server address of ZooKeeper for develop deploy environment
	private static final String DEV_ZK_CONNECT_ADDRESS = "dev.zookeeper.extantfuture.ali:2181";
	// client of ZooKeeper
	private ZooKeeper zooKeeper;
	// container of callbacks
	private List<MarsCallback> callbackList = new ArrayList<MarsCallback>();

	/**
	 * init module's config
	 * need to be call when module start
	 * 模块启动时需要执行初始化
	 *
	 * @param moduleName
	 */
	public void init(String moduleName) {
		String moduleZkRootPath = getZkRootPath(moduleName);
		// 初始时获取一次配置内容更新到内存缓存
		updateAndWatchConfigNode(moduleZkRootPath);
		log.info("init moduleName=" + moduleName + ", moduleZkRootPath=" + moduleZkRootPath);
	}

	/**
	 * get config data from zk node and update cache in memory, then call callbacks
	 * 获取某个zk节点下的配置数据更新并执行回调逻辑
	 *
	 * @param zkNodePath
	 */
	private void updateAndWatchConfigNode(String zkNodePath) {
		try {
			if (StringUtil.isNotEmpty(zkNodePath)) {
				byte[] value = getClient().getData(zkNodePath, watcher, null);
				if (CollectionUtil.isNotEmpty(value)) {
					String configContent = StringUtil.getUTF8String(value);
					MarsConfigManager.reloadConfigContent(configContent);
					GrayConfigManager.resetGrayConfigCache();
					// callback
					if (CollectionUtil.isNotEmpty(callbackList)) {
						for (MarsCallback callback : callbackList) {
							if (null != callback) {
								try {
									callback.reloadConfig();
								} catch (Exception e) {
									log.error("updateAndWatchConfigNode callback.reloadConfig exception, callback=" + callback, e);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("updateAndWatchConfigNode exception, path=" + zkNodePath, e);
		}
	}

	/**
	 * update config data to zk node
	 * 更新某个配置文件的一个配置项到zk中
	 *
	 * @param moduleName
	 * @param configFilename
	 * @param key
	 * @param value
	 */
	public void updateAndWatchConfigNode(String moduleName, String configFilename, String key, String value)
			throws IOException, KeeperException, InterruptedException {
		String zkNodePath = getZkRootPath(moduleName);
		byte[] bytes = getClient().getData(zkNodePath, watcher, null);
		if (CollectionUtil.isNotEmpty(bytes)) {
			String content = StringUtil.getUTF8String(bytes);
			Pattern pattern = Pattern.compile("[" + moduleName + "]");
			Matcher matcher = pattern.matcher(content);
			if (matcher.find()) {
				int start = matcher.end();
				pattern = Pattern.compile(key + "=.+\n");
				matcher = pattern.matcher(content);
				if (matcher.find(start)) {
					String newContent = matcher.replaceFirst(StringUtil.concat(key, "=", value, "\n"));
					getClient().setData(zkNodePath, StringUtil.getUTF8Bytes(newContent), -1);
				}
			}
		}
	}

	/**
	 * get zookeeper's connect address by deploy environment
	 * 根据部署环境获取当前应该连接的zk地址
	 *
	 * @return
	 */
	private String getZkConnectAddress() {
		String address = null;
		EnvUtil.Env env = EnvUtil.getEnv();
		if (null != env) {
			switch (env) {
				case PRODUCTION:
					address = FORMAL_ZK_CONNECT_ADDRESS;
					break;
				case PREVIEW:
					address = PREVIEW_ZK_CONNECT_ADDRESS;
					break;
				case DEV:
					address = DEV_ZK_CONNECT_ADDRESS;
					break;
			}
		}
		return address;
	}

	/**
	 * build module's root path in zookeeper
	 * 获取当前模块在zk中的根节点
	 *
	 * @param moduleName
	 * @return
	 */
	private String getZkRootPath(String moduleName) {
		String path = null;
		if (StringUtil.isNotEmpty(moduleName)) {
			path = StringUtil.concat(PRE_PATH, moduleName);
		}
		log.info("getZkRootPath moduleName=" + moduleName + ", path=" + path);
		return path;
	}

	private ZooKeeper getClient() throws IOException {
		if (null == zooKeeper) {
			String address = getZkConnectAddress();
			Objects.requireNonNull(address, "zookeeper connect address is NULL!");
			zooKeeper = new ZooKeeper(address, SESSION_TIMEOUT, null);
		}
		return zooKeeper;
	}

	/**
	 * watch node's create or update in zookeeper, this happens when config file changed
	 */
	private Watcher watcher = event -> {
		if (null != event && null != event.getType()) {
			if (Watcher.Event.EventType.NodeDataChanged.getIntValue() == event.getType().getIntValue()
					|| Watcher.Event.EventType.NodeCreated.getIntValue() == event.getType().getIntValue()) {
				updateAndWatchConfigNode(event.getPath());
			}
		}
	};

	private static final MarsZkManager instance = new MarsZkManager();

	private MarsZkManager() {
	}

	public static MarsZkManager getInstance() {
		return instance;
	}

	/**
	 * register callback for config update
	 * 添加配置更新回调
	 *
	 * @param callback
	 */
	public void registerCallback(MarsCallback callback) {
		if (null != callback && !callbackList.contains(callback)) {
			callbackList.add(callback);
		}
	}

	/**
	 * remove callback for config update
	 * 移除某个配置更新回调
	 *
	 * @param callback
	 */
	public void removeCallback(MarsCallback callback) {
		if (callbackList.contains(callback)) {
			callbackList.remove(callback);
		}
	}

	/**
	 * remove all callbacks for config update
	 * 移除所有配置更新回调
	 */
	public void removeAllCallback() {
		callbackList.clear();
		callbackList = new ArrayList<MarsCallback>();
	}

}

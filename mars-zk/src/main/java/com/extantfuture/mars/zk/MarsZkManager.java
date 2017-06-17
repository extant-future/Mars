package com.extantfuture.mars.zk;

import com.extantfuture.mars.config.MarsCallback;
import com.extantfuture.mars.config.MarsConfigManager;
import com.extantfuture.mars.config.gray.GrayConfigManager;
import com.extantfuture.mars.util.CollectionUtil;
import com.extantfuture.mars.util.EnvUtil;
import com.extantfuture.mars.util.StringUtil;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.*;

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
	private static final String PRE_PATH = "/mars/";
	private static final String PATH_SEP = "/";
	// timeout config for ZooKeeper connection session
	private static final int SESSION_TIMEOUT = 60000;
	// server address of ZooKeeper for formal deploy environment
	private static final String FORMAL_ZK_CONNECT_ADDRESS = "formal.zookeeper.mars:2181";// maybe multi zk node address
	// server address of ZooKeeper for preview deploy environment
	private static final String PREVIEW_ZK_CONNECT_ADDRESS = "preview.zookeeper.mars:2181";
	// server address of ZooKeeper for develop deploy environment
	private static final String DEV_ZK_CONNECT_ADDRESS = "dev.zookeeper.mars:2181";
	// client of ZooKeeper
	private ZooKeeper zooKeeper;
	// container of callbacks
	//	private List<MarsCallback> callbackList = new ArrayList<>();
	private Map<String, List<MarsCallback>> fileNameCallbackListMap = new HashMap<>();

	/**
	 * init module's config
	 * need to be call when module start
	 * 模块启动时需要执行初始化
	 *
	 * @param moduleName
	 */
	public void init(String moduleName) throws InterruptedException, IOException, KeeperException {
		final String moduleZkRootPath = getZkRootPath(moduleName);
		// 初始时获取一次配置内容更新到内存缓存
		reloadAndWatchConfigNode(moduleZkRootPath, null);
		log.info("init end, moduleName=" + moduleName + ", moduleZkRootPath=" + moduleZkRootPath);
	}

	/**
	 * get config data from zk node and update cache in memory, then call callbacks
	 * 获取某个zk节点下的配置数据更新并执行回调逻辑
	 *
	 * @param zkNodePath
	 * @param zkSubNodePath
	 */
	private void reloadAndWatchConfigNode(final String zkNodePath, final String zkSubNodePath)
			throws IOException, KeeperException, InterruptedException {
		long startTs = System.nanoTime();
		try {
			if (StringUtil.isNotEmpty(zkNodePath)) {
				final List<String> configFileNameList = getClient().getChildren(zkNodePath, event -> {
					if (null != event && null != event.getType()) {
						if (Watcher.Event.EventType.NodeChildrenChanged.getIntValue() == event.getType().getIntValue()) {
							// add new config file or delete config file
							try {
								reloadAndWatchConfigNode(zkNodePath, event.getPath());
							} catch (Throwable e) {
								log.error("reloadAndWatchConfigNode error when NodeChildrenChanged, event=" + event, e);
							}
						}
					}
				});
				if (StringUtil.isNotEmpty(zkSubNodePath)) {
					Stat stat = getClient().exists(zkSubNodePath, false);
					if (null == stat) {
						// deleted, this almost not happen in Production TODO remove config keys in memory
					} else {
						// new config file
						byte[] value = getClient().getData(zkSubNodePath, watcher, null);
						String configFileName = StringUtil.removeAll(zkSubNodePath, zkNodePath);
						reloadByConfigFile(configFileName, value);
					}
				} else {
					if (CollectionUtil.isNotEmpty(configFileNameList)) {
						for (String configFileName : configFileNameList) {
							if (StringUtil.isNotEmpty(configFileName)) {
								String childrenPath = StringUtil.concat(zkNodePath, PATH_SEP, configFileName);
								byte[] value = getClient().getData(childrenPath, watcher, null);
								reloadByConfigFile(configFileName, value);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("reloadAndWatchConfigNode exception, zkNodePath=" + zkNodePath + ", zkSubNodePath=" + zkSubNodePath, e);
			throw e;
		} finally {
			log.info(StringUtil.concat("reloadAndWatchConfigNode zkNodePath=", zkNodePath, ", zkSubNodePath=", zkSubNodePath,
									   ", cost=", System.nanoTime() - startTs, "ns"));
		}
	}

	private void reloadConfigFileNode(String zkConfigFileNodePath) throws IOException, KeeperException, InterruptedException {
		if (StringUtil.isNotEmpty(zkConfigFileNodePath)) {
			String[] array = StringUtil.split(zkConfigFileNodePath, PATH_SEP);
			if (CollectionUtil.isNotEmpty(array)) {
				String configFileName = array[array.length - 1];
				byte[] value = getClient().getData(zkConfigFileNodePath, watcher, null);
				reloadByConfigFile(configFileName, value);
			}
		}
	}

	/**
	 * reload config from file and handle callback
	 *
	 * @param configFileName
	 * @param fileContent
	 */
	private void reloadByConfigFile(String configFileName, byte[] fileContent) {
		if (CollectionUtil.isNotEmpty(fileContent)) {
			String configContent = StringUtil.getUTF8String(fileContent);
			MarsConfigManager.reloadConfigContent(configContent);
			GrayConfigManager.resetGrayConfigCache();

			log.info(StringUtil.concat("reloadByConfigFile configFileName=", configFileName));

			if (StringUtil.isNotEmpty(configFileName)) {
				List<MarsCallback> callbackList = fileNameCallbackListMap.get(configFileName);
				if (CollectionUtil.isNotEmpty(callbackList)) {
					for (int i = 0; i < callbackList.size(); i++) {
						MarsCallback callback = callbackList.get(i);
						if (null != callback) {
							callback.reloadConfig();
						}
					}
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
			if (Watcher.Event.EventType.NodeDataChanged.getIntValue() == event.getType().getIntValue()) {
				// 配置文件节点变更
				try {
					reloadConfigFileNode(event.getPath());
				} catch (Throwable e) {
					log.error("config file watcher reload error, event=" + event, e);
				}
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
		if (null != callback && StringUtil.isNotEmpty(callback.watchConfigFileName())) {
			String configFileName = callback.watchConfigFileName();
			List<MarsCallback> callbackList = fileNameCallbackListMap.get(configFileName);
			if (null == callbackList) {
				callbackList = new ArrayList<>();
				fileNameCallbackListMap.put(configFileName, callbackList);
			}
			if (!callbackList.contains(callback)) {
				callbackList.add(callback);
			}
		}
	}

	/**
	 * remove callback for config update
	 * 移除某个配置更新回调
	 *
	 * @param callback
	 */
	public void removeCallback(MarsCallback callback) {
		if (null != callback) {
			String configFileName = callback.watchConfigFileName();
			if (StringUtil.isNotEmpty(configFileName)) {
				List<MarsCallback> callbackList = fileNameCallbackListMap.get(configFileName);
				if (CollectionUtil.isNotEmpty(callbackList)) {
					callbackList.remove(callback);
				}
			}
		}
	}

	/**
	 * remove all callbacks for config update
	 * 移除所有配置更新回调
	 */
	public void removeAllCallback() {
		fileNameCallbackListMap.clear();
		fileNameCallbackListMap = new HashMap<>();
	}

}

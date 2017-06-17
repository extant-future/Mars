package com.extantfuture.mars.config;

/**
 * callback for config update
 * 配置变更时的回调，用于需要限时处理配置变更的业务逻辑
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:36
 */
public interface MarsCallback {

	/**
	 * called when config update
	 */
	void reloadConfig();

	/**
	 * the config file to watch
	 *
	 * @return
	 */
	String watchConfigFileName();
}

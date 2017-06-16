package com.extantfuture.mars.util;

/**
 * deploy environment util
 * 部署环境工具类
 *
 * @author Rambo, <rambo@extantfuture.com>
 * @date 2017/6/16 下午8:43
 */
public class EnvUtil {

	/**
	 * get current deploy environment
	 * 获取当前部署环境
	 *
	 * @return
	 */
	public static Env getEnv() {
		// 这里通过jvm环境变量来指定环境
		String env = System.getProperty("ef_conf_env");
		return Env.parse(env);
	}

	/**
	 * deploy environment
	 * 部署环境
	 */
	public enum Env {
		PRODUCTION("formal"),// 生产环境
		PREVIEW("preview"),// 预生产环境
		DEV("dev"),// 开发环境
		;

		private String value;

		private Env(String value) {
			this.value = value;
		}

		public static Env parse(String value) {
			for (Env env : values()) {
				if (env.equals(value)) {
					return env;
				}
			}
			return null;
		}

		public String getValue() {
			return value;
		}
	}
}

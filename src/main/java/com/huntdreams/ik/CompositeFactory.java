package com.huntdreams.ik;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 配置文件符合查询类
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/5/16 9:13 AM
 * Last-Modified: 7/22/16 10:50 AM
 */
public class CompositeFactory {

    private static CompositeConfiguration configuration;

    private CompositeFactory() {
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static CompositeConfiguration getInstance() {
        if (configuration == null) {
            synchronized (CompositeFactory.class) {
                configuration = new CompositeConfiguration();
                try {
                    PropertiesConfiguration pc = new PropertiesConfiguration();
                    pc.setEncoding("utf8");
                    pc.load("conf/sysconfig.properties");
                    configuration.addConfiguration(pc);
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }
        return configuration;
    }

    /**
     * 添加配置
     *
     * @param config
     */
    public static void addConfiguration(Configuration config) {
        configuration = getInstance();
        configuration.addConfiguration(config);
    }

    /**
     * 获得一个key的值
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return configuration.getString(key);
    }

    public static void main(String[] args) {
        String url = CompositeFactory.getInstance().getString("jdbc.url");
        System.out.println(url);
    }
}
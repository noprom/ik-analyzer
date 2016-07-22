package com.huntdreams.ik.util;

import org.apache.commons.configuration.CompositeConfiguration;
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

    /**
     * 内部类
     */
    private static class Holder {
        private final static CompositeConfiguration configuration = new CompositeConfiguration();

        public static void init() {
            try {
                PropertiesConfiguration pc = new PropertiesConfiguration();
                pc.setEncoding("utf8");
                pc.load(Constant.SYS_CONF_PATH);
                configuration.addConfiguration(pc);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
    }


    private CompositeFactory() {
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static CompositeConfiguration getInstance() {
        Holder.init();
        return Holder.configuration;
    }

    /**
     * 获得一个key的值
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return getInstance().getString(key);
    }

    public static void main(String[] args) {
        String url = CompositeFactory.getInstance().getString("jdbc.url");
        System.out.println(url);
    }
}
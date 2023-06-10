package com.knight.leaf.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 加载配置文件: leaf.properties
 * @desc
 * @author knight
 * @date 2023/6/6
 */
public class PropertyFactory {

    private static final Logger logger = LoggerFactory.getLogger(PropertyFactory.class);

    private static final Properties prop = new Properties();

    static {
        try {
            prop.load(PropertyFactory.class.getClassLoader().getResourceAsStream("leaf.properties"));
        } catch (IOException e) {
            logger.warn("Load Properties error: ", e);
        }
    }

    public static Properties getProperties() {
        return prop;
    }
}

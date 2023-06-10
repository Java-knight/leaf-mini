package com.knight.leaf.server.common;

/**
 * leaf 常量
 * @desc
 * @author knight
 * @date 2023/6/10
 */
public class LeafConstants {

    /**
     * segment 配置常量
     */
    public static final String LEAF_SEGMENT_ENABLE = "leaf.segment.enable";

    public static final String LEAF_JDBC_URL = "leaf.jdbc.url";

    public static final String LEAF_JDBC_USERNAME = "leaf.jdbc.username";

    public static final String LEAF_JDBC_PASSWORD = "leaf.jdbc.password";

    /**
     * 兼容 mysql 5.7.38 & driver 的配置
     */
    public static final String LEAF_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    public static final String LEAF_VALIDATION_QUERY = "select 1";
}

package com.knight.leaf;

import com.knight.leaf.common.Result;

/**
 * 分布式id生成接口
 */
public interface IDGen {

    /**
     * 获取分布式id
     * @param key 业务 biz_tag
     * @return
     */
    Result get(String key);

    /**
     * 启动
     * @return
     */
    boolean init();
}

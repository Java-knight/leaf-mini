package com.knight.leaf.common;

import com.knight.leaf.IDGen;

/**
 * zero id[leaf-service 启动失败会返回这个]
 * @desc
 * @author knight
 * @date 2023/6/6
 */
public class ZeroIDGen implements IDGen {
    @Override
    public Result get(String key) {
        return new Result(0, Status.SUCCESS);
    }

    @Override
    public boolean init() {
        return true;
    }
}

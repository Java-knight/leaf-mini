package com.knight.leaf.segment.dao;

import com.knight.leaf.segment.model.LeafAlloc;

import java.util.List;

/**
 * curd 接口
 * @desc
 * @author knight
 * @date 2023/6/7
 */
public interface IDAllocDao {

    /**
     * 返回 leaf_alloc 表中所有记录
     */
    List<LeafAlloc> getAllLeafAllocs();

    /**
     * 根据业务查询
     * @param tag biz_tag
     * @return
     */
    LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);

    /**
     * 更新记录
     * @param leafAlloc
     * @return
     */
    LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

    /**
     * 获取 leaf_alloc 表中所有 biz_tag
     * @return
     */
    List<String> getAllTags();
}

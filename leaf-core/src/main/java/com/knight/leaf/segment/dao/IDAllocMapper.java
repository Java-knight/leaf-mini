package com.knight.leaf.segment.dao;

import com.knight.leaf.segment.model.LeafAlloc;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * sql 语句[mybatis 注解方式实现]
 * @desc
 * @author knight
 * @date 2023/6/7
 */
public interface IDAllocMapper {

    @Select("select biz_tag, max_id, step, update_time from leaf_alloc")
    @Results(value = {
            @Result(column = "biz_tag", property = "key"),
            @Result(column = "max_id", property = "maxId"),
            @Result(column = "step", property = "step"),
            @Result(column = "update_time", property = "updateTime")
    })
    List<LeafAlloc> getAllLeafAllocs();

    @Select("select biz_tag, max_id, step from leaf_alloc where biz_tag = #{tag}")
    @Results(value = {
            @Result(column = "biz_tag", property = "key"),
            @Result(column = "max_id", property = "maxId"),
            @Result(column = "step", property = "step")
    })
    LeafAlloc getLeafAlloc(@Param("tag") String tag);

    @Update("update leaf_alloc set max_id = max_id + step where biz_tag = #{tag}")
    void updateMaxId(@Param("tag") String tag);

    @Update("update leaf_alloc set max_id = max_id + #{step} where biz_tag = #{key}")
    void updateMaxIdByCustomStep(@Param("leafAlloc") LeafAlloc leafAlloc);

    @Select("select biz_tag from leaf_alloc")
    List<String> getAllTags();
}

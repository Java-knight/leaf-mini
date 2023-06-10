package com.knight.leaf.server.controller;

import com.knight.leaf.common.Result;
import com.knight.leaf.common.Status;
import com.knight.leaf.server.exception.LeafServerException;
import com.knight.leaf.server.exception.NoKeyException;
import com.knight.leaf.server.service.SegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生成分布式id接口[http 接口]
 * @desc
 * @author knight
 * @date 2023/6/10
 */
@RequestMapping(value = "/api/leaf")
@RestController
public class LeafController {

    private Logger logger = LoggerFactory.getLogger(LeafController.class);

    @Autowired
    private SegmentService segmentService;

    @RequestMapping(value = "/segment/get/{key}")
    public String getSegmentId(@PathVariable("key") String key) {
        return get(key, segmentService.getId(key));
    }

    private String get(@PathVariable("key") String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }
        result = id;
        if (result.getStatus().equals(Status.FAIL)) {
            throw new LeafServerException(result.toString());
        }
        return String.valueOf(result.getId());
    }
}

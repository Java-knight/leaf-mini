package com.knight.leaf.server.controller;

import com.knight.leaf.segment.SegmentIDGenImpl;
import com.knight.leaf.segment.model.LeafAlloc;
import com.knight.leaf.segment.model.SegmentBuffer;
import com.knight.leaf.server.model.SegmentBufferView;
import com.knight.leaf.server.service.SegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监控接口[http 接口]
 * @desc 只能监控分段
 * @author knight
 * @date 2023/6/10
 */
@RestController
public class LeafMonitorController {

    private Logger logger = LoggerFactory.getLogger(LeafController.class);

    @Autowired
    private SegmentService segmentService;

    /**
     * 获取当前分段的cache中的状态[监控使用]
     * @param model
     * @return
     */
    @RequestMapping(value = "cache")
    public String getCache(Model model) {
        Map<String, SegmentBufferView> data = new HashMap<>();
        SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
        if (null == segmentIDGen) {  // 分段没有开启
            throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
        }
        Map<String, SegmentBuffer> cache = segmentIDGen.getCache();
        for (Map.Entry<String, SegmentBuffer> entry : cache.entrySet()) {
            SegmentBufferView bufferView = convertSegmentBufferView(entry.getValue());
            data.put(entry.getKey(), bufferView);
        }
        logger.info("Cache info {}", data);
        model.addAttribute("data", data);
        return "segment";
    }

    // buffer 转换成 view
    private SegmentBufferView convertSegmentBufferView(SegmentBuffer buffer) {
        SegmentBufferView bufferView = new SegmentBufferView();
        bufferView.setInitOk(buffer.isInitOk());
        bufferView.setKey(buffer.getKey());
        bufferView.setNextReady(buffer.isNextReady());
        bufferView.setPos(buffer.getCurrentPos());

        bufferView.setMax0(buffer.getSegments()[0].getMax());
        bufferView.setValue0(buffer.getSegments()[0].getValue().get());
        bufferView.setStep0(buffer.getSegments()[0].getStep());

        bufferView.setMax1(buffer.getSegments()[1].getMax());
        bufferView.setValue1(buffer.getSegments()[1].getValue().get());
        bufferView.setStep1(buffer.getSegments()[1].getStep());

        return bufferView;
    }

    /**
     * 获取 db 状态
     * @param model
     * @return
     */
    @RequestMapping(value = "db")
    public String getDb(Model model) {
        SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
        if (null == segmentIDGen) {
            throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
        }
        List<LeafAlloc> items = segmentIDGen.getAllLeafAllocs();
        logger.info("DB info {}", items);
        model.addAttribute("items", items);
        return "db";
    }
}

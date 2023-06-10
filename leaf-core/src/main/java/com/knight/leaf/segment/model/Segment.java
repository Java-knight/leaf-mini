package com.knight.leaf.segment.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 分段[内存中]
 * @desc
 * @author knight
 * @date 2023/6/7
 */
public class Segment {
    private AtomicLong value = new AtomicLong(0);  // 生成自增id

    private volatile long max;  // cur segment max value

    private volatile int step;  // cur segment step size

    private SegmentBuffer buffer;  // double segment buffer

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    public AtomicLong getValue() {
        return value;
    }

    public void setValue(AtomicLong value) {
        this.value = value;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public SegmentBuffer getBuffer() {
        return buffer;
    }

    // 获取当前 segment 剩余多少 id
    public long getIdle() {
        return this.getMax() - getValue().get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segment{");
        sb.append("value=").append(value);
        sb.append(", max=").append(max);
        sb.append(", step=").append(step);
        sb.append('}');
        return sb.toString();
    }
}

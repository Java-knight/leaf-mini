package com.knight.leaf.segment.model;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * double segment buffer
 * @desc
 * @author knight
 * @date 2023/6/7
 */
public class SegmentBuffer {

    private String key;  // biz_tag

    private Segment[] segments;  // double buffer, size = 2

    private volatile int currentPos;  // 当前使用的 segment 的 index [0/1]

    private volatile boolean nextReady;  // 下一个 segment 是否准备好了

    private volatile boolean initOk;  // 是否初始化完成

    private final AtomicBoolean threadRunning;  // 更新 segment 的线程运行标志

    private final ReadWriteLock lock;

    private volatile int step;  // 当前步长

    private volatile int minStep;  // 当前最小步长

    private volatile long updateTimestamp;

    public SegmentBuffer() {
        this.segments = new Segment[]{new Segment(this), new Segment(this)};
        this.currentPos = 0;
        this.nextReady = false;
        this.initOk = false;
        this.threadRunning = new AtomicBoolean(false);
        this.lock = new ReentrantReadWriteLock();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Segment getCurrent() {
        return segments[currentPos];
    }

    public boolean isNextReady() {
        return nextReady;
    }

    public void setNextReady(boolean nextReady) {
        this.nextReady = nextReady;
    }

    public boolean isInitOk() {
        return initOk;
    }

    public void setInitOk(boolean initOk) {
        this.initOk = initOk;
    }

    public AtomicBoolean getThreadRunning() {
        return threadRunning;
    }

    public Lock readLock() {
        return lock.readLock();
    }

    public Lock writeLock() {
        return lock.writeLock();
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getMinStep() {
        return minStep;
    }

    public void setMinStep(int minStep) {
        this.minStep = minStep;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public int nextPos() {
        return (currentPos + 1) % 2;
    }

    // 切换下一个 segment
    public void switchPos() {
        currentPos = nextPos();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SegmentBuffer{");
        sb.append("key='").append(key).append('\'');
        sb.append(", segments=").append( Arrays.toString(segments));
        sb.append(", currentPos=").append(currentPos);
        sb.append(", nextReady=").append(nextReady);
        sb.append(", initOk=").append(initOk);
        sb.append(", threadRunning=").append(threadRunning);
        sb.append(", step=").append(step);
        sb.append(", minStep=").append(minStep);
        sb.append(", updateTimestamp=").append(updateTimestamp);
        sb.append('}');
        return sb.toString();
    }
}

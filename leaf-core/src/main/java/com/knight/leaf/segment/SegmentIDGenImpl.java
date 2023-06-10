package com.knight.leaf.segment;

import com.knight.leaf.IDGen;
import com.knight.leaf.common.Result;
import com.knight.leaf.common.Status;
import com.knight.leaf.segment.dao.IDAllocDao;
import com.knight.leaf.segment.model.LeafAlloc;
import com.knight.leaf.segment.model.Segment;
import com.knight.leaf.segment.model.SegmentBuffer;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分段id 的实现
 * @desc
 * @author knight
 * @date 2023/6/6
 */
public class SegmentIDGenImpl implements IDGen {

    private static final Logger logger = LoggerFactory.getLogger(SegmentIDGenImpl.class);

    /**
     * IdCache 初始化成功时的异常码
     */
    private static final long EXCEPTION_ID_IDCACHE_INIT_FALSE = -1;

    /**
     * key 不存在时的异常码
     */
    private static final long EXCEPTION_ID_KEY_EXISTS = -2;

    /**
     * segmentBuffer 中的两个 Segment 均为从 DB 中装在时的异常码
     */
    public static final long EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL = -3;

    /**
     * 最大步长不超过100 0000
     */
    public static final int MAX_STEP = 1000000;

    /**
     * 一个 Segemnt 维持时间为 15min
     */
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;

    /**
     * 工作线程池[更新db]
     */
    private ExecutorService service = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new UpdateThreadFactory());

    private volatile boolean initOk = false;

    /**
     * 缓存
     */
    private Map<String, SegmentBuffer> cache = new ConcurrentHashMap<>();

    private IDAllocDao idAllocDao;
    /**
     * 生产更新db的线程
     */
    public static class UpdateThreadFactory implements ThreadFactory {

        public static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + nextThreadNum());
        }
    }

    @Override
    public boolean init() {
        logger.info("Init...");
        // db —> cache
        updateCacheFromDb();
        initOk = true;
        updateCacheFromDbAtEveryMinute();
        return initOk;
    }

    /**
     * 定时更新cache的线程[守护线程]
     * xxx-jdb
     */
    private void updateCacheFromDbAtEveryMinute() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor((r) -> {
            Thread thread = new Thread(r);
            thread.setName("check-idCache-thread");
            thread.setDaemon(true);
            return thread;
        });
        service.scheduleWithFixedDelay(() -> {
            updateCacheFromDb();
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 更新cache [db —> cache]
     */
    private void updateCacheFromDb() {
        logger.info("update cache from db");
        StopWatch stopWatch = new Slf4JStopWatch();
        try {
            List<String> dbTags = idAllocDao.getAllTags();
            if (null == dbTags || dbTags.isEmpty()) {
                return;
            }
            List<String> cacheTags = new ArrayList<>(cache.keySet());
            Set<String> insertTagSet = new HashSet<>(dbTags);
            Set<String> removeTagSet = new HashSet<>(cacheTags);
            // (1) 把 dbTags = dbTags - cacheTags
            for (int i = 0; i < cacheTags.size(); i++) {
                String tmp = cacheTags.get(i);
                if (insertTagSet.contains(tmp)) {
                    insertTagSet.remove(tmp);
                }
            }

            // (2) cache = cache - cacheTags[dbTags中都是 cache 中没有的, 加入到 cache]
            for (String tag : insertTagSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
                logger.info("Add tag {} from db to IdCache, SegmentBuffer {}", tag, buffer);
            }

            // (3) cacheTags = cacheTags - dbTags[将db中已经被删除的从cache中失效掉]
            for (String tmp : dbTags) {
                if (removeTagSet.contains(tmp)) {
                    removeTagSet.remove(tmp);
                }
            }

            // (4) cache = cache - cacheTags[失效cache清理掉]
            for (String tag : removeTagSet) {
                cache.remove(tag);
                logger.info("Remove tag {} from IdCache", tag);
            }
        } catch (Exception e) {
            logger.warn("update cache from db exception: {}", e);
        } finally {
            stopWatch.stop("updateCacheFromDb");
        }
    }

    /**
     * 获取 id
     * @param key 业务 biz_tag
     * @return
     */
    @Override
    public Result get(final String key) {
        if (!initOk) {
            return new Result(EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.FAIL);
        }
        if (cache.containsKey(key)) {
            SegmentBuffer buffer = cache.get(key);
            if (!buffer.isInitOk()) {
                synchronized (buffer) {
                    try {
                        updateSegmentFromDb(key, buffer.getCurrent());
                        logger.info("Init buffer. Update leaf-key {} {} from db", key, buffer.getCurrent());
                        buffer.setInitOk(true);
                    } catch (Exception e) {
                        logger.warn("Init buffer {}, exception: {}", buffer.getCurrent(), e);
                    }
                }
            }
            return getIdFromSegmentBuffer(cache.get(key));
        }
        return new Result(EXCEPTION_ID_KEY_EXISTS, Status.FAIL);
    }

    /**
     * 更新 segment [db —> segment]
     * 判断是否过期
     * @param key
     * @param segment
     */
    public void updateSegmentFromDb(String key, Segment segment) {
        StopWatch stopWatch = new Slf4JStopWatch();
        SegmentBuffer buffer = segment.getBuffer();
        LeafAlloc leafAlloc;
        if (!buffer.isInitOk()) {  // buffer 不可用[第一次访问buffer/后续更新]
            leafAlloc = idAllocDao.updateMaxIdAndGetLeafAlloc(key);
            buffer.setStep(leafAlloc.getStep());
            buffer.setMinStep(leafAlloc.getStep());
        } else if (buffer.getUpdateTimestamp() == 0) {  // buffer 不可用[第一次访问 buffer]
            leafAlloc = idAllocDao.updateMaxIdAndGetLeafAlloc(key);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(leafAlloc.getStep());
            buffer.setMinStep(leafAlloc.getStep());
        } else {  // buffer 是可用的[check下是否过期, 扩容/缩容机制]
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < SEGMENT_DURATION) {  // 时间窗口内(0, 15min), 需要扩容[不能超过step的最大值]
                nextStep = nextStep << 1 > MAX_STEP ? nextStep : nextStep << 1;
            } else if (duration < SEGMENT_DURATION << 1) {  // [15min, 30min)
                // do nothing with nextStep
            } else {  // [30min, MAX) 超过2倍时间没有使用完, 进行缩容
                nextStep = nextStep >> 1 >= buffer.getMinStep() ? nextStep >> 1 : nextStep;
            }
            logger.info("leaf-key[{}], step[{}], duration[{}mins], nextStep[{}].", key, buffer.getStep(), String.format("%.2f", ((double) duration / (1000 * 60))), nextStep);
            LeafAlloc temp = new LeafAlloc();
            temp.setKey(key);
            temp.setStep(nextStep);
            leafAlloc = idAllocDao.updateMaxIdByCustomStepAndGetLeafAlloc(temp);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            buffer.setMinStep(leafAlloc.getStep());
        }

        // 必须在设置max之前设置value
        long value = leafAlloc.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(leafAlloc.getMaxId());
        segment.setStep(buffer.getStep());
        stopWatch.stop("updateSegmentFromDb", key + " " + segment);
    }

    /**
     * 从 segment 中获取id
     * (1) 先加readLock进行获取id
     * (2) 如果(1)获取失败[buffer 正在更新], 加 writeLock 进行id的获取
     * @param buffer
     * @return
     */
    public Result getIdFromSegmentBuffer(final SegmentBuffer buffer) {
        while (true) {
            // (1) readLock
            buffer.readLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                // 异步更新下一个 segment
                if (!buffer.isNextReady() &&
                        segment.getIdle() < 0.9 * segment.getStep() &&
                        buffer.getThreadRunning().compareAndSet(false, true)) {
                    service.execute(() -> {
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            updateSegmentFromDb(buffer.getKey(), next);
                            updateOk = true;
                            logger.info("update segment {} from db {}", buffer.getKey(), next);
                        } catch (Exception e) {
                            logger.warn(buffer.getKey() + "updateSegmentFromDb exception: ", e);
                        } finally {
                            if (updateOk) {  // 更新 完成下一个 segment
                                buffer.writeLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                buffer.writeLock().unlock();
                            } else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                // 获取 seq[自增序列]
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return new Result(value, Status.SUCCESS);
                }
            } finally {
                buffer.readLock().unlock();
            }

            // (2) writeLock
            waitAndSleep(buffer);
            buffer.writeLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().incrementAndGet();
                if (value < segment.getMax()) {
                    return new Result(value, Status.SUCCESS);
                }
                if (buffer.isNextReady()) { // buffer 切换 segment
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    logger.error("Both two segments in {} are not ready!", buffer);
                    return new Result(EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL, Status.FAIL);
                }
            } finally {
                buffer.writeLock().unlock();
            }
        }
    }

    /**
     * 查看buffer的线程标识[是否有线程在更新 segment]
     * @param buffer
     */
    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if (roll > 10000) {  // 100s 怎么都更新完了[正常来说不会超过30ms的, 这是为避免网络抖动]
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn("Thread {} Interrupted", Thread.currentThread().getName());
                    break;
                }
            }
        }
    }

    /**
     * 获取当前db 中的记录[web管理端查看]
     * @return
     */
    public List<LeafAlloc> getAllLeafAllocs() {
        return idAllocDao.getAllLeafAllocs();
    }

    /**
     * 获取 cache
     * @return
     */
    public Map<String, SegmentBuffer> getCache() {
        return cache;
    }

    public IDAllocDao getIdAllocDao() {
        return idAllocDao;
    }

    public void setIdAllocDao(IDAllocDao idAllocDao) {
        this.idAllocDao = idAllocDao;
    }
}

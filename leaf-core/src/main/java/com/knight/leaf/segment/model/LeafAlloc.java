package com.knight.leaf.segment.model;

/**
 * leaf_alloc 表对应的模型
 * @desc
 * @author knight
 * @date 2023/6/7
 */
public class LeafAlloc {

    private String key;  // biz_tag

    private long maxId;  // 当前的最大id

    private int step;  // 当前segment的步长

    private String updateTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LeafAlloc{");
        sb.append("key='").append(key).append('\'');
        sb.append(", maxId=").append(maxId);
        sb.append(", step=").append(step);
        sb.append(", updateTime='").append(updateTime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

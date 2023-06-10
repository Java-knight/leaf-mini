package com.knight.leaf.common;

/**
 * 获取 分布式id 的返回结果
 * @desc
 * @author knight
 * @date 2023/6/6
 */
public class Result {

    /**
     * 分布式id
     */
    private long id;

    /**
     * 返回结果状态
     */
    private Status status;

    public Result() {
    }

    public Result(long id, Status status) {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Result{");
        sb.append("id=").append(id);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}

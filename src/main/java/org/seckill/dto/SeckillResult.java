package org.seckill.dto;

/**
 * 是一个VO, value object
 * 所有的AJAX请求的返回类型,封装JSON结果
 * Created by Edwin on 2017/3/13.
 */
public class SeckillResult<T> {
    private boolean success; // 请求是否成功
    private T data;
    private String error;

    /**
     * 成功的构造
     * @param success
     * @param data
     */
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    /**
     * 失败的构造
     * @param success
     * @param error
     */
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

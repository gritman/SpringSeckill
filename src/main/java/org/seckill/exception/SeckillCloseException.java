package org.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by Edwin on 2017/3/12.
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}

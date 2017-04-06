package org.seckill.exception;

/**
 * 重复秒杀异常(运行期异常)
 * spring的声明式事务回滚策略只接受运行时异常,如果遇到非运行时异常,不会回滚
 * Created by Edwin on 2017/3/12.
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}

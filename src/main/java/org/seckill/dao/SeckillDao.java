package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Created by Edwin on 2017/3/12.
 * DAO关注的是数据库的操作,仅此而已
 */
public interface SeckillDao {
    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数大于1,表示更新的行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据ID查询库存实体
     * @param seckillId
     * @return
     */
    Seckill queryById(@Param("seckillId") long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    // java没有保存形参的记录,(int offset, int limit)会被java当成(int arg0, int arg1)
    // 所以会报 Parameter 'offset' not found的错误
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}

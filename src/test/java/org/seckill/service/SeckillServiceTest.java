package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Edwin on 2017/3/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
        //exposer=Exposer{exposed=true, md5='699d36e9d75790b2c1d8f0ab5502e779', seckillId=1000, now=0, start=0, end=0}
    }

    @Test
    public void executeSeckill() throws Exception {
        long id = 1000L;
        long phone = 12345678991L;
        String md5 = "699d36e9d75790b2c1d8f0ab5502e779";
        try {
            SeckillExcution execution = seckillService.executeSeckill(id, phone, md5);
            logger.info("execution={}", execution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 测试代码完整逻辑,注意,可以重复执行
     * 注意测试覆盖
     * @throws Exception
     */
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1000L;
        long phone = 12345678993L;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(id);
            if(exposer.isExposed()) {
                logger.warn("exposed, exposer={}", exposer);
                String md5 = exposer.getMd5();
                SeckillExcution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("execution={}", execution);
            } else {
                logger.warn("not exposed exposer={}", exposer);
            }
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }
    }

}
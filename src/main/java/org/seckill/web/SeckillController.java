package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Edwin on 2017/3/13.
 */
@Controller
@RequestMapping("/seckill") //一级url: /模块/资源/{id}/细分 比如 /seckill/list
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @param model 用来传递数据给jsp
     * @return jsp文件名
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET) // 二级url
    public String list(Model model) {
        // 根据service获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        // list.jsp + model = ModelAndView
        return "list"; // 相当于 WEB-INF/jsp/list.jsp
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(
            @PathVariable("seckillId")  Long seckillId,
            Model model) {
        if(seckillId == null) { // 注意,此处的Long用大写
            // 当seckillId不存在时,就把页面重定向到/seckill/list上
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     *
     * ajax接口,返回类型是json
     * @param seckillId 只接受POST,直接访问链接是无效的
     * @return
     */
    @RequestMapping(
            value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody // 会把返回对象封装成一个JSON
    public SeckillResult<Exposer> exposer(
            @PathVariable Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    /**
     *
     * @param seckillId
     * @param md5
     * @param phone 这个值不是从URL中读的,而是从cookie中读到的,required置为true时如果读不到会报错
     * @return
     */
    @RequestMapping(
            value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExcution> execute(
            @PathVariable("seckillId") Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long phone) {
        // 验证killPhone有没有传 也可以用springmvc valid
        if(phone == null) {
            return new SeckillResult<SeckillExcution>(false, "未注册");
        }
        //TODO 可以用springmvc的全局异常处理来处理这些异常
        SeckillResult<SeckillExcution> result;
        try {
            SeckillExcution execution = seckillService.executeSeckill(seckillId, phone, md5);
            result =  new SeckillResult<SeckillExcution>(true, execution);
        } catch (RepeatKillException e) {
            SeckillExcution execution = new SeckillExcution(seckillId, SeckillStatEnum.REPEAT_KILL);
            result = new SeckillResult<SeckillExcution>(true, execution);
        } catch (SeckillCloseException e) {
            SeckillExcution execution = new SeckillExcution(seckillId, SeckillStatEnum.END);
            result = new SeckillResult<SeckillExcution>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExcution execution = new SeckillExcution(seckillId, SeckillStatEnum.INNER_ERROR);
            result = new SeckillResult<SeckillExcution>(true, execution);
        }
        return result;
    }

    @RequestMapping(
            value = "/time/now",
            method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }


}

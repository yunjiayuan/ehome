package com.busi.timerController;

import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationTianqi;
import com.busi.servive.EpidemicSituationService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.EpidemicSituationUtils;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.color.CMMException;
import java.util.Date;
import java.util.Map;


/**
 * @program: ehome
 * @description: quartz定时器:疫情更新数据
 * @author: ZHaoJiaJie
 * @create: 2020-02-15 11:47:17
 */
@Slf4j
@Component
public class EpidemicSituationTimerController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    EpidemicSituationService epidemicSituationService;

    @Scheduled(cron = "0 */15 * * * ?") //每15分钟一次
    public void epidemicSituationTimer() throws Exception {
        log.info("开始查询第三方最新疫情数据并更新数据库...");
//        EpidemicSituationTianqi epidemicSituationTianqi = EpidemicSituationUtils.getEpidemicSituationByTianqi();
//        if (epidemicSituationTianqi != null && !CommonUtils.checkFull(epidemicSituationTianqi.getDate())) {
//            EpidemicSituationTianqi situation = epidemicSituationService.findEStianQi(epidemicSituationTianqi.getDate());
//            if (situation == null) {
//                epidemicSituationTianqi.setTime(new Date());
//                //清除缓存
//                redisUtils.expire(Constants.REDIS_KEY_EPIDEMICSITUATION, 0);
//                epidemicSituationService.addTianQi(epidemicSituationTianqi);
//                //放入缓存
//                Map<String, Object> map = CommonUtils.objectToMap(epidemicSituationTianqi);
//                redisUtils.hmset(Constants.REDIS_KEY_EPIDEMICSITUATION, map, Constants.USER_TIME_OUT);
//                log.info("查询&更新天气平台最新疫情数据成功...");
//            }
//        }
        EpidemicSituation epidemicSituation = EpidemicSituationUtils.getEpidemicSituationtianXing();
        if (epidemicSituation != null && epidemicSituation.getModifyTime() > 0) {
            EpidemicSituation situation = epidemicSituationService.findEpidemicSituation(epidemicSituation.getModifyTime());
            if (situation == null) {
                epidemicSituation.setTime(new Date());
                //清除缓存
                redisUtils.expire(Constants.REDIS_KEY_EPIDEMICSITUATION, 0);
                epidemicSituationService.add(epidemicSituation);
                //放入缓存
                Map<String, Object> map = CommonUtils.objectToMap(epidemicSituation);
                redisUtils.hmset(Constants.REDIS_KEY_EPIDEMICSITUATION, map, Constants.USER_TIME_OUT);
                log.info("查询&更新最新疫情数据成功...");
            }
        }
    }
}

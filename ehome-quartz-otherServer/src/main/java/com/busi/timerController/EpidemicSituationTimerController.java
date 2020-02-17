package com.busi.timerController;

import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationTianqi;
import com.busi.servive.EpidemicSituationService;
import com.busi.utils.CommonUtils;
import com.busi.utils.EpidemicSituationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.color.CMMException;
import java.util.Date;


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
    EpidemicSituationService epidemicSituationService;

    @Scheduled(cron = "0 */30 * * * ?") //每30分钟一次
    public void epidemicSituationTimer() throws Exception {
        log.info("开始查询第三方最新疫情数据并更新数据库...");
        EpidemicSituationTianqi epidemicSituationTianqi = EpidemicSituationUtils.getEpidemicSituationByTianqi();
        if (epidemicSituationTianqi != null && !CommonUtils.checkFull(epidemicSituationTianqi.getDate())) {
            EpidemicSituationTianqi situation = epidemicSituationService.findEStianQi(epidemicSituationTianqi.getDate());
            if (situation == null) {
                epidemicSituationTianqi.setTime(new Date());
                epidemicSituationService.addTianQi(epidemicSituationTianqi);
                log.info("查询&更新天气平台最新疫情数据成功...");
            }
        }
        EpidemicSituation epidemicSituation = EpidemicSituationUtils.getEpidemicSituation();
        if (epidemicSituation != null && epidemicSituation.getModifyTime() > 0) {
            EpidemicSituation situation = epidemicSituationService.findEpidemicSituation(epidemicSituation.getModifyTime());
            if (situation == null) {
                epidemicSituationService.add(epidemicSituation);
                log.info("查询&更新最新疫情数据成功...");
            }
        }
    }
}

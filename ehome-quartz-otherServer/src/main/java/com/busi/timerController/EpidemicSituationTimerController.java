package com.busi.timerController;

import com.busi.entity.EpidemicSituation;
import com.busi.fegin.EpidemicSituationLControllerFegin;
import com.busi.utils.EpidemicSituationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


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
    EpidemicSituationLControllerFegin epidemicSituationLControllerFegin;

    @Scheduled(cron = "0 0/30 * * * ?") //每30分钟一次
    public void epidemicSituationTimer() throws Exception {
        log.info("开始查询第三方最新疫情数据并更新数据库...");
        EpidemicSituation epidemicSituation = EpidemicSituationUtils.getEpidemicSituation();
        if (epidemicSituation != null) {
            epidemicSituationLControllerFegin.addEpidemicSituation(epidemicSituation);
        }
    }
}

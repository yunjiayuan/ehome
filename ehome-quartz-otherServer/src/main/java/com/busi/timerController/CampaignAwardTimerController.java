package com.busi.timerController;


import com.busi.entity.CampaignAwardActivity;
import com.busi.servive.EpidemicSituationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @program: ehome
 * @description: quartz定时器:战役评奖刷票数
 * @author: ZHaoJiaJie
 * @create: 2020-02-25 14:11:52
 */
@Slf4j
@Component
public class CampaignAwardTimerController {
    @Autowired
    EpidemicSituationService epidemicSituationService;

    @Scheduled(cron = "0 */5 * * * ?") //每5分钟一次
    public void campaignAwardTimer() throws Exception {
        log.info("开始更新战役评奖作品票数...");
        List list = null;
        Random ra = new Random();
        long millisecond = 3600000;//一小时毫秒数
        long now = new Date().getTime();//当前时间毫秒数
        list = epidemicSituationService.getCampaignAward();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                CampaignAwardActivity activity = (CampaignAwardActivity) list.get(i);
                if (activity != null) {
                    long time = activity.getTime().getTime();//发布时间
                    //票数小于10 发布时间小于1小时 随机20-50内
                    if (activity.getVotesCounts() < 10 && now - millisecond < time) {
                        activity.setVotesCounts(ra.nextInt(30) + 20 + activity.getVotesCounts());
                    } else if (activity.getVotesCounts() < 60 && now - millisecond > time && now - millisecond * 3 < time) {//票数小于60  时间上大于1个小时小于3小时 随机60-100内
                        activity.setVotesCounts(ra.nextInt(40) + 60 + activity.getVotesCounts());
                    } else if (activity.getVotesCounts() < 100 && now - millisecond * 10 > time) { //票数小于100  时间上大于10个小时 随机加100-200内
                        activity.setVotesCounts(ra.nextInt(100) + 100 + activity.getVotesCounts());
                    }
                    epidemicSituationService.updateNumber(activity);
                }
            }
            log.info("更新战役评奖作品票数成功...");
        }
    }
}

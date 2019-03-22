package com.busi.timerController;

import com.busi.entity.Footprint;
import com.busi.servive.FootprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 更新脚印离开时间
 * @author: ZHaoJiaJie
 * @create: 2019-03-19 10:01
 */
@Slf4j
@Component
public class FootprintTimerController {

    @Autowired
    FootprintService footprintService;

    @Scheduled(cron = "0 0 0 * * ?") // 每天零点执行一次
    public void cashbackMembership() {
        //更新无离开时间的脚印记录
        List list = footprintService.find();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Footprint foot = (Footprint) list.get(i);
                foot.setAwayTime(new Date());
                footprintService.update(foot);
            }
        }
        log.info("动态更新了(" + list.size() + ")个离开时间为空的脚印记录...");
    }
}

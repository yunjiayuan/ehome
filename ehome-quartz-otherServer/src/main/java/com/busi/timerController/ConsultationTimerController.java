package com.busi.timerController;

import com.busi.entity.CampaignAwardActivity;
import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.LawyerCircleRecord;
import com.busi.servive.HomeHospitalRecordService;
import com.busi.servive.LawyerCircleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @program: ehome
 * @description: quartz定时器:刷新医生、律师咨询状态
 * @author: ZHaoJiaJie
 * @create: 2020-06-01 16:28:24
 */
@Slf4j
@Component
public class ConsultationTimerController {

    @Autowired
    LawyerCircleService lawyerCircleService;

    @Autowired
    HomeHospitalRecordService homeHospitalRecordService;

    @Scheduled(cron = "0 */2 * * * ?") //每2分钟一次
    public void campaignAwardTimer() throws Exception {
        log.info("开始刷新医生、律师咨询状态...");
        List list = null;
        List list2 = null;
        long time = 0;
        long duration = 0;
        long millisecond = 60000;//一分钟毫秒数
        long now = new Date().getTime();//当前时间毫秒数
        //医生
        list = homeHospitalRecordService.findList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HomeHospitalRecord record = (HomeHospitalRecord) list.get(i);
                if (record == null) {
                    continue;
                }
                time = record.getTime().getTime();//支付时间毫秒数
                duration = record.getDuration() * millisecond;//咨询时间毫秒数
                if (time + duration <= now) {//判断咨询时间是否已过
                    record.setConsultationStatus(2);
                    homeHospitalRecordService.upConsultationStatus(record);
                }
            }
        }
        //律师
        list2 = lawyerCircleService.findList();
        if (list2 != null && list2.size() > 0) {
            for (int i = 0; i < list2.size(); i++) {
                LawyerCircleRecord record = (LawyerCircleRecord) list.get(i);
                if (record == null) {
                    continue;
                }
                time = record.getTime().getTime();//支付时间毫秒数
                duration = record.getDuration() * millisecond;//咨询时间毫秒数
                if (time + duration <= now) {//判断咨询时间是否已过
                    record.setConsultationStatus(2);
                    lawyerCircleService.upConsultationStatus(record);
                }
            }
        }
    }
}

package com.busi.timerController;

import com.busi.entity.CloudVideoActivities;
import com.busi.entity.SelfChannel;
import com.busi.entity.SelfChannelDuration;
import com.busi.servive.WheelPlantingService;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * quartz定时器:云视频轮播假数据
 * author：ZHaoJiaJie
 * create time：2019-4-3 16:50:23
 */
@Slf4j
@Component
public class WheelPlantingController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    WheelPlantingService wheelPlantingService;

    /**
     * Cron表达式的格式：秒 分 时 日 月 周 年(可选)。
     * <p>
     * “*”可用在所有字段中，表示对应时间域的每一个时刻，例如，*在分钟字段时，表示“每分钟”；
     * <p>
     * “?”字符：表示不确定的值 该字符只在日期和星期字段中使用，它通常指定为“无意义的值”，相当于点位符；
     * <p>
     * “,”字符：指定数个值 表达一个列表值，如在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期五；
     * <p>
     * “-”字符：指定一个值的范围 如在小时字段中使用“10-12”，则表示从10到12点，即10,11,12；
     * <p>
     * “/”字符：指定一个值的增加幅度。n/m表示从n开始，每次增加m
     * <p>
     * “L”字符：用在日表示一个月中的最后一天，用在周表示该月最后一个星期X
     * <p>
     * “W”字符：指定离给定日期最近的工作日(周一到周五)
     * <p>
     * “#”字符：表示该月第几个周X。6#3表示该月第3个周五
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 30 22 * * ?") //晚上十点三十分
    public void wheelPlantingTimer() throws Exception {
        log.info("开始补充次日轮播视频...");
        int num = 0;
        Date nextTime = null;
        List channelList = null;
        int surplusTime = 0;  //剩余时长
        int seconds = 86400;//一天秒数
        CloudVideoActivities activities = null;
        SelfChannel selfChannel = new SelfChannel();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        int timeStamp = Integer.valueOf(simpleDateFormat.format(getNextDay(simpleDateFormat.format(new Date()))).substring(0, 8));//一天后的时间
        //查询档期(明天的档期)
        SelfChannelDuration selfChannelDuration = wheelPlantingService.findTimeStamp(timeStamp);
        if (selfChannelDuration == null) {//无排挡信息
            //新增档期信息 //首增
            Date date = getNextDay(simpleDateFormat.format(new Date()));//一天后凌晨0点的时间

            SelfChannelDuration selfChannelDuration1 = new SelfChannelDuration();
            selfChannelDuration1.setNextTime(date);//下一个播出时间
            selfChannelDuration1.setTimeStamp(timeStamp);//时间戳
            selfChannelDuration1.setSurplusTime(seconds);//剩余秒数
            wheelPlantingService.addDuration(selfChannelDuration1);
        }
        channelList = wheelPlantingService.findGearShiftList();//查询现有的排挡信息
        SelfChannelDuration duration = wheelPlantingService.findTimeStamp(timeStamp);
        nextTime = duration.getNextTime();  //下一个排挡时间
        surplusTime = duration.getSurplusTime();//剩余时长
        if (channelList != null && channelList.size() > 0) {
            for (int i = 0; i < channelList.size(); i++) {
                activities = (CloudVideoActivities) channelList.get(i);
                if (activities != null) {
                    if (surplusTime >= activities.getDuration()) {
                        if (i == channelList.size() - 1) {
                            i = 0;
                        }
                        selfChannel.setId(0);
                        selfChannel.setAddtime(new Date());
                        selfChannel.setCity(activities.getCity());
                        selfChannel.setUserId(activities.getUserId());
                        selfChannel.setDistrict(activities.getDistrict());
                        selfChannel.setDuration(activities.getDuration());
                        selfChannel.setProvince(activities.getProvince());
                        selfChannel.setSinger(activities.getSinger());
                        selfChannel.setSongName(activities.getSongName());
                        selfChannel.setBirthday(activities.getBirthday());
                        selfChannel.setVideoCover(activities.getVideoCover());
                        selfChannel.setVideoUrl(activities.getVideoUrl());
                        selfChannel.setTime(nextTime);

                        Calendar calendar3 = new GregorianCalendar();
                        calendar3.setTime(selfChannel.getTime());
                        calendar3.add(calendar3.SECOND, selfChannel.getDuration());//加上当前视频时长
                        Date date3 = calendar3.getTime();
                        duration.setNextTime(date3);//下一个视频播出时间
                        duration.setSurplusTime(surplusTime - selfChannel.getDuration());//剩余秒数
                        nextTime = duration.getNextTime(); //下一个排挡时间
                        surplusTime = duration.getSurplusTime();//剩余时长
                        //新增排挡
                        wheelPlantingService.addSelfChannel(selfChannel);
                        //更新时长表
                        wheelPlantingService.updateDuration(duration);
                        num++;
                    } else {
                        break;
                    }
                }
            }
            log.info("新增轮播视频[" + num + "]条");
        }
    }

    //当前时间到第二天凌晨的时间
    public Date getNextDay(String dateTime) {
        SimpleDateFormat simpleDateFormat = new
                SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(dateTime);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime();
    }
}

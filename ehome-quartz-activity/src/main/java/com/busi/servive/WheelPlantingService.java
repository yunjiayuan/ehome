package com.busi.servive;

import com.busi.dao.WheelPlantingDao;
import com.busi.entity.CloudVideoActivities;
import com.busi.entity.SelfChannel;
import com.busi.entity.SelfChannelDuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 厨房订单
 * @author: ZHaoJiaJie
 * @create: 2019-03-07 16:28
 */
@Service
public class WheelPlantingService {

    @Autowired
    private WheelPlantingDao wheelPlantingDao;

    /***
     * 查询档期
     * @param timeStamp
     * @return
     */
    public SelfChannelDuration findTimeStamp(int timeStamp) {
        return wheelPlantingDao.findTimeStamp(timeStamp);
    }

    /***
     * 新增档期
     * @param selfChannelDuration
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDuration(SelfChannelDuration selfChannelDuration) {
        return wheelPlantingDao.addDuration(selfChannelDuration);
    }

    /***
     * 查询排挡视频列表
     * @return
     */
    public List<CloudVideoActivities> findGearShiftList() {

        List<CloudVideoActivities> list;
        list = wheelPlantingDao.findGearShiftList();
        return list;
    }

    /***
     * 新增排挡信息
     * @param selfChannel
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSelfChannel(SelfChannel selfChannel) {
        return wheelPlantingDao.addSelfChannel(selfChannel);
    }

    /***
     * 更新档期剩余时长
     * @param selfChannelDuration
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDuration(SelfChannelDuration selfChannelDuration) {
        return wheelPlantingDao.updateDuration(selfChannelDuration);
    }

    /***
     * 清除过期轮播视频
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del() {
        return wheelPlantingDao.del();
    }
}

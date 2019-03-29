package com.busi.service;

import com.busi.dao.SelfChannelVipDao;
import com.busi.entity.PageBean;
import com.busi.entity.SelfChannel;
import com.busi.entity.SelfChannelDuration;
import com.busi.entity.SelfChannelVip;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 自频道会员
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 13:57
 */
@Service
public class SelfChannelVipService {

    @Autowired
    private SelfChannelVipDao selfChannelVipDao;

    /***
     * 新增会员信息
     * @param selfChannelVip
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(SelfChannelVip selfChannelVip) {
        return selfChannelVipDao.add(selfChannelVip);
    }

    /***
     * 查询会员信息
     * @param userId
     * @return
     */
    public SelfChannelVip findDetails(long userId) {
        return selfChannelVipDao.findDetails(userId);
    }

    /***
     * 查询档期
     * @param timeStamp
     * @return
     */
    public SelfChannelDuration findTimeStamp(int timeStamp) {
        return selfChannelVipDao.findTimeStamp(timeStamp);
    }

    /***
     * 新增排挡信息
     * @param selfChannel
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSelfChannel(SelfChannel selfChannel) {
        return selfChannelVipDao.addSelfChannel(selfChannel);
    }

    /***
     * 更新档期剩余时长
     * @param selfChannelDuration
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDuration(SelfChannelDuration selfChannelDuration) {
        return selfChannelVipDao.updateDuration(selfChannelDuration);
    }

    /***
     * 新增档期
     * @param selfChannelDuration
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDuration(SelfChannelDuration selfChannelDuration) {
        return selfChannelVipDao.addDuration(selfChannelDuration);
    }

    /***
     * 查询排挡视频列表
     * @param timeStamp  开始时间（当前进来时间）
     * @param timeStamp2  第二天凌晨时间
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<SelfChannel> findGearShiftList(Date timeStamp,Date timeStamp2, int page, int count) {

        List<SelfChannel> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = selfChannelVipDao.findGearShiftList(timeStamp,timeStamp2);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 判断当天是否已经排过档
     * @param userId
     * @return
     */
    public SelfChannel findIs(long userId, int selectionType) {
        return selfChannelVipDao.findIs(userId, selectionType);
    }
}

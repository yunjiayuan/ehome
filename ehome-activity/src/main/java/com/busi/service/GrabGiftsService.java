package com.busi.service;

import com.busi.dao.GrabGiftsDrawDao;
import com.busi.entity.*;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 抢礼物Service
 * @author: ZHaoJiaJie
 * @create: 2020-04-03 13:03:04
 */
@Service
public class GrabGiftsService {

    @Autowired
    private GrabGiftsDrawDao giftsDrawDao;

    /***
     * 统计该用户当日次数
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId) {
        return giftsDrawDao.findNum(userId);
    }

    /***
     * 新增记录
     * @param prizesLuckyDraw
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(GrabMedium prizesLuckyDraw) {
        return giftsDrawDao.add(prizesLuckyDraw);
    }

    /***
     * 查询中奖人员列表
     */
    public PageBean<GrabMedium> findList(int page, int count) {
        List<GrabMedium> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = giftsDrawDao.findList();

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询自己的记录
     */
    public PageBean<GrabMedium> findOweList(long userId, int page, int count) {
        List<GrabMedium> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = giftsDrawDao.findOweList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询奖品
     */
    public GrabGifts findGifts() {
        return giftsDrawDao.findGifts();
    }
}

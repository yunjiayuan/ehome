package com.busi.service;

import com.busi.dao.GrabGiftsDrawDao;
import com.busi.entity.DrawingRecords;
import com.busi.entity.Drawings;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 抽签Service
 * @author: ZHaoJiaJie
 * @create: 2020-09-15 14:39:20
 */
@Service
public class DrawingsService {

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
    public int add(DrawingRecords prizesLuckyDraw) {
        return giftsDrawDao.add(prizesLuckyDraw);
    }

    /***
     * 查询记录
     */
    public PageBean<DrawingRecords> findOweList(long userId, int page, int count) {
        List<DrawingRecords> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = giftsDrawDao.findOweList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询详情
     */
    public Drawings findGifts(long id) {
        return giftsDrawDao.findGifts(id);
    }

}

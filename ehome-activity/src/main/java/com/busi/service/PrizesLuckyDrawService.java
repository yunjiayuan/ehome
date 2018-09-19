package com.busi.service;

import com.busi.dao.PrizesLuckyDrawDao;
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
 * @description: 赢大奖Service
 * @author: ZHaoJiaJie
 * @create: 2018-09-17 13:09
 */
@Service
public class PrizesLuckyDrawService {

    @Autowired
    private PrizesLuckyDrawDao prizesLuckyDrawDao;

    /***
     * 新增中奖记录
     * @param prizesLuckyDraw
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addLucky(PrizesLuckyDraw prizesLuckyDraw) {
        return prizesLuckyDrawDao.addLucky(prizesLuckyDraw);
    }

    /***
     * 新增收货信息
     * @param prizesReceipt
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addReceipt(PrizesReceipt prizesReceipt) {
        return prizesLuckyDrawDao.addReceipt(prizesReceipt);
    }

    /***
     * 更新中奖状态
     * @param prizesLuckyDraw
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDraw(PrizesLuckyDraw prizesLuckyDraw) {
        return prizesLuckyDrawDao.updateDraw(prizesLuckyDraw);
    }

    /***
     * 查询参与活动信息
     */
    public PrizesLuckyDraw findIn(long userId, int issue) {
        return prizesLuckyDrawDao.findIn(userId, issue);
    }

    /***
     * 查询可领奖信息
     */
    public PrizesLuckyDraw findWinning(long userId, long infoId) {
        return prizesLuckyDrawDao.findWinning(userId, infoId);
    }

    /***
     * 查询指定一期指定等级奖品
     */
    public PrizesEvent findEvent(int issue) {
        return prizesLuckyDrawDao.findEvent(issue);
    }

    /***
     * 查询指定纪念奖奖品
     */
    public PrizesMemorial findMemorial(int issue, String name) {
        return prizesLuckyDrawDao.findMemorial(issue, name);
    }

    /***
     * 查询指定一期纪念奖奖品
     */
    public PageBean<PrizesMemorial> findIssueMemorial(int issue, int page, int count) {
        List<PrizesMemorial> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = prizesLuckyDrawDao.findIssueMemorial(issue);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询最新一期奖品
     */
    public PageBean<PrizesEvent> findNew(int time, int page, int count) {
        List<PrizesEvent> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = prizesLuckyDrawDao.findNew(time);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询指定一期指定等级奖品
     */
    public List<PrizesEvent> findGradeEvent(int issue, int grade) {
        List<PrizesEvent> list;
        list = prizesLuckyDrawDao.findGradeEvent(issue, grade);
        return list;
    }

    /***
     * 分页查询指定期数等级奖品记录
     * @param issue
     * @param grade
     * @return
     */
    public PageBean<PrizesLuckyDraw> findGradeList(int issue, int grade, int page, int count) {

        List<PrizesLuckyDraw> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = prizesLuckyDrawDao.findGradeList(issue, grade);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询自己奖品
     * @param userId
     * @return
     */
    public PageBean<PrizesLuckyDraw> findOweList(long userId, int page, int count) {

        List<PrizesLuckyDraw> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = prizesLuckyDrawDao.findOweList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询指定开奖时间奖品
     */
    public List<PrizesEvent> findOpenTime(String openTime) {
        List<PrizesEvent> list;
        list = prizesLuckyDrawDao.findOpenTime(openTime);
        return list;
    }

    /***
     * 查询指定一期奖品
     */
    public List<PrizesEvent> findAppointEvent(int issue) {
        List<PrizesEvent> list;
        list = prizesLuckyDrawDao.findAppointEvent(issue);
        return list;
    }

}

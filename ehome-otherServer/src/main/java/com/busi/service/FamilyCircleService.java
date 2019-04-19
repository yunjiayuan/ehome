package com.busi.service;

import com.busi.dao.FamilyCircleDao;
import com.busi.entity.FamilyComments;
import com.busi.entity.FamilyGreeting;
import com.busi.entity.FamilyTodayPlan;
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
 * @description: 家人圈
 * @author: ZHaoJiaJie
 * @create: 2019-04-18 15:21
 */
@Service
public class FamilyCircleService {

    @Autowired
    private FamilyCircleDao familyCircleDao;

    /***
     * 新增评论
     * @param familyComments
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addFComment(FamilyComments familyComments) {
        return familyCircleDao.addFComment(familyComments);
    }

    /***
     * 新增家族问候
     * @param familyGreeting
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addGreeting(FamilyGreeting familyGreeting) {
        return familyCircleDao.addGreeting(familyGreeting);
    }

    /***
     * 新增今日记事
     * @param familyTodayPlan
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addInfor(FamilyTodayPlan familyTodayPlan) {
        return familyCircleDao.addInfor(familyTodayPlan);
    }

    /***
     * 删除家族评论
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delFComment(long userId, String[] ids) {
        return familyCircleDao.delFComment(userId, ids);
    }

    /***
     * 删除今日记事
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delInfor(long userId, String[] ids) {
        return familyCircleDao.delInfor(userId, ids);
    }

    /***
     * 分页查询家族用户评论
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<FamilyComments> findFCommentList(long userId, int page, int count) {

        List<FamilyComments> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = familyCircleDao.findFCommentList(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询家族问候
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<FamilyGreeting> findGreetingList2(long userId, int page, int count) {

        List<FamilyGreeting> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = familyCircleDao.findGreetingList2(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询今日家族问候
     * @param userId 用户
     * @return
     */
    public List<FamilyGreeting> findGreetingList(long userId) {
        List<FamilyGreeting> list;
        list = familyCircleDao.findGreetingList(userId);
        return list;
    }

    /***
     * 分页查询今日记事
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<FamilyTodayPlan> findInforList(long userId, int page, int count) {

        List<FamilyTodayPlan> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = familyCircleDao.findInforList(userId);
        return PageUtils.getPageBean(p, list);
    }

}

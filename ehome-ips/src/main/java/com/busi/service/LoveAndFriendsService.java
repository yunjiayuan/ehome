package com.busi.service;

import com.busi.dao.LoveAndFriendsDao;
import com.busi.entity.PageBean;
import com.busi.entity.LoveAndFriends;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 婚恋交友Service
 * author：zhaojiajie
 * create time：2018-8-2 11:24:17
 */
@Service
public class LoveAndFriendsService {

    @Autowired
    private LoveAndFriendsDao loveAndFriendsDao;

    /***
     * 新增用户
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(LoveAndFriends loveAndFriends) {
        return loveAndFriendsDao.add(loveAndFriends);
    }

    /***
     * 删除
     * @param userId 将要删除的userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long userId) {
        return loveAndFriendsDao.del(userId);
    }

    /***
     * 更新
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(LoveAndFriends loveAndFriends) {
        return loveAndFriendsDao.update(loveAndFriends);
    }

    /***
     * 更新删除状态
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(LoveAndFriends loveAndFriends) {
        return loveAndFriendsDao.updateDel(loveAndFriends);
    }

    /***
     * 刷新公告时间
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTime(LoveAndFriends loveAndFriends) {
        return loveAndFriendsDao.updateTime(loveAndFriends);
    }

    /***
     * 更新浏览量
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateSee(LoveAndFriends loveAndFriends) {
        return loveAndFriendsDao.updateSee(loveAndFriends);
    }

    /***
     * 置顶公告
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setTop(LoveAndFriends loveAndFriends) {
        return loveAndFriendsDao.setTop(loveAndFriends);
    }

    /***
     * 统计当月置顶次数
     * @param userId
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int statistics(long userId){
        return loveAndFriendsDao.statistics(userId);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public LoveAndFriends findUserById(long id) {
        return loveAndFriendsDao.findUserById(id);
    }

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    public LoveAndFriends findByIdUser(long userId) {
        return loveAndFriendsDao.findByIdUser(userId);
    }

    /***
     * 分页条件查询
     * @param screen   性别:0不限，1男，2女
     * @param sort   默认0智能排序，1时间倒序
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<LoveAndFriends> findList(int screen, int sort, int sex, int age, int income, int page, int count) {

        List<LoveAndFriends> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = loveAndFriendsDao.findList(screen, sort, sex, age, income);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页条件查询
     * @param userId   用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<LoveAndFriends> findUList(long userId, int page, int count) {

        List<LoveAndFriends> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = loveAndFriendsDao.findUList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * home推荐列表用
     * @param userId   用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<LoveAndFriends> findHList(long userId, int page, int count) {

        List<LoveAndFriends> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = loveAndFriendsDao.findHList(userId);

        return PageUtils.getPageBean(p, list);
    }

}

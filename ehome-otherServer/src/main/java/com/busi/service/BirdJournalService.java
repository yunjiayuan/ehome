package com.busi.service;

import com.busi.dao.BirdJournalDao;
import com.busi.entity.*;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: 喂鸟Service
 * @author: ZHaoJiaJie
 * @create: 2018-09-04 14:15
 */
@Service
public class BirdJournalService {

    @Autowired
    private BirdJournalDao birdJournalDao;

    /***
     * 新增喂鸟历史记录
     * @param birdFeedingRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addJourna(BirdFeedingRecord birdFeedingRecord) {
        return birdJournalDao.addJourna(birdFeedingRecord);
    }

    /***
     * 新增互动次数
     * @param birdInteraction
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addInteraction(BirdInteraction birdInteraction) {
        return birdJournalDao.addInteraction(birdInteraction);
    }

    /***
     * 新增喂鸟详细数据
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addData(BirdFeedingData birdFeedingData) {
        return birdJournalDao.addData(birdFeedingData);
    }

    /***
     * 新增砸蛋记录
     * @param birdEggSmash
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addEgg(BirdEggSmash birdEggSmash) {
        return birdJournalDao.addEgg(birdEggSmash);
    }

    /***
     * 新增中奖记录
     * @param theWinners
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addWinners(BirdTheWinners theWinners) {
        return birdJournalDao.addWinners(theWinners);
    }

    /***
     * 更新被喂者数据
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUsa(BirdFeedingData birdFeedingData) {
        return birdJournalDao.updateUsa(birdFeedingData);
    }

    /***
     * 更新产蛋状态产蛋数
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUsc(BirdFeedingData birdFeedingData) {
        return birdJournalDao.updateUsc(birdFeedingData);
    }

    /***
     * 更新产蛋时间产蛋状态
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUsb(BirdFeedingData birdFeedingData) {
        return birdJournalDao.updateUsb(birdFeedingData);
    }

    /***
     * 更新喂鸟者数据
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateMya(BirdFeedingData birdFeedingData) {
        return birdJournalDao.updateMya(birdFeedingData);
    }

    /***
     * 更新互动次数
     * @param birdInteraction
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateMyb(BirdInteraction birdInteraction) {
        return birdJournalDao.updateMyb(birdInteraction);
    }

    /***
     * 更新对方家蛋状态
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUserEgg(BirdFeedingData birdFeedingData) {
        return birdJournalDao.updateUserEgg(birdFeedingData);
    }

    /***
     * 删除喂鸟记录
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id) {
        return birdJournalDao.del(id);
    }

    /***
     * 条件删除喂鸟记录
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int batchDel(int birdCount, int userIdstart, int count) {
        return birdJournalDao.batchDel(birdCount, userIdstart, count);
    }

    /***
     * 根据userId查询
     * @param userId  用户ID
     * @return
     */
    public BirdFeedingData findUserById(long userId) {

        return birdJournalDao.findUserById(userId);
    }

    /***
     * 根据用户Id查询双方的互动记录
     * @param userId  用户ID
     * @return
     */
    public BirdInteraction findInterac(long userId, long visitId) {

        return birdJournalDao.findInterac(userId, visitId);
    }

    /***
     * 分页查询喂鸟详情
     * @param userId 用户ID
     * @param state 0喂我的  1我喂的
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<BirdFeedingRecord> findList(long userId, int state, int page, int count) {

        List<BirdFeedingRecord> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = birdJournalDao.findList(userId, state);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询互动次数
     * @param userId 用户ID
     * @param users  用户id集
     * @param state 0喂我的  1我喂的
     * @return
     */
    public List<BirdInteraction> findUserList(long userId, String[] users, int state) {
        List<BirdInteraction> list;
        list = birdJournalDao.findUserList(userId, users, state);
        return list;
    }

    /***
     * 查询最新一期奖品
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @return
     */
    public List<BirdPrize> findNewList(int eggType, int time) {
        List<BirdPrize> list;
        list = birdJournalDao.findNewList(eggType, time);
        return list;
    }

    /***
     * 查询指定一期奖品
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param issue 期号
     * @return
     */
    public List<BirdPrize> findAppointList(int eggType, int issue) {
        List<BirdPrize> list;
        list = birdJournalDao.findAppointList(eggType, issue);
        return list;
    }

    /***
     * 查询指定一期奖品
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param issue 期号
     * @return
     */
    public PageBean<BirdTheWinners> findPrizeList(int eggType, int issue, int page, int count) {
        List<BirdTheWinners> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = birdJournalDao.findPrizeList(eggType, issue);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询砸蛋记录
     * @param userId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<BirdEggSmash> findEggList(long userId, int page, int count) {
        List<BirdEggSmash> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = birdJournalDao.findEggList(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询自己奖品
     * @param userId  用户ID
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<BirdTheWinners> findWinnersList(long userId, int eggType, int page, int count) {
        List<BirdTheWinners> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = birdJournalDao.findWinnersList(userId, eggType);
        return PageUtils.getPageBean(p, list);
    }

}

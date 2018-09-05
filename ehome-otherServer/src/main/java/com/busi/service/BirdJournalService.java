package com.busi.service;

import com.busi.dao.BirdJournalDao;
import com.busi.entity.BirdFeedingData;
import com.busi.entity.BirdFeedingRecord;
import com.busi.entity.PageBean;
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
     * 新增
     * @param birdFeedingRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(BirdFeedingRecord birdFeedingRecord) {
        return birdJournalDao.add(birdFeedingRecord);
    }

    /***
     * 新增
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addData(BirdFeedingData birdFeedingData) {
        return birdJournalDao.addData(birdFeedingData);
    }

    /***
     * 更新
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateUsa(BirdFeedingData birdFeedingData){
        return  birdJournalDao.updateUsa(birdFeedingData);
    }

    /***
     * 更新
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateUsb(BirdFeedingData birdFeedingData){
        return  birdJournalDao.updateUsb(birdFeedingData);
    }

    /***
     * 更新
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateMya(BirdFeedingData birdFeedingData){
        return  birdJournalDao.updateMya(birdFeedingData);
    }

    /***
     * 删除
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return birdJournalDao.del(ids, userId);
    }


    /***
     * 根据userId查询
     * @param userId  用户ID
     * @return
     */
    public BirdFeedingData findUserById(long userId){

        return birdJournalDao.findUserById(userId);
    }

    /***
     * 分页查询
     * @param myId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<BirdFeedingRecord> findList(long myId, int page, int count) {

        List<BirdFeedingRecord> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = birdJournalDao.findList(myId);

        return PageUtils.getPageBean(p, list);
    }
}

package com.busi.servive;

import com.busi.dao.BirdJournalDao;
import com.busi.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 新增喂鸟详细数据
     * @param birdFeedingData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addData(BirdFeedingData birdFeedingData) {
        return birdJournalDao.addData(birdFeedingData);
    }

    /***
     * 条件删除喂鸟记录
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int batchDel(int birdCount, int userIdstart, int count) {
        return birdJournalDao.batchDel(birdCount, userIdstart, count);
    }

}

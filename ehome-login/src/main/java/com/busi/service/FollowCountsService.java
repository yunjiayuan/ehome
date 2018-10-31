package com.busi.service;

import com.busi.dao.FollowCountsDao;
import com.busi.entity.FollowCounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 粉丝数统计Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class FollowCountsService {

    @Autowired
    private FollowCountsDao followCountsDao;

    /***
     * 新增
     * @param followCounts
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(FollowCounts followCounts){
        return followCountsDao.add(followCounts);
    }

    /***
     * 更新
     * @param followCounts
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(FollowCounts followCounts){
        return followCountsDao.update(followCounts);
    }

    /***
     * 查询粉丝数
     * @param userId  被查询用户ID
     */
    public FollowCounts findFollowInfo(long userId){
        return followCountsDao.findFollowCounts(userId);
    }


}

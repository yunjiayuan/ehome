package com.busi.service;

import com.busi.dao.RewardTotalMoneyLogDao;
import com.busi.entity.RewardTotalMoneyLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: 用户奖励记录service
 * author：suntj
 * create time：2019-3-6 15:46:21
 */
@Service
public class RewardTotalMoneyLogService {

    @Autowired
    private RewardTotalMoneyLogDao rewardTotalMoneyLogDao;

    /***
     * 新增奖励总金额记录
     * @param rewardTotalMoneyLog
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(RewardTotalMoneyLog rewardTotalMoneyLog) {
        return rewardTotalMoneyLogDao.add(rewardTotalMoneyLog);
    }

    /***
     * 查询指定用户的奖励总金额
     * @param userId  用户ID
     * @return
     */
    public RewardTotalMoneyLog findRewardTotalMoneyLogInfo(long userId) {
        return rewardTotalMoneyLogDao.findRewardTotalMoneyLogInfo(userId);
    }

    /***
     * 更新奖励总金额
     * @param rewardTotalMoneyLog
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(RewardTotalMoneyLog rewardTotalMoneyLog) {
        return rewardTotalMoneyLogDao.update(rewardTotalMoneyLog);
    }

}

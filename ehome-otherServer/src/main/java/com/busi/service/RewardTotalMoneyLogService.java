package com.busi.service;

import com.busi.dao.RewardTotalMoneyLogDao;
import com.busi.entity.PageBean;
import com.busi.entity.RewardTotalMoneyLog;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     *   查询指定用户的奖励总金额列表
     * @param userId  userId 被查询人的用户ID  -1查询所有人
     * @param page
     * @param count
     * @return
     */
    public PageBean<RewardTotalMoneyLog> findRewardTotalMoneyLogInfoList(long userId, int page, int count) {
        List<RewardTotalMoneyLog> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = rewardTotalMoneyLogDao.findRewardTotalMoneyLogInfoList(userId);

        return PageUtils.getPageBean(p, list);
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

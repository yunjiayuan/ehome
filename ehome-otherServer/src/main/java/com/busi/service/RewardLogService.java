package com.busi.service;

import com.busi.dao.RewardLogDao;
import com.busi.entity.PageBean;
import com.busi.entity.RewardLog;
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
public class RewardLogService {

    @Autowired
    private RewardLogDao rewardLogDao;

    /***
     * 新增奖励记录
     * @param rewardLog
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(RewardLog rewardLog) {
        return rewardLogDao.add(rewardLog);
    }

    /***
     * 更新奖励记录未读状态
     * @param userId 将要被更新的用户ID
     * @param ids    将要被更新的记录ID组合 格式 1,2,3
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateIsNew(long userId ,String ids) {
        return rewardLogDao.updateIsNew(userId,ids.split(","));
    }

    /***
     * 查询指定用户的奖励列表
     * @param userId  用户ID
     * @param rewardType  奖励类型 -1所以 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
     * @return
     */
    public PageBean<RewardLog> findList(long userId, int rewardType, int page, int count) {

        List<RewardLog> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = rewardLogDao.findList(userId, rewardType);

        return PageUtils.getPageBean(p, list);
    }
    /***
     * 查询指定用户的今天的稿费作品奖励列表
     * @param userId  用户ID
     * @return
     */
    public List<RewardLog> findListByUserId(long userId) {
        List<RewardLog> list = null;
        list = rewardLogDao.findListByUserId(userId);
        return list;
    }

    public RewardLog findRewardLog(long userId, int rewardType, long infoId) {
        return rewardLogDao.findRewardLog(userId, rewardType, infoId);
    }

    public List<RewardLog> findRewardLogNewList(long userId) {
        return rewardLogDao.findRewardLogNewList(userId);
    }

}

package com.busi.dao;

import com.busi.entity.RewardTotalMoneyLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 用户奖励总金额记录Dao
 * author：suntj
 * create time：2019-3-6 15:46:21
 */
@Mapper
@Repository
public interface RewardTotalMoneyLogDao {

    /***
     * 新增奖励总金额记录
     * @param rewardTotalMoneyLog
     * @return
     */
    @Insert("insert into rewardTotalMoneyLog(userId,rewardTotalMoney) " +
            "values (#{userId},#{rewardTotalMoney})")
    @Options(useGeneratedKeys = true)
    int add(RewardTotalMoneyLog rewardTotalMoneyLog);

    /***
     * 查询指定用户的奖励总金额
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from rewardTotalMoneyLog" +
            " where 1=1" +
            "<if test=\" userId != -1 \">"+
               " and userId=#{userId}" +
            "</if>" +

            "</script>")
    RewardTotalMoneyLog findRewardTotalMoneyLogInfo(@Param("userId") long userId);

    /***
     * 更新奖励总金额
     * @param rewardTotalMoneyLog
     * @return
     */
    @Update("<script>" +
            "update rewardTotalMoneyLog set" +
            " rewardTotalMoney=#{rewardTotalMoney}" +
            " where 1=1" +
            " and userId=#{userId}" +
            "</script>")
    int update(RewardTotalMoneyLog rewardTotalMoneyLog);

}

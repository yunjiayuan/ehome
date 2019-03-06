package com.busi.dao;

import com.busi.entity.RewardLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 用户奖励记录Dao
 * author：suntj
 * create time：2019-3-6 15:46:21
 */
@Mapper
@Repository
public interface RewardLogDao {

    /***
     * 新增奖励记录
     * @param rewardLog
     * @return
     */
    @Insert("insert into rewardLog(userId,rewardType,rewardMoneyType,rewardMoney,time) " +
            "values (#{userId},#{rewardType},#{rewardMoneyType},#{rewardMoney},#{time})")
    @Options(useGeneratedKeys = true)
    int add(RewardLog rewardLog);

    /***
     * 查询指定用户的奖励列表
     * @param userId  用户ID
     * @param rewardType  奖励类型 -1所以 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
     * @return
     */
    @Select("<script>" +
            "select * from rewardLog" +
            " where 1=1" +
            "<if test=\"rewardType != -1\">" +
            " and rewardType=#{rewardType}" +
            "</if>" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<RewardLog> findList(@Param("userId") long userId, @Param("rewardType") int rewardType);


}

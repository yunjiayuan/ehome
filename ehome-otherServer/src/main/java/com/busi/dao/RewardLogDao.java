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
    @Insert("insert into rewardLog(userId,rewardType,rewardMoneyType,isNew,rewardMoney,time) " +
            "values (#{userId},#{rewardType},#{rewardMoneyType},#{isNew},#{rewardMoney},#{time})")
    @Options(useGeneratedKeys = true)
    int add(RewardLog rewardLog);

    /***
     * 更新奖励记录未读状态
     * @param userId 将要被更新的用户ID
     * @param ids    将要被更新的记录ID组合 格式 1,2,3
     * @return
     */
    @Update("<script>" +
            "update rewardLog set" +
            " isNew=1" +
            " where userId=#{userId}" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateIsNew(@Param("userId") long userId, @Param("ids") String[] ids);

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

    /***
     * 查询记录信息
     * @param userId      用户ID
     * @param rewardType  奖励类型 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
     * @param infoId      生活圈的主键ID 默认为0
     * @return
     */
    @Select("<script>" +
            "select * from rewardLog" +
            " where 1=1" +
            "<if test=\"infoId > 0 \">" +
            " and infoId=#{infoId}" +
            "</if>" +
            " and rewardType=#{rewardType}" +
            " and userId=#{userId}" +
            "</script>")
    RewardLog findRewardLog(@Param("userId") long userId, @Param("rewardType") int rewardType,@Param("infoId") int infoId);


}

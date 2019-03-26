package com.busi.dao;

import com.busi.entity.SelfChannel;
import com.busi.entity.SelfChannelDuration;
import com.busi.entity.SelfChannelVip;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自频道会员DAO
 *
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 13:57
 */
@Mapper
@Repository
public interface SelfChannelVipDao {

    /***
     * 新增会员
     * @param selfChannelVip
     * @return
     */
    @Insert("insert into SelfChannelVip(userId,startTime,expiretTime," +
            "memberShipStatus) " +
            "values (#{userId},#{startTime},#{expiretTime}," +
            "#{memberShipStatus})")
    @Options(useGeneratedKeys = true)
    int add(SelfChannelVip selfChannelVip);

    /***
     * 查询会员信息
     * @param userId
     * @return
     */
    @Select("select * from SelfChannelVip where userId = #{userId} and memberShipStatus = 0 and TO_DAYS(expiretTime)>TO_DAYS(NOW())")
    SelfChannelVip findDetails(@Param("userId") long userId);

    /***
     * 查询档期
     * @param timeStamp
     * @return
     */
    @Select("select * from SelfChannelDuration where timeStamp = #{timeStamp}")
    SelfChannelDuration findTimeStamp(@Param("timeStamp") int timeStamp);

    /***
     * 新增排挡信息
     * @param selfChannel
     * @return
     */
    @Insert("insert into SelfChannel(userId,selectionType,province,city,district,singer,songName,duration,sex,birthday,videoUrl" +
            ",videoCover,time) " +
            "values (#{userId},#{selectionType},#{province},#{city},#{district},#{singer},#{songName},#{duration},#{sex},#{birthday},#{videoUrl}" +
            ",#{videoCover},#{time})")
    @Options(useGeneratedKeys = true)
    int addSelfChannel(SelfChannel selfChannel);

    /***
     * 更新档期剩余时长
     * @param selfChannelDuration
     * @return
     */
    @Update("<script>" +
            "update SelfChannelDuration set" +
            " surplusTime=#{surplusTime}" +
            " where id=#{id}" +
            "</script>")
    int updateDuration(SelfChannelDuration selfChannelDuration);

    /***
     * 新增档期
     * @param selfChannelDuration
     * @return
     */
    @Insert("insert into SelfChannelDuration(timeStamp,surplusTime) " +
            "values (#{timeStamp},#{surplusTime})")
    @Options(useGeneratedKeys = true)
    int addDuration(SelfChannelDuration selfChannelDuration);

    /***
     * 查询排挡视频列表
     * @param timeStamp  开始时间（当前进来时间）
     * @param timeStamp2  结束时间（第二天凌晨0点）
     * @return
     */
    @Select("<script>" +
            "select * from SelfChannel" +
            " where 1=1" +
            " and timeStamp > #{timeStamp}" +
            " and timeStamp &lt; #{timeStamp2}" +
            " order by time desc" +
            "</script>")
    List<SelfChannel> findGearShiftList(@Param("timeStamp") int timeStamp, @Param("timeStamp2") int timeStamp2);

}

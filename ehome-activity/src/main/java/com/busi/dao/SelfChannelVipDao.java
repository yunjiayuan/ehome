package com.busi.dao;

import com.busi.entity.SelfChannel;
import com.busi.entity.SelfChannelDuration;
import com.busi.entity.SelfChannelVip;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
    @Insert("insert into SelfChannel(userId,selectionType,province,city,district,singer,songName,duration,birthday,videoUrl" +
            ",videoCover,time,addtime) " +
            "values (#{userId},#{selectionType},#{province},#{city},#{district},#{singer},#{songName},#{duration},#{birthday},#{videoUrl}" +
            ",#{videoCover},#{time},#{addtime})")
    @Options(useGeneratedKeys = true)
    int addSelfChannel(SelfChannel selfChannel);

    /***
     * 更新档期剩余时长
     * @param selfChannelDuration
     * @return
     */
    @Update("<script>" +
            "update SelfChannelDuration set" +
            " nextTime=#{nextTime}," +
            " surplusTime=#{surplusTime}" +
            " where id=#{id}" +
            "</script>")
    int updateDuration(SelfChannelDuration selfChannelDuration);

    /***
     * 新增档期
     * @param selfChannelDuration
     * @return
     */
    @Insert("insert into SelfChannelDuration(timeStamp,surplusTime,nextTime) " +
            "values (#{timeStamp},#{surplusTime},#{nextTime})")
    @Options(useGeneratedKeys = true)
    int addDuration(SelfChannelDuration selfChannelDuration);

    /***
     * 查询排挡视频列表
     * @param timeStamp  开始时间（当前进来时间）
     * @param timeStamp2  第二天凌晨时间
     * @return
     */
    @Select("<script>" +
            "select * from SelfChannel" +
            " where 1=1" +
//            " and TO_DAYS(time) >= TO_DAYS(#{timeStamp})" +
            " and unix_timestamp(time) > unix_timestamp(#{timeStamp}) " +
            " and unix_timestamp(#{timeStamp}) > unix_timestamp(time) " +
            " order by addtime asc" +
            "</script>")
    List<SelfChannel> findGearShiftList(@Param("timeStamp") Date timeStamp, @Param("timeStamp2") Date timeStamp2);

    /***
     * 判断当天是否已经排过档
     * @param userId
     * @return
     */
    @Select("select * from SelfChannel where userId=#{userId} " +
            " and userId = #{userId} " +
            " and selectionType = #{selectionType}" +
            " and TO_DAYS(addtime)=TO_DAYS(NOW())"
    )
    SelfChannel findIs(@Param("userId") long userId, @Param("selectionType") int selectionType);

}

package com.busi.dao;


import com.busi.entity.CloudVideoActivities;
import com.busi.entity.SelfChannel;
import com.busi.entity.SelfChannelDuration;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 厨房相关Dao
 * author：zhaojiajie
 * create time：2019-3-7 17:44:35
 */
@Mapper
@Repository
public interface WheelPlantingDao {

    /***
     * 查询档期
     * @param timeStamp
     * @return
     */
    @Select("select * from SelfChannelDuration where timeStamp = #{timeStamp}")
    SelfChannelDuration findTimeStamp(@Param("timeStamp") int timeStamp);

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
     * 查询活动视频列表
     * @return
     */
    @Select("<script>" +
            "select * from CloudVideoActivities where 10000 >= userId" +
            "</script>")
    List<CloudVideoActivities> findGearShiftList();

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
     * 清除过期轮播视频
     * @return
     */
    @Delete("<script>" +
            "delete from SelfChannel" +
            " where date_sub(CURDATE(), interval 1 day) >= addtime" + //获取当前时间格式为yyyy-MM-dd
            "</script>")
    int del();
}

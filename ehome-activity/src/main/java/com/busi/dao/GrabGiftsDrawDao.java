package com.busi.dao;

import com.busi.entity.GrabGifts;
import com.busi.entity.GrabMedium;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 抢礼物Dao
 * @author: ZHaoJiaJie
 * @create: 2020-04-03 13:21:15
 */
@Mapper
@Repository
public interface GrabGiftsDrawDao {
    /***
     * 统计该用户当天次数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from GrabMedium" +
            " where 1=1 " +
            " and userId = #{userId}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())" +
            "</script>")
    int findNum(@Param("userId") long userId);

    /***
     * 新增记录
     * @param prizesLuckyDraw
     * @return
     */
    @Insert("insert into GrabMedium(userId, time, price, winningState, cost) " +
            "values (#{userId},#{time},#{price},#{winningState},#{cost})")
    @Options(useGeneratedKeys = true)
    int add(GrabMedium prizesLuckyDraw);

    /***
     * 查询自己的记录
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from GrabMedium" +
            " where userId=#{userId} " +
            " order by time desc" +
            "</script>")
    List<GrabMedium> findOweList(@Param("userId") long userId);

    /***
     * 查询中奖人员列表
     * @return
     */
    @Select("<script>" +
            "select * from GrabMedium" +
            " where winningState=1 " +
            " order by time desc" +
            "</script>")
    List<GrabMedium> findList();

    /***
     * 查询奖品
     */
    @Select("select * from GrabGifts")
    GrabGifts findGifts();
}

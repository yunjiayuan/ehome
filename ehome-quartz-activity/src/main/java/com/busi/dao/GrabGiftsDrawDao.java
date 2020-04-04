package com.busi.dao;

import com.busi.entity.GrabGifts;
import com.busi.entity.GrabMedium;
import com.busi.entity.SelfChannelVip;
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
     * 新增记录
     * @param prizesLuckyDraw
     * @return
     */
    @Insert("insert into GrabMedium(userId, time, price, winningState, cost) " +
            "values (#{userId},#{time},#{price},#{winningState},#{cost})")
    @Options(useGeneratedKeys = true)
    int add(GrabMedium prizesLuckyDraw);

    /***
     * 更新数量
     * @param userMembership
     * @return
     */
    @Update("<script>" +
            "update GrabGifts set" +
            " number=#{number}" +
            " where id=#{id}" +
            "</script>")
    int update(GrabGifts userMembership);


    /***
     * 查询奖品
     */
    @Select("select * from GrabGifts")
    GrabGifts findGifts();
}

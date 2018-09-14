package com.busi.dao;

import com.busi.entity.PrizesEvent;
import com.busi.entity.PrizesLuckyDraw;
import com.busi.entity.PrizesMemorial;
import com.busi.entity.PrizesReceipt;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @program: ehome
 * @description: 赢大奖Dao
 * @author: ZHaoJiaJie
 * @create: 2018-09-14 16:20
 */
@Mapper
@Repository
public interface PrizesLuckyDrawDao {

    /***
     * 新增中奖记录
     * @param prizesLuckyDraw
     * @return
     */
    @Insert("insert into prizesLuckyDraw(userId, time, grade, prize, issue, winningState, cost) " +
            "values (#{userId},#{time},#{grade},#{prize},#{issue},#{winningState},#{cost})")
    @Options(useGeneratedKeys = true)
    int addLucky(PrizesLuckyDraw prizesLuckyDraw);

    /***
     * 新增收货信息
     * @param prizesReceipt
     * @return
     */
    @Insert("insert into prizesReceipt(userId, costName, describe, issue, price, imgUrl, contactsName,contactsPhone,province,city,district,postalcode,address,addTime) " +
            "values (#{userId},#{costName},#{describe},#{issue},#{price},#{imgUrl},#{contactsName},#{contactsPhone},#{province},#{city},#{district},#{postalcode},#{address},#{addTime})")
    @Options(useGeneratedKeys = true)
    int addReceipt(PrizesReceipt prizesReceipt);

    /***
     * 更新中奖状态
     * @param prizesLuckyDraw
     * @return
     */
    @Update("<script>" +
            "update prizesLuckyDraw set" +
            " winningState=#{winningState}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDraw(PrizesLuckyDraw prizesLuckyDraw);

    /***
     * 查询参与活动信息
     */
    @Select("select * from PrizesLuckyDraw where userId=#{userId} and issue=#{issue}")
    PrizesLuckyDraw findIn(@Param("userId") long userId, @Param("issue") int issue);

    /***
     * 查询可领奖信息
     */
    @Select("select * from PrizesLuckyDraw where userId=#{userId} and id=#{infoId} and winningState=1")
    PrizesLuckyDraw findWinning(@Param("userId") long userId, @Param("issue") int issue, @Param("infoId") long infoId);

    /***
     * 查询指定一期奖品
     */
    @Select("select * from PrizesEvent where issue=#{issue} and grade=1")
    PrizesEvent findEvent(@Param("issue") int issue);

    /***
     * 查询指定纪念奖奖品
     */
    @Select("select * from PrizesMemorial where issue=#{issue} and name like '#{name}'")
    PrizesMemorial findMemorial(@Param("issue") int issue, @Param("name") String name);

    /***
     * 查询指定一期纪念奖奖品
     */
    @Select("select * from PrizesMemorial where issue=#{issue} order by price asc")
    PrizesMemorial findIssueMemorial(@Param("issue") int issue);

    /***
     * 查询最新一期纪念奖奖品
     */
    @Select("select * from PrizesMemorial where time >= startTime and time <= endTime")
    PrizesEvent findNew(@Param("issue") int time);

}

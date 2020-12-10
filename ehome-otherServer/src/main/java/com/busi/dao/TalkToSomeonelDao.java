package com.busi.dao;

import com.busi.entity.TalkToSomeone;
import com.busi.entity.TalkToSomeoneOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 找人倾诉相关
 * @author: ZhaoJiaJie
 * @create: 2020-11-23 16:22:25
 */
@Mapper
@Repository
public interface TalkToSomeonelDao {

    /***
     * 新增
     * @param kitchen
     * @return
     */
    @Insert("insert into TalkToSomeone(userId,state,money,time,remarks)" +
            "values (#{userId},#{state},#{money},#{time},#{remarks})")
    @Options(useGeneratedKeys = true)
    int add(TalkToSomeone kitchen);

    /***
     * 新增
     * @param kitchen
     * @return
     */
    @Insert("insert into TalkToSomeoneOrder(userId,myId,money,addTime,no,payTime)" +
            "values (#{userId},#{myId},#{money},#{addTime},#{no},#{payTime})")
    @Options(useGeneratedKeys = true)
    int talkToSomeone(TalkToSomeoneOrder kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update TalkToSomeone set" +
            " money=#{money}," +
            " remarks=#{remarks}" +
            " where userId=#{userId}" +
            "</script>")
    int update(TalkToSomeone kitchen);

    @Update("<script>" +
            "update TalkToSomeone set" +
            " state=#{state}" +
            " where userId=#{userId}" +
            "</script>")
    int update2(TalkToSomeone kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update TalkToSomeoneOrder set" +
            " status=#{status}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeSomeoneState(TalkToSomeoneOrder kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update TalkToSomeoneOrder set" +
            " payTime=#{payTime}," +
            " payState=#{payState}" +
            " where no=#{no}" +
            "</script>")
    int updateOrders(TalkToSomeoneOrder kitchen);

    /***
     * 查询列表
     * @return
     */
    @Select("<script>" +
            "select * from TalkToSomeone" +
            " where state = 1" +
            " order by time desc" +
            "</script>")
    List<TalkToSomeone> findSomeoneList();

    /***
     * 查询列表
     * @param type
     * @return
     */
    @Select("<script>" +
            "select * from TalkToSomeoneOrder" +
            " where myId=#{myId}" +
            "<if test=\"type >= 0\">" +
            " and status=#{type}" +
            "</if>" +
            " and payState=1" +
            " order by payTime desc" +
            "</script>")
    List<TalkToSomeoneOrder> findSomeoneHistoryList(@Param("myId") long myId, @Param("type") int type);

    /***
     * 根据ID查询
     * @param userId
     * @return
     */
    @Select("select * from TalkToSomeone where userId=#{userId}")
    TalkToSomeone findSomeone(@Param("userId") long userId);

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    @Select("select * from TalkToSomeoneOrder where id=#{id}")
    TalkToSomeoneOrder findSomeone2(@Param("id") long id);
}

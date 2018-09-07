package com.busi.dao;

import com.busi.entity.RedPacketsCensus;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 红包统计信息相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface RedPacketsCensusDao {

    /***
     * 新增
     * @param redPacketsCensus
     * @return
     */
    @Insert("insert into redPacketsCensus (userId,receivedCounts,sendCounts,receivedAmount,sendAmount) values (#{userId},#{receivedCounts},#{sendCounts},#{receivedAmount},#{sendAmount})")
    @Options(useGeneratedKeys = true)
    int addRedPacketsCensus(RedPacketsCensus redPacketsCensus);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from redPacketsCensus where userId = #{userId}")
    RedPacketsCensus findRedPacketsCensus(@Param("userId") long userId);

    /***
     * 更新
     * @param redPacketsCensus
     * @return
     */
    @Update("<script>" +
            "update redPacketsCensus set"+
            "<if test=\"receivedCounts != 0 \">"+
            " receivedCounts=#{receivedCounts}," +
            "</if>" +
            "<if test=\"sendCounts != 0 \">"+
            " sendCounts=#{sendCounts}," +
            "</if>" +
            "<if test=\"receivedAmount != 0 \">"+
            " receivedAmount=#{receivedAmount}," +
            "</if>" +
            "<if test=\"sendAmount != 0 \">"+
            " sendAmount=#{sendAmount}," +
            "</if>" +
            " userId=#{userId}" +
            " where userId=#{userId}"+
            "</script>")
    int updateredPacketsCensus(RedPacketsCensus redPacketsCensus);
}

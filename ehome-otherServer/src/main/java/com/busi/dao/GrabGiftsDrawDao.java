package com.busi.dao;

import com.busi.entity.DrawingRecords;
import com.busi.entity.Drawings;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 抽签Dao
 * @author: ZHaoJiaJie
 * @create: 2020-09-15 14:43:30
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
            "select count(id) from DrawingRecords" +
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
    @Insert("insert into DrawingRecords(userId, time, drawingId, signature) " +
            "values (#{userId},#{time},#{drawingId},#{signature})")
    @Options(useGeneratedKeys = true)
    int add(DrawingRecords prizesLuckyDraw);

    /***
     * 查询记录
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from DrawingRecords" +
            " where userId=#{userId} " +
            " order by time desc" +
            "</script>")
    List<DrawingRecords> findOweList(@Param("userId") long userId);

    /***
     * 查询奖品
     */
    @Select("select * from Drawings where id = #{id}")
    Drawings findGifts(long id);
}

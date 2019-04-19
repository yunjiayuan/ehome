package com.busi.dao;

import com.busi.entity.FamilyComments;
import com.busi.entity.FamilyGreeting;
import com.busi.entity.FamilyTodayPlan;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家人圈Dao
 * author：zhaojiajie
 * create time：2019-4-18 15:35:21
 */
@Mapper
@Repository
public interface FamilyCircleDao {

    /***
     * 新增评论
     * @param familyComments
     * @return
     */
    @Insert("insert into FamilyComments(userId,myId,content,time,state) " +
            "values (#{userId},#{myId},#{content},#{time},#{state})")
    @Options(useGeneratedKeys = true)
    int addFComment(FamilyComments familyComments);

    /***
     * 新增家族问候
     * @param familyGreeting
     * @return
     */
    @Insert("insert into FamilyGreeting(userId,visitUserId,time) " +
            "values (#{userId},#{visitUserId},#{time})")
    @Options(useGeneratedKeys = true)
    int addGreeting(FamilyGreeting familyGreeting);

    /***
     * 新增今日记事
     * @param familyTodayPlan
     * @return
     */
    @Insert("insert into FamilyTodayPlan(userId,content,time,state) " +
            "values (#{userId},#{content},#{time},#{state})")
    @Options(useGeneratedKeys = true)
    int addInfor(FamilyTodayPlan familyTodayPlan);

    /***
     * 删除家族评论
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update FamilyComments set" +
            " state=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and (userId=#{userId} or myId=#{userId})" +
            "</script>")
    int delFComment(@Param("userId") long userId, @Param("ids") String[] ids);

    /***
     * 删除今日记事
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update FamilyTodayPlan set" +
            " state=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delInfor(@Param("userId") long userId, @Param("ids") String[] ids);

    /***
     * 分页查询家族用户评论
     * @param userId 用户
     * @return
     */
    @Select("<script>" +
            "select * from FamilyComments" +
            " where 1=1" +
            " and state=0" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<FamilyComments> findFCommentList(@Param("userId") long userId);

    /***
     * 分页查询家族问候
     * @param userId 用户
     * @return
     */
    @Select("<script>" +
            "select * from FamilyGreeting" +
            " where 1=1" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<FamilyGreeting> findGreetingList2(@Param("userId") long userId);

    /***
     * 查询当天家族问候
     * @param userId 用户
     * @return
     */
    @Select("<script>" +
            "select * from FamilyGreeting" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())" +
            " order by time desc" +
            "</script>")
    List<FamilyGreeting> findGreetingList(@Param("userId") long userId);

    /***
     * 分页查询今日记事
     * @param userId 用户
     * @return
     */
    @Select("<script>" +
            "select * from FamilyTodayPlan" +
            " where 1=1" +
            " and state=0" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<FamilyTodayPlan> findInforList(@Param("userId") long userId);
}

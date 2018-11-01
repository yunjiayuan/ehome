package com.busi.dao;

import com.busi.entity.FollowInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关注DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface FollowInfoDao {

    /***
     * 加关注
     * @param followInfo
     * @return
     */
    @Insert("insert into followInfo(userId,followUserId,time) " +
            "values (#{userId},#{followUserId},#{time})")
    @Options(useGeneratedKeys = true)
    int addFollow(FollowInfo followInfo);


    /***
     * 取消关注
     * @param userId       关注者用户ID
     * @param followUserId 被关注者用户ID
     * @return
     */
    @Delete("delete from followInfo where userId = #{userId} and followUserId = #{followUserId}")
    int delFollow(@Param("userId") long userId, @Param("followUserId") long followUserId);

    /***
     * 查询是否存在关注关系
     * @param userId       关注者用户ID
     * @param followUserId 被关注者用户ID
     */
    @Select("select * from followInfo where userId = #{userId} and followUserId = #{followUserId}")
    FollowInfo findFollowInfo(@Param("userId") long userId, @Param("followUserId") long followUserId);

    /***
     * 查询关注数 （粉丝数在单独的记录表中）
     * @param userId     将要查询的用户ID
     * @return
     */
    @Select("select count(id) from followInfo where userId = #{userId}")
    int findFollowCounts(@Param("userId") long userId);
    
    /***
     * 查询关注列表
     * @param userId     将要查询的用户ID
     * @param searchType 0 表示查询我关注的人列表  1表示关注我的用户列表
     * @return
     */
    @Select("<script>" +
            "select * from followInfo" +
            " where 1=1" +
            "<if test=\"searchType == 0\">"+
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"searchType == 1\">"+
            " and followUserId = #{userId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<FollowInfo> findFollowList(@Param("userId") long userId,@Param("searchType") int searchType);

}

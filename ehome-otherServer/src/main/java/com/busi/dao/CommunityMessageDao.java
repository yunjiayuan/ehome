package com.busi.dao;

import com.busi.entity.CommunityMessage;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息Dao
 * author：zhaojiajie
 * create time：2020-03-24 18:43:58
 */
@Mapper
@Repository
public interface CommunityMessageDao {
    /***
     * 新增消息
     * @param homeBlogMessage
     * @return
     */
    @Insert("insert into CommunityMessage(userId,replayId,commentId,content,time,newsType,newsState,status,communityId,type) " +
            "values (#{userId},#{replayId},#{commentId},#{content},#{time},#{newsType},#{newsState},#{status},#{communityId},#{type})")
    @Options(useGeneratedKeys = true)
    int addMessage(CommunityMessage homeBlogMessage);

    /***
     * 查询消息接口
     * @param communityType     类别   0居委会  1物业
     * @param communityId     type=0时为居委会ID  type=1时为物业ID
     * @param userId     用户ID
     * @param type       查询类型  0所有 1未读 2已读
     * @return
     */
    @Select("<script>" +
            "select * from CommunityMessage" +
            " where 1=1" +
            "<if test=\"type == 1\">" +
            " and newsState=1" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and newsState=0" +
            "</if>" +
            " and type=#{communityType} and communityId=#{communityId}" +
            " and replayId=#{userId} and status=0" +
            " order by time desc" +
            "</script>")
    List<CommunityMessage> findMessageList(@Param("communityType") int communityType, @Param("communityId") long communityId, @Param("type") int type, @Param("userId") long userId);

    /***
     * 统计该用户未读消息数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from CommunityMessage" +
            " where replayId=#{userId} and status=0 and newsState=1" +
            " and type=#{type} and communityId=#{communityId}" +
            "</script>")
    int getCount(@Param("type") int type, @Param("communityId") long communityId, @Param("userId") long userId);

    /***
     * 更新未读状态
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update CommunityMessage set" +
            " newsState=0" +
            " where newsState=1 and replayId=#{userId}" +
            " and type=#{type} and communityId=#{communityId}" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateState(@Param("type") int type, @Param("communityId") long communityId, @Param("userId") long userId, @Param("ids") String[] ids);

    /***
     * 更新未读状态
     * @param users
     * @return
     */
    @Update("<script>" +
            "update CommunityMessage set" +
            " newsState=0" +
            " where newsState=1" +
            " and type=#{type} and communityId=#{communityId}" +
            " and replayId in" +
            "<foreach collection='users' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateState2(@Param("type") int type, @Param("communityId") long communityId, @Param("users") String[] users);
}

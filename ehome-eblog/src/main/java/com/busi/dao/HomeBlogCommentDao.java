package com.busi.dao;

import com.busi.entity.HomeBlog;
import com.busi.entity.HomeBlogComment;
import com.busi.entity.HomeBlogMessage;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 生活圈评论Dao
 * author：zhaojiajie
 * create time：2018-11-5 16:30:20
 */
@Mapper
@Repository
public interface HomeBlogCommentDao {

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Insert("insert into HomeBlogComment(userId,blogId,replayId,masterId,content,time,replyType,replyStatus) " +
            "values (#{userId},#{blogId},#{replayId},#{masterId},#{content},#{time},#{replyType},#{replyStatus})")
    @Options(useGeneratedKeys = true)
    int addComment(HomeBlogComment homeBlogComment);

    /***
     * 新增消息
     * @param homeBlogMessage
     * @return
     */
    @Insert("insert into homeBlogMessage(userId,blog,replayId,commentId,content,time,newsType,newsState,status) " +
            "values (#{userId},#{blog},#{replayId},#{commentId},#{content},#{time},#{newsType},#{newsState},#{status})")
    @Options(useGeneratedKeys = true)
    int addMessage(HomeBlogMessage homeBlogMessage);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from HomeBlogComment where id = #{id} and blogId = #{blogId}")
    HomeBlogComment find(@Param("id") long id, @Param("blogId") long blogId);

    /***
     * 更新删除状态
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update homeBlogComment set" +
            " replyStatus=#{replyStatus}" +
            " where id=#{id}" +
            "</script>")
    int update(HomeBlogComment homeBlogComment);

    /***
     * 查询评论列表(只查评论replyType = 0)
     * @param blogId  博文ID
     * @return
     */
    @Select("<script>" +
            "select * from HomeBlogComment" +
            " where 1=1" +
            " and blogId=#{blogId} and replyStatus=0 and replyType = 0" +
            " order by time desc" +
            "</script>")
    List<HomeBlogComment> findList(@Param("blogId") long blogId);

    /***
     * 查询回复列表(只查评论replyType = 1)
     * @param blogId  博文ID
     * @return
     */
    @Select("<script>" +
            "select * from HomeBlogComment" +
            " where 1=1" +
            " and blogId=#{blogId} and replyStatus=0 and replyType = 1" +
            "</script>")
    List<HomeBlogComment> findReplyList(@Param("blogId") long blogId);

    /***
     * 查询消息列表
     * @param type  查询类型  0所有 1未读 2已读
     * @return
     */
    @Select("<script>" +
            "select * from HomeBlogMessage" +
            " where 1=1" +
            "<if test=\"type == 1\">" +
            " and newsState=1" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and newsState=0" +
            "</if>" +
            " and replayId=#{userId} and status=0" +
            " order by time desc" +
            "</script>")
    List<HomeBlogMessage> findMessageList(@Param("type") int type, @Param("userId") long userId);

    /***
     * 统计该用户未读消息数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from HomeBlogMessage" +
            " where replayId=#{userId} and status=0 and newsState=1" +
            "</script>")
    int getCount(@Param("userId") long userId);

    /***
     * 更新未读状态
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update HomeBlogMessage set" +
            " newsState=0" +
            " where newsState=1 and replayId=#{userId}" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateState(@Param("userId") long userId, @Param("ids") String[] ids);

    /***
     * 查询朋友圈列表
     * @param blIds  博文IDs
     * @return
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            " and id in" +
            "<foreach collection='blIds' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and blogStatus = 0" +
            "</script>")
    List<HomeBlog> findIdList(@Param("blIds") String[] blIds);


}

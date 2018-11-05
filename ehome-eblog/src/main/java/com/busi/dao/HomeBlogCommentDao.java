package com.busi.dao;

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
     * 新增评论消息
     * @param homeBlogMessage
     * @return
     */
    @Insert("insert into homeBlogMessage(userId,blogId,replayId,masterId,commentId,content,time,newsType,newsState,status) " +
            "values (#{userId},#{blogId},#{replayId},#{masterId},#{commentId},#{content},#{time},#{newsType},#{newsState},#{status})")
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
     * 查询评论列表
     * @param blogId  博文ID
     * @return
     */
    @Select("<script>" +
            "select * from HomeBlogComment" +
            " where 1=1" +
            " and blogId=#{blogId} and replyStatus=0" +
            " order by time desc" +
            "</script>")
    List<HomeBlogComment> findList(@Param("blogId") long blogId);

}

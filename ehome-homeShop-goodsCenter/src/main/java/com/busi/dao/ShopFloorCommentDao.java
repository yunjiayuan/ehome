package com.busi.dao;

import com.busi.entity.ShopFloorComment;
import com.busi.entity.ShopFloorGoods;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 搂店评论Dao
 * author：zhaojiajie
 * create time：2020-02-27 16:09:45
 */
@Mapper
@Repository
public interface ShopFloorCommentDao {

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Insert("insert into ShopFloorComment(userId,goodsId,replayId,content,time,replyType,replyStatus,fatherId) " +
            "values (#{userId},#{goodsId},#{replayId},#{content},#{time},#{replyType},#{replyStatus},#{fatherId})")
    @Options(useGeneratedKeys = true)
    int addComment(ShopFloorComment homeBlogComment);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from ShopFloorComment where id = #{id} and replyStatus=0")
    ShopFloorComment find(@Param("id") long id);

    /***
     * 更新删除状态
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update ShopFloorComment set" +
            " replyStatus=#{replyStatus}" +
            " where id=#{id}" +
            "</script>")
    int update(ShopFloorComment homeBlogComment);

    /***
     * 更新回复数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update ShopFloorComment set" +
            " replyNumber=#{replyNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateCommentNum(ShopFloorComment homeBlogComment);

    /***
     * 更新评论数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update ShopFloorGoods set" +
            " commentNumber=#{commentNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateBlogCounts(ShopFloorGoods homeBlogComment);

    /***
     * 查询评论列表(只查评论replyType = 0)
     * @param goodsId  商品ID
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorComment" +
            " where 1=1" +
            " and goodsId=#{goodsId} and replyStatus=0 and replyType = 0" +
            " order by time desc" +
            "</script>")
    List<ShopFloorComment> findList(@Param("goodsId") long goodsId);

    /***
     * 查询回复列表(只查回复replyType = 1)
     * @param contentId  评论ID
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorComment" +
            " where 1=1" +
            " and fatherId=#{contentId} and replyStatus=0 and replyType = 1" +
            " order by time desc" +
            "</script>")
    List<ShopFloorComment> findReplyList(@Param("contentId") long contentId);

    /***
     * 更新回复删除状态
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update ShopFloorComment set" +
            " replyStatus=1" +
            " where replyType=1" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateReplyState(@Param("ids") String[] ids);

    /***
     * 统计该评论下回复数量
     * @param commentId  评论ID
     * @return
     */
    @Select("<script>" +
            "select count(id) from ShopFloorComment" +
            " where fatherId=#{commentId} and replyStatus=0 and replyType=1" +
            "</script>")
    int getReplayCount(@Param("commentId") long commentId);

}

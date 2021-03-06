package com.busi.dao;

import com.busi.entity.Hotel;
import com.busi.entity.HotelOrder;
import com.busi.entity.Hotel;
import com.busi.entity.HotelComment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 家门口酒店民宿订单
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 17:31:38
 */
@Mapper
@Repository
public interface HotelOrderDao {

    /***
     * 新增家门口旅游订单
     * @param kitchenBookedOrders
     * @return
     */
    @Insert("insert into HotelOrder(userId,myId,hotelId,no,dishameCost,ordersType,ordersState,hotelName,addTime,checkInTime,money,smallMap,inspectTicketTime," +
            "completeTime,checkInNumber,address_Phone,address_Name,voucherCode,hotelType)" +
            "values (#{userId},#{myId},#{hotelId},#{no},#{dishameCost},#{ordersType},#{ordersState},#{hotelName},#{addTime},#{checkInTime},#{money},#{smallMap},#{inspectTicketTime}" +
            ",#{completeTime},#{checkInNumber},#{address_Phone},#{address_Name},#{voucherCode},#{hotelType})")
    @Options(useGeneratedKeys = true)
    int addOrders(HotelOrder kitchenBookedOrders);

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景 0删除 1由未验票改为已验票 2由已验票改为已完成 3取消订单
     * @return
     */
    @Select("<script>" +
            "select * from HotelOrder" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and ordersType >1 and ordersState!=3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0 and (ordersType =1 or ordersType=0)" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and ordersType=1 and myId=#{userId} and paymentStatus = 1" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState = 0 and ordersType=0 " +
            "</if>" +
            "</script>")
    HotelOrder findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from HotelOrder" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    HotelOrder findByNo(@Param("no") String no);

    /***
     *  更新家门口旅游订单状态
     *  updateCategory 更新类别  0删除状态  1由未验票改为已验票  2由已验票改为已完成  3更新支付状态  4取消订单、评价状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update HotelOrder set" +
            "<if test=\"updateCategory == 0\">" +
            " ordersState =#{ordersState}" +
            "</if>" +
            "<if test=\"updateCategory == 1\">" +
            " ordersType =#{ordersType}," +
            " inspectTicketTime =#{inspectTicketTime}" +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " ordersType =#{ordersType}," +
            " completeTime =#{completeTime}" +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " paymentStatus =#{paymentStatus}," +
            " paymentTime=#{paymentTime}" +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            " where id=#{id} and ordersState=0" +
            "</script>")
    int updateOrders(HotelOrder orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  -1全部 0待付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成未评价  4卖家取消订单 5用户取消订单 6已过期
     * @return
     */
    @Select("<script>" +
            "select * from HotelOrder" +
            " where ordersState=0" +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"ordersType >0 and ordersType &lt; 4\">" +
            " and ordersType = #{ordersType}" +
            "</if>" +
            "<if test=\"ordersType == 0\">" +
            " and paymentStatus = 0" +
            "</if>" +
            "<if test=\"ordersType >= 4\">" +
            " and ordersType in(4,5,6)" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<HotelOrder> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType);

    /***
     * 更新评分
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Hotel set" +
            " totalScore=#{totalScore}," +
            " averageScore=#{averageScore}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateScore(Hotel kitchen);

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Insert("insert into HotelComment(userId,masterId,replayId,content,time,replyType,replyStatus,fatherId,secondFatherId,replyNumber,orderId,imgUrls,score,anonymousType) " +
            "values (#{userId},#{masterId},#{replayId},#{content},#{time},#{replyType},#{replyStatus},#{fatherId},#{secondFatherId},#{replyNumber},#{orderId},#{imgUrls},#{score},#{anonymousType})")
    @Options(useGeneratedKeys = true)
    int addComment(HotelComment homeBlogComment);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from HotelComment where id = #{id} and replyStatus=0")
    HotelComment find(@Param("id") long id);

    /***
     * 更新删除状态
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update HotelComment set" +
            " replyStatus=#{replyStatus}" +
            " where id=#{id}" +
            "</script>")
    int update(HotelComment homeBlogComment);

    /***
     * 更新回复数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update HotelComment set" +
            " replyNumber=#{replyNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateCommentNum(HotelComment homeBlogComment);

    /***
     * 更新评论数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update Hotel set" +
            " totalEvaluate=#{totalEvaluate}" +
            " where id=#{id}" +
            "</script>")
    int updateBlogCounts(Hotel homeBlogComment);

    /***
     * 查询评论列表(只查评论replyType = 0)
     * @param masterId  景区ID
     * @return
     */
    @Select("<script>" +
            "select * from HotelComment" +
            " where 1=1" +
            " and masterId=#{masterId} and replyStatus=0 and replyType = 0" +
            " order by time desc" +
            "</script>")
    List<HotelComment> findList(@Param("masterId") long masterId);

    /***
     * 查询回复列表(只查回复replyType = 1)
     * @param contentId  评论ID
     * @return
     */
    @Select("<script>" +
            "select * from HotelComment" +
            " where 1=1" +
            " and fatherId=#{contentId} and replyStatus=0 and replyType = 1" +
            " order by time desc" +
            "</script>")
    List<HotelComment> findReplyList(@Param("contentId") long contentId);

    /***
     * 查询指定用户评论
     * @param id  指定景区ID
     * @return
     */
    @Select("<script>" +
            "select * from HotelComment" +
            " where 1=1" +
            " and masterId=#{id} and replyStatus=0 and replyType = 0" +
            " order by time desc" +
            "</script>")
    List<HotelComment> findCommentList(@Param("id") long id);

    /***
     * 更新回复删除状态
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update HotelComment set" +
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
            "select count(id) from HotelComment" +
            " where fatherId=#{commentId} and replyStatus=0 and replyType=1" +
            "</script>")
    int getReplayCount(@Param("commentId") long commentId);
}

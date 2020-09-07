package com.busi.dao;

import com.busi.entity.Pharmacy;
import com.busi.entity.PharmacyComment;
import com.busi.entity.PharmacyOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 家门口药店订单
 * @author: ZhaoJiaJie
 * @create: 2020-08-11 18:21:57
 */
@Mapper
@Repository
public interface PharmacyOrderDao {

    /***
     * 新增家门口药店订单
     * @param kitchenBookedOrders
     * @return
     */
    @Insert("insert into PharmacyOrder(userId,myId,pharmacyId,no,dishameCost,ordersState,pharmacyName,addTime,distributionMode,money,smallMap,inspectTicketTime," +
            "completeTime,address,address_Phone,address_Name,voucherCode,serviceTime,addressId,verificationType)" +
            "values (#{userId},#{myId},#{pharmacyId},#{no},#{dishameCost},#{ordersState},#{pharmacyName},#{addTime},#{distributionMode},#{money},#{smallMap},#{inspectTicketTime}" +
            ",#{completeTime},#{address},#{address_Phone},#{address_Name},#{voucherCode},#{serviceTime},#{addressId},#{verificationType})")
    @Options(useGeneratedKeys = true)
    int addOrders(PharmacyOrder kitchenBookedOrders);

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景  0删除 1接单 2配送 3验票 4完成 5取消订单
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyOrder" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and verificationType > 1 and ordersState != 3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0 and verificationType=0 and userId=#{userId} and paymentStatus = 1 and distributionMode = 0" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and verificationType=1 and userId=#{userId} and paymentStatus = 1 and distributionMode = 0" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState=0 " +
            "</if>" +
            "<if test=\"type == 4\">" +
            " and ordersState = 0 and verificationType=2 and paymentStatus = 1 and distributionMode = 0" +
            "</if>" +
            "<if test=\"type == 5\">" +
            " and ordersState = 0" +
            "</if>" +
            "</script>")
    PharmacyOrder findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyOrder" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    PharmacyOrder findByNo(@Param("no") String no);

    /***
     *  更新家门口药店订单状态
     *  updateCategory 更新类别  0删除状态  1待配送  2配送中  3已送达 4更新支付状态  5取消订单、评价状态  6验证状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update PharmacyOrder set" +
            "<if test=\"updateCategory == 0\">" +
            " ordersState =#{ordersState}" +
            "</if>" +
            "<if test=\"updateCategory == 1\">" +
            " verificationType =#{verificationType}," +
            " orderTime =#{orderTime}" +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " verificationType =#{verificationType}," +
            " deliveryTime =#{deliveryTime}" +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " verificationType =#{verificationType}," +
            " completeTime =#{completeTime}" +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " paymentStatus =#{paymentStatus}," +
            " paymentTime=#{paymentTime}" +
            "</if>" +
            "<if test=\"updateCategory == 5\">" +
            " verificationType =#{verificationType}" +
            "</if>" +
            "<if test=\"updateCategory == 6\">" +
            " verificationType =#{verificationType}," +
            " inspectTicketTime =#{inspectTicketTime}," +
            " completeTime =#{completeTime}" +
            "</if>" +
            " where id=#{id} and ordersState=0" +
            "</script>")
    int updateOrders(PharmacyOrder orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param verificationType  : 订单类型:  -1全部 0待验证,1已验证
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyOrder" +
            " where ordersState=0" +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"verificationType == 0\">" +
            " and verificationType = 0" +
            "</if>" +
            "<if test=\"verificationType == 1\">" +
            " and paymentStatus = 1" +
            " and verificationType = 1" +
            "</if>" +
            "<if test=\"verificationType == 2\">" +
            " and verificationType = 2" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<PharmacyOrder> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("verificationType") int verificationType);

    /***
     * 更新评分
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Pharmacy set" +
            " totalScore=#{totalScore}," +
            " averageScore=#{averageScore}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateScore(Pharmacy kitchen);

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Insert("insert into PharmacyComment(userId,masterId,replayId,content,time,replyType,replyStatus,fatherId,secondFatherId,replyNumber,orderId,imgUrls,score,anonymousType) " +
            "values (#{userId},#{masterId},#{replayId},#{content},#{time},#{replyType},#{replyStatus},#{fatherId},#{secondFatherId},#{replyNumber},#{orderId},#{imgUrls},#{score},#{anonymousType})")
    @Options(useGeneratedKeys = true)
    int addComment(PharmacyComment homeBlogComment);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from PharmacyComment where id = #{id} and replyStatus=0")
    PharmacyComment find(@Param("id") long id);

    /***
     * 更新删除状态
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update PharmacyComment set" +
            " replyStatus=#{replyStatus}" +
            " where id=#{id}" +
            "</script>")
    int update(PharmacyComment homeBlogComment);

    /***
     * 更新回复数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update PharmacyComment set" +
            " replyNumber=#{replyNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateCommentNum(PharmacyComment homeBlogComment);

    /***
     * 更新评论数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update Pharmacy set" +
            " totalEvaluate=#{totalEvaluate}" +
            " where id=#{id}" +
            "</script>")
    int updateBlogCounts(Pharmacy homeBlogComment);

    /***
     * 查询评论列表(只查评论replyType = 0)
     * @param masterId  景区ID
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyComment" +
            " where 1=1" +
            " and masterId=#{masterId} and replyStatus=0 and replyType = 0" +
            " order by time desc" +
            "</script>")
    List<PharmacyComment> findList(@Param("masterId") long masterId);

    /***
     * 查询回复列表(只查回复replyType = 1)
     * @param contentId  评论ID
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyComment" +
            " where 1=1" +
            " and fatherId=#{contentId} and replyStatus=0 and replyType = 1" +
            " order by time desc" +
            "</script>")
    List<PharmacyComment> findReplyList(@Param("contentId") long contentId);

    /***
     * 查询指定用户评论
     * @param id  指定景区ID
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyComment" +
            " where 1=1" +
            " and masterId=#{id} and replyStatus=0 and replyType = 0" +
            " order by time desc" +
            "</script>")
    List<PharmacyComment> findCommentList(@Param("id") long id);

    /***
     * 更新回复删除状态
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update PharmacyComment set" +
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
            "select count(id) from PharmacyComment" +
            " where fatherId=#{commentId} and replyStatus=0 and replyType=1" +
            "</script>")
    int getReplayCount(@Param("commentId") long commentId);
}

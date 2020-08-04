package com.busi.dao;

import com.busi.entity.ScenicSpotOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 家门口旅游订单
 * @author: ZhaoJiaJie
 * @create: 2020-07-30 14:43:56
 */
@Mapper
@Repository
public interface TravelOrderDao {

    /***
     * 新增家门口旅游订单
     * @param kitchenBookedOrders
     * @return
     */
    @Insert("insert into ScenicSpotOrder(userId,myId,scenicSpotId,no,dishameCost,ordersType,ordersState,scenicSpotName,addTime,playTime,money,smallMap,inspectTicketTime," +
            "completeTime,playNumber,address_Phone,address_Name,voucherCode)" +
            "values (#{userId},#{myId},#{scenicSpotId},#{no},#{dishameCost},#{ordersType},#{ordersState},#{scenicSpotName},#{addTime},#{playTime},#{money},#{smallMap},#{inspectTicketTime}" +
            ",#{completeTime},#{playNumber},#{address_Phone},#{address_Name},#{voucherCode})")
    @Options(useGeneratedKeys = true)
    int addOrders(ScenicSpotOrder kitchenBookedOrders);

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景 0删除 1由未验票改为已验票 2由已验票改为已完成 3取消订单
     * @return
     */
    @Select("<script>" +
            "select * from ScenicSpotOrder" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and ordersType >1 and ordersState!=3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0 and paymentStatus = 1" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and ordersType=1 and myId=#{userId} and paymentStatus = 1" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState = 0 and ordersType=0 " +
            "</if>" +
            "</script>")
    ScenicSpotOrder findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from ScenicSpotOrder" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    ScenicSpotOrder findByNo(@Param("no") String no);

    /***
     *  更新家门口旅游订单状态
     *  updateCategory 更新类别  0删除状态  1由未验票改为已验票  2由已验票改为已完成  3更新支付状态  4取消订单、评价状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update ScenicSpotOrder set" +
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
    int updateOrders(ScenicSpotOrder orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  -1全部 0待付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成未评价  4卖家取消订单 5用户取消订单 6已过期
     * @return
     */
    @Select("<script>" +
            "select * from ScenicSpotOrder" +
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
    List<ScenicSpotOrder> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType);
}

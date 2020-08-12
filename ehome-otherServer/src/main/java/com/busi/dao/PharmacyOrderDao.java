package com.busi.dao;

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
    @Insert("insert into PharmacyOrder(userId,myId,pharmacyId,no,dishameCost,ordersType,ordersState,pharmacyName,addTime,distributionMode,money,smallMap,inspectTicketTime," +
            "completeTime,address,address_Phone,address_Name,voucherCode,serviceTime,addressId)" +
            "values (#{userId},#{myId},#{pharmacyId},#{no},#{dishameCost},#{ordersType},#{ordersState},#{pharmacyName},#{addTime},#{distributionMode},#{money},#{smallMap},#{inspectTicketTime}" +
            ",#{completeTime},#{address},#{address_Phone},#{address_Name},#{voucherCode},#{serviceTime},#{addressId})")
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
            " and ordersType > 2 and ordersState != 3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0 and ordersType=0 and userId=#{userId} and paymentStatus = 1 and distributionMode = 0" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and ordersType=1 and userId=#{userId} and paymentStatus = 1 and distributionMode = 0" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState=0 " +
            "</if>" +
            "<if test=\"type == 4\">" +
            " and ordersState = 0 and ordersType=2 and paymentStatus = 1 and distributionMode = 0" +
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
            " ordersType =#{ordersType}," +
            " orderTime =#{orderTime}" +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " ordersType =#{ordersType}," +
            " deliveryTime =#{deliveryTime}" +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " ordersType =#{ordersType}," +
            " completeTime =#{completeTime}" +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " paymentStatus =#{paymentStatus}," +
            " paymentTime=#{paymentTime}" +
            "</if>" +
            "<if test=\"updateCategory == 5\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 6\">" +
            " ordersType =#{ordersType}," +
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
     * @param ordersType  : 订单类型:  -1全部 0待支付 1待验证, 2待评价
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
            "<if test=\"ordersType == 0\">" +
            " and paymentStatus = 0" +
            " and ordersType = 0" +
            "</if>" +
            "<if test=\"ordersType == 1\">" +
            " and paymentStatus = 1" +
            " and verificationType = 0" +
            " and distributionMode = 1" +
            "</if>" +
            "<if test=\"ordersType == 2\">" +
            " and ordersType = 3" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<PharmacyOrder> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType);

}

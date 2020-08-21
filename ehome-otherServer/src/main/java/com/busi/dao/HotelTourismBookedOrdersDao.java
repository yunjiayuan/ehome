package com.busi.dao;


import com.busi.entity.HotelTourismBookedOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 景区酒店订座订单
 * @author: ZHaoJiaJie
 * @create: 2020-08-20 18:13:07
 */
@Mapper
@Repository
public interface HotelTourismBookedOrdersDao {

    /***
     * 新增景区酒店订座订单
     * @param kitchenBookedOrders
     * @return
     */
    @Insert("insert into HotelTourismBookedOrders(userId,myId,kitchenId,no,dishameCost,ordersType,ordersState,kitchenName,addTime,completeTime,money,smallMap,eatNumber," +
            "eatTime,position,address_Phone,address_Name,remarks,dispensing,sex,positionId,elegantName,type)" +
            "values (#{userId},#{myId},#{kitchenId},#{no},#{dishameCost},#{ordersType},#{ordersState},#{kitchenName},#{addTime},#{completeTime},#{money},#{smallMap},#{eatNumber}" +
            ",#{eatTime},#{position},#{address_Phone},#{address_Name},#{remarks},#{dispensing},#{sex},#{positionId},#{elegantName},#{type})")
    @Options(useGeneratedKeys = true)
    int addOrders(HotelTourismBookedOrders kitchenBookedOrders);

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景 0删除 1由未接单改为已接单 2由已接单改为已上桌 3由进餐中改为完成 5加菜 6取消订单 7清桌
     * @return
     */
    @Select("<script>" +
            "select * from HotelTourismBookedOrders" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and ordersType >4 and ordersState!=3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0  and ordersType=1 and userId=#{userId} and addTime > date_sub(now(), interval 30 minute)" +//接单时间在30分钟内
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and ordersType=2 and userId=#{userId}" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState = 0 and ordersType=3 and myId=#{userId} and paymentStatus = 1" +
            "</if>" +
            "<if test=\"type == 5\">" +
            " and ordersState = 0 and(ordersType = 3 or ordersType = 2)" +
            "</if>" +
            "<if test=\"type == 6\">" +
            " and ordersState = 0" +
            "</if>" +
            "<if test=\"type == 7\">" +
            " and ordersState = 0 and ordersType = 5 and userId=#{userId}" +
            "</if>" +
            "</script>")
    HotelTourismBookedOrders findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from HotelTourismBookedOrders" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    HotelTourismBookedOrders findByNo(@Param("no") String no);

    /***
     *  更新景区酒店订座订单状态
     *  updateCategory 更新类别  0删除状态  1由未接单改为已接单  2由已接单改为菜已上桌  3由菜已上桌改为进餐中  4更新支付状态  5完成   6取消订单、评价状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update HotelTourismBookedOrders set" +
            "<if test=\"updateCategory == 0\">" +
            " ordersState =#{ordersState}" +
            "</if>" +
            "<if test=\"updateCategory == 1\">" +
            " orderTime =#{orderTime}," +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " ordersType =#{ordersType}," +
            " upperTableTime =#{upperTableTime}" +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " paymentStatus =#{paymentStatus}," +
            " addToFoodMoney =#{addToFoodMoney}," +
            " paymentTime=#{paymentTime}" +
            "</if>" +
            "<if test=\"updateCategory == 5\">" +
            " ordersType =#{ordersType}," +
            " completeTime =#{completeTime}" +
            "</if>" +
            "<if test=\"updateCategory == 6\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            " where id=#{id} and ordersState=0" +
            "</script>")
    int updateOrders(HotelTourismBookedOrders orders);

    /***
     *  更新景区酒店订座订单
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update HotelTourismBookedOrders set" +
            " money =#{money}," +
            " addToFood =#{addToFood}," +
            " paymentStatus =#{paymentStatus}," +
            " addToFoodMoney =#{addToFoodMoney}" +
            " where id=#{id} and ordersState=0" +
            "</script>")
    int upOrders(HotelTourismBookedOrders orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  0全部 1未接单,2已接单,3进餐中，4完成  5退款
     * @return
     */
    @Select("<script>" +
            "select * from HotelTourismBookedOrders" +
            " where ordersState=0" +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"ordersType > 0 and ordersType &lt; 3\">" +
            " and ordersType = #{ordersType}" +
            "</if>" +
            "<if test=\"ordersType == 3\">" +
            " and ordersType = 3" +
            "</if>" +
            "<if test=\"ordersType == 4\">" +
            " and ordersType = 5" +
            "</if>" +
            " and type = #{type}" +
            " order by addTime desc" +
            "</script>")
    List<HotelTourismBookedOrders> findOrderList(@Param("type") int type, @Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType);

    /***
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Select("<script>" +
            "select * from HotelTourismBookedOrders" +
            " where 1=1 " +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            " and type = #{type}" +
            " and ordersState = 0" +
            "</script>")
    List<HotelTourismBookedOrders> findIdentity(@Param("type") int type, @Param("identity") int identity, @Param("userId") long userId);

    /***
     * 查询订单（查询包间列表时）
     * @return
     */
    @Select("<script>" +
            "select * from HotelTourismBookedOrders" +
            " where 1=1 " +
            " and type = #{type}" +
            " and userId = #{userId}" +
            " and position = #{bookedType}" +
            " and ordersState = 0" +
            " and ordersType &lt; 5" +
            " and #{eatTime} >= date_sub(eatTime, interval 3 hour)" +
            " and #{eatTime} &lt;= date_add(eatTime, interval 3 hour)" +
            "</script>")
    List<HotelTourismBookedOrders> findOrdersList(@Param("type") int type,@Param("userId") long userId, @Param("eatTime") Date eatTime, @Param("bookedType") int bookedType);
}

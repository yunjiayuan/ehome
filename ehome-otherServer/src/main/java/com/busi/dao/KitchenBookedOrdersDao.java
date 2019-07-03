package com.busi.dao;


import com.busi.entity.KitchenBookedOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 厨房订座订单
 * @author: ZHaoJiaJie
 * @create: 2019-06-27 14:55
 */
@Mapper
@Repository
public interface KitchenBookedOrdersDao {

    /***
     * 新增厨房订座订单
     * @param kitchenBookedOrders
     * @return
     */
    @Insert("insert into KitchenBookedOrders(userId,myId,kitchenId,no,dishameCost,ordersType,ordersState,kitchenName,addTime,completeTime,money,smallMap,eatNumber," +
            "eatDate,eatTime,position,address_Phone,address_Name,remarks,dispensing,sex)" +
            "values (#{userId},#{myId},#{kitchenId},#{no},#{dishameCost},#{ordersType},#{ordersState},#{kitchenName},#{addTime},#{completeTime},#{money},#{smallMap},#{eatNumber}" +
            ",#{eatDate},#{eatTime},#{position},#{address_Phone},#{address_Name},#{remarks},#{dispensing},#{sex})")
    @Options(useGeneratedKeys = true)
    int addOrders(KitchenBookedOrders kitchenBookedOrders);

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景 0删除 1由未接单改为已接单 2由已接单改为已完成 3取消订单
     * @return
     */
    @Select("<script>" +
            "select * from KitchenBookedOrders" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and ordersType >2 and ordersState!=3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0  and ordersType=1 and userId=#{userId} and addTime > date_sub(now(), interval 5 minute)" +//接单时间在五分钟内
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and ordersType=2 and myId=#{userId}" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState = 0" +
            "</if>" +
            "</script>")
    KitchenBookedOrders findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from KitchenBookedOrders" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    KitchenBookedOrders findByNo(@Param("no") String no);

    /***
     *  更新厨房订座订单状态
     *  updateCategory 更新类别  默认0删除状态  1由未接单改为已接单  2由已接单改为已完成  3取消订单  4更新支付状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update KitchenBookedOrders set" +
            "<if test=\"updateCategory == 0\">" +
            " ordersState =#{ordersState}" +
            "</if>" +
            "<if test=\"updateCategory == 1\">" +
            " orderTime =#{orderTime}," +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " ordersType =#{ordersType}," +
            " completeTime =#{completeTime}" +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " ordersType =#{ordersType}," +
            " paymentTime=#{paymentTime}" +
            "</if>" +
            " where id=#{id}" +
            "</script>")
    int updateOrders(KitchenBookedOrders orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2已接单,3已完成  4卖家取消订单 5用户取消订单 6付款超时 7接单超时
     * @return
     */
    @Select("<script>" +
            "select * from KitchenBookedOrders" +
            " where ordersState=0" +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"ordersType >= 0 and ordersType &lt; 4\">" +
            " and ordersType = #{ordersType}" +
            "</if>" +
            "<if test=\"ordersType ==4\">" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<KitchenBookedOrders> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType, @Param("ids") String[] ids);

    /***
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Select("<script>" +
            "select * from KitchenBookedOrders" +
            " where 1=1 " +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            " and ordersState = 0" +
            "</script>")
    List<KitchenBookedOrders> findIdentity(@Param("identity") int identity, @Param("userId") long userId);

}
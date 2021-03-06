package com.busi.dao;

import com.busi.entity.ShopFloorOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店订单
 * @author: ZHaoJiaJie
 * @create: 2019-12-18 15:44
 */
@Mapper
@Repository
public interface ShopFloorOrdersDao {

    /***
     * 新增订单
     * @param kitchenBookedOrders
     * @return
     */
    @Insert("insert into ShopFloorOrders(buyerId,shopId,goods,money,addTime,paymentTime,deliveryTime,receivingTime,no,ordersState,ordersType," +
            "addressId,distributioMode,shopName,remarks,address,addressName,addressPhone,addressProvince,addressCity,addressDistrict,type,recipientId)" +
            "values (#{buyerId},#{shopId},#{goods},#{money},#{addTime},#{paymentTime},#{deliveryTime},#{receivingTime},#{no},#{ordersState},#{ordersType}" +
            ",#{addressId},#{distributioMode},#{shopName},#{remarks},#{address},#{addressName},#{addressPhone},#{addressProvince},#{addressCity},#{addressDistrict},#{type},#{recipientId})")
    @Options(useGeneratedKeys = true)
    int addOrders(ShopFloorOrders kitchenBookedOrders);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ShopFloorOrders set" +
            "<if test=\"recipientId > 0\">" +
            " recipientId=#{recipientId}," +
            "</if>" +
            " addressCity=#{addressCity}," +
            " addressDistrict=#{addressDistrict}," +
            " addressProvince=#{addressProvince}," +
            " addressPhone=#{addressPhone}," +
            " addressName=#{addressName}," +
            " receiveState=#{receiveState}," +
            " shopId=#{shopId}," +
            " shopName=#{shopName}," +
            " ordersType=#{ordersType}," +
            " address=#{address}" +
            " where id=#{id}" +
            "</script>")
    int upSFreceiveState(ShopFloorOrders kitchen);

    /***
     * 根据ID查询订单
     * @param id
     * @param type  查询场景 0删除 1由未发货改为已发货 2由未收货改为已收货 3取消订单  4由未送出改为待发货（已送出）
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorOrders" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and ordersType >2 and ordersState!=3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0  and ordersType=1" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0  and ordersType=2" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState=0 and (ordersType &lt; 3 or ordersType=8)" +
            "</if>" +
            "<if test=\"type == 4\">" +
            " and ordersState=0  and ordersType=8" +
            "</if>" +
            "</script>")
    ShopFloorOrders findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorOrders" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    ShopFloorOrders findByNo(@Param("no") String no);

    /***
     *  更新楼店订单状态
     *  updateCategory 更新类别  0删除状态  1由未发货改为已发货 2由未收货改为已收货 3取消订单 4更新支付状态  5更新礼尚往来领取状态  6更新礼尚往来接收者
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update ShopFloorOrders set" +
            "<if test=\"updateCategory == 0\">" +
            " ordersState =1," +
            "</if>" +
            "<if test=\"updateCategory == 1\">" +
            " deliveryTime =#{deliveryTime}," +
            " ordersType =#{ordersType}," +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " ordersType =#{ordersType}," +
            " receivingTime =#{receivingTime}," +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " ordersType =#{ordersType}," +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " paymentTime =#{paymentTime}," +
            " ordersType =#{ordersType}," +
            "</if>" +
            "<if test=\"updateCategory == 5\">" +
            " receiveState =#{receiveState}," +
            "</if>" +
            "<if test=\"updateCategory == 6\">" +
            " ordersType =#{ordersType}," +
            " recipientId =#{recipientId}," +
            "</if>" +
            " id=#{id} " +
            " where id=#{id} and ordersState=0" +
            "</script>")
    int updateOrders(ShopFloorOrders orders);

    /***
     * 分页查询订单列表
     * @param type    0黑店订单  1礼尚往来
     * @param ordersType 订单类型: -1全部 0待付款,1待发货(已付款),2已发货（待收货）, 3已收货（待评价）  4已评价  5付款超时、发货超时、取消订单  8待送出（礼尚往来）
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorOrders" +
            " where ordersState=0" +
            "<if test=\"type == 0 \">" +
            " and type = #{type}" +
            "</if>" +
            "<if test=\"type == 1 \">" +
            " and type in(1,2) " +
            "</if>" +
            "<if test=\"ordersType == -1\">" +
            " and (buyerId = #{userId} or recipientId = #{userId})" +
            "</if>" +
            "<if test=\"ordersType >= 0 and ordersType &lt; 5\">" +
            "<if test=\"type == 1 and ordersType == 1\">" +
            " and receiveState = 1 " +
            "</if>" +
            " and buyerId = #{userId}" +
            " and ordersType = #{ordersType}" +
            "</if>" +
            "<if test=\"ordersType >= 5 and ordersType &lt; 8\">" +
            " and buyerId = #{userId}" +
            " and ordersType > 4 and ordersType &lt; 8" +
            "</if>" +
            "<if test=\"ordersType == 8\">" +
            " and ordersType = 8" +
            " and buyerId = #{userId}" +
            "</if>" +
            "<if test=\"ordersType == 9\">" +
            " and recipientId = #{userId} " +
            " and ordersType = 1" +
            " and receiveState = 0" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<ShopFloorOrders> findOrderList(@Param("type") int type, @Param("userId") long userId, @Param("ordersType") int ordersType);

    /***
     * 分页查询订单列表
     * @param shopId    店铺ID  只在商家查询黑店订单时有效
     * @param ordersType 订单类型: -1全部 0待付款,1待发货(已付款),2已发货（待收货）, 3已收货（待评价）  4已评价  5付款超时、发货超时、取消订单
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorOrders" +
            " where ordersState=0 and type = 0" +
            "<if test=\"shopId > 0 \">" +
            " and shopId = #{shopId}" +
            "</if>" +
            "<if test=\"ordersType >= 0 and ordersType &lt; 5\">" +
            " and ordersType = #{ordersType}" +
            "</if>" +
            "<if test=\"ordersType >= 5 and ordersType &lt; 8\">" +
            " and ordersType > 4 and ordersType &lt; 8" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<ShopFloorOrders> findOrderList2(@Param("shopId") long shopId, @Param("ordersType") int ordersType);

    /***
     * 统计各类订单数量
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorOrders" +
            " where 1=1 " +
            " and (buyerId = #{userId} or recipientId = #{userId}) and type!=3" +
            " and ordersState = 0" +
            " order by addTime desc" +
            "</script>")
    List<ShopFloorOrders> findIdentity(@Param("userId") long userId);


}

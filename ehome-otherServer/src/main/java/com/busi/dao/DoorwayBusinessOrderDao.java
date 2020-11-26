package com.busi.dao;

import com.busi.entity.DoorwayBusinessOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 家门口隐形商家订单相关
 * @author: ZhaoJiaJie
 * @create: 2020-11-18 14:29:08
 */
@Mapper
@Repository
public interface DoorwayBusinessOrderDao {

    /***
     * 新增家门口商家订单
     * @param kitchenBookedOrders
     * @return
     */
    @Insert("insert into DoorwayBusinessOrder(userId,myId,pharmacyId,no,dishameCost,ordersState,pharmacyName,addTime,distributionMode,money,smallMap,inspectTicketTime," +
            "completeTime,address,address_Phone,address_Name,voucherCode,serviceTime,addressId,verificationType,remarks)" +
            "values (#{userId},#{myId},#{pharmacyId},#{no},#{dishameCost},#{ordersState},#{pharmacyName},#{addTime},#{distributionMode},#{money},#{smallMap},#{inspectTicketTime}" +
            ",#{completeTime},#{address},#{address_Phone},#{address_Name},#{voucherCode},#{serviceTime},#{addressId},#{verificationType},#{remarks})")
    @Options(useGeneratedKeys = true)
    int addOrders(DoorwayBusinessOrder kitchenBookedOrders);

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景  0删除 1接单 2配送 3验票 4完成 5取消订单
     * @return
     */
    @Select("<script>" +
            "select * from DoorwayBusinessOrder" +
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
    DoorwayBusinessOrder findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from DoorwayBusinessOrder" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    DoorwayBusinessOrder findByNo(@Param("no") String no);

    /***
     *  更新家门口商家订单状态
     *  updateCategory 更新类别  0删除状态  1待配送  2配送中  3已送达 4更新支付状态  5取消订单、评价状态  6验证状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusinessOrder set" +
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
    int updateOrders(DoorwayBusinessOrder orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param verificationType  : 订单类型:  -1全部 0待验证,1已验证
     * @return
     */
    @Select("<script>" +
            "select * from DoorwayBusinessOrder" +
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
    List<DoorwayBusinessOrder> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("verificationType") int verificationType);

}

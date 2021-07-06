package com.busi.dao;


import com.busi.entity.KitchenEvaluate;
import com.busi.entity.KitchenFabulous;
import com.busi.entity.KitchenOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 厨房相关Dao
 * author：zhaojiajie
 * create time：2019-3-7 17:44:35
 */
@Mapper
@Repository
public interface KitchenOrdersDao {

    /***
     * 新增厨房订单
     * @param orders
     * @return
     */
    @Insert("insert into KitchenOrders(userId,myId,kitchenId,no,dishameCost,ordersType,ordersState,kitchenName,addTime,orderTime,deliveryTime,serviceTime,receivingTime,money,smallMap,eatNumber," +
            "remarks,addressId,address_Name,address_Phone,address_province,address_city,address_district,address_postalcode,address)" +
            "values (#{userId},#{myId},#{kitchenId},#{no},#{dishameCost},#{ordersType},#{ordersState},#{kitchenName},#{addTime},#{orderTime},#{deliveryTime},#{serviceTime},#{receivingTime},#{money},#{smallMap},#{eatNumber}" +
            ",#{remarks},#{addressId},#{address_Name},#{address_Phone},#{address_province},#{address_city},#{address_district},#{address_postalcode},#{address})")
    @Options(useGeneratedKeys = true)
    int addOrders(KitchenOrders orders);

    /***
     * 新增菜品点赞
     * @param fabulous
     * @return
     */
    @Insert("insert into KitchenFabulous(userId,time,myId,status,dishesId,bookedState) " +
            "values (#{userId},#{time},#{myId},#{status},#{dishesId},#{bookedState})")
    @Options(useGeneratedKeys = true)
    int addLike(KitchenFabulous fabulous);

    /***
     * 新增评价
     * @param evaluate
     * @return
     */
    @Insert("insert into KitchenEvaluate(userId,content,orderId,kitchenId,imgUrls,kitchenCover,time,state,score,anonymousType,bookedState) " +
            "values (#{userId},#{content},#{orderId},#{kitchenId},#{imgUrls},#{kitchenCover},#{time},#{state},#{score},#{anonymousType},#{bookedState})")
    @Options(useGeneratedKeys = true)
    int addEvaluate(KitchenEvaluate evaluate);

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景 0删除 1由未接单改为制作中 2由制作中改为配送中 3由配送中改为已卖出  4查看订单详情、取消订单  5评价
     * @return
     */
    @Select("<script>" +
            "select * from KitchenOrders" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and ordersType >3 and ordersState!=3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0  and ordersType=1 and userId=#{userId} and addTime > date_sub(now(), interval 5 minute)" +//接单时间在五分钟内
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and ordersType=2 and userId=#{userId}" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState=0 and ordersType=3 and userId=#{userId}" +
            "</if>" +
            "<if test=\"type == 4\">" +
            " and ordersState = 0" +
            "</if>" +
            "<if test=\"type == 5\">" +
            " and ordersState = 0 and ordersType =4 and myId=#{userId}" +
            "</if>" +
            "</script>")
    KitchenOrders findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from KitchenOrders" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    KitchenOrders findByNo(@Param("no") String no);


    /***
     * 根据评价ID查询
     * @param id
     * @return
     */
    @Update("<script>" +
            "update KitchenEvaluate set" +
            " state=1" +
            " where id = #{id}" +
            "</script>")
    int findEvaluateId(@Param("id") long id);

    /***
     *  更新厨房订单状态
     *  updateCategory 更新类别  默认0删除状态  1由未接单改为制作中  2由制作中改为配送中  3由配送中改为已卖出  4取消订单  5更新订单状态为已评价  6更新支付状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update KitchenOrders set" +
            "<if test=\"updateCategory == 0\">" +
            " ordersState =#{ordersState}" +
            "</if>" +
            "<if test=\"updateCategory == 1\">" +
            " orderTime =#{orderTime}," +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " ordersType =#{ordersType}," +
            " deliveryTime =#{deliveryTime}" +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " ordersType =#{ordersType}," +
            " receivingTime =#{receivingTime}" +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 5\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 6\">" +
            " ordersType =#{ordersType}," +
            " paymentTime=#{paymentTime}" +
            "</if>" +
            " where id=#{id}" +
            "</script>")
    int updateOrders(KitchenOrders orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2制作中(已接单未发货),3配送(已发货未收货),4已卖出(已收货未评价),  5卖家取消订单 6付款超时 7接单超时 8发货超时 9用户取消订单 10 已评价
     * @return
     */
    @Select("<script>" +
            "select * from KitchenOrders" +
            " where ordersState=0" +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId} and paymentTime != ''" +
            "</if>" +
            "<if test=\"ordersType >= 0 and ordersType &lt; 5\">" +
            " and ordersType = #{ordersType}" +
            "</if>" +
            "<if test=\"ordersType == 10\">" +
            " and ordersType = 10" +
            "</if>" +
            "<if test=\"ordersType ==5\">" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<KitchenOrders> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType, @Param("ids") String[] ids);

    /***
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Select("<script>" +
            "select * from KitchenOrders" +
            " where 1=1 " +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            " and ordersState = 0" +
            "</script>")
    List<KitchenOrders> findIdentity(@Param("identity") int identity, @Param("userId") long userId);
}

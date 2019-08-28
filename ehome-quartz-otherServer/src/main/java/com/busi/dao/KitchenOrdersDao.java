package com.busi.dao;


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
            " and ordersState=0 and ordersType=3 and myId=#{userId}" +
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
     *  更新厨房订单类型
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update KitchenOrders set" +
            "<if test=\"receivingTime != null\">" +
            " receivingTime=#{receivingTime}," +
            "</if>" +
            " ordersType=#{ordersType}" +
            " where id=#{id}" +
            "</script>")
    int updateOrders(KitchenOrders orders);

    /***
     * 订单条件查询
     * @return
     */
    @Select("<script>" +
            "select * from KitchenOrders" +
            " where ordersState=0" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<KitchenOrders> findOrderList(@Param("ids") String[] ids);

}

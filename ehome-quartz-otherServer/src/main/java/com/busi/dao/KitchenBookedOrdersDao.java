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
     *  更新厨房订座订单状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update KitchenBookedOrders set" +
            "<if test=\"completeTime != null\">" +
            " completeTime=#{completeTime}," +
            "</if>" +
            " ordersType=#{ordersType}" +
            " where id=#{id} and ordersState=0" +
            "</script>")
    int upOrders(KitchenBookedOrders orders);

    /***
     * 订单条件查询
     * @return
     */
    @Select("<script>" +
            "select * from KitchenBookedOrders" +
            " where ordersState=0" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<KitchenBookedOrders> findOrderList(@Param("ids") String[] ids);

}

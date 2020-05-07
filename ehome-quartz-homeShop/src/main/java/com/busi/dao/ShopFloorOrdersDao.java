package com.busi.dao;

import com.busi.entity.ShopFloorOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 搂店、礼尚往来、合伙购订单Dao
 * author：zhaojiajie
 * create time：2018-10-26 10:00:28
 */
@Mapper
@Repository
public interface ShopFloorOrdersDao {

    /***
     * 查询所有二手订单
     * @param //ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价)
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorOrders" +
            " where 1=1" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and ordersState = 0" +
            "</script>")
    List<ShopFloorOrders> findOrderList2(@Param("ids") String[] ids);

    /***
     * 取消订单
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " ordersType=#{ordersType}" +
            " where id=#{id} and buyerId=#{buyerId}" +
            "</script>")
    int cancelOrders(ShopFloorOrders usedDealOrders);


    /***
     * 更新收货状态
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " ordersType=#{ordersType}," +
            " receivingTime=#{receivingTime}" +
            " where id=#{id} and buyerId=#{buyerId}" +
            "</script>")
    int updateCollect(ShopFloorOrders usedDealOrders);
}

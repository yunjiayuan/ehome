package com.busi.dao;

import com.busi.entity.UsedDealExpress;
import com.busi.entity.UsedDealLogistics;
import com.busi.entity.UsedDealOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 二手订单Dao
 * author：zhaojiajie
 * create time：2018-10-26 10:00:28
 */
@Mapper
@Repository
public interface UsedDealOrdersDao {

    /***
     * 查询所有二手订单
     * @param //ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价), 4用户取消订单  5卖家取消订单  6付款超时
     * @return
     */
    @Select("<script>" +
            "select * from UsedDealOrders" +
            " where 1=1" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and ordersState = 0" +
            "</script>")
    List<UsedDealOrders> findOrderList2(@Param("ids") String[] ids);

    /***
     * 取消订单
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " ordersType=#{ordersType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int cancelOrders(UsedDealOrders usedDealOrders);


    /***
     * 更新收货状态
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " ordersType=#{ordersType}," +
            " receivingTime=#{receivingTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateCollect(UsedDealOrders usedDealOrders);
}

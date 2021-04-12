package com.busi.dao;

import com.busi.entity.RentAhouse;
import com.busi.entity.RentAhouseOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 租房买房
 * @author: ZHaoJiaJie
 * @create: 2021-03-30 14:42:10
 */
@Mapper
@Repository
public interface RentAhouseOrderDao {

    /***
     * 订单条件查询
     * @return
     */
    @Select("<script>" +
            "select * from RentAhouseOrder" +
            " where ordersState=0" +
            " and 2 > paymentStatus" +
            " and makeMoneyStatus = 0" +
            "</script>")
    List<RentAhouseOrder> findOrderList();

    /***
     * 更新订单
     * @param rentAhouseOrder
     * @return
     */
    @Update("<script>" +
            "update RentAhouseOrder set" +
            " makeMoneyStatus=1" +
            " where id=#{id}" +
            "</script>")
    int upOrders(RentAhouseOrder rentAhouseOrder);

    /***
     * 更新付款状态
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update RentAhouseOrder set" +
            " paymentStatus=#{paymentStatus}," +
            " where no = #{no}" +
            "</script>")
    int updatePayType(RentAhouseOrder usedDealOrders);

    /***
     * 更新房源
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update RentAhouse set" +
            " sellState=#{sellState}" +
            " where id=#{id}" +
            "</script>")
    int changeCommunityState(RentAhouse kitchenBooked);
}

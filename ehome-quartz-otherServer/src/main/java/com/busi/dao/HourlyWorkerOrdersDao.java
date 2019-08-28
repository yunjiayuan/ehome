package com.busi.dao;

import com.busi.entity.HourlyWorkerOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 小时工订单相关Dao
 * author：zhaojiajie
 * create time：2019-4-25 13:45:15
 */
@Mapper
@Repository
public interface HourlyWorkerOrdersDao {

    /***
     * 根据用户ID查询订单
     * @param userId
     * @param type  查询场景 0删除 1由未接单改为已接单 2由服务中改为已完成 3查看订单详情、取消订单  4评价
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerOrders" +
            " where id = #{id}" +
            "<if test=\"type == 0\">" +
            " and ordersType >2 and ordersState!=3" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and ordersState=0  and ordersType=8 and userId=#{userId} and addTime > date_sub(now(), interval 30 minute)" +//接单时间在30分钟内
            "</if>" +
            "<if test=\"type == 2\">" +
            " and ordersState=0 and ordersType=1 and myId=#{userId}" +
            "</if>" +
            "<if test=\"type == 3\">" +
            " and ordersState = 0" +
            "</if>" +
            "<if test=\"type == 4\">" +
            " and ordersState = 0 and ordersType =2 and myId=#{userId}" +
            "</if>" +
            "</script>")
    HourlyWorkerOrders findById(@Param("id") long id, @Param("userId") long userId, @Param("type") int type);

    /***
     *  更新小时工订单状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update HourlyWorkerOrders set" +
            "<if test=\"receivingTime != null\">" +
            " receivingTime=#{receivingTime}," +
            "</if>" +
            " ordersType=#{ordersType}" +
            " where id=#{id}" +
            "</script>")
    int updateOrders(HourlyWorkerOrders orders);

    /***
     * 订单条件查询
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerOrders" +
            " where ordersState=0" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<HourlyWorkerOrders> findOrderList(@Param("ids") String[] ids);

}

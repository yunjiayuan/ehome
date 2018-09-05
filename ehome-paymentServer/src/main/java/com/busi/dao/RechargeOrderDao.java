package com.busi.dao;

import com.busi.entity.RechargeOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 充值订单相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface RechargeOrderDao {

    /***
     * 新增
     * @param rechargeOrder
     * @return
     */
    @Insert("insert into rechargeOrder (orderNumber,userId,money,payStatus,time) values (#{orderNumber},#{userId},#{money},#{payStatus},#{time})")
    int addRechargeOrder(RechargeOrder rechargeOrder);

    /***
     * 根据订单号和用户ID查询
     * @param userId      用户ID
     * @param orderNumber 订单号
     * @return
     */
    @Select("select * from rechargeOrder where userId = #{userId} and orderNumber = #{orderNumber}")
    RechargeOrder findRechargeOrder(@Param("userId") long userId,@Param("orderNumber") String orderNumber);

    /***
     * 更新支付状态
     * @param userId      用户ID
     * @param orderNumber 订单号
     * @return
     */
    @Update("update rechargeOrder set payStatus = 1 where userId = #{userId} and orderNumber = #{orderNumber}")
    int updateRechargeOrder(@Param("userId") long userId,@Param("orderNumber") String orderNumber);
}

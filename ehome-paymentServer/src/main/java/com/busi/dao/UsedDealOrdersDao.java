package com.busi.dao;

import com.busi.entity.UsedDealOrders;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 二手订单Dao
 * author：zhaojiajie
 * create time：2018-10-26 10:00:28
 */
@Mapper
@Repository
public interface UsedDealOrdersDao {

    /***
     * 买家更新订单状态 为已付款
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " ordersType=1" +
            " where orderNumber=#{orderNumber} and myId=#{myId}" +
            "</script>")
    int updateUsedDealOrdersType(UsedDealOrders usedDealOrders);

}

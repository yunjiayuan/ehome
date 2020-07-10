package com.busi.dao;

import com.busi.entity.CashOutOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 提现相关DAO
 * author：SunTianJie
 * create time：2020-7-1 15:16:53
 */
@Mapper
@Repository
public interface CashOutDao {

    /***
     * 查询未到账订单
     * @return
     */
    @Select("select * from cashOutOrder where cashOutStatus = 0 and date_sub(now(), interval 30 minute) > time") //提现发起时间超过30分钟
    List<CashOutOrder> findCashOutOrderList();

}

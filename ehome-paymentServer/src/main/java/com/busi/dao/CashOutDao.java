package com.busi.dao;

import com.busi.entity.CashOutOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 提现相关DAO
 * author：SunTianJie
 * create time：2020-7-1 15:16:53
 */
@Mapper
@Repository
public interface CashOutDao {

    /***
     * 新增
     * @param cashOutOrder
     * @return
     */
    @Insert("insert into cashOutOrder (id,userId,openid,type,money,payStatus,time,cashOutStatus) " +
            "values (#{id},#{userId},#{openid},#{type},#{money},#{payStatus},#{time},#{cashOutStatus})")
//    @Options(useGeneratedKeys = true)
    int addCashOutOrder(CashOutOrder cashOutOrder);

    /***
     * 根据ID查询提现订单信息
     * @param userId
     * @param id
     * @return
     */
    @Select("select * from cashOutOrder where userId = #{userId} and id = #{id}")
    CashOutOrder findCashOutOrder(@Param("userId") long userId, @Param("id") String id);

    /***
     * 更新支付状态
     * @param cashOutOrder
     * @return
     */
    @Update("update cashOutOrder set payStatus = #{payStatus} where userId = #{userId} and id = #{id}")
    int updateCashOutOrder(CashOutOrder cashOutOrder);

    /***
     * 更新到账状态
     * @param cashOutOrder
     * @return
     */
    @Update("update cashOutOrder set cashOutStatus = #{cashOutStatus},accountTime = #{accountTime},type = #{type} where userId = #{userId} and id = #{id}")
    int updateCashOutStatus(CashOutOrder cashOutOrder);


}

package com.busi.dao;

import com.busi.entity.RentAhouseOrder;
import com.busi.entity.UsedDealOrders;
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
     * 新增房源
     * @param kitchenBooked
     * @return
     */
    @Insert("insert into RentAhouseOrder(userId,roomState,myId,houseId,picture,no," +
            "villageName,province,city,district,houseNumber,houseCompany," +
            "unitNumber,unitCompany,roomNumber,residence," +
            "livingRoom,toilet,roomType," +
            "bedroomType,leaseContract,deposit,money,price," +
            "paymentMethod,paymentStatus,addTime,paymentTime,nextPaymentTime,duration,rentMoney,ordersState)" +
            "values (#{userId},#{roomState},#{myId},#{houseId},#{picture},#{no}," +
            "#{villageName},#{province},#{city},#{district},#{houseNumber},#{houseCompany}," +
            "#{unitNumber},#{unitCompany},#{roomNumber},#{residence},#{livingRoom},#{toilet}," +
            "#{roomType},#{bedroomType},#{leaseContract},#{deposit}," +
            "#{money},#{price},#{paymentMethod},#{paymentStatus},#{addTime},#{paymentTime},#{nextPaymentTime},#{duration}" +
            ",#{rentMoney},#{ordersState})")
    @Options(useGeneratedKeys = true)
    int addCommunity(RentAhouseOrder kitchenBooked);

    /***
     * 根据Id查询房源
     * @param no
     * @return
     */
    @Select("select * from RentAhouseOrder where no=#{no} and ordersState=0")
    RentAhouseOrder findByUserId(@Param("no") String no);

    /***
     * 分页条件查询 按userId查询
     * @param userId   用户ID
     * @return
     */
    @Select("<script>" +
            "select * from RentAhouseOrder" +
            " where ordersState=0" +
            "<if test=\"ordersType >= 0\">" +
            " and roomState = #{ordersType}" +
            "</if>" +
            " and myId = #{userId}" +
            " order by addTime desc" +
            "</script>")
    List<RentAhouseOrder> findHList(@Param("userId") long userId, @Param("ordersType") int ordersType);

    /***
     * 更新付款状态
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update RentAhouseOrder set" +
            " paymentStatus=#{paymentStatus}," +
            " paymentTime=#{paymentTime}" +
            " where no = #{no} and myId=#{myId}" +
            "</script>")
    int updatePayType(RentAhouseOrder usedDealOrders);
}

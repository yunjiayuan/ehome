package com.busi.dao;

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
     * 新增房源
     * @param kitchenBooked
     * @return
     */
    @Insert("insert into RentAhouseOrder(userId,roomState,myId,houseId,picture,no," +
            "villageName,province,city,district,houseNumber,houseCompany," +
            "unitNumber,unitCompany,roomNumber,residence," +
            "livingRoom,toilet,roomType,housingArea,orientation," +
            "bedroomType,leaseContract,deposit,money,price," +
            "paymentMethod,paymentStatus,addTime,paymentTime,nextPaymentTime,duration,rentMoney,ordersState,telephone)" +
            "values (#{userId},#{roomState},#{myId},#{houseId},#{picture},#{no}," +
            "#{villageName},#{province},#{city},#{district},#{houseNumber},#{houseCompany}," +
            "#{unitNumber},#{unitCompany},#{roomNumber},#{residence},#{livingRoom},#{toilet}," +
            "#{roomType},#{housingArea},#{orientation},#{bedroomType},#{leaseContract},#{deposit}," +
            "#{money},#{price},#{paymentMethod},#{paymentStatus},#{addTime},#{paymentTime},#{nextPaymentTime},#{duration}" +
            ",#{rentMoney},#{ordersState},#{telephone})")
    @Options(useGeneratedKeys = true)
    int addCommunity(RentAhouseOrder kitchenBooked);

    /***
     * 更新订单
     * @param rentAhouseOrder
     * @return
     */
    @Update("<script>" +
            "update RentAhouseOrder set" +
            " renewalState=#{renewalState}," +
            " duration=#{duration}," +
            " rentMoney=#{rentMoney}," +
            " makeMoneyStatus=#{makeMoneyStatus}," +
            " price=#{price}" +
            " where id=#{id}" +
            "</script>")
    int upOrders(RentAhouseOrder rentAhouseOrder);

    /***
     * 根据Id查询房源
     * @param no
     * @return
     */
    @Select("select * from RentAhouseOrder where no=#{no} and ordersState=0")
    RentAhouseOrder findByUserId(@Param("no") String no);

    /***
     * 分页查询订单列表
     * @param type  房屋类型: -1默认全部 0购房  1租房
     * @param ordersType 订单类型:  type=0时：0购房  1出售  type=1时：0租房  1出租
     * @return
     */
    @Select("<script>" +
            "select * from RentAhouseOrder" +
            " where ordersState=0" +
            "<if test=\"type >= 0\">" +
            " and roomState = #{type}" +
            "</if>" +
            "<if test=\"ordersType == 0\">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"ordersType == 1\">" +
            " and userId = #{userId}" +
            " and paymentStatus = 1" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<RentAhouseOrder> findHList(@Param("userId") long userId, @Param("type") int type, @Param("ordersType") int ordersType);

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

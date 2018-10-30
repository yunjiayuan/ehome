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
     * 新增订单
     * @param usedDealOrders
     * @return
     */
    @Insert("insert into usedDealOrders(myId,userId,goodsId,logisticsId,addTime,paymentTime,deliveryTime,receivingTime,delayTime,orderNumber," +
            "ordersState,title,money,sellingPrice,postage,pinkageType,picture,distributioMode,extendFrequency,afficheType,ordersType,addressId,address_Name,address_Phone,address_province,address_city,address_district,address_postalcode,address) " +
            "values (#{myId},#{userId},#{goodsId},#{logisticsId},#{addTime},#{paymentTime},#{deliveryTime},#{receivingTime},#{delayTime},#{orderNumber}," +
            "#{ordersState},#{title},#{money},#{sellingPrice},#{postage},#{pinkageType},#{picture},#{distributioMode},#{extendFrequency},#{afficheType},#{ordersType},#{addressId},#{address_Name},#{address_Phone},#{address_province},#{address_city},#{address_district},#{address_postalcode},#{address})")
    @Options(useGeneratedKeys = true)
    int addOrders(UsedDealOrders usedDealOrders);

    /***
     * 新增物流
     * @param usedDealLogistics
     * @return
     */
    @Insert("insert into usedDealLogistics(myId,userId,brand,no,status,data,orders) " +
            "values (#{myId},#{userId},#{brand},#{no},#{status},#{data},#{orders})")
    @Options(useGeneratedKeys = true)
    int addLogistics(UsedDealLogistics usedDealLogistics);

    /***
     * 更新删除状态
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " ordersState=#{ordersState}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int delOrders(UsedDealOrders usedDealOrders);

    /***
     * 根据Id查询订单
     * @param id
     */
    @Select("select * from UsedDealOrders where id=#{id} and ordersState < 3 and ordersType = 0 or ordersType > 2")
    UsedDealOrders findDelOrId(@Param("id") long id);

    /***
     * 根据Id查询订单
     * @param id
     */
    @Select("select * from UsedDealOrders where id=#{id} and ordersState=0  and ordersType=1")
    UsedDealOrders findDeliverOrId(@Param("id") long id);

    /***
     * 根据Id查询订单
     * @param id
     */
    @Select("select * from UsedDealOrders where id=#{id} and ordersState=0 and ordersType=2 ")
    UsedDealOrders findReceiptOrId(@Param("id") long id);

    /***
     * 根据Id查询订单
     * @param id
     */
    @Select("select * from UsedDealOrders where id=#{id} and ordersState=0 and ordersType=0 ")
    UsedDealOrders findCancelOrId(@Param("id") long id);


    /***
     * 根据Id查询订单
     * @param id
     */
    @Select("select * from UsedDealOrders where id=#{id} and ordersState=0")
    UsedDealOrders findDetailsOrId(@Param("id") long id);

    /***
     * 根据Id查询物流
     * @param id
     */
    @Select("select * from UsedDealLogistics where id=#{id}")
    UsedDealLogistics findLogistics(@Param("id") long id);

    /***
     * 更新物流
     * @param usedDealLogistics
     * @return
     */
    @Update("<script>" +
            "update usedDealLogistics set" +
            " no=#{no}," +
            " brand=#{brand}," +
            " data=#{data}" +
            " where id=#{id}" +
            "</script>")
    int updateLogistics(UsedDealLogistics usedDealLogistics);

    /***
     * 更新发货状态
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " ordersType=#{ordersType}," +
            " deliveryTime=#{deliveryTime}," +
            " delayTime=#{delayTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDelivery(UsedDealOrders usedDealOrders);

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

    /***
     * 分页查询二手订单列表
     * @param identity  身份区分：1买家 2商家
     * @param ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价), 4用户取消订单  5卖家取消订单  6付款超时
     * @return
     */
    @Select("<script>" +
            "select * from UsedDealOrders" +
            " where 1=1" +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"ordersType >= 0 and ordersType &lt; 4\">" +
            " and ordersType = #{ordersType}" +
            "</if>" +
            "<if test=\"ordersType >=4\">" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            " and ordersState = 0" +
            " order by addTime desc" +
            "</script>")
    List<UsedDealOrders> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType, @Param("ids") String[] ids);

    /***
     * 延长收货时间
     * @param usedDealOrders
     * @return
     */
    @Update("<script>" +
            "update usedDealOrders set" +
            " delayTime=#{delayTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int timeExpand(UsedDealOrders usedDealOrders);

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
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Select("<script>" +
            "select count(id) from usedDealOrders" +
            " where 1=1 " +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"type != -1 \">" +
            " and ordersType=#{type} " +
            "</if>" +
            " and ordersState = 0" +
            "</script>")
    int findNum(@Param("identity") int identity, @Param("type") int type, @Param("userId") long userId);

    /***
     * 判断该用户快递个数是否达到上限  最多10条
     * @return
     */
    @Select("<script>" +
            "select count(id) from UsedDealExpress" +
            " where 1=1 " +
            " and userId = #{userId}" +
            " and expressSate = 0" +
            "</script>")
    int findExpressNum(@Param("userId") long userId);

    /***
     * 新增快递
     * @param usedDealExpress
     * @return
     */
    @Insert("insert into UsedDealExpress(expressSate,userId,addTime,postage,expressMode) " +
            "values (#{expressSate},#{userId},#{addTime},#{postage},#{expressMode})")
    @Options(useGeneratedKeys = true)
    int addExpress(UsedDealExpress usedDealExpress);

    /***
     * 根据Id查询快递
     * @param id
     */
    @Select("select * from UsedDealExpress where id=#{id} and expressSate = 0")
    UsedDealExpress findExpress(@Param("id") long id);

    /***
     * 更新快递
     * @param usedDealExpress
     * @return
     */
    @Update("<script>" +
            "update usedDealExpress set" +
            " postage=#{postage}," +
            " expressMode=#{expressMode}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateExpress(UsedDealExpress usedDealExpress);

    /***
     * 更新快递删除状态
     * @param usedDealExpress
     * @return
     */
    @Update("<script>" +
            "update usedDealExpress set" +
            " expressSate=#{expressSate}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int delExpress(UsedDealExpress usedDealExpress);

    /***
     * 分页查询快递列表
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from usedDealExpress" +
            " where 1=1" +
            " and userId = #{userId}" +
            " and expressSate = 0" +
            " order by addTime desc" +
            "</script>")
    List<UsedDealExpress> findExpressList(@Param("userId") long userId);

    /***
     * 根据Id查询物流
     * @param id
     */
    @Select("select * from UsedDealLogistics where id=#{id}")
    UsedDealLogistics logisticsDetails(@Param("id") long id);

}

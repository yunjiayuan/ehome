package com.busi.dao;

import com.busi.entity.HourlyWorkerEvaluate;
import com.busi.entity.HourlyWorkerFabulous;
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
     * 新增厨房订单
     * @param orders
     * @return
     */
    @Insert("insert into HourlyWorkerOrders(userId,ordersState,ordersType,addTime,money,myId,addressId,receivingTime,address_Name,address_Phone,address_province,address_city,address_district,address_postalcode,address,remarks," +
            "serviceTime,orderTime,no,shopId,workerTypeIds,coverMap,name)" +
            "values (#{userId},#{ordersState},#{ordersType},#{addTime},#{money},#{myId},#{addressId},#{receivingTime},#{address_Name},#{address_Phone},#{address_province},#{address_city},#{address_district},#{address_postalcode},#{address},#{remarks}" +
            ",#{serviceTime},#{orderTime},#{no},#{shopId},#{workerTypeIds},#{coverMap},#{name})")
    @Options(useGeneratedKeys = true)
    int addOrders(HourlyWorkerOrders orders);

    /***
     * 新增菜品点赞
     * @param fabulous
     * @return
     */
    @Insert("insert into HourlyWorkerFabulous(userId,time,myId,status,typeId) " +
            "values (#{userId},#{time},#{myId},#{status},#{typeId})")
    @Options(useGeneratedKeys = true)
    int addLike(HourlyWorkerFabulous fabulous);

    /***
     * 新增评价
     * @param evaluate
     * @return
     */
    @Insert("insert into HourlyWorkerEvaluate(userId,content,orderId,workerId,imgUrls,cover,time,state,score,anonymousType) " +
            "values (#{userId},#{content},#{orderId},#{workerId},#{imgUrls},#{cover},#{time},#{state},#{score},#{anonymousType})")
    @Options(useGeneratedKeys = true)
    int addEvaluate(HourlyWorkerEvaluate evaluate);

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
     *  updateCategory 更新类别  默认0删除状态  1由未接单改为已接单  2由服务中改为已完成  3取消订单  4更新订单状态为已评价  5更新支付状态
     * @param orders
     * @return
     */
    @Update("<script>" +
            "update HourlyWorkerOrders set" +
            "<if test=\"updateCategory == 0\">" +
            " ordersState =#{ordersState}" +
            "</if>" +
            "<if test=\"updateCategory == 1\">" +
            " orderTime =#{orderTime}," +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 2\">" +
            " ordersType =#{ordersType}," +
            " receivingTime =#{receivingTime}" +
            "</if>" +
            "<if test=\"updateCategory == 3\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 4\">" +
            " ordersType =#{ordersType}" +
            "</if>" +
            "<if test=\"updateCategory == 5\">" +
            " ordersType =#{ordersType}," +
            " paymentTime=#{paymentTime}" +
            "</if>" +
            " where id=#{id}" +
            "</script>")
    int updateOrders(HourlyWorkerOrders orders);

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  订单类型:   0已下单未付款  1未接单(已付款未接单)  ,2已接单未完成,  3已完成(已完成未评价)  4已评价 5用户取消订单 、 商家取消订单 、 接单超时 、 付款超时
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerOrders" +
            " where ordersState=0" +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"ordersType == 0 \">" +
            " and ordersType = 0" +
            "</if>" +
            "<if test=\"ordersType == 1 \">" +
            " and ordersType = 8" +
            "</if>" +
            "<if test=\"ordersType == 2 \">" +
            " and ordersType = 1" +
            "</if>" +
            "<if test=\"ordersType == 3 \">" +
            " and ordersType = 2" +
            "</if>" +
            "<if test=\"ordersType == 4 \">" +
            " and ordersType = 6" +
            "</if>" +
            "<if test=\"ordersType == 5\">" +
            " and ordersType in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<HourlyWorkerOrders> findOrderList(@Param("identity") int identity, @Param("userId") long userId, @Param("ordersType") int ordersType, @Param("ids") String[] ids);

    /***
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerOrders" +
            " where 1=1 " +
            "<if test=\"identity == 1 \">" +
            " and myId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 2 \">" +
            " and userId = #{userId}" +
            "</if>" +
            " and ordersState = 0" +
            "</script>")
    List<HourlyWorkerOrders> findIdentity(@Param("identity") int identity, @Param("userId") long userId);

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerOrders" +
            " where no = #{no}" +
            " and ordersState = 0" +
            "</script>")
    HourlyWorkerOrders findByNo(@Param("no") String no);
}

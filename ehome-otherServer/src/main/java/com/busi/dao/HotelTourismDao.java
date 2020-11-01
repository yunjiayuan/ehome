package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 订座设置
 * @author: ZHaoJiaJie
 * @create: 2020-08-13 21:06:35
 */
@Mapper
@Repository
public interface HotelTourismDao {

    /***
     * 新增厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Insert("insert into KitchenReserveBooked(userId,kitchenId,earliestTime,reserveDays,latestTime,servingTimeType,type)" +
            "values (#{userId},#{kitchenId},#{earliestTime},#{reserveDays},#{latestTime},#{servingTimeType},#{type})")
    @Options(useGeneratedKeys = true)
    int add(KitchenReserveBooked kitchenBooked);

    /***
     * 更新厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update KitchenReserveBooked set" +
            " servingTimeType = #{servingTimeType}," +
            " earliestTime=#{earliestTime}," +
            " latestTime=#{latestTime}," +
            " reserveDays=#{reserveDays}" +
            " where id=#{id}" +
            "</script>")
    int updateBooked(KitchenReserveBooked kitchenBooked);

    /***
     * 更新厨房订座剩余数量
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update KitchenReserveBooked set" +
            " roomsTotal=#{roomsTotal}," +
            " looseTableTotal=#{looseTableTotal}" +
            " where id=#{id}" +
            "</script>")
    int updatePosition(KitchenReserveBooked kitchenBooked);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from KitchenReserveBooked where userId=#{userId} and type=#{type}")
    KitchenReserveBooked findByUserId(@Param("userId") long userId, @Param("type") int type);

    /***
     * 新增包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Insert("insert into KitchenReserveRoom(userId,kitchenId,imgUrl,elegantName,leastNumber,mostNumber,bookedType,videoUrl,videoCoverUrl,type)" +
            "values (#{userId},#{kitchenId},#{imgUrl},#{elegantName},#{leastNumber},#{mostNumber},#{bookedType},#{videoUrl},#{videoCoverUrl},#{type})")
    @Options(useGeneratedKeys = true)
    int addPrivateRoom(KitchenReserveRoom kitchenPrivateRoom);

    /***
     * 更新包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Update("<script>" +
            "update KitchenReserveRoom set" +
            " imgUrl=#{imgUrl}," +
            " elegantName=#{elegantName}," +
            " leastNumber=#{leastNumber}," +
            " mostNumber=#{mostNumber}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}" +
            " where id=#{id}" +
            "</script>")
    int upPrivateRoom(KitchenReserveRoom kitchenPrivateRoom);

    /***
     * 删除厨房菜品分类
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from KitchenReserveRoom" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delPrivateRoom(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据Id查询包间or大厅
     * @param id
     * @return
     */
    @Select("select * from KitchenReserveRoom where id=#{id}")
    KitchenReserveRoom findPrivateRoom(@Param("id") long id);

    /***
     * 条件查询预定厨房（条件搜索）
     * @param userId 用户ID
     * @param bookedType 包间0  散桌1
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserveRoom" +
            " where userId = #{userId}" +
            " and bookedType = #{bookedType}" +
            " and type = #{type}" +
            "</script>")
    List<KitchenReserveRoom> findRoomList(@Param("type") int type, @Param("userId") long userId, @Param("bookedType") int bookedType);

    /***
     * 新增上菜时间
     * @param dishes
     * @return
     */
    @Insert("insert into KitchenReserveServingTime(userId,kitchenId,upperTime,type) " +
            "values (#{userId},#{kitchenId},#{upperTime},#{type})")
    @Options(useGeneratedKeys = true)
    int addUpperTime(KitchenReserveServingTime dishes);

    /***
     * 更新上菜时间
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update KitchenReserveServingTime set" +
            " upperTime=#{upperTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateUpperTime(KitchenReserveServingTime kitchenDishes);

    /***
     * 查询上菜时间
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserveServingTime" +
            " where kitchenId=#{kitchenId}" +
            " and type=#{type}" +
            "</script>")
    KitchenReserveServingTime findUpperTime(@Param("kitchenId") long kitchenId, @Param("type") int type);

    /***
     * 新增菜品分类
     * @param dishes
     * @return
     */
    @Insert("insert into KitchenDishesSort(userId,kitchenId,name,bookedState) " +
            "values (#{userId},#{kitchenId},#{name},#{bookedState})")
    @Options(useGeneratedKeys = true)
    int addSort(KitchenDishesSort dishes);

    /***
     * 查询审核列表
     * @param auditType  0待审核 1已审核
     * @return
     */
    @Select("<script>" +
            "select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from Hotel" +
            " where deleteType = 0 and licence != '' and claimStatus = 1" +
            " and auditType = #{auditType}" +
            " order by juli asc" +
            "</script>")
    List<Hotel> findAuditTypeList(@Param("auditType") int auditType, @Param("lat") double lat, @Param("lon") double lon);

    @Select("<script>" +
            "select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from ScenicSpot" +
            " where deleteType = 0 and licence != '' and claimStatus = 1" +
            " and auditType = #{auditType}" +
            " order by juli asc" +
            "</script>")
    List<ScenicSpot> findAuditTypeList2(@Param("auditType") int auditType, @Param("lat") double lat, @Param("lon") double lon);

    @Select("<script>" +
            "select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from Pharmacy" +
            " where deleteType = 0 and licence != '' and claimStatus = 1" +
            " and auditType = #{auditType}" +
            " order by juli asc" +
            "</script>")
    List<Pharmacy> findAuditTypeList3(@Param("auditType") int auditType, @Param("lat") double lat, @Param("lon") double lon);

    @Select("<script>" +
            "select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from KitchenReserve" +
            " where deleteType = 0 and healthyCard != '' and claimStatus = 1" +
            " and auditType = #{auditType}" +
            " order by juli asc" +
            "</script>")
    List<KitchenReserve> findAuditTypeList4(@Param("auditType") int auditType, @Param("lat") double lat, @Param("lon") double lon);

    @Select("<script>" +
            "select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from Kitchen" +
            " where deleteType = 0 and healthyCard != ''" +
            " and auditType = #{auditType}" +
            " order by juli asc" +
            "</script>")
    List<Kitchen> findAuditTypeList5(@Param("auditType") int auditType, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 根据主键ID查询并更新审核状态
     * @return
     */
    @Update("<script>" +
            "<if test=\"type == 0\">" +
            "update Hotel set" +
            "<if test=\"auditType == 1\">" +
            " claimStatus = 1," +
            "</if>" +
            " auditType = #{auditType}" +
            " where deleteType = 0 and id=#{id} and licence != '' " +
            "</if>" +
            "<if test=\"type == 1\">" +
            "update ScenicSpot set" +
            "<if test=\"auditType == 1\">" +
            " claimStatus = 1," +
            "</if>" +
            " auditType = #{auditType}" +
            " where deleteType = 0 and id=#{id} and licence != '' " +
            "</if>" +
            "<if test=\"type == 2\">" +
            "update Pharmacy set" +
            "<if test=\"auditType == 1\">" +
            " claimStatus = 1," +
            "</if>" +
            " auditType = #{auditType}" +
            " where deleteType = 0 and id=#{id} and licence != '' " +
            "</if>" +
            "<if test=\"type == 3\">" +
            "update KitchenReserve set" +
            "<if test=\"auditType == 1\">" +
            " claimStatus = 1," +
            "</if>" +
            " auditType = #{auditType}" +
            " where deleteType = 0 and id=#{id} and healthyCard != '' " +
            "</if>" +
            "<if test=\"type == 4\">" +
            "update Kitchen set" +
            " auditType = #{auditType}" +
            " where deleteType = 0 and id=#{id} and healthyCard != '' " +
            "</if>" +
            "</script>")
    int changeAuditType(@Param("type") int type, @Param("auditType") int auditType, @Param("id") long id);

    /***
     * 统计各类审核数量
     * @return
     */
    @Select("<script>" +
            "select * from Hotel" +
            " where deleteType = 0 and licence != ''" +
            "</script>")
    List<Hotel> countAuditType();

    /***
     * 统计各类审核数量
     * @return
     */
    @Select("<script>" +
            "select * from ScenicSpot" +
            " where deleteType = 0 and licence != ''" +
            "</script>")
    List<ScenicSpot> countAuditType1();

    /***
     * 统计各类审核数量
     * @return
     */
    @Select("<script>" +
            "select * from Pharmacy" +
            " where deleteType = 0 and licence != ''" +
            "</script>")
    List<Pharmacy> countAuditType2();

    /***
     * 统计各类审核数量
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserve" +
            " where deleteType = 0 and healthyCard != ''" +
            "</script>")
    List<KitchenReserve> countAuditType3();

    /***
     * 统计各类审核数量
     * @return
     */
    @Select("<script>" +
            "select * from Kitchen" +
            " where deleteType = 0 and healthyCard != ''" +
            "</script>")
    List<Kitchen> countAuditType4();
}

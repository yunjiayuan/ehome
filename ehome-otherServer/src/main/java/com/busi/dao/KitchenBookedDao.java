package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 厨房订座设置
 * @author: ZHaoJiaJie
 * @create: 2019-06-27 10:09
 */
@Mapper
@Repository
public interface KitchenBookedDao {

    /***
     * 新增厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Insert("insert into KitchenBooked(userId,kitchenId,earliestTime,reserveDays,latestTime,servingTimeType)" +
            "values (#{userId},#{kitchenId},#{earliestTime},#{reserveDays},#{latestTime},#{servingTimeType})")
    @Options(useGeneratedKeys = true)
    int add(KitchenBooked kitchenBooked);

    /***
     * 更新厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update KitchenBooked set" +
            "<if test=\"servingTimeType > 0\">" +
            " servingTimeType = 1," +
            "</if>" +
            " earliestTime=#{earliestTime}," +
            " latestTime=#{latestTime}," +
            " reserveDays=#{reserveDays}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBooked(KitchenBooked kitchenBooked);

    /***
     * 更新厨房订座剩余数量
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update KitchenBooked set" +
            " roomsTotal=#{roomsTotal}," +
            " looseTableTotal=#{looseTableTotal}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updatePosition(KitchenBooked kitchenBooked);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from KitchenBooked where userId=#{userId}")
    KitchenBooked findByUserId(@Param("userId") long userId);

    /***
     * 新增包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Insert("insert into KitchenPrivateRoom(userId,kitchenId,imgUrl,elegantName,leastNumber,mostNumber,bookedType,videoUrl,videoCoverUrl)" +
            "values (#{userId},#{kitchenId},#{imgUrl},#{elegantName},#{leastNumber},#{mostNumber},#{bookedType},#{videoUrl},#{videoCoverUrl})")
    @Options(useGeneratedKeys = true)
    int addPrivateRoom(KitchenPrivateRoom kitchenPrivateRoom);

    /***
     * 更新包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Update("<script>" +
            "update KitchenPrivateRoom set" +
            " imgUrl=#{imgUrl}," +
            " elegantName=#{elegantName}," +
            " leastNumber=#{leastNumber}," +
            " mostNumber=#{mostNumber}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int upPrivateRoom(KitchenPrivateRoom kitchenPrivateRoom);

    /***
     * 删除厨房菜品分类
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from KitchenPrivateRoom" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delPrivateRoom(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from KitchenPrivateRoom where userId=#{userId}")
    KitchenPrivateRoom findUserId(@Param("userId") long userId);

    /***
     * 新增预定厨房
     * @param kitchen
     * @return
     */
    @Insert("insert into KitchenReserve(userId,businessStatus,deleteType,auditType,cuisine,goodFood,kitchenName,startingTime,addTime,healthyCard,kitchenCover,content,totalSales,totalScore,lat,lon," +
            "address,videoUrl,videoCoverUrl)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{cuisine},#{goodFood},#{kitchenName},#{startingTime},#{addTime},#{healthyCard},#{kitchenCover},#{content},#{totalSales},#{totalScore},#{lat},#{lon}" +
            ",#{address},#{videoUrl},#{videoCoverUrl})")
    @Options(useGeneratedKeys = true)
    int addKitchen(KitchenReserve kitchen);

    /***
     * 更新预定厨房
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update KitchenReserve set" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " cuisine=#{cuisine}," +
            " content=#{content}," +
            " address=#{address}," +
            " goodFood=#{goodFood}," +
            " startingTime=#{startingTime}," +
            " kitchenName=#{kitchenName}," +
            " healthyCard=#{healthyCard}," +
            " kitchenCover=#{kitchenCover}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1" +
            "</script>")
    int updateKitchen(KitchenReserve kitchen);

    /***
     * 更新预定厨房删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update KitchenReserve set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1" +
            "</script>")
    int updateDel(KitchenReserve kitchen);

    /***
     * 更新预定厨房销量
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update KitchenReserve set" +
            " totalSales=#{totalSales}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1" +
            "</script>")
    int updateNumber(KitchenReserve kitchen);

    /***
     * 根据Id查询包间or大厅
     * @param id
     * @return
     */
    @Select("select * from KitchenPrivateRoom where id=#{id}")
    KitchenPrivateRoom findPrivateRoom(@Param("id") long id);

    /***
     * 根据userId查询预定
     * @param userId
     * @return
     */
    @Select("select * from KitchenReserve where userId=#{userId} and deleteType = 0 and auditType=1")
    KitchenReserve findReserve(@Param("userId") long userId);

    /***
     * 根据Id查询预定
     * @param id
     * @return
     */
    @Select("select * from KitchenReserve where id=#{id} and deleteType = 0 and auditType=1 ")
    KitchenReserve findById(@Param("id") long id);

    /***
     * 更新预定厨房营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update KitchenReserve set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1 " +
            "</script>")
    int updateBusiness(KitchenReserve kitchen);

    /***
     * 更新预定厨房总评分
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update KitchenReserve set" +
            " totalScore=#{totalScore}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1 " +
            "</script>")
    int updateScore(KitchenReserve kitchen);

    /***
     * 条件查询预定厨房(模糊搜索)
     * @param userId 用户ID
     * @param watchVideos 筛选视频：0否 1是
     * @param kitchenName  厨房名字
     * @param cuisine  菜系
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserve" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            "<if test=\"kitchenName != null and kitchenName != '' \">" +
            " and kitchenName LIKE CONCAT('%',#{kitchenName},'%')" +
            "</if>" +
            "<if test=\"cuisine != null and cuisine != '' \">" +
            " and cuisine LIKE CONCAT('%',#{cuisine},'%')" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "</script>")
    List<KitchenReserve> findKitchenList(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("kitchenName") String kitchenName, @Param("cuisine") String cuisine);

    /***
     * 条件查询预定厨房（距离最近）
     * @param userId 用户ID
     * @param watchVideos 筛选视频：0否 1是
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserve where" +
            " userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl is not null" +
            " and videoUrl != ''" +
            "</if>" +
            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
            " and lat &lt; #{lat}+1 and lon > #{lon}-1" +
            " and lon &lt; #{lon}+1 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            "</script>")
    List<KitchenReserve> findKitchenList2(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 条件查询预定厨房（条件搜索）
     * @param userId 用户ID
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType  排序类型：默认0综合排序  1距离最近  2销量最高  3评分最高
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserve" +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"sortType == 0\">" +
            " order by totalSales desc,totalScore desc" +
            "</if>" +
            "<if test=\"sortType == 2\">" +
            " order by totalSales desc" +
            "</if>" +
            "<if test=\"sortType == 3\">" +
            " order by totalScore desc" +
            "</if>" +
            "</script>")
    List<KitchenReserve> findKitchenList3(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("sortType") int sortType);

    /***
     * 条件查询预定厨房（条件搜索）
     * @param userId 用户ID
     * @param bookedType 包间0  散桌1
     * @return
     */
    @Select("<script>" +
            "select * from KitchenPrivateRoom" +
            " where userId = #{userId}" +
            " and bookedType = #{bookedType}" +
            "</script>")
    List<KitchenPrivateRoom> findRoomList(@Param("userId") long userId, @Param("bookedType") int bookedType);

    /***
     * 新增菜品
     * @param dishes
     * @return
     */
    @Insert("insert into KitchenReserveDishes(userId,kitchenId,dishame,cuisine,cost,ingredients,addTime,imgUrl,sortId) " +
            "values (#{userId},#{kitchenId},#{dishame},#{cuisine},#{cost},#{ingredients},#{addTime},#{imgUrl},#{sortId})")
    @Options(useGeneratedKeys = true)
    int addDishes(KitchenReserveDishes dishes);

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update KitchenReserveDishes set" +
            " cost=#{cost}," +
            " cuisine=#{cuisine}," +
            " dishame=#{dishame}," +
            " ingredients=#{ingredients}," +
            " imgUrl=#{imgUrl}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishes(KitchenReserveDishes kitchenDishes);

    /***
     * 删除厨房菜品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update KitchenReserveDishes set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delDishes(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据ID查询菜品
     * @param id
     * @return
     */
    @Select("select * from KitchenReserveDishes where id=#{id} and deleteType =0")
    KitchenReserveDishes disheSdetails(@Param("id") long id);

    /***
     * 查询厨房菜品列表
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserveDishes" +
            " where deleteType = 0" +
            " and kitchenId=#{kitchenId}" +
            " order by addTime desc" +
            "</script>")
    List<KitchenReserveDishes> findDishesList(@Param("kitchenId") long kitchenId);

    /***
     * 统计该用户上菜时间数量
     * @param kitchenId
     * @return
     */
    @Select("<script>" +
            "select count(id) from KitchenServingTime" +
            " where 1=1 " +
            " and kitchenId = #{kitchenId}" +
            "</script>")
    int findNum(@Param("kitchenId") long kitchenId);

    /***
     * 新增上菜时间
     * @param dishes
     * @return
     */
    @Insert("insert into KitchenServingTime(userId,kitchenId,upperTime) " +
            "values (#{userId},#{kitchenId},#{upperTime})")
    @Options(useGeneratedKeys = true)
    int addUpperTime(KitchenServingTime dishes);

    /***
     * 更新上菜时间
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update KitchenServingTime set" +
            " upperTime=#{upperTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateUpperTime(KitchenServingTime kitchenDishes);


    /***
     * 查询上菜时间列表
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from KitchenServingTime" +
            " where kitchenId=#{kitchenId}" +
            "</script>")
    List<KitchenServingTime> findUpperTimeList(@Param("kitchenId") long kitchenId);

    /***
     * 查询上菜时间
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from KitchenServingTime" +
            " where kitchenId=#{kitchenId}" +
            "</script>")
    KitchenServingTime findUpperTime(@Param("kitchenId") long kitchenId);

}

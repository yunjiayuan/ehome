package com.busi.dao;

import com.busi.entity.Kitchen;
import com.busi.entity.KitchenCollection;
import com.busi.entity.KitchenDishes;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 厨房相关Dao
 * author：zhaojiajie
 * create time：2019-3-4 16:48:51
 */
@Mapper
@Repository
public interface KitchenDao {

    /***
     * 新增厨房
     * @param kitchen
     * @return
     */
    @Insert("insert into kitchen(userId,businessStatus,deleteType,auditType,cuisine,goodFood,kitchenName,startingTime,addTime,healthyCard,kitchenCover,content,totalSales,totalScore,lat,lon," +
            "address,videoUrl,videoCoverUrl,bookedState)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{cuisine},#{goodFood},#{kitchenName},#{startingTime},#{addTime},#{healthyCard},#{kitchenCover},#{content},#{totalSales},#{totalScore},#{lat},#{lon}" +
            ",#{address},#{videoUrl},#{videoCoverUrl},#{bookedState})")
    @Options(useGeneratedKeys = true)
    int addKitchen(Kitchen kitchen);

    /***
     * 更新厨房
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update kitchen set" +
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
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateKitchen(Kitchen kitchen);

    /***
     * 更新厨房删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update kitchen set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(Kitchen kitchen);

    /***
     * 更新厨房删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update kitchen set" +
            " totalSales=#{totalSales}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateNumber(Kitchen kitchen);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from kitchen where userId=#{userId} and bookedState=#{bookedState}")
    Kitchen findByUserId(@Param("userId") long userId, @Param("bookedState") int bookedState);

    /***
     * 根据Id查询
     * @param id
     * @return
     */
    @Select("select * from kitchen where id=#{id}")
    Kitchen findById(@Param("id") long id);

    /***
     * 删除指定厨房下菜品
     * @param id
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update KitchenDishes set" +
            " deleteType=1" +
            " where kitchenId = #{id}" +
            " and userId=#{userId}" +
            "</script>")
    int deleteFood(@Param("userId") long userId, @Param("id") long id);

    /***
     * 更新厨房营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update kitchen set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(Kitchen kitchen);

    /***
     * 更新厨房订座状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update kitchen set" +
            " bookedState=1" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBookedState(Kitchen kitchen);

    /***
     * 更新厨房总评分
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update kitchen set" +
            " totalScore=#{totalScore}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateScore(Kitchen kitchen);

    /***
     * 条件查询厨房(模糊搜索)
     * @param userId 用户ID
     * @param watchVideos 筛选视频：0否 1是
     * @param watchBooked 筛选订座：0否 1是
     * @param kitchenName  厨房名字
     * @return
     */
    @Select("<script>" +
            "select * from kitchen" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            "<if test=\"watchBooked == 1\">" +
            " and bookedState = 1" +
            "</if>" +
            "<if test=\"kitchenName != null and kitchenName != '' \">" +
            " and kitchenName LIKE CONCAT('%',#{kitchenName},'%')" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "</script>")
    List<Kitchen> findKitchenList(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("kitchenName") String kitchenName, @Param("watchBooked") int watchBooked);

    /***
     * 条件查询厨房（距离最近）
     * @param userId 用户ID
    //     * @param lat 纬度
    //     * @param lon 经度
    //     * @param raidus 半径
     * @param watchVideos 筛选视频：0否 1是
     * @return
     */
//    @Select("<script>" +      //查询指定半径以内信息
//            "select id,userId,businessStatus,deleteType,auditType,cuisine,goodFood,kitchenName,startingTime,addTime,healthyCard,kitchenCover,videoUrl,videoCoverUrl,content,totalSales,totalScore,lat,lon,address," +
//            " ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - lon * PI() / 180) / 2),2) + COS(#{lat} * PI() / 180) * COS(lon * PI() / 180) * POW(SIN((#{lon} * PI() / 180 - lat * PI() / 180) / 2),2))) * #{raidus}) AS distance" +
//            " FROM Kitchen where userId != #{userId} and businessStatus=0 and deleteType = 0 and auditType=1" +
//            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl is not null" +
//            "</if>" +
//            " ORDER BY distance ASC" +
//            "</script>")
//    List<Kitchen> findKitchenList2(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("raidus") int raidus, @Param("lat") double lat, @Param("lon") double lon);
    @Select("<script>" +
            "select * from Kitchen where" +
            " userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchBooked == 1\">" +
            " and bookedState = 1" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl is not null" +
            " and videoUrl != ''" +
            "</if>" +
            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
            " and lat &lt; #{lat}+1 and lon > #{lon}-1" +
            " and lon &lt; #{lon}+1 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            "</script>")
    List<Kitchen> findKitchenList2(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("lat") double lat, @Param("lon") double lon, @Param("watchBooked") int watchBooked);

    /***
     * 条件查询厨房（条件搜索）
     * @param userId 用户ID
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType  排序类型：默认0综合排序  1距离最近  2销量最高  3评分最高
     * @return
     */
    @Select("<script>" +
            "select * from Kitchen" +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchBooked == 1\">" +
            " and bookedState = 1" +
            "</if>" +
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
    List<Kitchen> findKitchenList3(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("sortType") int sortType, @Param("watchBooked") int watchBooked);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from KitchenCollection where userId=#{userId} and kitchend=#{id}")
    KitchenCollection findWhether(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增收藏
     * @param collect
     * @return
     */
    @Insert("insert into KitchenCollection(userId,kitchend,kitchenName,goodFood,cuisine,kitchenCover,time,beUserId) " +
            "values (#{userId},#{kitchend},#{kitchenName},#{goodFood},#{cuisine},#{kitchenCover},#{time},#{beUserId})")
    @Options(useGeneratedKeys = true)
    int addCollect(KitchenCollection collect);

    /***
     * 查询厨房收藏列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from KitchenCollection" +
            " where 1=1" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<KitchenCollection> findCollectionList(@Param("userId") long userId);

    /***
     * 批量查询指定的厨房
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from Kitchen" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<Kitchen> findKitchenList4(@Param("ids") String[] ids);

    /***
     * 批量查询指定的厨房菜品
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from KitchenDishes" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<KitchenDishes> findDishesList2(@Param("ids") String[] ids);

    /***
     * 删除厨房收藏
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from KitchenCollection" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int del(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 新增菜品
     * @param dishes
     * @return
     */
    @Insert("insert into kitchenDishes(userId,kitchenId,dishame,cuisine,cost,ingredients,addTime,imgUrl) " +
            "values (#{userId},#{kitchenId},#{dishame},#{cuisine},#{cost},#{ingredients},#{addTime},#{imgUrl})")
    @Options(useGeneratedKeys = true)
    int addDishes(KitchenDishes dishes);

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update kitchenDishes set" +
            " cost=#{cost}," +
            " cuisine=#{cuisine}," +
            " dishame=#{dishame}," +
            " ingredients=#{ingredients}," +
            " imgUrl=#{imgUrl}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishes(KitchenDishes kitchenDishes);

    /***
     * 更新菜品点赞数
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update kitchenDishes set" +
            " pointNumber=#{pointNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateLike(KitchenDishes kitchenDishes);

    /***
     * 删除厨房菜品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update kitchenDishes set" +
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
    @Select("select * from kitchenDishes where id=#{id} and deleteType =0")
    KitchenDishes disheSdetails(@Param("id") long id);

    /***
     * 查询厨房菜品列表
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from KitchenDishes" +
            " where deleteType = 0" +
            " and kitchenId=#{kitchenId}" +
            " order by addTime desc" +
            "</script>")
    List<KitchenDishes> findDishesList(@Param("kitchenId") long kitchenId);
}


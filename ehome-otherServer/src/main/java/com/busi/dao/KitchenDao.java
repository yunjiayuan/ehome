package com.busi.dao;

import com.busi.entity.*;
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
            "address,videoUrl,videoCoverUrl,type)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{cuisine},#{goodFood},#{kitchenName},#{startingTime},#{addTime},#{healthyCard},#{kitchenCover},#{content},#{totalSales},#{totalScore},#{lat},#{lon}" +
            ",#{address},#{videoUrl},#{videoCoverUrl},#{type})")
    @Options(useGeneratedKeys = true)
    int addKitchen(Kitchen kitchen);

    /***
     * 更新厨房
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update kitchen set" +
            " type=#{type}," +
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
    @Select("select * from kitchen where userId=#{userId}")
    Kitchen findByUserId(@Param("userId") long userId);

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
     * @param kitchenName  厨房名字
     * @return
     */
    @Select("<script>" +
            "select * from kitchen" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            "<if test=\"kitchenName != null and kitchenName != '' \">" +
            " and kitchenName LIKE CONCAT('%',#{kitchenName},'%')" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"type > 0\">" +
            " and type = #{type}" +
            "</if>" +
            "</script>")
    List<Kitchen> findKitchenList(@Param("userId") long userId, @Param("type") int type, @Param("watchVideos") int watchVideos, @Param("kitchenName") String kitchenName);

    /***
     * 条件查询厨房（距离最近）
     * @param userId 用户ID
     * @param lat 纬度
     * @param lon 经度
     * @param watchVideos 筛选视频：0否 1是
     * @return
     */
    @Select("<script>" +
//            "select * from Kitchen where" +
//            " userId != #{userId}" +
//            " and businessStatus=0 and deleteType = 0 and auditType=1" +
//            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl != ''" +
//            "</if>" +
//            "<if test=\"type > 0\">" +
//            " and type = #{type}" +
//            "</if>" +
//            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
//            " and lat &lt; #{lat}+1 and lon > #{lon}-1" +
//            " and lon &lt; #{lon}+1 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
//            "</script>")
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from Kitchen " +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"type > 0\">" +
            " and type = #{type}" +
            "</if>" +
//            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算
//            " and lat &lt; #{lat}+1" +
//            " and lon > #{lon}-1" +
//            " and lon &lt; #{lon}+1" +
            " order by juli asc" +
            "</script>")
    List<Kitchen> findKitchenList2(@Param("userId") long userId, @Param("type") int type, @Param("watchVideos") int watchVideos, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 条件查询厨房（条件搜索）
     * @param userId 用户ID
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType  排序类型：默认0综合排序(推荐)  1距离最近  2销量最高  3评分最高
     * @param type        厨房类型： 0综合 1面点 2熟食 3豆制品 4桌菜
     * @return
     */
//    @Select("<script>" +
//            "select * from Kitchen" +
//            " where userId != #{userId}" +
//            " and businessStatus=0 and deleteType = 0 and auditType=1" +
//            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl != ''" +
//            "</if>" +
//            "<if test=\"type > 0\">" +
//            " and type = #{type}" +
//            "</if>" +
//            "<if test=\"sortType == 0\">" +
//            " order by totalSales desc,totalScore desc" +
//            "</if>" +
//            "<if test=\"sortType == 2\">" +
//            " order by totalSales desc" +
//            "</if>" +
//            "<if test=\"sortType == 3\">" +
//            " order by totalScore desc" +
//            "</if>" +
//            "</script>")
    @Select("<script>" +
            "<if test=\"sortType == 0\">" +
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from Kitchen " +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"type > 0\">" +
            " and type = #{type}" +
            "</if>" +
//            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算
//            " and lat &lt; #{lat}+1" +
//            " and lon > #{lon}-1" +
//            " and lon &lt; #{lon}+1" +
            " order by juli asc,totalScore desc" +
            "</if>" +
            "<if test=\"sortType == 2\">" +
            "select * from Kitchen" +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"type > 0\">" +
            " and type = #{type}" +
            "</if>" +
            " order by totalSales desc" +
            "</if>" +
            "<if test=\"sortType == 3\">" +
            "select * from Kitchen" +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"type > 0\">" +
            " and type = #{type}" +
            "</if>" +
            " order by totalScore desc" +
            "</if>" +
            "</script>")
    List<Kitchen> findKitchenList3(@Param("userId") long userId, @Param("type") int type, @Param("watchVideos") int watchVideos, @Param("sortType") int sortType, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from KitchenCollection where userId=#{userId} and beUserId=#{beUserId} and bookedState=#{bookedState}")
    KitchenCollection findWhether(@Param("userId") long userId, @Param("beUserId") long beUserId, @Param("bookedState") int bookedState);

    /***
     * 验证用户是否收藏过
     * @param kitchenId
     * @return
     */
    @Select("select * from KitchenCollection where kitchend=#{kitchenId} and userId=#{userId} and bookedState=#{bookedState}")
    KitchenCollection findWhether2(@Param("bookedState") int bookedState, @Param("userId") long userId, @Param("kitchenId") long kitchenId);

    /***
     * 新增收藏
     * @param collect
     * @return
     */
    @Insert("insert into KitchenCollection(userId,kitchend,kitchenName,goodFood,cuisine,kitchenCover,time,beUserId,bookedState) " +
            "values (#{userId},#{kitchend},#{kitchenName},#{goodFood},#{cuisine},#{kitchenCover},#{time},#{beUserId},#{bookedState})")
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
            " and bookedState=#{bookedState}" +
            " order by time desc" +
            "</script>")
    List<KitchenCollection> findCollectionList(@Param("userId") long userId, @Param("bookedState") int bookedState);

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
     * 批量查询指定的厨房
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from KitchenReserve" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<KitchenReserve> findKitchenList5(@Param("ids") String[] ids);

    /***
     * 查询指定的厨房评论
     * @param id
     * @return
     */
    @Select("<script>" +
            "select * from KitchenEvaluate" +
            " where id = #{id} and state =0 and bookedState=#{type}" +
            "</script>")
    List<KitchenEvaluate> findKitchenList6(@Param("id") long id, @Param("type") int type);

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
    @Insert("insert into kitchenDishes(userId,kitchenId,dishame,cuisine,cost,ingredients,addTime,imgUrl,sortId,bookedState) " +
            "values (#{userId},#{kitchenId},#{dishame},#{cuisine},#{cost},#{ingredients},#{addTime},#{imgUrl},#{sortId},#{bookedState})")
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
            " sortId=#{sortId}," +
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
            " and bookedState=#{bookedState}" +
            " order by addTime desc" +
            "</script>")
    List<KitchenDishes> findDishesList(@Param("bookedState") int bookedState, @Param("kitchenId") long kitchenId);

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
     * 更新菜品分类
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update KitchenDishesSort set" +
            " name=#{name}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishesSort(KitchenDishesSort kitchenDishes);

    /***
     * 删除厨房菜品分类
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from KitchenDishesSort" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delFoodSort(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据ID查询菜品
     * @param id
     * @return
     */
    @Select("select * from KitchenDishesSort where id=#{id}")
    KitchenDishesSort findDishesSort(@Param("id") long id);

    /***
     * 统计该用户分类数量
     * @param bookedState
     * @return
     */
    @Select("<script>" +
            "select count(id) from KitchenDishesSort" +
            " where 1=1 " +
            " and kitchenId = #{kitchenId}" +
            " and bookedState = #{bookedState}" +
            "</script>")
    int findNum(@Param("bookedState") int bookedState, @Param("kitchenId") long kitchenId);

    /***
     * 查询厨房菜品分类列表
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from KitchenDishesSort" +
            " where kitchenId=#{kitchenId}" +
            " and bookedState=#{bookedState}" +
            "</script>")
    List<KitchenDishesSort> findDishesSortList(@Param("bookedState") int bookedState, @Param("kitchenId") long kitchenId);
}


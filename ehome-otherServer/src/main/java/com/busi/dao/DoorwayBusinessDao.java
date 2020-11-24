package com.busi.dao;

import com.busi.entity.DoorwayBusiness;
import com.busi.entity.DoorwayBusinessCollection;
import com.busi.entity.DoorwayBusinessCommodity;
import com.busi.entity.ScenicSpot;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 家门口商品
 * @author: ZhaoJiaJie
 * @create: 2020-11-11 17:01:37
 */
@Mapper
@Repository
public interface DoorwayBusinessDao {

    /***
     * 新增商家
     * @param kitchen
     * @return
     */
    @Insert("insert into DoorwayBusiness(userId,businessStatus,deleteType,auditType,businessName,licence,addTime,picture,content,province,city,lat,lon," +
            "district,videoUrl,videoCoverUrl,type,phone,address,openTime,closeTime,openType,tips,free,freeCost)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{businessName},#{licence},#{addTime},#{picture},#{content},#{province},#{city},#{lat},#{lon}" +
            ",#{district},#{videoUrl},#{videoCoverUrl},#{type},#{phone},#{address},#{openTime},#{closeTime},#{openType},#{tips},#{free},#{freeCost})")
    @Options(useGeneratedKeys = true)
    int addKitchen(DoorwayBusiness kitchen);

    /***
     * 更新商家
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusiness set" +
            " openType=#{openType}," +
            " freeCost=#{freeCost}," +
            " free=#{free}," +
            " tips=#{tips}," +
            "<if test=\"openType == 1 \">" +
            " openTime=#{openTime}," +
            " closeTime=#{closeTime}," +
            "</if>" +
            "<if test=\"lat > 0 \">" +
            " lat=#{lat}," +
            "</if>" +
            "<if test=\"lon > 0 \">" +
            " lon=#{lon}," +
            "</if>" +
            "<if test=\"businessName != null and businessName != '' \">" +
            " businessName=#{businessName}," +
            "</if>" +
            "<if test=\"picture != null and picture != '' \">" +
            " picture=#{picture}," +
            "</if>" +
            "<if test=\"content != null and content != '' \">" +
            " content=#{content}," +
            "</if>" +
            "<if test=\"province >= 0 \">" +
            " province=#{province}," +
            "</if>" +
            "<if test=\"city >= 0 \">" +
            " city=#{city}," +
            "</if>" +
            "<if test=\"videoUrl != null and videoUrl != '' \">" +
            " videoUrl=#{videoUrl}," +
            "</if>" +
            "<if test=\"videoCoverUrl != null and videoCoverUrl != '' \">" +
            " videoCoverUrl=#{videoCoverUrl}," +
            "</if>" +
            "<if test=\"district >= 0 \">" +
            " district=#{district}," +
            "</if>" +
            "<if test=\"phone != null and phone != '' \">" +
            " phone=#{phone}," +
            "</if>" +
            "<if test=\"type >= 0 \">" +
            " type=#{type}," +
            "</if>" +
            "<if test=\"address != null and address != '' \">" +
            " address=#{address}," +
            "</if>" +
            " id=#{id}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen(DoorwayBusiness kitchen);

    @Update("<script>" +
            "update DoorwayBusiness set" +
            " licence=#{licence}," +
            " auditType=#{auditType}," +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen2(DoorwayBusiness kitchen);

    /***
     * 更新商家评分
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusiness set" +
            " totalScore=#{totalScore}," +
            " averageScore=#{averageScore}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateScore(DoorwayBusiness kitchen);

    /***
     * 更新商家删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusiness set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateDel(DoorwayBusiness kitchen);

    /***
     * 根据userId查询预定
     * @param userId
     * @return
     */
    @Select("select * from DoorwayBusiness where userId=#{userId} and deleteType = 0")
    DoorwayBusiness findReserve(@Param("userId") long userId);

    /***
     * 根据Id查询预定
     * @param id
     * @return
     */
    @Select("select * from DoorwayBusiness where id=#{id} and deleteType = 0 ")
    DoorwayBusiness findById(@Param("id") long id);

    /***
     * 更新商家营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusiness set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0  " +
            "</script>")
    int updateBusiness(DoorwayBusiness kitchen);

    /***
     * 条件查询商家
     * @param watchVideos 筛选视频：0否 1是
     * @param name    模糊搜索
     * @param province     省
     * @param city      市
     * @param district    区
     * @param lat      纬度
     * @param lon      经度
     * @return
     */
    @Select("<script>" +
            "<if test=\"name != null and name != '' \">" +
            "select * from DoorwayBusiness" +
            " where deleteType = 0 and businessStatus=0 and auditType=1" +
            " and userId != #{userId}" +
            "<if test=\"type >= 0\">" +
            " and type = #{type}" +
            "</if>" +
            " and scenicSpotName LIKE CONCAT('%',#{name},'%')" +
            "</if>" +
            "<if test=\"name == null or name == '' \">" +
            " select *" +
            "<if test=\"lat > 0 and lon > 0 \">" +
            ", ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            "</if>" +
            " from DoorwayBusiness " +
            " where userId != #{userId}" +
            " and deleteType = 0 and businessStatus=0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"type >= 0\">" +
            " and type = #{type}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"lat > 0 and lon > 0 \">" +
            " order by juli asc" +
            "</if>" +
            "<if test=\"0 >= lat or 0 >= lon \">" +
            " order by addTime desc,totalScore desc" +
            "</if>" +
            "</if>" +
            "</script>")
    List<DoorwayBusiness> findKitchenList(@Param("type") int type, @Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("name") String name, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 新增商品
     * @param dishes
     * @return
     */
    @Insert("insert into DoorwayBusinessCommodity(userId,businessId,cost,name,addTime,describes,picture,stock,company) " +
            "values (#{userId},#{businessId},#{cost},#{name},#{addTime},#{describes},#{picture},#{stock},#{company})")
    @Options(useGeneratedKeys = true)
    int addDishes(DoorwayBusinessCommodity dishes);

    /***
     * 更新商品
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusinessCommodity set" +
            " cost=#{cost}," +
            " stock=#{stock}," +
            " company=#{company}," +
            " picture=#{picture}," +
            " describes=#{describes}," +
            " name=#{name}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishes(DoorwayBusinessCommodity kitchenDishes);

    /***
     * 删除商家商品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusinessCommodity set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delDishes(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据ID查询商品
     * @param id
     * @return
     */
    @Select("select * from DoorwayBusinessCommodity where id=#{id} and deleteType =0")
    DoorwayBusinessCommodity disheSdetails(@Param("id") long id);

    /***
     * 查询商品列表
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from DoorwayBusinessCommodity" +
            " where deleteType = 0" +
            " and businessId=#{kitchenId}" +
            " order by cost asc" +
            "</script>")
    List<DoorwayBusinessCommodity> findDishesList(@Param("kitchenId") long kitchenId);

    /***
     * 删除指定商家下商品
     * @param id
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update DoorwayBusinessCommodity set" +
            " deleteType=1" +
            " where businessId = #{id}" +
            " and userId=#{userId}" +
            "</script>")
    int delDoorwayBusiness(@Param("userId") long userId, @Param("id") long id);

    /***
     * 批量查询指定的商品
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from DoorwayBusinessCommodity" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<DoorwayBusinessCommodity> findDishesList2(@Param("ids") String[] ids);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from DoorwayBusinessCollection where myId=#{userId} and userId=#{id}")
    DoorwayBusinessCollection findWhether(@Param("userId") long userId, @Param("id") long id);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from DoorwayBusinessCollection where myId=#{userId} and businessId=#{id}")
    DoorwayBusinessCollection findWhether2(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增收藏
     * @param DoorwayBusinessCollection
     * @return
     */
    @Insert("insert into DoorwayBusinessCollection(myId,userId,name,picture,time,type,businessId) " +
            "values (#{myId},#{userId},#{name},#{picture},#{time},#{type},#{businessId})")
    @Options(useGeneratedKeys = true)
    int addCollect(DoorwayBusinessCollection DoorwayBusinessCollection);

    /***
     * 查询商家收藏列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from DoorwayBusinessCollection" +
            " where 1=1" +
            " and myId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<DoorwayBusinessCollection> findCollectionList(@Param("userId") long userId);

    /***
     * 删除商家收藏
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from DoorwayBusinessCollection" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and myId=#{userId}" +
            "</script>")
    int del(@Param("ids") String[] ids, @Param("userId") long userId);

    @Update("<script>" +
            "update DoorwayBusiness set" +
            " cost=#{cost}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen3(DoorwayBusiness kitchen);
}

package com.busi.dao;

import com.busi.entity.Hotel;
import com.busi.entity.HotelCollection;
import com.busi.entity.HotelRoom;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 酒店民宿
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 15:55:51
 */
@Mapper
@Repository
public interface HotelDao {

    /***
     * 新增酒店民宿
     * @param kitchen
     * @return
     */
    @Insert("insert into Hotel(userId,businessStatus,deleteType,auditType,hotelName,openTime,closeTime,licence,addTime,picture,tips,content,province,city,lat,lon," +
            "district,videoUrl,videoCoverUrl,type,phone,levels,hotelType,openType)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{hotelName},#{openTime},#{closeTime},#{licence},#{addTime},#{picture},#{tips},#{content},#{province},#{city},#{lat},#{lon}" +
            ",#{district},#{videoUrl},#{videoCoverUrl},#{type},#{phone},#{levels},#{hotelType},#{openType})")
    @Options(useGeneratedKeys = true)
    int addKitchen(Hotel kitchen);

    /***
     * 更新酒店民宿
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Hotel set" +
            "<if test=\"licence != null and licence != '' \">" +
            " licence=#{licence}," +
            " auditType=1," +
            "</if>" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " hotelName=#{hotelName}," +
            " hotelType=#{hotelType}," +
            " openType=#{openType}," +
            "<if test=\"openType == 1 \">" +
            " openTime=#{openTime}," +
            " closeTime=#{closeTime}," +
            "</if>" +
            " picture=#{picture}," +
            " tips=#{tips}," +
            " content=#{content}," +
            " province=#{province}," +
            " city=#{city}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " district=#{district}," +
            " phone=#{phone}," +
            " levels=#{levels}," +
            " type=#{type}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen(Hotel kitchen);

    @Update("<script>" +
            "update Hotel set" +
            " licence=#{licence}," +
            " auditType=#{auditType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen2(Hotel kitchen);

    @Update("<script>" +
            "update Hotel set" +
            " cost=#{cost}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen3(Hotel kitchen);

    @Update("<script>" +
            "update Hotel set" +
            "<if test=\"relationReservation > 0\">" +
            " relationReservation=#{relationReservation}" +
            "</if>" +
            " where userId=#{userId} and deleteType = 0" +
            "</script>")
    int update(Hotel kitchen);

    /***
     * 更新酒店民宿删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Hotel set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateDel(Hotel kitchen);

    /***
     * 根据userId查询预定
     * @param userId
     * @return
     */
    @Select("select * from Hotel where userId=#{userId} and deleteType = 0")
    Hotel findReserve(@Param("userId") long userId);

    /***
     * 根据Id查询预定
     * @param id
     * @return
     */
    @Select("select * from Hotel where id=#{id} and deleteType = 0 and type=#{type}")
    Hotel findById(@Param("id") long id, @Param("type") int type);

    /***
     * 更新酒店民宿营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Hotel set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0  " +
            "</script>")
    int updateBusiness(Hotel kitchen);

    /***
     * 条件查询酒店民宿
     * @param watchVideos 筛选视频：0否 1是
     * @param hotelType 筛选：-1全部 0酒店 1民宿
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
            "select * from Hotel" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            " and hotelName LIKE CONCAT('%',#{name},'%')" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"hotelType >= 0\">" +
            " and hotelType = #{hotelType}" +
            "</if>" +
            "</if>" +
            "<if test=\"name == null or name == '' \">" +
            " select *" +
            "<if test=\"lat > 0 and lon > 0 \">" +
            ", ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            "</if>" +
            " from Hotel " +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"hotelType >= 0\">" +
            " and hotelType = #{hotelType}" +
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
    List<Hotel> findKitchenList(@Param("userId") long userId, @Param("hotelType") int hotelType, @Param("watchVideos") int watchVideos, @Param("name") String name, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 新增房间
     * @param dishes
     * @return
     */
    @Insert("insert into HotelRoom(userId,hotelId,cost,name,addTime,describes,squareMetre,picture,type) " +
            "values (#{userId},#{hotelId},#{cost},#{name},#{addTime},#{describes},#{squareMetre},#{picture},#{type})")
    @Options(useGeneratedKeys = true)
    int addDishes(HotelRoom dishes);

    /***
     * 更新房间
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update HotelRoom set" +
            " cost=#{cost}," +
            " picture=#{picture}," +
            " describes=#{describes}," +
            " squareMetre=#{squareMetre}," +
            " name=#{name}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishes(HotelRoom kitchenDishes);

    /***
     * 删除酒店民宿房间
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update HotelRoom set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delDishes(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据ID查询房间
     * @param id
     * @return
     */
    @Select("select * from HotelRoom where id=#{id} and deleteType =0")
    HotelRoom disheSdetails(@Param("id") long id);

    /***
     * 查询房间列表
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from HotelRoom" +
            " where deleteType = 0" +
            " and hotelId=#{kitchenId}" +
            " order by addTime asc" +
            "</script>")
    List<HotelRoom> findDishesList(@Param("kitchenId") long kitchenId);

    /***
     * 删除指定酒店民宿下房间
     * @param id
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update HotelRoom set" +
            " deleteType=1" +
            " where hotelId = #{id}" +
            " and userId=#{userId}" +
            "</script>")
    int delHotel(@Param("userId") long userId, @Param("id") long id);

    /***
     * 批量查询指定的房间
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from HotelRoom" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<HotelRoom> findDishesList2(@Param("ids") String[] ids);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from HotelCollection where myId=#{userId} and userId=#{id}")
    HotelCollection findWhether(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增收藏
     * @param HotelCollection
     * @return
     */
    @Insert("insert into HotelCollection(myId,userId,name,picture,time) " +
            "values (#{myId},#{userId},#{name},#{picture},#{time})")
    @Options(useGeneratedKeys = true)
    int addCollect(HotelCollection HotelCollection);

    /***
     * 查询酒店民宿收藏列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from HotelCollection" +
            " where 1=1" +
            " and myId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<HotelCollection> findCollectionList(@Param("userId") long userId);

    /***
     * 删除酒店民宿收藏
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from HotelCollection" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and myId=#{userId}" +
            "</script>")
    int del(@Param("ids") String[] ids, @Param("userId") long userId);
}

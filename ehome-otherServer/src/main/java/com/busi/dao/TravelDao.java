package com.busi.dao;

import com.busi.entity.ScenicSpot;
import com.busi.entity.ScenicSpotTickets;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 景区
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 14:46:53
 */
@Mapper
@Repository
public interface TravelDao {

    /***
     * 新增景区
     * @param kitchen
     * @return
     */
    @Insert("insert into ScenicSpot(userId,businessStatus,deleteType,auditType,scenicSpotName,openTime,closeTime,licence,addTime,picture,tips,content,province,city,lat,lon," +
            "district,videoUrl,videoCoverUrl,type,phone,claimTime)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{scenicSpotName},#{openTime},#{closeTime},#{licence},#{addTime},#{picture},#{tips},#{content},#{province},#{city},#{lat},#{lon}" +
            ",#{district},#{videoUrl},#{videoCoverUrl},#{type},#{phone})")
    @Options(useGeneratedKeys = true)
    int addKitchen(ScenicSpot kitchen);

    /***
     * 更新景区
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " scenicSpotName=#{scenicSpotName}," +
            " openTime=#{openTime}," +
            " closeTime=#{closeTime}," +
            " picture=#{picture}," +
            " tips=#{tips}," +
            " content=#{content}," +
            " province=#{province}," +
            " city=#{city}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " district=#{district}," +
            " phone=#{phone}," +
            " type=#{type}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1" +
            "</script>")
    int updateKitchen(ScenicSpot kitchen);

    @Update("<script>" +
            "update ScenicSpot set" +
            " licence=#{licence}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1" +
            "</script>")
    int updateKitchen2(ScenicSpot kitchen);

    /***
     * 更新景区删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1" +
            "</script>")
    int updateDel(ScenicSpot kitchen);

    /***
     * 根据userId查询预定
     * @param userId
     * @return
     */
    @Select("select * from ScenicSpot where userId=#{userId} and deleteType = 0 and auditType=1")
    ScenicSpot findReserve(@Param("userId") long userId);

    /***
     * 根据Id查询预定
     * @param id
     * @return
     */
    @Select("select * from ScenicSpot where id=#{id} and deleteType = 0 and auditType=1 ")
    ScenicSpot findById(@Param("id") long id);

    /***
     * 更新景区营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0 and auditType=1 " +
            "</script>")
    int updateBusiness(ScenicSpot kitchen);

    /***
     * 条件查询景区
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
            "select * from ScenicSpot" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            " and scenicSpotName LIKE CONCAT('%',#{name},'%')" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "</if>" +
            "<if test=\"name == null or name == '' \">" +
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from ScenicSpot " +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
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
            " order by juli asc" +
            "</if>" +
            "</script>")
    List<ScenicSpot> findKitchenList(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("name") String name, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 新增门票
     * @param dishes
     * @return
     */
    @Insert("insert into ScenicSpotTickets(userId,scenicSpotId,cost,name,addTime) " +
            "values (#{userId},#{scenicSpotId},#{cost},#{name},#{addTime})")
    @Options(useGeneratedKeys = true)
    int addDishes(ScenicSpotTickets dishes);

    /***
     * 更新门票
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update ScenicSpotTickets set" +
            " cost=#{cost}," +
            " name=#{name}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishes(ScenicSpotTickets kitchenDishes);

    /***
     * 删除景区门票
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update ScenicSpotTickets set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delDishes(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据ID查询门票
     * @param id
     * @return
     */
    @Select("select * from ScenicSpotTickets where id=#{id} and deleteType =0")
    ScenicSpotTickets disheSdetails(@Param("id") long id);

    /***
     * 查询门票列表
     * @param kitchenId  厨房ID
     * @return
     */
    @Select("<script>" +
            "select * from ScenicSpotTickets" +
            " where deleteType = 0" +
            " and scenicSpotId=#{kitchenId}" +
            " order by addTime desc" +
            "</script>")
    List<ScenicSpotTickets> findDishesList(@Param("kitchenId") long kitchenId);

    /***
     * 删除指定景区下门票
     * @param id
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update ScenicSpotTickets set" +
            " deleteType=1" +
            " where scenicSpotId = #{id}" +
            " and userId=#{userId}" +
            "</script>")
    int delScenicSpot(@Param("userId") long userId, @Param("id") long id);
}

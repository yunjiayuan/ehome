package com.busi.dao;

import com.busi.entity.Pharmacy;
import com.busi.entity.PharmacyCollection;
import com.busi.entity.PharmacyDrugs;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 药店
 * @author: ZhaoJiaJie
 * @create: 2020-08-10 15:23:15
 */
@Mapper
@Repository
public interface PharmacyDao {

    /***
     * 新增药店
     * @param kitchen
     * @return
     */
    @Insert("insert into Pharmacy(userId,businessStatus,deleteType,auditType,pharmacyName,openTime,closeTime,licence,addTime,picture,tips,content,province,city,lat,lon," +
            "district,videoUrl,videoCoverUrl,type,phone,levels,distributionMode,openType,initialCost)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{pharmacyName},#{openTime},#{closeTime},#{licence},#{addTime},#{picture},#{tips},#{content},#{province},#{city},#{lat},#{lon}" +
            ",#{district},#{videoUrl},#{videoCoverUrl},#{type},#{phone},#{levels},#{distributionMode},#{openType},#{initialCost})")
    @Options(useGeneratedKeys = true)
    int addKitchen(Pharmacy kitchen);

    /***
     * 更新药店
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Pharmacy set" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " pharmacyName=#{pharmacyName}," +
            " initialCost=#{initialCost}," +
            " distributionMode=#{distributionMode}," +
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
    int updateKitchen(Pharmacy kitchen);

    @Update("<script>" +
            "update Pharmacy set" +
            " licence=#{licence}," +
            " auditType=#{auditType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen2(Pharmacy kitchen);

    @Update("<script>" +
            "update Pharmacy set" +
            " cost=#{cost}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen3(Pharmacy kitchen);

    /***
     * 更新药店删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Pharmacy set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateDel(Pharmacy kitchen);

    /***
     * 根据userId查询预定
     * @param userId
     * @return
     */
    @Select("select * from Pharmacy where userId=#{userId} and deleteType = 0")
    Pharmacy findReserve(@Param("userId") long userId);

    /***
     * 根据Id查询预定
     * @param id
     * @return
     */
    @Select("select * from Pharmacy where id=#{id} and deleteType = 0 ")
    Pharmacy findById(@Param("id") long id);

    /***
     * 更新药店营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Pharmacy set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0  " +
            "</script>")
    int updateBusiness(Pharmacy kitchen);

    /***
     * 条件查询药店
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
            "select * from Pharmacy" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            " and pharmacyName LIKE CONCAT('%',#{name},'%')" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "</if>" +
            "<if test=\"name == null or name == '' \">" +
            " select *" +
            "<if test=\"lat > 0 and lon > 0 \">" +
            ", ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            "</if>" +
            " from Pharmacy " +
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
            "<if test=\"lat > 0 and lon > 0 \">" +
            " order by juli asc" +
            "</if>" +
            "<if test=\"0 >= lat or 0 >= lon \">" +
            " order by addTime desc,totalScore desc" +
            "</if>" +
            "</if>" +
            "</script>")
    List<Pharmacy> findKitchenList(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("name") String name, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 新增药品
     * @param dishes
     * @return
     */
    @Insert("insert into PharmacyDrugs(userId,pharmacyId,cost,name,addTime,describes,prescriptionType,picture,natureType,specifications,company,numbers) " +
            "values (#{userId},#{pharmacyId},#{cost},#{name},#{addTime},#{describes},#{prescriptionType},#{picture},#{natureType},#{specifications},#{company},#{numbers})")
    @Options(useGeneratedKeys = true)
    int addDishes(PharmacyDrugs dishes);

    /***
     * 更新药品
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update PharmacyDrugs set" +
            " cost=#{cost}," +
            " picture=#{picture}," +
            " describes=#{describes}," +
            " prescriptionType=#{prescriptionType}," +
            " natureType=#{natureType}," +
            " specifications=#{specifications}," +
            " company=#{company}," +
            " numbers=#{numbers}," +
            " name=#{name}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishes(PharmacyDrugs kitchenDishes);

    /***
     * 删除药店药品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update PharmacyDrugs set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delDishes(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据ID查询药品
     * @param id
     * @return
     */
    @Select("select * from PharmacyDrugs where id=#{id} and deleteType =0")
    PharmacyDrugs disheSdetails(@Param("id") long id);

    /***
     * 查询药品列表
     * @param kitchenId  药店ID
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyDrugs" +
            " where deleteType = 0" +
            " and pharmacyId=#{kitchenId}" +
            "<if test=\"natureType >= 0\">" +
            " and natureType=#{natureType}" +
            "</if>" +
            " order by cost asc" +
            "</script>")
    List<PharmacyDrugs> findDishesList(@Param("kitchenId") long kitchenId, @Param("natureType") int natureType);

    /***
     * 删除指定药店下药品
     * @param id
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update PharmacyDrugs set" +
            " deleteType=1" +
            " where pharmacyId = #{id}" +
            " and userId=#{userId}" +
            "</script>")
    int delPharmacy(@Param("userId") long userId, @Param("id") long id);

    /***
     * 批量查询指定的药品
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyDrugs" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<PharmacyDrugs> findDishesList2(@Param("ids") String[] ids);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from PharmacyCollection where myId=#{userId} and userId=#{id}")
    PharmacyCollection findWhether(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增收藏
     * @param PharmacyCollection
     * @return
     */
    @Insert("insert into PharmacyCollection(myId,userId,name,picture,time) " +
            "values (#{myId},#{userId},#{name},#{picture},#{time})")
    @Options(useGeneratedKeys = true)
    int addCollect(PharmacyCollection PharmacyCollection);

    /***
     * 查询药店收藏列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from PharmacyCollection" +
            " where 1=1" +
            " and myId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<PharmacyCollection> findCollectionList(@Param("userId") long userId);

    /***
     * 删除药店收藏
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from PharmacyCollection" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and myId=#{userId}" +
            "</script>")
    int del(@Param("ids") String[] ids, @Param("userId") long userId);
}

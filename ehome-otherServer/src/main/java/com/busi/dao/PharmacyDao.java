package com.busi.dao;

import com.busi.entity.*;
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
            "district,videoUrl,videoCoverUrl,type,phone,levels,distributionMode,openType,initialCost,address,claimId,claimStatus,claimTime,totalScore)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{pharmacyName},#{openTime},#{closeTime},#{licence},#{addTime},#{picture},#{tips},#{content},#{province},#{city},#{lat},#{lon}" +
            ",#{district},#{videoUrl},#{videoCoverUrl},#{type},#{phone},#{levels},#{distributionMode},#{openType},#{initialCost},#{address},#{claimId},#{claimStatus},#{claimTime},#{totalScore})")
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
            " address=#{address}," +
            " type=#{type}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen(Pharmacy kitchen);

    @Update("<script>" +
            "update Pharmacy set" +
            " licence=#{licence}," +
            " auditType=#{auditType}," +
            " businessStatus=#{businessStatus}" +
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
            " where deleteType = 0 and (businessStatus=0 and auditType=1 OR (claimId != '' and claimStatus = 0))" +
            " and userId != #{userId}" +
            " and pharmacyName LIKE CONCAT('%',#{name},'%')" +
//            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl != ''" +
//            "</if>" +
            "</if>" +
            "<if test=\"name == null or name == '' \">" +
            " select *" +
            "<if test=\"lat > 0 and lon > 0 \">" +
            ", ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            "</if>" +
            " from Pharmacy " +
            " where userId != #{userId}" +
            " and deleteType = 0 and (businessStatus=0 and auditType=1 OR (claimId != '' and claimStatus = 0))" +
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
            " order by addTime asc" +
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
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from PharmacyCollection where myId=#{userId} and pharmacyId=#{id}")
    PharmacyCollection findWhether2(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增收藏
     * @param PharmacyCollection
     * @return
     */
    @Insert("insert into PharmacyCollection(myId,userId,name,picture,time,type,levels,pharmacyId,distributionMode,openType,openTime,closeTime) " +
            "values (#{myId},#{userId},#{name},#{picture},#{time},#{type},#{levels},#{pharmacyId},#{distributionMode},#{openType},#{openTime},#{closeTime})")
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

    /***
     * 根据Id查询药店
     * @param id
     * @return
     */
    @Select("select * from PharmacyData where id=#{id}")
    PharmacyData findReserveData(@Param("id") long id);

    /***
     * 条件查询药店
     * @return
     */
    @Select("<script>" +
            "<if test=\"kitchenName != null and kitchenName != '' \">" +
            "select * from PharmacyData where claimStatus=0 " +
            " and name LIKE CONCAT('%',#{kitchenName},'%')" +
            "</if>" +
            "<if test=\"kitchenName == null and latitude > 0 \">" +
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{latitude}*PI()/180-latitude*PI()/180)/2),2)+COS(#{latitude}*PI()/180)*COS(latitude*PI()/180)*POW(SIN((#{longitude}*PI()/180-longitude*PI()/180)/2),2)))*1000) AS juli " +
            " from PharmacyData " +
            " where claimStatus=0" +
            " and latitude > #{latitude}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算
            " and latitude &lt; #{latitude}+1" +
            " and longitude > #{longitude}-1" +
            " and longitude &lt; #{longitude}+1" +
            " order by juli asc,overallRating desc" +
            "</if>" +
            "</script>")
    List<PharmacyData> findReserveDataList(@Param("kitchenName") String kitchenName, @Param("latitude") double latitude, @Param("longitude") double longitude);

    /***
     * 更新药店认领状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update PharmacyData set" +
            " userId=#{userId}," +
            " claimTime=#{claimTime}," +
            " claimStatus=#{claimStatus}" +
            " where id=#{id}" +
            "</script>")
    int claimKitchen(PharmacyData kitchen);

    /***
     * 更新药店认领状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update Pharmacy set" +
            " userId=#{userId}," +
            " invitationCode=#{invitationCode}," +
            " licence=#{licence}," +
            " phone=#{phone}," +
            " claimTime=#{claimTime}," +
            " businessStatus=#{businessStatus}," +
            " claimStatus=#{claimStatus}" +
            " where claimId=#{claimId}" +
            "</script>")
    int claimKitchen2(Pharmacy kitchen);

    /***
     * 根据uid查询药店
     * @param uid
     * @return
     */
    @Select("select * from PharmacyData where uid=#{uid}")
    PharmacyData findReserveDataId(@Param("uid") String uid);

    /***
     * 新增药店数据
     * @param kitchen
     * @return
     */
    @Insert("insert into PharmacyData(userId,uid,streetID,name,addTime,province,city,area,latitude,longitude," +
            "address,distance,claimStatus,claimTime,type,phone,tag,detailURL,price,openingHours,overallRating,tasteRating," +
            "serviceRating,environmentRating,hygieneRating,technologyRating,facilityRating,imageNumber,grouponNumber,discountNumber,commentNumber,favoriteNumber,checkInNumber)" +
            "values (#{userId},#{uid},#{streetID},#{name},#{addTime},#{province},#{city},#{area},#{latitude},#{longitude}" +
            ",#{address},#{distance},#{claimStatus},#{claimTime},#{type},#{phone},#{tag},#{detailURL},#{price},#{openingHours},#{overallRating},#{tasteRating},#{serviceRating},#{environmentRating},#{hygieneRating}" +
            ",#{technologyRating},#{facilityRating},#{imageNumber},#{grouponNumber},#{discountNumber},#{commentNumber},#{favoriteNumber},#{checkInNumber})")
    @Options(useGeneratedKeys = true)
    int addReserveData(PharmacyData kitchen);

    /***
     * 更新药店数据
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update PharmacyData set" +
            " province=#{province}," +
            " city=#{city}," +
            " area=#{area}," +
            " name=#{name}," +
            " latitude=#{latitude}," +
            " longitude=#{longitude}," +
            " address=#{address}," +
            " phone=#{phone}," +
            " distance=#{distance}," +
            " type=#{type}," +
            " tag=#{tag}," +
            " detailURL=#{detailURL}," +
            " price=#{price}," +
            " openingHours=#{openingHours}," +
            " imageNumber=#{imageNumber}," +
            " grouponNumber=#{grouponNumber}," +
            " discountNumber=#{discountNumber}," +
            " overallRating=#{overallRating}," +
            " commentNumber=#{commentNumber}," +
            " tasteRating=#{tasteRating}," +
            " serviceRating=#{serviceRating}," +
            " environmentRating=#{environmentRating}," +
            " hygieneRating=#{hygieneRating}," +
            " technologyRating=#{technologyRating}," +
            " facilityRating=#{facilityRating}," +
            " favoriteNumber=#{favoriteNumber}," +
            " checkInNumber=#{checkInNumber}," +
            " userId=#{userId}" +
            " where id=#{id}" +
            "</script>")
    int updateReserveData(PharmacyData kitchen);

}

package com.busi.dao;

import com.busi.entity.*;
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
            "district,videoUrl,videoCoverUrl,type,phone,levels,free,address,claimId,claimStatus,claimTime,totalScore)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{scenicSpotName},#{openTime},#{closeTime},#{licence},#{addTime},#{picture},#{tips},#{content},#{province},#{city},#{lat},#{lon}" +
            ",#{district},#{videoUrl},#{videoCoverUrl},#{type},#{phone},#{levels},#{free},#{address},#{claimId},#{claimStatus},#{claimTime},#{totalScore})")
    @Options(useGeneratedKeys = true)
    int addKitchen(ScenicSpot kitchen);

    /***
     * 更新景区
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            "<if test=\"lat > 0 \">" +
            " lat=#{lat}," +
            "</if>" +
            "<if test=\"lon > 0 \">" +
            " lon=#{lon}," +
            "</if>" +
            "<if test=\"free > 0 \">" +
            " free=#{free}," +
            "</if>" +
            "<if test=\"scenicSpotName != null and scenicSpotName != '' \">" +
            " scenicSpotName=#{scenicSpotName}," +
            "</if>" +
            "<if test=\"openTime != null and openTime != '' \">" +
            " openTime=#{openTime}," +
            "</if>" +
            "<if test=\"closeTime != null and closeTime != '' \">" +
            " closeTime=#{closeTime}," +
            "</if>" +
            "<if test=\"picture != null and picture != '' \">" +
            " picture=#{picture}," +
            "</if>" +
            "<if test=\"tips != null and tips != '' \">" +
            " tips=#{tips}," +
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
            "<if test=\"levels >= 0 \">" +
            " levels=#{levels}," +
            "</if>" +
            "<if test=\"type != null and type != '' \">" +
            " type=#{type}," +
            "</if>" +
            "<if test=\"address != null and address != '' \">" +
            " address=#{address}," +
            "</if>" +
            " id=#{id}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen(ScenicSpot kitchen);

    @Update("<script>" +
            "update ScenicSpot set" +
            " cost=#{cost}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen3(ScenicSpot kitchen);

    @Update("<script>" +
            "update ScenicSpot set" +
            " licence=#{licence}," +
            " auditType=#{auditType}," +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateKitchen2(ScenicSpot kitchen);


    @Update("<script>" +
            "update ScenicSpot set" +
            "<if test=\"relationHotel >= 0\">" +
            " relationHotel=#{relationHotel}" +
            "</if>" +
            "<if test=\"relationReservation >= 0\">" +
            " relationReservation=#{relationReservation}" +
            "</if>" +
            " where userId=#{userId} and deleteType = 0" +
            "</script>")
    int update(ScenicSpot kitchen);

    /***
     * 更新景区评分
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            " totalScore=#{totalScore}," +
            " averageScore=#{averageScore}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateScore(ScenicSpot kitchen);

    /***
     * 更新景区删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0" +
            "</script>")
    int updateDel(ScenicSpot kitchen);

    /***
     * 根据userId查询预定
     * @param userId
     * @return
     */
    @Select("select * from ScenicSpot where userId=#{userId} and deleteType = 0")
    ScenicSpot findReserve(@Param("userId") long userId);

    /***
     * 根据Id查询预定
     * @param id
     * @return
     */
    @Select("select * from ScenicSpot where id=#{id} and deleteType = 0 ")
    ScenicSpot findById(@Param("id") long id);

    /***
     * 更新景区营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId} and deleteType = 0  " +
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
            " where deleteType = 0 and (businessStatus=0 and auditType=1 OR (claimId != '' and claimStatus = 0))" +
            " and userId != #{userId}" +
            " and scenicSpotName LIKE CONCAT('%',#{name},'%')" +
//            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl != ''" +
//            "</if>" +
            "</if>" +
            "<if test=\"name == null or name == '' \">" +
            " select *" +
            "<if test=\"lat > 0 and lon > 0 \">" +
            ", ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            "</if>" +
            " from ScenicSpot " +
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
    List<ScenicSpot> findKitchenList(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("name") String name, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 新增门票
     * @param dishes
     * @return
     */
    @Insert("insert into ScenicSpotTickets(userId,scenicSpotId,cost,name,addTime,describes,picture) " +
            "values (#{userId},#{scenicSpotId},#{cost},#{name},#{addTime},#{describes},#{picture})")
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
            " picture=#{picture}," +
            " describes=#{describes}," +
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
            " order by cost asc" +
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

    /***
     * 批量查询指定的门票
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from ScenicSpotTickets" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<ScenicSpotTickets> findDishesList2(@Param("ids") String[] ids);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from ScenicSpotCollection where myId=#{userId} and userId=#{id}")
    ScenicSpotCollection findWhether(@Param("userId") long userId, @Param("id") long id);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from ScenicSpotCollection where myId=#{userId} and scenicSpotId=#{id}")
    ScenicSpotCollection findWhether2(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增收藏
     * @param ScenicSpotCollection
     * @return
     */
    @Insert("insert into ScenicSpotCollection(myId,userId,name,picture,time,type,levels,scenicSpotId) " +
            "values (#{myId},#{userId},#{name},#{picture},#{time},#{type},#{levels},#{scenicSpotId})")
    @Options(useGeneratedKeys = true)
    int addCollect(ScenicSpotCollection ScenicSpotCollection);

    /***
     * 查询景区收藏列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from ScenicSpotCollection" +
            " where 1=1" +
            " and myId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<ScenicSpotCollection> findCollectionList(@Param("userId") long userId);

    /***
     * 删除景区收藏
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from ScenicSpotCollection" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and myId=#{userId}" +
            "</script>")
    int del(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据Id查询酒店民宿
     * @param id
     * @return
     */
    @Select("select * from ScenicSpotData where id=#{id}")
    ScenicSpotData findReserveData(@Param("id") long id);

    /***
     * 条件查询酒店民宿
     * @return
     */
    @Select("<script>" +
            "<if test=\"kitchenName != null and kitchenName != '' \">" +
            "select * from ScenicSpotData where claimStatus=0 " +
            " and name LIKE CONCAT('%',#{kitchenName},'%')" +
            "</if>" +
            "<if test=\"kitchenName == null and latitude > 0 \">" +
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{latitude}*PI()/180-latitude*PI()/180)/2),2)+COS(#{latitude}*PI()/180)*COS(latitude*PI()/180)*POW(SIN((#{longitude}*PI()/180-longitude*PI()/180)/2),2)))*1000) AS juli " +
            " from ScenicSpotData " +
            " where claimStatus=0" +
            " and latitude > #{latitude}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算
            " and latitude &lt; #{latitude}+1" +
            " and longitude > #{longitude}-1" +
            " and longitude &lt; #{longitude}+1" +
            " order by juli asc,overallRating desc" +
            "</if>" +
            "</script>")
    List<ScenicSpotData> findReserveDataList(@Param("kitchenName") String kitchenName, @Param("latitude") double latitude, @Param("longitude") double longitude);

    /***
     * 更新酒店民宿认领状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpotData set" +
            " userId=#{userId}," +
            " claimTime=#{claimTime}," +
            " claimStatus=#{claimStatus}" +
            " where uid=#{uid}" +
            "</script>")
    int claimKitchen(ScenicSpotData kitchen);

    /***
     * 更新酒店民宿认领状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpot set" +
            " userId=#{userId}," +
            " invitationCode=#{invitationCode}," +
            " licence=#{licence}," +
            " phone=#{phone}," +
            " claimTime=#{claimTime}," +
            " businessStatus=#{businessStatus}," +
            " claimStatus=#{claimStatus}" +
            " where claimId=#{claimId}" +
            "</script>")
    int claimKitchen2(ScenicSpot kitchen);

    /***
     * 根据uid查询酒店民宿
     * @param uid
     * @return
     */
    @Select("select * from ScenicSpotData where uid=#{uid}")
    ScenicSpotData findReserveDataId(@Param("uid") String uid);

    /***
     * 新增酒店民宿数据
     * @param kitchen
     * @return
     */
    @Insert("insert into ScenicSpotData(userId,uid,streetID,name,addTime,province,city,area,latitude,longitude," +
            "address,distance,claimStatus,claimTime,type,phone,tag,detailURL,price,openingHours,overallRating,tasteRating," +
            "serviceRating,environmentRating,hygieneRating,technologyRating,facilityRating,imageNumber,grouponNumber,discountNumber,commentNumber,favoriteNumber,checkInNumber)" +
            "values (#{userId},#{uid},#{streetID},#{name},#{addTime},#{province},#{city},#{area},#{latitude},#{longitude}" +
            ",#{address},#{distance},#{claimStatus},#{claimTime},#{type},#{phone},#{tag},#{detailURL},#{price},#{openingHours},#{overallRating},#{tasteRating},#{serviceRating},#{environmentRating},#{hygieneRating}" +
            ",#{technologyRating},#{facilityRating},#{imageNumber},#{grouponNumber},#{discountNumber},#{commentNumber},#{favoriteNumber},#{checkInNumber})")
    @Options(useGeneratedKeys = true)
    int addReserveData(ScenicSpotData kitchen);

    /***
     * 更新酒店民宿数据
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update ScenicSpotData set" +
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
    int updateReserveData(ScenicSpotData kitchen);

}

package com.busi.dao;

import com.busi.entity.HourlyWorker;
import com.busi.entity.HourlyWorkerCollection;
import com.busi.entity.HourlyWorkerType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 小时工
 * @author: ZHaoJiaJie
 * @create: 2019-04-23 14:23
 */
@Mapper
@Repository
public interface HourlyWorkerDao {

    /***
     * 新增小时工
     * @param hourlyWorker
     * @return
     */
    @Insert("insert into HourlyWorker(userId,businessStatus,deleteType,auditType,arriveTime,healthyImgUrl,coverCover,videoUrl,addTime,videoCoverUrl,content,totalSales,totalScore,lat,lon," +
            "address,workerType,housekeeping,name,birthday,sex)" +
            "values (#{userId},#{businessStatus},#{deleteType},#{auditType},#{arriveTime},#{healthyImgUrl},#{coverCover},#{videoUrl},#{addTime},#{videoCoverUrl},#{content},#{totalSales},#{totalScore},#{lat},#{lon}" +
            ",#{address},#{workerType},#{housekeeping},#{name},#{birthday},#{sex})")
    @Options(useGeneratedKeys = true)
    int addHourly(HourlyWorker hourlyWorker);

    /***
     * 更新小时工
     * @param hourlyWorker
     * @return
     */
    @Update("<script>" +
            "update HourlyWorker set" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " content=#{content}," +
            " address=#{address}," +
            " arriveTime=#{arriveTime}," +
            " workerType=#{workerType}," +
            " housekeeping=#{housekeeping}," +
            " healthyImgUrl=#{healthyImgUrl}," +
            " coverCover=#{coverCover}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateHourly(HourlyWorker hourlyWorker);

    /***
     * 更新小时工总服务次数
     * @param hourlyWorker
     * @return
     */
    @Update("<script>" +
            "update HourlyWorker set" +
            " totalSales=#{totalSales}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateNumber(HourlyWorker hourlyWorker);

    /***
     * 更新小时工总评分
     * @param hourlyWorker
     * @return
     */
    @Update("<script>" +
            "update HourlyWorker set" +
            " totalScore=#{totalScore}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateScore(HourlyWorker hourlyWorker);

    /***
     * 更新小时工实名信息
     * @param hourlyWorker
     * @return
     */
    @Update("<script>" +
            "update HourlyWorker set" +
            " name=#{name}," +
            " birthday=#{birthday}," +
            " sex=#{sex}," +
            " userId=#{userId}" +
            " where userId=#{userId}" +
            "</script>")
    int updateRealName(HourlyWorker hourlyWorker);

    /***
     * 更新小时工删除状态
     * @param hourlyWorker
     * @return
     */
    @Update("<script>" +
            "update hourlyWorker set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(HourlyWorker hourlyWorker);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from HourlyWorker where userId=#{userId} and deleteType =0 and auditType=1")
    HourlyWorker findByUserId(@Param("userId") long userId);

    /***
     * 根据Id查询
     * @param id
     * @return
     */
    @Select("select * from HourlyWorker where id=#{id} and deleteType =0 and auditType=1")
    HourlyWorker findById(@Param("id") long id);

    /***
     * 更新小时工营业状态
     * @param hourlyWorker
     * @return
     */
    @Update("<script>" +
            "update HourlyWorker set" +
            " sex=#{sex}," +
            " name=#{name}," +
            " birthday=#{birthday}," +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(HourlyWorker hourlyWorker);

    /***
     * 条件查询小时工(模糊搜索)
     * @param userId 用户ID
     * @param name  小时工名字
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorker" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            "<if test=\"name != null and name != '' \">" +
            " and name LIKE CONCAT('%',#{name},'%')" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "</script>")
    List<HourlyWorker> findHourlyList(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("name") String name);

    /***
     * 条件查询小时工（距离最近）
     * @param userId 用户ID
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    @Select("<script>" +
//            "select * from HourlyWorker where" +
//            " userId != #{userId}" +
//            " and businessStatus=0 and deleteType = 0 and auditType=1" +
//            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl != ''" +
//            "</if>" +
//            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
//            " and lat &lt; #{lat}+1 and lon > #{lon}-1" +
//            " and lon &lt; #{lon}+1 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from HourlyWorker " +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
//            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算
//            " and lat &lt; #{lat}+1" +
//            " and lon > #{lon}-1" +
//            " and lon &lt; #{lon}+1" +
            " order by juli asc" +
            "</script>")
    List<HourlyWorker> findHourlyList2(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 条件查询小时工（条件搜索）
     * @param userId 用户ID
     * @param sortType  排序类型：默认0综合排序  1距离最近  2销量最高  3评分最高
     * @return
     */
    @Select("<script>" +
//            "select * from HourlyWorker" +
//            " where userId != #{userId}" +
//            " and businessStatus=0 and deleteType = 0 and auditType=1" +
//            "<if test=\"watchVideos == 1\">" +
//            " and videoUrl != ''" +
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
            "<if test=\"sortType == 0\">" +
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from HourlyWorker " +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
//            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算
//            " and lat &lt; #{lat}+1" +
//            " and lon > #{lon}-1" +
//            " and lon &lt; #{lon}+1" +
            " order by juli asc,totalScore desc" +
            "</if>" +
            "<if test=\"sortType == 2\">" +
            "select * from HourlyWorker" +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            " order by totalSales desc" +
            "</if>" +
            "<if test=\"sortType == 3\">" +
            "select * from HourlyWorker" +
            " where userId != #{userId}" +
            " and businessStatus=0 and deleteType = 0 and auditType=1" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            " order by totalScore desc" +
            "</if>" +
            "</script>")
    List<HourlyWorker> findHourlyList3(@Param("userId") long userId, @Param("watchVideos") int watchVideos, @Param("sortType") int sortType, @Param("lat") double lat, @Param("lon") double lon);

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    @Select("select * from hourlyWorkerCollection where myId=#{userId} and workerId=#{id}")
    HourlyWorkerCollection findWhether(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @Insert("insert into hourlyWorkerCollection(myId,workerId,workerName,workerCover,time) " +
            "values (#{myId},#{workerId},#{workerName},#{workerCover},#{time})")
    @Options(useGeneratedKeys = true)
    int addCollect(HourlyWorkerCollection hourlyWorkerCollection);

    /***
     * 查询小时工收藏列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerCollection" +
            " where 1=1" +
            " and myId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<HourlyWorkerCollection> findCollectionList(@Param("userId") long userId);

    /***
     * 批量查询指定的小时工
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorker" +
            " where userId in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<HourlyWorker> findKitchenList4(@Param("ids") String[] ids);

    /***
     * 批量查询指定的工作类型
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerType" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<HourlyWorkerType> findDishesList2(@Param("ids") String[] ids);

    /***
     * 删除小时工收藏
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from HourlyWorkerCollection" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and myId=#{userId}" +
            "</script>")
    int del(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 新增工作类型
     * @param hourlyWorkerType
     * @return
     */
    @Insert("insert into HourlyWorkerType(userId,workerId,charge,workerType,addTime,remarks) " +
            "values (#{userId},#{workerId},#{charge},#{workerType},#{addTime},#{remarks})")
    @Options(useGeneratedKeys = true)
    int addDishes(HourlyWorkerType hourlyWorkerType);

    /***
     * 更新工作类型
     * @param hourlyWorkerType
     * @return
     */
    @Update("<script>" +
            "update HourlyWorkerType set" +
            " charge=#{charge}," +
            " remarks=#{remarks}," +
            " workerType=#{workerType}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDishes(HourlyWorkerType hourlyWorkerType);

    /***
     * 更新工作类型服务次数
     * @param hourlyWorkerType
     * @return
     */
    @Update("<script>" +
            "update HourlyWorkerType set" +
            " sales=#{sales}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateType(HourlyWorkerType hourlyWorkerType);

    /***
     * 更新工作类型点赞数
     * @param hourlyWorkerType
     * @return
     */
    @Update("<script>" +
            "update HourlyWorkerType set" +
            " pointNumber=#{pointNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateLike(HourlyWorkerType hourlyWorkerType);

    /***
     * 删除工作类型
     * @param ids
     * @return
     */
    @Update("<script>" +
            "delete from HourlyWorkerType" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delDishes(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据ID查询工作类型
     * @param id
     * @return
     */
    @Select("select * from HourlyWorkerType where id=#{id}")
    HourlyWorkerType disheSdetails(@Param("id") long id);

    /***
     * 查询工作类型列表
     * @param workerId  小时工ID
     * @return
     */
    @Select("<script>" +
            "select * from HourlyWorkerType" +
            " where workerId=#{workerId}" +
            " order by addTime desc" +
            "</script>")
    List<HourlyWorkerType> findDishesList(@Param("workerId") long workerId);

    /***
     * 统计该用户工作类型数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from HourlyWorker" +
            " where userId=#{userId} and deleteType = 0 and auditType=1" +
            "</script>")
    int findNum(@Param("userId") long userId);
}

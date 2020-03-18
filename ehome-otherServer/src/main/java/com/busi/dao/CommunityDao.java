package com.busi.dao;

import com.busi.entity.Community;
import com.busi.entity.CommunityResident;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 居委会
 * @author: ZHaoJiaJie
 * @create: 2020-03-18 11:32:23
 */
@Mapper
@Repository
public interface CommunityDao {
    /***
     * 删除居民
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunityResident" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delResident(@Param("ids") String[] ids);

    /***
     * 新增居委会
     * @param selectionVote
     * @return
     */
    @Insert("insert into Community(userId,name,province,city,district,lat,lon,address,cover,photo,content,notice,time,review) " +
            "values (#{userId},#{name},#{province},#{city},#{district},#{lat},#{lon},#{address},#{cover},#{photo},#{content},#{notice},#{time},#{review})")
    @Options(useGeneratedKeys = true)
    int addCommunity(Community selectionVote);

    /***
     * 更新居委会
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update Community set" +
            " name=#{name}," +
            " province=#{province}," +
            " city=#{city}," +
            " district=#{district}," +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " address=#{address}," +
            " cover=#{cover}," +
            " photo=#{photo}," +
            "<if test=\"notice != null and notice != '' \">" +
            " notice=#{notice}," +
            "</if>" +
            " content=#{content}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeCommunity(Community selectionActivities);

    /***
     * 根据ID查询居委会
     * @param id
     * @return
     */
    @Select("select * from Community where id = #{id}")
    Community findCommunity(@Param("id") long id);

    /***
     * 查询是否已加入居委会
     * @param userId
     * @return
     */
    @Select("select * from CommunityResident where userId = #{userId}")
    CommunityResident findJoin(@Param("userId") long userId);

    /***
     * 查询居委会列表
     * @param lon     经度
     * @param lat     纬度
     * @param province     省
     * @param city      市
     * @param district    区
     * @param string    模糊搜索
     * @return
     */
    @Select("<script>" +
            "select * from Community" +
            " where 1=1" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"string == null or string == '' \">" +
            " and lat > #{lat}-0.09009" +  //只对于经度和纬度大于或小于该用户10公里（1度111公里)范围内的居委会进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
            " and lat &lt; #{lat}+0.09009 and lon > #{lon}-0.09009" +
            " and lon &lt; #{lon}+0.09009 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            "</if>" +
            "<if test=\"string != null and string != '' \">" +
            " and name LIKE CONCAT('%',#{string},'%')" +
            "</if>" +
            " ORDER BY time desc" +
            "</script>")
    List<Community> findCommunityList(@Param("lon") double lon, @Param("lat") double lat, @Param("string") String string, @Param("province") int province, @Param("city") int city, @Param("district") int district);

}

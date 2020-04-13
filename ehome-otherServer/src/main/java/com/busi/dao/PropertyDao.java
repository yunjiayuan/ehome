package com.busi.dao;

import com.busi.entity.*;
import com.busi.entity.Property;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 物业
 * @author: ZHaoJiaJie
 * @create: 2020-04-07 18:18:17
 */
@Mapper
@Repository
public interface PropertyDao {

    /***
     * 新增物业
     * @param selectionVote
     * @return
     */
    @Insert("insert into Property(userId,name,province,city,district,lat,lon,address,cover,photo,content,notice,time,review,communityId) " +
            "values (#{userId},#{name},#{province},#{city},#{district},#{lat},#{lon},#{address},#{cover},#{photo},#{content},#{notice},#{time},#{review},#{communityId})")
    @Options(useGeneratedKeys = true)
    int addProperty(Property selectionVote);

    /***
     * 更新物业
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update Property set" +
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
    int changeProperty(Property selectionActivities);

    /***
     * 设置所属居委会
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update Property set" +
            " communityId=#{communityId}" +
            " where id=#{id}" +
            "</script>")
    int subordinateProperty(Property selectionActivities);

    /***
     * 根据ID查询物业
     * @param id
     * @return
     */
    @Select("select * from Property where id = #{id}")
    Property findProperty(@Param("id") long id);

    /***
     * 查询已加入的物业
     * @param userId
     * @return
     */
    @Select("select * from PropertyResident where userId = #{userId} ORDER BY refreshTime desc")
    List<PropertyResident> findJoin(@Param("userId") long userId);

    /***
     * 查询指定物业
     * @param ids    物业Ids
     * @return
     */
    @Select("<script>" +
            "select * from Property" +
            " where 1=1" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " ORDER BY time desc" +
            "</script>")
    List<Property> findPropertyList3(@Param("ids") String[] ids);

    /***
     * 查询物业列表
     * @param province     省
     * @param city      市
     * @param district    区
     * @param string    模糊搜索
     * @return
     */
    @Select("<script>" +
            "select * from Property" +
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
            "<if test=\"string != null and string != '' \">" +
            " and name LIKE CONCAT('%',#{string},'%')" +
            "</if>" +
            " ORDER BY time desc" +
            "</script>")
    List<Property> findPropertyList(@Param("string") String string, @Param("province") int province, @Param("city") int city, @Param("district") int district);

    @Select("<script>" +
            "select * from Property" +
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
            "<if test=\"province == -1\">" +
            " and lat > #{lat}-0.09009" +  //只对于经度和纬度大于或小于该用户10公里（1度111公里)范围内的物业进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
            " and lat &lt; #{lat}+0.09009 and lon > #{lon}-0.09009" +
            " and lon &lt; #{lon}+0.09009 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            "</if>" +
            "</script>")
    List<Property> findPropertyList2(@Param("lon") double lon, @Param("lat") double lat, @Param("province") int province, @Param("city") int city, @Param("district") int district);

    /***
     * 加入物业
     * @param selectionVote
     * @return
     */
    @Insert("insert into PropertyResident(userId,propertyId,masterId,identity,type,time,refreshTime,tags) " +
            "values (#{userId},#{propertyId},#{masterId},#{identity},#{type},#{time},#{refreshTime},#{tags})")
    @Options(useGeneratedKeys = true)
    int addResident(PropertyResident selectionVote);

    /***
     * 更新居民权限
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update PropertyResident set" +
            " identity=#{identity}" +
            " where propertyId=#{propertyId} and userId=#{userId}" +
            "</script>")
    int changeResident(PropertyResident selectionActivities);

    /***
     * 更新居民标签
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update PropertyResident set" +
            " tags=#{tags}" +
            " where id=#{id}" +
            "</script>")
    int changeResidentTag(PropertyResident selectionActivities);

    /***
     * 查询居民
     * @param userId
     * @return
     */
    @Select("select * from PropertyResident where userId = #{userId} and propertyId=#{propertyId}")
    PropertyResident findResident(@Param("propertyId") long propertyId, @Param("userId") long userId);

    /***
     * 删除居民
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "<if test=\"type == 1\">" +
            "update PropertyResident set" +
            " identity=0" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            "<if test=\"type == 0\">" +
            "delete from PropertyResident" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            "</script>")
    int delResident(@Param("type") int type, @Param("ids") String[] ids);

    /***
     * 查询居民列表
     * @param propertyId    物业
     * @return
     */
    @Select("<script>" +
            "select * from PropertyResident" +
            " where 1=1" +
            "<if test=\"type == 1\">" +
            " and identity > 0" +
            "</if>" +
            " and propertyId = #{propertyId}" +
            " ORDER BY identity desc" +
            "</script>")
    List<PropertyResident> findResidentList(@Param("type") int type, @Param("propertyId") long propertyId);

    /***
     * 查询指定物业居民列表
     * @param ids    物业
     * @return
     */
    @Select("<script>" +
            "select * from PropertyResident" +
            " where 1=1" +
            " and userId = #{userId}" +
            " and propertyId in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " ORDER BY time desc" +
            "</script>")
    List<PropertyResident> findIsList2(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 查询指定居民列表
     * @param ids    物业
     * @return
     */
    @Select("<script>" +
            "select * from PropertyResident" +
            " where 1=1" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<PropertyResident> findIsList3(@Param("ids") String[] ids);

    /***
     * 删除物业人员设置
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from PropertySetUp" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delSetUp(@Param("ids") String[] ids);

    /***
     * 新增物业人员设置
     * @param communityHouse
     * @return
     */
    @Insert("insert into PropertySetUp(propertyId,post,head,name) " +
            "values (#{propertyId},#{post},#{head},#{name})")
    @Options(useGeneratedKeys = true)
    int addSetUp(PropertySetUp communityHouse);

    /***
     * 更新物业人员设置
     * @param communityHouse
     * @return
     */
    @Update("<script>" +
            "update PropertySetUp set" +
            " post=#{post}," +
            " head=#{head}," +
            " name=#{name}" +
            " where id=#{id}" +
            "</script>")
    int changeSetUp(PropertySetUp communityHouse);

    /***
     * 查询物业人员设置列表
     * @return
     */
    @Select("<script>" +
            "select * from PropertySetUp" +
            " where 1=1" +
            " and propertyId = #{propertyId}" +
            " ORDER BY post asc" +
            "</script>")
    List<PropertySetUp> findSetUpList(@Param("propertyId") long propertyId);

    /***
     * 更新物业
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update PropertyResident set" +
            " refreshTime=#{refreshTime}" +
            " where propertyId=#{propertyId} and userId=#{userId}" +
            "</script>")
    int changeCommunityTime(PropertyResident selectionActivities);

    /***
     * 更新评论数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update Property set" +
            " commentNumber=#{commentNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateBlogCounts(Property homeBlogComment);

    /***
     * 查询管理员列表
     * @param communityId    物业
     * @return
     */
    @Select("<script>" +
            "select * from PropertyResident" +
            " where 1=1" +
            " and propertyId = #{communityId} and identity>0" +
            " ORDER BY time desc" +
            "</script>")
    List<PropertyResident> findWardenList(@Param("communityId") long communityId);

}

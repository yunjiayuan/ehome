package com.busi.dao;

import com.busi.entity.Community;
import com.busi.entity.CommunityMessageBoard;
import com.busi.entity.CommunityResident;
import com.busi.entity.CommunitySetUp;
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

    /***
     * 加入居委会
     * @param selectionVote
     * @return
     */
    @Insert("insert into CommunityResident(userId,communityId,masterId,identity,type,time) " +
            "values (#{userId},#{communityId},#{masterId},#{identity},#{type},#{time})")
    @Options(useGeneratedKeys = true)
    int addResident(CommunityResident selectionVote);

    /***
     * 更新居民权限
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update CommunityResident set" +
            " identity=#{identity}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeResident(CommunityResident selectionActivities);

    /***
     * 查询居民
     * @param userId
     * @return
     */
    @Select("select * from CommunityResident where userId = #{userId} and communityId=#{communityId}")
    CommunityResident findResident(@Param("communityId") long communityId, @Param("userId") long userId);

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
     * 查询居民列表
     * @param communityId    居委会
     * @return
     */
    @Select("<script>" +
            "select * from CommunityResident" +
            " where 1=1" +
            " and communityId = #{communityId}" +
            " ORDER BY time desc" +
            "</script>")
    List<CommunityResident> findResidentList(@Param("communityId") long communityId);

    /***
     * 查询指定居民列表
     * @param communityId    居委会
     * @return
     */
    @Select("<script>" +
            "select * from CommunityResident" +
            " where 1=1" +
            " and communityId = #{communityId}" +
            " and userId in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " ORDER BY time desc" +
            "</script>")
    List<CommunityResident> findIsList(@Param("communityId") long communityId, @Param("ids") String[] ids);

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Insert("insert into CommunityMessageBoard(userId,communityId,replayId,content,time,replyType,replyStatus,fatherId,secondFatherId) " +
            "values (#{userId},#{communityId},#{replayId},#{content},#{time},#{replyType},#{replyStatus},#{fatherId},#{secondFatherId})")
    @Options(useGeneratedKeys = true)
    int addComment(CommunityMessageBoard homeBlogComment);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from CommunityMessageBoard where id = #{id} and replyStatus=0")
    CommunityMessageBoard find(@Param("id") long id);

    /***
     * 更新删除状态
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update CommunityMessageBoard set" +
            " replyStatus=#{replyStatus}" +
            " where id=#{id}" +
            "</script>")
    int update(CommunityMessageBoard homeBlogComment);

    /***
     * 更新回复数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update CommunityMessageBoard set" +
            " replyNumber=#{replyNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateCommentNum(CommunityMessageBoard homeBlogComment);

    /***
     * 更新评论数
     * @param homeBlogComment
     * @return
     */
    @Update("<script>" +
            "update Community set" +
            " commentNumber=#{commentNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateBlogCounts(Community homeBlogComment);

    /***
     * 查询评论列表(只查评论replyType = 0)
     * @param communityId  居委会ID
     * @return
     */
    @Select("<script>" +
            "select * from CommunityMessageBoard" +
            " where 1=1" +
            " and communityId=#{communityId} and replyStatus=0 and replyType = 0" +
            " order by time desc" +
            "</script>")
    List<CommunityMessageBoard> findList(@Param("communityId") long communityId);

    /***
     * 查询回复列表(只查回复replyType = 1)
     * @param contentId  评论ID
     * @return
     */
    @Select("<script>" +
            "select * from CommunityMessageBoard" +
            " where 1=1" +
            " and fatherId=#{contentId} and replyStatus=0 and replyType = 1" +
            " order by time desc" +
            "</script>")
    List<CommunityMessageBoard> findReplyList(@Param("contentId") long contentId);

    /***
     * 更新回复删除状态
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update CommunityMessageBoard set" +
            " replyStatus=1" +
            " where replyType=1" +
            " and id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateReplyState(@Param("ids") String[] ids);

    /***
     * 统计该评论下回复数量
     * @param commentId  评论ID
     * @return
     */
    @Select("<script>" +
            "select count(id) from CommunityMessageBoard" +
            " where fatherId=#{commentId} and replyStatus=0 and replyType=1" +
            "</script>")
    int getReplayCount(@Param("commentId") long commentId);

    /***
     * 删除居委会人员设置
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunitySetUp" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delSetUp(@Param("ids") String[] ids);

    /***
     * 新增居委会人员设置
     * @param communityHouse
     * @return
     */
    @Insert("insert into CommunitySetUp(communityId,post,head) " +
            "values (#{communityId},#{post},#{head})")
    @Options(useGeneratedKeys = true)
    int addSetUp(CommunitySetUp communityHouse);

    /***
     * 更新居委会人员设置
     * @param communityHouse
     * @return
     */
    @Update("<script>" +
            "update CommunitySetUp set" +
            " post=#{post}," +
            " head=#{head}" +
            " where id=#{id}" +
            "</script>")
    int changeSetUp(CommunitySetUp communityHouse);

    /***
     * 查询居委会人员设置列表
     * @return
     */
    @Select("<script>" +
            "select * from CommunitySetUp" +
            " where 1=1" +
            " and communityId = #{communityId}" +
            " ORDER BY post asc" +
            "</script>")
    List<CommunitySetUp> findSetUpList(@Param("communityId") long communityId);

}

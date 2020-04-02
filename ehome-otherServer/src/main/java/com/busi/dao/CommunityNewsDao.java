package com.busi.dao;

import com.busi.entity.CommunityLook;
import com.busi.entity.CommunityNews;
import com.busi.entity.CommunitySetUp;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 资讯
 * @author: ZHaoJiaJie
 * @create: 2020-03-20 11:50:07
 */
@Mapper
@Repository
public interface CommunityNewsDao {
    /***
     * 新增
     * @param todayNews
     * @return
     */
    @Insert("insert into CommunityNews(communityId,userId, title, content, imgUrls, videoUrl, coverUrl, newsType,newsFormat,addTime,refreshTime,commentCount,newsState,identity,lookUserIds) " +
            "values (#{communityId},#{userId},#{title},#{content},#{imgUrls},#{videoUrl},#{coverUrl},#{newsType},#{newsFormat},#{addTime},#{refreshTime},#{commentCount},#{newsState},#{identity},#{lookUserIds})")
    @Options(useGeneratedKeys = true)
    int add(CommunityNews todayNews);

    /***
     * 更新
     * @param todayNews
     * @return
     */
    @Update("<script>" +
            "update CommunityNews set" +
            "<if test=\"title != null and title != ''\">" +
            " title=#{title}," +
            "</if>" +
            "<if test=\"content != null and content != ''\">" +
            " content=#{content}," +
            "</if>" +
            "<if test=\"imgUrls != null and imgUrls != ''\">" +
            " imgUrls=#{imgUrls}," +
            "</if>" +
            "<if test=\"videoUrl != null and videoUrl != ''\">" +
            " videoUrl=#{videoUrl}," +
            "</if>" +
            "<if test=\"coverUrl != null and coverUrl != ''\">" +
            " coverUrl=#{coverUrl}," +
            "</if>" +
            " newsType=#{newsType}," +
            " identity=#{identity}," +
            " newsFormat=#{newsFormat}," +
            " lookUserIds=#{lookUserIds}," +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int editNews(CommunityNews todayNews);

    /***
     * 删除
     * @param id
     * @return
     */
    @Update("<script>" +
            "update CommunityNews set" +
            " newsState=1" +
            " where id = #{id}" +
            "</script>")
    int del(@Param("id") long id);

    /***
     * 查询
     */
    @Select("select * from CommunityNews where id=#{infoId} AND newsState=0")
    CommunityNews findInfo(@Param("infoId") long infoId);

    /***
     * 普通用户分页查询
     * @param newsType
     * @return
     */
    @Select("<script>" +
            "select * from CommunityNews" +
            " where 1=1 " +
            "<if test=\"tags != null and newsType==3 \">" +
                "<foreach collection='tags' index='index' item='item' open='(' separator=',' close=')'>" +
                    " #{item}" +
//                    " and identity LIKE CONCAT(CONCAT('%',#{item}),'%')" +
                    " and identity LIKE CONCAT('%',#{item},'%')" +
                "</foreach>" +
            "</if>" +
            "<if test=\"uId != null and uId !='' and newsType==3 \">" +
                " and lookUserIds LIKE CONCAT('%',#{uId},'%')" +
            "</if>" +
            " and newsType=#{newsType} " +
            " and newsState=0 " +
            " and communityId=#{communityId}" +
            " order by refreshTime desc" +
            "</script>")
    List<CommunityNews> findList(@Param("communityId") long communityId, @Param("newsType") int newsType,@Param("uId") String uId,@Param("tags")  String[] tags);

    /***
     * 管理员分页查询
     * @param newsType
     * @return
     */
    @Select("<script>" +
            "select * from CommunityNews" +
            " where 1=1 " +
            " and newsType=#{newsType} " +
            " and newsState=0 " +
            " and communityId=#{communityId}" +
            " order by refreshTime desc" +
            "</script>")
    List<CommunityNews> findListByadmin(@Param("communityId") long communityId, @Param("newsType") int newsType);

    /***
     * 查询列表
     * @return
     */
    @Select("<script>" +
            "select * from CommunityLook" +
            " where 1=1" +
            " and infoId = #{id}" +
            " ORDER BY time desc" +
            "</script>")
    List<CommunityLook> findLook(@Param("id") long id);

    /***
     * 新增
     * @param communityHouse
     * @return
     */
    @Insert("insert into CommunityLook(communityId,title,infoId,time,userId) " +
            "values (#{communityId},#{title},#{infoId},#{time},#{userId})")
    @Options(useGeneratedKeys = true)
    int addLook(CommunityLook communityHouse);

    /***
     * 删除
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunityLook" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delLook(@Param("ids") String[] ids);

}

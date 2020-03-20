package com.busi.dao;

import com.busi.entity.CommunityNews;
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
    @Insert("insert into CommunityNews(communityId,userId, title, content, imgUrls, videoUrl, coverUrl, newsType,newsFormat,addTime,refreshTime,commentCount,newsState) " +
            "values (#{communityId},#{userId},#{title},#{content},#{imgUrls},#{videoUrl},#{coverUrl},#{newsType},#{newsFormat},#{addTime},#{refreshTime},#{commentCount},#{newsState})")
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
            " newsFormat=#{newsFormat}," +
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
     * 分页查询
     * @param newsType
     * @return
     */
    @Select("<script>" +
            "select * from CommunityNews" +
            " where newsType=#{newsType} AND newsState=0 and communityId=#{communityId}" +
            " order by refreshTime desc" +
            "</script>")
    List<CommunityNews> findList(@Param("communityId") long communityId, @Param("newsType") int newsType);

}

package com.busi.dao;

import com.busi.entity.TodayNews;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 资讯
 * @author: ZHaoJiaJie
 * @create: 2018-09-27 13:24
 */
@Mapper
@Repository
public interface TodayNewsDao {

    /***
     * 新增
     * @param todayNews
     * @return
     */
    @Insert("insert into TodayNews(userId, title, content, imgUrls, videoUrl, coverUrl, newsType,newsFormat,addTime,refreshTime,commentCount,newsState) " +
            "values (#{userId},#{title},#{content},#{imgUrls},#{videoUrl},#{coverUrl},#{newsType},#{newsFormat},#{addTime},#{refreshTime},#{commentCount},#{newsState})")
    @Options(useGeneratedKeys = true)
    int add(TodayNews todayNews);

    /***
     * 更新
     * @param todayNews
     * @return
     */
    @Update("<script>" +
            "update TodayNews set" +
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
    int editNews(TodayNews todayNews);

    /***
     * 删除
     * @param id
     * @return
     */
    @Update("<script>" +
            "update TodayNews set" +
            " newsState=1" +
            " where id = #{id}" +
            "</script>")
    int del(@Param("id") long id);

    /***
     * 查询
     */
    @Select("select * from TodayNews where id=#{infoId} AND newsState=0")
    TodayNews findInfo(@Param("infoId") long infoId);

    /***
     * 分页查询
     * @param newsType
     * @return
     */
    @Select("<script>" +
            "select * from TodayNews" +
            " where newsType=#{newsType} AND newsState=0" +
            " order by refreshTime desc" +
            "</script>")
    List<TodayNews> findList(@Param("newsType") int newsType);

}

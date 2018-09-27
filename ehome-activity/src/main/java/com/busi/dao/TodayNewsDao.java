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

package com.busi.dao;

import com.busi.entity.HomeBlog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 生活圈DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface HomeBlogDao {

    /***
     * 新增生活圈
     * @param homeBlog
     * @return
     */
    @Insert("insert into homeBlog(userId,title,content,contentTxt,imgUrl,videoUrl,videoCoverUrl,audioUrl,sendType,classify,tag,blogType,shareBlogId,shareUserId,origBlogId,origUserId,reprintContent,accessId,blogStatus,longitude,latitude,position,anonymousType,shareInfo,reward,firstPayUserId,solve,time) " +
            "values (#{userId},#{title},#{content},#{contentTxt},#{imgUrl},#{videoUrl},#{videoCoverUrl},#{audioUrl},#{sendType},#{classify},#{tag},#{blogType},#{shareBlogId},#{shareUserId},#{origBlogId},#{origUserId},#{reprintContent},#{accessId},#{blogStatus},#{longitude},#{latitude},#{position},#{anonymousType},#{shareInfo},#{reward},#{firstPayUserId},#{solve},#{time})")
    @Options(useGeneratedKeys = true)
    int add(HomeBlog homeBlog);

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param id
     */
    @Select("select * from HomeBlog where id = #{id} and blogStatus = 0")
    HomeBlog findBlogInfo(@Param("id") long id);

    /***
     * 删除指定生活圈接口(只更新状态)
     * @param userId 生活圈发布者用户ID
     * @param id     将要被删除的生活圈
     * @return
     */
    @Update("update homeBlog set blogStatus = 1 where id = #{id} and userId = #{userId}")
    int delBlog(@Param("id") long id,@Param("userId") long userId);

    /***
     * 根据兴趣标签查询列表
     * @param tags       标签数组格式 1,2,3
     * @param searchType 博文类型：0所有 1只看视频
     * @return
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            "<if test=\"tags != null and tags != ''\">"+
            " and tags in" +
            "<foreach collection='tags' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            "<if test=\"searchType != 0\">"+
            " and sendType = 2" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<HomeBlog> findBlogListByTags(@Param("tags") String[] tags, @Param("searchType") int searchType);


}

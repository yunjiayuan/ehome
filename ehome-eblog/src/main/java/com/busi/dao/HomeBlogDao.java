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
    @Insert("insert into homeBlog(likeCount,lookCount,userId,title,content,contentTxt,imgUrl,videoUrl,videoCoverUrl,audioUrl,musicId,singer,songName,sendType,classify,classifyUserIds,tag,blogType,shareBlogId,shareUserId,origBlogId,origUserId,reprintContent,accessId,blogStatus,longitude,latitude,position,cityId,anonymousType,shareInfo,reward,firstPayUserId,solve,time) " +
            "values (#{likeCount},#{lookCount},#{userId},#{title},#{content},#{contentTxt},#{imgUrl},#{videoUrl},#{videoCoverUrl},#{audioUrl},#{musicId},#{singer},#{songName},#{sendType},#{classify},#{classifyUserIds},#{tag},#{blogType},#{shareBlogId},#{shareUserId},#{origBlogId},#{origUserId},#{reprintContent},#{accessId},#{blogStatus},#{longitude},#{latitude},#{position},#{cityId},#{anonymousType},#{shareInfo},#{reward},#{firstPayUserId},#{solve},#{time})")
    @Options(useGeneratedKeys = true)
    int add(HomeBlog homeBlog);

    /***
     * 更新生活圈评论数、点赞数、浏览量、转发量
     * @param homeBlog
     * @return
     */
    @Update("<script>" +
            "update homeBlog set" +
            " shareCount=#{shareCount}," +
            " likeCount=#{likeCount}," +
            " lookCount=#{lookCount}," +
            " commentCount=#{commentCount}" +
            " where id=#{id}" +
            "</script>")
    @Options(useGeneratedKeys = true)
    int updateBlog(HomeBlog homeBlog);
    /***
     * 更新生活圈稿费级别
     * @param homeBlog
     * @return
     */
    @Update("<script>" +
            "update homeBlog set" +
            " remunerationStatus=#{remunerationStatus}," +
            " where id=#{id}" +
            "</script>")
    @Options(useGeneratedKeys = true)
    int updateGradeBlog(HomeBlog homeBlog);

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param id      生活圈ID
     * @param userId  未特殊处理的登录者用户ID 用于判断可见范围
     * @param userIds 处理过的登录者用户ID 用于判断可见范围
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            " and (( classify = 2 and locate(#{userIds},classifyUserIds)>0) or (classify = 3 and locate(#{userIds},classifyUserIds)=0 ) or classify = 0 or userId=#{userId} )" +
            " and id = #{id}" +
            "</script>")
    HomeBlog findBlogInfo(@Param("id") long id, @Param("userId") long userId, @Param("userIds") String userIds);

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param id      生活圈ID
     * @param userId  未特殊处理的登录者用户ID 用于判断可见范围
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            " and id = #{id} and userId = #{userId}" +
            "</script>")
    HomeBlog findInfo(@Param("id") long id, @Param("userId") long userId);

    /***
     * 删除指定生活圈接口(只更新状态)
     * @param userId 生活圈发布者用户ID
     * @param id     将要被删除的生活圈
     * @return
     */
    @Update("update homeBlog set blogStatus = 1 where id = #{id} and userId = #{userId}")
    int delBlog(@Param("id") long id, @Param("userId") long userId);

    /***
     * 查询朋友圈列表
     * @param firendUserIds  好友用户ID组合
     * @param searchType     博文类型：0所有 1只看生活秀视频  2只看今日现场  3只看娱乐圈
     * @param timeType       查询时间类型：0不限制 1只看今天发布视频
     * @param userIds        处理过的登录者用户ID 用于判断可见范围
     * @return
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            "<if test=\"userIds != null\">" +
            " and userId in" +
            "<foreach collection='firendUserIds' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            " and (( classify = 2 and locate(#{userIds},classifyUserIds)>0) or (classify = 3 and locate(#{userIds},classifyUserIds)=0 ) or classify = 0  or userId=#{userId} )" +
//            "<if test=\"searchType == 0\">" +
//                " and tag != 42" +
//                " and tag != 43" +
//            "</if>" +
            "<if test=\"searchType == 1\">" +
                " and sendType = 2" +
                " and blogType != 1" +
//                " and tag != 42" +
//                " and tag != 43" +
            "</if>" +
            "<if test=\"searchType == 2\">" +
                " and sendType = 2" +
                " and blogType != 1" +
                " and tag = 40" +
            "</if>" +
            "<if test=\"searchType == 3\">" +
                " and sendType = 2" +
                " and blogType != 1" +
                " and tag = 39" +
            "</if>" +
            "<if test=\"timeType == 1\">" +
            " and to_days(time) = to_days(now())" +
            "</if>" +
            " and blogStatus = 0" +
            " order by time desc" +
            "</script>")
    List<HomeBlog> findBlogListByFirend(@Param("firendUserIds") String[] firendUserIds, @Param("userId") long userId, @Param("userIds") String userIds ,@Param("searchType") int searchType,@Param("timeType") int timeType);

    /***
     * 根据兴趣标签查询列表
     * @param tags       标签数组格式 1,2,3
     * @param searchType 博文类型：0所有 1只看视频
     * @param userId     当前登录用户ID
     * @param userIds    处理过的登录者用户ID 用于判断可见范围
     * @return
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            "<if test=\"tags != null\">" +
            " and tag in" +
            "<foreach collection='tags' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            " and (( classify = 2 and locate(#{userIds},classifyUserIds)>0) or (classify = 3 and locate(#{userIds},classifyUserIds)=0 ) or classify = 0 or userId=#{userId} )" +
            "<if test=\"searchType != 0\">" +
            " and sendType = 2" +
            " and blogType != 1" +
            "</if>" +
            " and blogStatus = 0" +
            " order by time desc" +
            "</script>")
    List<HomeBlog> findBlogListByTags(@Param("tags") String[] tags, @Param("searchType") int searchType, @Param("userId") long userId, @Param("userIds") String userIds);

    /***
     * 根据指定用户ID查询列表
     * @param searchType 博文类型：0查自己 1查别人
     * @param sendType   博文类型：0所有 1只看生活秀视频  2只看今日现场  3只看娱乐圈
     * @param userId     被查询用户ID
     * @param userIds    处理过的登录者用户ID 用于判断可见范围
     * @return
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            "<if test=\"searchType != 0\">" +
            " and (( classify = 2 and locate(#{userIds},classifyUserIds)>0) or (classify = 3 and locate(#{userIds},classifyUserIds)=0 ) or classify = 0)" +
            "</if>" +
//            "<if test=\"sendType == 0\">" +
//                " and tag != 42" +
//                " and tag != 43" +
//            "</if>" +
            "<if test=\"sendType == 1\">" +
                " and sendType = 2" +
                " and blogType != 1" +
//                " and tag != 42" +
//                " and tag != 43" +
            "</if>" +
            "<if test=\"sendType == 2\">" +
                " and sendType = 2" +
                " and blogType != 1" +
                " and tag = 40" +
            "</if>" +
            "<if test=\"sendType == 3\">" +
                " and sendType = 2" +
                " and blogType != 1" +
                " and tag = 39" +
            "</if>" +
            " and userId = #{userId}" +
            " and blogStatus = 0" +
            " order by time desc" +
            "</script>")
    List<HomeBlog> findBlogListByUserId(@Param("userId") long userId, @Param("userIds") String userIds, @Param("searchType") int searchType ,@Param("sendType") int sendType);

    /***
     * 根据城市ID查询 同城生活秀
     * @param cityId 博文类型：0查自己 1查别人
     * @param userId 当前用户ID
     * @param searchType     查询类型：0所有 1只看生活秀视频  2只看今日现场  3只看娱乐圈
     * @return
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            " and (( classify = 2 and locate(#{userIds},classifyUserIds)>0) or (classify = 3 and locate(#{userIds},classifyUserIds)=0 ) or classify = 0)" +
            " and userId != #{userId}" +
            " and cityId = #{cityId}" +
//            "<if test=\"searchType == 0\">" +
//                " and tag != 42" +
//                " and tag != 43" +
//            "</if>" +
            "<if test=\"searchType == 1\">" +
                " and sendType = 2" +
                " and blogType != 1" +
//                " and tag != 42" +
//                " and tag != 43" +
            "</if>" +
            "<if test=\"searchType == 2\">" +
                " and sendType = 2" +
                " and blogType != 1" +
                " and tag = 40" +
            "</if>" +
            "<if test=\"searchType == 3\">" +
                " and sendType = 2" +
                " and blogType != 1" +
                " and tag = 39" +
            "</if>" +
            " and blogStatus = 0" +
            " order by time desc" +
            "</script>")
    List<HomeBlog> findBlogListByCityId(@Param("userId") long userId, @Param("userIds") String userIds, @Param("cityId") int cityId,@Param("searchType") int searchType);

    /***
     * 查询点赞数够级别的生活秀列表
     * @param likeCount 赞数
     * @return
     */
    @Select("<script>" +
            "select * from homeBlog" +
            " where 1=1" +
            " and likeCount > #{likeCount}" +
            " order by time desc" +
            "</script>")
    List<HomeBlog> findBlogListBylikeCount(@Param("likeCount") long likeCount);

}

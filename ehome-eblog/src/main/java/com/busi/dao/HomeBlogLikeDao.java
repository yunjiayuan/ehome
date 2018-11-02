package com.busi.dao;

import com.busi.entity.HomeBlogLike;
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
public interface HomeBlogLikeDao {

    /***
     * 新增点赞
     * @param homeBlogLike
     * @return
     */
    @Insert("insert into homeBlogLike(userId,blogId,time) " +
            "values (#{userId},#{blogId},#{time})")
    @Options(useGeneratedKeys = true)
    int addHomeBlogLike(HomeBlogLike homeBlogLike);

    /***
     * 删除点赞接口
     * @param userId 将要删除的点赞用户ID
     * @param blogId 将要操作的生活圈ID
     * @return
     */
    @Delete("delete from homeBlogLike  where userId = #{userId} and blogId = #{blogId}")
    int delHomeBlogLike(@Param("userId") long userId, @Param("blogId") long blogId);

    /***
     * 条件查询点赞列表接口
     * @param blogId     将要操作的生活圈ID
     * @return
     */
    @Select("<script>" +
            "select * from homeBlogLike" +
            " where 1=1" +
            " and blogId = #{blogId}" +
            " order by time desc" +
            "</script>")
    List<HomeBlogLike> findHomeBlogLikeList( @Param("blogId") long blogId);

    /***
     * 验证指定用户对指定生活圈是否点过赞
     * @param userId  用户ID
     * @param blogId  将要操作的生活圈ID
     * @return
     */
    @Select("<script>" +
            "select * from homeBlogLike" +
            " where 1=1" +
            " and blogId = #{blogId}" +
            " and userId = #{userId}" +
            "</script>")
    HomeBlogLike checkHomeBlogLike( @Param("userId") long userId,@Param("blogId") long blogId);


}

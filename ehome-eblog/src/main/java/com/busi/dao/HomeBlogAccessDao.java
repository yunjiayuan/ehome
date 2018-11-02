package com.busi.dao;

import com.busi.entity.HomeBlogAccess;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 生活圈标签Dao
 * author：zhaojiajie
 * create time：2018-11-1 17:07:13
 */
@Mapper
@Repository
public interface HomeBlogAccessDao {

    /***
     * 新增
     * @param homeBlogAccess
     * @return
     */
    @Insert("insert into HomeBlogAccess(userId,tagName,time,users) " +
            "values (#{userId},#{tagName},#{time},#{users})")
    @Options(useGeneratedKeys = true)
    int add(HomeBlogAccess homeBlogAccess);

    /***
     * 更新
     * @param homeBlogAccess
     * @return
     */
    @Update("<script>" +
            "update homeBlogAccess set" +
            "<if test=\"tagName != null and tagName != ''\">" +
            " tagName=#{tagName}," +
            "</if>" +
            "<if test=\"users != null and users != ''\">" +
            " users=#{users}," +
            "</if>" +
            " userId=#{userId}" +
            " where id=#{id}" +
            "</script>")
    int update(HomeBlogAccess homeBlogAccess);

    /***
     * 删除
     * @param id
     * @return
     */
    @Delete("delete from homeBlogAccess where id=#{id} and userId=#{userId}")
    int del(@Param("id") long id, @Param("userId") long userId);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from HomeBlogAccess where id = #{id}")
    HomeBlogAccess find(@Param("id") long id);

    /***
     * 查询标签列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from HomeBlogAccess" +
            " where 1=1" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<HomeBlogAccess> findList(@Param("userId") long userId);

    /***
     * 统计该用户标签数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from HomeBlogAccess" +
            " where userId=#{userId}" +
            "</script>")
    int findNum(@Param("userId") long userId);

}

package com.busi.dao;

import com.busi.entity.HomeBlogUserTag;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 生活圈兴趣标签Dao
 * author：zhaojiajie
 * create time：2018-11-2 17:23:08
 */
@Mapper
@Repository
public interface HomeBlogTagDao {

    /***
     * 新增
     * @param homeBlogUserTag
     * @return
     */
    @Insert("insert into homeBlogUserTag(userId,tags) " +
            "values (#{userId},#{tags})")
    @Options(useGeneratedKeys = true)
    int add(HomeBlogUserTag homeBlogUserTag);

    /***
     * 更新
     * @param homeBlogUserTag
     * @return
     */
    @Update("<script>" +
            "update homeBlogUserTag set" +
            "<if test=\"tags != null and tags != ''\">" +
            " tags=#{tags}," +
            "</if>" +
            " userId=#{userId}" +
            " where userId=#{userId}" +
            "</script>")
    int update(HomeBlogUserTag homeBlogUserTag);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from homeBlogUserTag where userId = #{userId}")
    HomeBlogUserTag find(@Param("userId") long userId);
}

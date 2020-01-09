package com.busi.dao;

import com.busi.entity.HomeBlogTag;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 生活圈兴趣标签Dao
 * author：suntj
 * create time：2020-1-8 17:14:20
 */
@Mapper
@Repository
public interface BlogTagDao {

    /***
     * 新增
     * @param homeBlogTag
     * @return
     */
    @Insert("insert into homeBlogTag(tagValue,tagName,status) " +
            "values (#{tagValue},#{tagName},#{status})")
    @Options(useGeneratedKeys = true)
    int add(HomeBlogTag homeBlogTag);

    /***
     * 更新
     * @param homeBlogTag
     * @return
     */
    @Update("<script>" +
            "update homeBlogTag set" +
            " tagName=#{tagName}," +
            " status=#{status}" +
            " where tagValue=#{tagValue}" +
            "</script>")
    int update(HomeBlogTag homeBlogTag);

    /***
     * 查询标签列表
     */
    @Select("select * from homeBlogTag ORDER BY orderType ASC")
    List<HomeBlogTag> findList();
}

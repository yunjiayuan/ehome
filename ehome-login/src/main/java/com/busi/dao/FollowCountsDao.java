package com.busi.dao;

import com.busi.entity.FollowCounts;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 粉丝数统计DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface FollowCountsDao {

    /***
     * 新增
     * @param followCounts
     * @return
     */
    @Insert("insert into followCounts(userId,counts) " +
            "values (#{userId},#{counts})")
    @Options(useGeneratedKeys = true)
    int add(FollowCounts followCounts);


    /***
     * 更新
     * @param followCounts
     * @return
     */
    @Update("update followCounts set counts=#{counts} where userId = #{userId}")
    int update(FollowCounts followCounts);

    /***
     * 查询粉丝数
     * @param userId  被查询用户ID
     */
    @Select("select * from followCounts where userId = #{userId}")
    FollowCounts findFollowCounts(@Param("userId") long userId);


}

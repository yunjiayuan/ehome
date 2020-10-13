package com.busi.dao;

import com.busi.entity.VisitView;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 访问量信息DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface VisitViewDao {

    /***
     * 新增访问量信息
     * @param visitView
     * @return
     */
    @Insert("insert into visitView(userId,todayVisitCount,totalVisitCount) values (#{userId},#{todayVisitCount},#{totalVisitCount})")
    @Options(useGeneratedKeys = true)
    int add(VisitView visitView);

    /***
     * 更新访问量信息
     * @param visitView
     * @return
     */
    @Update(("update visitView set todayVisitCount=#{todayVisitCount},totalVisitCount=#{totalVisitCount} where userId=#{userId}"))
    int update(VisitView visitView);

    /***
     * 查询访问量信息
     * @param userId
     * @return
     */
    @Select(("select * from visitView where userId=#{userId}"))
    VisitView findVisitView(@Param("userId") long userId);

}

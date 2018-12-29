package com.busi.dao;

import com.busi.entity.UserJurisdiction;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 用户权限相关（设置功能中的权限设置 包括房间锁和被访问权限）DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserJurisdictionDao {

    /***
     * 新增
     * @param userJurisdiction
     * @return
     */
    @Insert("insert into UserJurisdiction(userId,garden,livingRoom,homeStore,storageRoom,accessRights,switchLamp) values (#{userId},#{garden},#{livingRoom},#{homeStore},#{storageRoom},#{accessRights},#{switchLamp})")
    @Options(useGeneratedKeys = true)
    int add(UserJurisdiction userJurisdiction);

    /***
     * 更新
     * @param userJurisdiction
     * @return
     */
    @Update("<script>" +
            "update UserJurisdiction set"+
            "<if test=\"garden != 0\">"+
            " garden=#{garden}," +
            "</if>" +
            "<if test=\"livingRoom != 0\">"+
            " livingRoom=#{livingRoom}," +
            "</if>" +
            "<if test=\"homeStore != 0\">"+
            " homeStore=#{homeStore}," +
            "</if>" +
            "<if test=\"storageRoom != 0\">"+
            " storageRoom=#{storageRoom}," +
            "</if>" +
            "<if test=\"accessRights != 0\">"+
            " accessRights=#{accessRights}," +
            "</if>" +
            "<if test=\"switchLamp != 0\">"+
            " switchLamp=#{switchLamp}," +
            "</if>" +
            "<if test=\"userId != 0\">"+
            " userId=#{userId}" +
            "</if>" +
            " where userId=#{userId}"+
            "</script>")
    int update(UserJurisdiction userJurisdiction);

    /***
     * 查询
     * @param userId
     * @return
     */
    @Select(("select * from UserJurisdiction where userId=#{userId}"))
    UserJurisdiction findUserJurisdiction(@Param("userId") long userId);

}

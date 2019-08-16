package com.busi.dao;

import com.busi.entity.UserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserInfoDao {

    /***
     * 条件查找用户信息
     * @return
     */
    @Select("select * from userInfo where (userId > 10000 and 13870 > userId ) or (userId > 53870 ) ")
    List<UserInfo> findCondition();

    /***
     * 根据用户ID查询用户信息
     * @param userId
     */
    @Select("select * from userInfo where userId = #{userId}")
    UserInfo findUserInfo(@Param("userId") long userId);
}

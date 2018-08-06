package com.busi.dao;

import com.busi.entity.Test;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Test DAO
 * author：zhaojiajie
 * create time：2018-7-24 16:57:05
 */
@Mapper
@Repository
public interface TestDao {
    /***
     * 新增用户
     * @param test
     * @return
     */
    @Insert("insert into Test(name,userId,time) values (#{name},#{userId},#{time})")
    @Options(useGeneratedKeys = true)
    int add(Test test);

    /***
     * 删除
     * @param id
     * @return
     */
    @Delete(("delete from Test where id=#{id}"))
    int del(@Param("id") long id);

    /***
     * 更新
     * @param test
     * @return
     */
    @Update(("update Test set name=#{name} where id=#{id}" ))
    int update(Test test);

    /***
     * 根据userId查询用户信息
     * @param id
     */
    @Select("select * from Test where id = #{id}")
    Test findUserById(@Param("id") long id);

    /***
     * 查询列表 默认按时间降序排序
     * @param userId
     * @return
     */
    @Select("select * from Test where userId=#{userId} order by time")
    List<Test> findList(@Param("userId") long userId);

}

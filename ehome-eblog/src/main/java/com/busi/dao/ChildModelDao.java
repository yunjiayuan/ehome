package com.busi.dao;

import com.busi.entity.ChildModelPwd;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 儿童锁
 * author：zhaojiajie
 * create time：2020-06-16 23:30:11
 */
@Mapper
@Repository
public interface ChildModelDao {

    /***
     * 新增密码
     * @param homeAlbumPwd
     * @return
     */
    @Insert("insert into ChildModelPwd(password,status,userId) " +
            "values (#{password},#{status},#{userId})")
    @Options(useGeneratedKeys = true)
    int addPwd(ChildModelPwd homeAlbumPwd);

    /***
     * 更新密码
     * @param homeAlbumPwd
     * @return
     */
    @Update("<script>" +
            "update ChildModelPwd set" +
            "<if test=\"password != null and password != ''\">" +
            " password=#{password}," +
            "</if>" +
            "<if test=\"status != null and status != ''\">" +
            " status=#{status}," +
            "</if>" +
            " id=#{id}" +
            " where id=#{id}" +
            "</script>")
    int updatePwd(ChildModelPwd homeAlbumPwd);

    /***
     * 根据ID查询用户密码
     * @param userId
     */
    @Select("select * from ChildModelPwd where userId = #{userId}")
    ChildModelPwd findById(@Param("userId") long userId);

    /***
     * 删除密码
     * @param userId
     * @return
     */
    @Delete("<script>" +
            "delete from ChildModelPwd" +
            " where userId =#{userId}" +
            "</script>")
    int delPwd(@Param("userId") long userId);

}

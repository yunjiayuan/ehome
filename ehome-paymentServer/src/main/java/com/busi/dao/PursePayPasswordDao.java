package com.busi.dao;

import com.busi.entity.PursePayPassword;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 支付密码相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface PursePayPasswordDao {

    /***
     * 新增
     * @param pursePayPassword
     * @return
     */
    @Insert("insert into pursePayPassword (userId,payPassword,payCode) values (#{userId},#{payPassword},#{payCode})")
    @Options(useGeneratedKeys = true)
    int addPursePayPassword(PursePayPassword pursePayPassword);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from pursePayPassword where userId = #{userId}")
    PursePayPassword findPursePayPassword(@Param("userId") long userId);

    /***
     * 更新
     * @param pursePayPassword
     * @return
     */
    @Update("<script>" +
            "update pursePayPassword set"+
            " payPassword=#{payPassword}" +
            " payCode=#{payCode}" +
            " where userId=#{userId}"+
            "</script>")
    int updatePursePayPassword(PursePayPassword pursePayPassword);
}

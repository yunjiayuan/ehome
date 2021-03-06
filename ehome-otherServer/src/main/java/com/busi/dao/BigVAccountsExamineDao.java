package com.busi.dao;

import com.busi.entity.BigVAccountsExamine;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 大V审核
 * author：zhaojiajie
 * create time：2020-07-07 16:03:44
 */
@Mapper
@Repository
public interface BigVAccountsExamineDao {

    /***
     * 新增
     * @param homeAlbumPwd
     * @return
     */
    @Insert("insert into BigVAccountsExamine(idPositive,userId,idBack,opinion,time,state,sort) " +
            "values (#{idPositive},#{userId},#{idBack},#{opinion},#{time},#{state},#{sort})")
    @Options(useGeneratedKeys = true)
    int add(BigVAccountsExamine homeAlbumPwd);

    /***
     * 更新
     * @param homeAlbumPwd
     * @return
     */
    @Update("<script>" +
            "update BigVAccountsExamine set" +
            "<if test=\"idPositive != null and idPositive != ''\">" +
            " idPositive=#{idPositive}," +
            "</if>" +
            "<if test=\"idBack != null and idBack != ''\">" +
            " idBack=#{idBack}," +
            "</if>" +
            "<if test=\"opinion != null and opinion != ''\">" +
            " opinion=#{opinion}," +
            "</if>" +
            "<if test=\"time != null\">" +
            " time=#{time}," +
            "</if>" +
            " state=#{state}," +
            " sort=#{sort}" +
            " where id=#{id}" +
            "</script>")
    int changeAppealState(BigVAccountsExamine homeAlbumPwd);

    /***
     * 根据用户ID查询
     * @param userId
     */
    @Select("select * from BigVAccountsExamine where userId = #{userId}")
    BigVAccountsExamine findById(@Param("userId") long userId);

    /***
     * 查询列表
     * @return
     */
    @Select("<script>" +
            "select * from BigVAccountsExamine" +
            " where 1=1" +
            " and state = 0" +
            " order by time desc" +
            "</script>")
    List<BigVAccountsExamine> findChildAppealList();
}

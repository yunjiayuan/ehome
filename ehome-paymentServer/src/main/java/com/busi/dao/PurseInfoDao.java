package com.busi.dao;

import com.busi.entity.Purse;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 钱包信息相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface PurseInfoDao {

    /***
     * 新增
     * @param purse
     * @return
     */
    @Insert("insert into purse (userId,homeCoin,homePoint,spareMoney,time) values (#{userId},#{homeCoin},#{homePoint},#{spareMoney},#{time})")
    @Options(useGeneratedKeys = true)
    int addPurseInfo(Purse purse);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from purse where userId = #{userId}")
    Purse findPurseInfo(@Param("userId") long userId);

    /***
     * 更新
     * @param purse
     * @return
     */
    @Update("<script>" +
            "update purse set"+
            "<if test=\"homeCoin != 0 \">"+
            " homeCoin=#{homeCoin}," +
            "</if>" +
            "<if test=\"homePoint != 0 \">"+
            " homePoint=#{homePoint}," +
            "</if>" +
            "<if test=\"spareMoney != 0 \">"+
            " spareMoney=#{spareMoney}," +
            "</if>" +
            " userId=#{userId}" +
            " where userId=#{userId}"+
            "</script>")
    int updatePurseInfo(Purse purse);
}

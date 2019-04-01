package com.busi.dao;

import com.busi.entity.GoodNumber;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预售靓号Dao
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
@Mapper
@Repository
public interface GoodNumberDao {

    /***
     * 新增
     * @param goodNumber
     * @return
     */
    @Insert("insert into goodNumber(proId,house_number,theme,label,numberDigit,goodNumberPrice,status) " +
            "values (#{proId},#{house_number},#{theme},#{label},#{numberDigit},#{goodNumberPrice},#{status})")
    @Options(useGeneratedKeys = true)
    int add(GoodNumber goodNumber);

    /***
     * 更新靓号状态
     * @param goodNumber
     * @return
     */
    @Update("<script>" +
            "update goodNumber set" +
            " status=1" +
            " where proId=#{proId} and house_number=#{house_number}" +
            "</script>")
    int update(GoodNumber goodNumber);

    /**
     * 根据省简称ID+门牌号查询 预售靓号是否存在
     * @param proId
     * @param house_number
     * @return
     */
    @Select("select * from goodNumber where proId = #{proId} and house_number= #{house_number} and status=0")
    GoodNumber findGoodNumberInfo(@Param("proId") int proId,@Param("house_number") long house_number);

    /**
     * 根据省简称ID+门牌号查询 预售靓号是否存在
     * @param proId
     * @param house_number
     * @return
     */
    @Select("<script>" +
            "select * from goodNumber" +
            " where 1=1" +
            "<if test=\"proId != -1\">" +
            " and proId=#{proId}" +
            "</if>" +
            " and house_number like CONCAT('%',#{house_number},'%')" +
            " and status=0" +
            "</script>")
    List<GoodNumber> findGoodNumberListByNumber(@Param("proId") int proId,@Param("house_number") long house_number);


    /***
     * 条件查询预售靓号列表
     * @param proId       省简称ID 默认-1不限
     * @param theme       主题ID 默认-1不限
     * @param label       数字规则ID 默认null不限
     * @param numberDigit 靓号位数ID 默认-1不限 (例如7表示7位)
     * @param orderType   省简称ID 默认-1不限
     * @return
     */
    @Select("<script>" +
            "select * from goodNumber" +
            " where 1=1" +
            "<if test=\"proId != -1\">" +
            " and proId=#{proId}" +
            "</if>" +
            "<if test=\"theme != -1\">" +
            " and theme=#{theme}" +
            "</if>" +
            "<if test=\"label != null and label != '' and label != 'null'\">" +
            " and label like CONCAT('%#',#{label},'#%')" +
            "</if>" +
            "<if test=\"numberDigit != -1\">" +
            " and numberDigit=#{numberDigit}" +
            "</if>" +
            " and status=0" +
            "<if test=\"orderType == 1\">" +
            " order by goodNumberPrice desc" +
            "</if>" +
            "<if test=\"orderType == 2\">" +
            " order by goodNumberPrice asc" +
            "</if>" +
            "</script>")
    List<GoodNumber> findList(@Param("proId") int proId, @Param("theme") int theme, @Param("label") String label, @Param("numberDigit") int numberDigit, @Param("orderType") int orderType);
}

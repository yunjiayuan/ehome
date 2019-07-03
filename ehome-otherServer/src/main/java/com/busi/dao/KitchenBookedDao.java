package com.busi.dao;

import com.busi.entity.KitchenBooked;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @program: ehome
 * @description: 厨房订座设置
 * @author: ZHaoJiaJie
 * @create: 2019-06-27 10:09
 */
@Mapper
@Repository
public interface KitchenBookedDao {

    /***
     * 新增厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Insert("insert into KitchenBooked(userId,kitchenId,roomsTotal,looseTableTotal,leastNumber,mostNumber,earliestTime,reserveDays,latestTime,looseTable,privateRoom)" +
            "values (#{userId},#{kitchenId},#{roomsTotal},#{looseTableTotal},#{leastNumber},#{mostNumber},#{earliestTime},#{reserveDays},#{latestTime},#{looseTable},#{privateRoom})")
    @Options(useGeneratedKeys = true)
    int add(KitchenBooked kitchenBooked);

    /***
     * 更新厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update KitchenBooked set" +
            " roomsTotal=#{roomsTotal}," +
            " looseTableTotal=#{looseTableTotal}," +
            " leastNumber=#{leastNumber}," +
            " mostNumber=#{mostNumber}," +
            " earliestTime=#{earliestTime}," +
            " latestTime=#{latestTime}," +
            " reserveDays=#{reserveDays}," +
            " looseTable=#{looseTable}," +
            " privateRoom=#{privateRoom}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBooked(KitchenBooked kitchenBooked);

    /***
     * 更新厨房订座剩余数量
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update KitchenBooked set" +
            " roomsTotal=#{roomsTotal}," +
            " looseTableTotal=#{looseTableTotal}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updatePosition(KitchenBooked kitchenBooked);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from KitchenBooked where userId=#{userId}")
    KitchenBooked findByUserId(@Param("userId") long userId);

}

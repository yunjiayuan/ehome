package com.busi.dao;

import com.busi.entity.ShippingAddress;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收货地址Dao
 * author：zhaojiajie
 * create time：2018-9-20 16:23:57
 */
@Mapper
@Repository
public interface ShippingAddressDao {

    /***
     * 新增
     * @param shippingAddress
     * @return
     */
    @Insert("insert into shippingAddress(userId,contactsName,contactsPhone,lat,lon,postalcode,address,addressState,addTime," +
            "refreshTime,defaultAddress) " +
            "values (#{userId},#{contactsName},#{contactsPhone},#{lat},#{lon},#{postalcode},#{address},#{addressState},#{addTime}," +
            "#{refreshTime},#{defaultAddress})")
    @Options(useGeneratedKeys = true)
    int add(ShippingAddress shippingAddress);

    /***
     * 更新
     * @param shippingAddress
     * @return
     */
    @Update("<script>" +
            "update shippingAddress set" +
            "<if test=\"contactsName != null and contactsName != ''\">" +
            " contactsName=#{contactsName}," +
            "</if>" +
            "<if test=\"contactsPhone != null and contactsPhone != ''\">" +
            " contactsPhone=#{contactsPhone}," +
            "</if>" +
            "<if test=\"postalcode != null and postalcode != ''\">" +
            " postalcode=#{postalcode}," +
            "</if>" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " address=#{address}," +
            " defaultAddress=#{defaultAddress}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(ShippingAddress shippingAddress);

    /***
     * 更新默认地址状态
     * @param shippingAddress
     * @return
     */
    @Update("<script>" +
            "update shippingAddress set" +
            " defaultAddress=#{defaultAddress}" +
            " where id=#{id} and userId=#{userId} and addressState=0" +
            "</script>")
    int updateDefault(ShippingAddress shippingAddress);

    /***
     * 更新删除状态
     * @param shippingAddress
     * @return
     */
    @Update("<script>" +
            "update shippingAddress set" +
            " addressState=#{addressState}" +
            " where id=#{id} and userId=#{userId} and addressState=0" +
            "</script>")
    int updateDel(ShippingAddress shippingAddress);

    /***
     * 统计该用户地址数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from shippingAddress" +
            " where userId=#{userId} and addressState=0" +
            "</script>")
    int findNum(@Param("userId") long userId);

    /***
     * 查询默认地址
     * @param userId
     */
    @Select("select * from ShippingAddress where userId=#{userId} and addressState=0 and defaultAddress=1")
    ShippingAddress findDefault(@Param("userId") long userId);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from ShippingAddress where id = #{id} and addressState=0")
    ShippingAddress findAppoint(@Param("id") long id);

    /***
     * 查询我的收货地址
     * @param userId
     */
    @Select("select * from ShippingAddress where userId = #{userId} and addressState=0")
    List<ShippingAddress> findList(@Param("userId") long userId);

    /***
     * 分页查询收货地址
     * @return
     */
    @Select("<script>" +
            "select * from ShippingAddress" +
            " where userId=#{userId}" +
            " and addressState=0" +
            " order by defaultAddress desc,refreshTime desc" +
            "</script>")
    List<ShippingAddress> findAoList(@Param("userId") long userId);

}

package com.busi.dao;

import com.busi.entity.HomeShopCenter;
import com.busi.entity.HomeShopPersonalData;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 店铺信息相关DAO
 * author：ZHJJ
 * create time：2019-5-13 11:05:12
 */
@Mapper
@Repository
public interface ShopCenterDao {

    /***
     * 新增家店
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into HomeShopCenter(userId,shopName,shopHead,videoUrl,videoCoverUrl,content,managementType,deleteType,addTime,province,city,district,address,sourceOfSupply,entityShop,warehouse," +
            "location,phone)" +
            "values (#{userId},#{shopName},#{shopHead},#{videoUrl},#{videoCoverUrl},#{content},#{managementType},#{deleteType},#{addTime},#{province},#{city},#{district},#{address},#{sourceOfSupply},#{entityShop},#{warehouse}" +
            ",#{location},#{phone})")
    @Options(useGeneratedKeys = true)
    int addHomeShop(HomeShopCenter homeShopCenter);

    /***
     * 更新家店
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update HomeShopCenter set" +
            " shopName=#{shopName}," +
            " shopHead=#{shopHead}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " content=#{content}," +
            " managementType=#{managementType}," +
            " province=#{province}," +
            " city=#{city}," +
            " district=#{district}," +
            " address=#{address}," +
            " phone=#{phone}," +
            " sourceOfSupply=#{sourceOfSupply}," +
            " warehouse=#{warehouse}," +
            " location=#{location}," +
            " entityShop=#{entityShop}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateHomeShop(HomeShopCenter homeShopCenter);

    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update HomeShopCenter set" +
            " shopState=#{shopState}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(HomeShopCenter homeShopCenter);

    /***
     * 新增个人信息
     * @param homeShopPersonalData
     * @return
     */
    @Insert("insert into HomeShopPersonalData(userId,realName,idCard,idCardExpireTime,phone,holdIDCardUrl,halfBodyUrl,addTime,province,city,district,address,positiveIDCardUrl,backIDCardUrl,localNewsUrl," +
            "acState)" +
            "values (#{userId},#{realName},#{idCard},#{idCardExpireTime},#{phone},#{holdIDCardUrl},#{halfBodyUrl},#{addTime},#{province},#{city},#{district},#{address},#{positiveIDCardUrl},#{backIDCardUrl},#{localNewsUrl}" +
            ",#{acState})")
    @Options(useGeneratedKeys = true)
    int addPersonalData(HomeShopPersonalData homeShopPersonalData);

    /***
     * 更新个人信息
     * @param homeShopPersonalData
     * @return
     */
    @Update("<script>" +
            "update HomeShopPersonalData set" +
            " realName=#{realName}," +
            " idCard=#{idCard}," +
            " idCardExpireTime=#{idCardExpireTime}," +
            " phone=#{phone}," +
            " holdIDCardUrl=#{holdIDCardUrl}," +
            " halfBodyUrl=#{halfBodyUrl}," +
            " province=#{province}," +
            " city=#{city}," +
            " district=#{district}," +
            " address=#{address}," +
            " positiveIDCardUrl=#{positiveIDCardUrl}," +
            " backIDCardUrl=#{backIDCardUrl}," +
            " localNewsUrl=#{localNewsUrl}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updPersonalData(HomeShopPersonalData homeShopPersonalData);

    /***
     * 根据用户ID查询店铺状态
     * @param userId
     * @return
     */
    @Select("select * from HomeShopCenter where userId=#{userId} and deleteType=0")
    HomeShopCenter findByUserId(@Param("userId") long userId);

    /***
     * 根据用户ID查询个人信息
     * @param userId
     * @return
     */
    @Select("select * from HomeShopPersonalData where userId=#{userId}")
    HomeShopPersonalData findPersonalData(@Param("userId") long userId);
}

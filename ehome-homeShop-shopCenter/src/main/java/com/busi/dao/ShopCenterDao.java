package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    //调整商品分类（临时调用）
//    @Update("<script>" +
//            "update GoodsCategory set" +
//            " levelOne=#{levelOne}," +
//            " levelTwo=#{levelTwo}," +
//            " levelThree=#{levelThree}," +
//            " levelFour=#{levelFour}," +
//            " levelFive=#{levelFive}" +
//            " where id=#{id}" +
//            "</script>")
//    int updateBusiness(GoodsCategory category);


    /***
     * 新增个人信息
     * @param homeShopPersonalData
     * @return
     */
    @Insert("insert into HomeShopPersonalData(userId,realName,idCard,idCardExpireTime,phone,holdIDCardUrl,halfBodyUrl,addTime,province,city,district,address,positiveIDCardUrl,backIDCardUrl,localNewsUrl," +
            "acState,idCardType)" +
            "values (#{userId},#{realName},#{idCard},#{idCardExpireTime},#{phone},#{holdIDCardUrl},#{halfBodyUrl},#{addTime},#{province},#{city},#{district},#{address},#{positiveIDCardUrl},#{backIDCardUrl},#{localNewsUrl}" +
            ",#{acState},#{idCardType})")
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
            " idCardType=#{idCardType}," +
            "<if test=\"idCardType !=1\">" +
            " idCardExpireTime=#{idCardExpireTime}," +
            "</if>" +
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

    //调整商品分类（临时调用）
    @Select("select * from GoodsCategory")
    List<GoodsCategory> findByUserId1();

    /***
     * 根据用户ID查询个人信息
     * @param userId
     * @return
     */
    @Select("select * from HomeShopPersonalData where userId=#{userId}")
    HomeShopPersonalData findPersonalData(@Param("userId") long userId);

    /***
     * 查询商品分类
     * @param levelOne 商品1级分类  默认为0, -2为不限:0图书、音像、电子书刊  1手机、数码  2家用电器  3家居家装  4电脑、办公  5厨具  6个护化妆  7服饰内衣  8钟表  9鞋靴  10母婴  11礼品箱包  12食品饮料、保健食品  13珠宝  14汽车用品  15运动健康  16玩具乐器  17彩票、旅行、充值、票务
     * @param levelTwo 商品2级分类  默认为0, -2为不限
     * @param levelThree 商品3级分类  默认为0, -2为不限
     * @param levelFour 商品4级分类  默认为0, -2为不限
     * @param levelFive 商品5级分类  默认为0, -2为不限
     * @return
     */
    @Select("<script>" +
            "select * from GoodsCategory" +
            " where 1=1" +
//            "<if test=\"levelOne >= 0 \">" +
//            " and levelTwo > -1" +
//            " and levelOne = #{levelOne}" +
//            "</if>" +
//            "<if test=\"levelOne == -2 \">" +
//            " and levelOne > -1" +
//            " and levelTwo = -1" +
//            "</if>" +
//            "<if test=\"levelTwo >= 0 \">" +
//            " and levelThree > -1" +
//            " and levelTwo = #{levelTwo}" +
//            "</if>" +
//            "<if test=\"levelTwo == -2 \">" +
//            " and levelTwo > -1" +
//            " and levelThree = -1" +
//            "</if>" +
//            "<if test=\"levelThree >= 0 \">" +
//            " and levelFour > -1" +
//            " and levelThree = #{levelThree}" +
//            "</if>" +
//            "<if test=\"levelThree == -2 \">" +
//            " and levelThree > -1" +
//            " and levelFour = -1" +
//            "</if>" +
//            "<if test=\"levelFour >= 0 \">" +
//            " and levelFive > -1" +
//            " and levelFour = #{levelFour}" +
//            "</if>" +
//            "<if test=\"levelFour == -2 \">" +
//            " and levelFour > -1" +
//            " and levelFive = -1" +
//            "</if>" +
//            "<if test=\"levelFive == -2 \">" +
//            " and levelFive > -1" +
//            "</if>" +
//            "<if test=\"levelFive >= 0 \">" +
//            " and levelFive = #{levelFive}" +
//            "</if>" +

            "<if test=\"levelOne == -2\">" +
            " and levelOne > -1" +
            " and levelTwo = -1" +
            " and levelThree = -1" +
            " and levelFour = -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelOne >= 0 \">" +
            " and levelOne = #{levelOne}" +
            "<if test=\"levelTwo == -2 \">" +
            " and levelTwo > -1" +
            " and levelThree = -1" +
            " and levelFour = -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelTwo >= 0 \">" +
            " and levelTwo = #{levelTwo}" +
            "<if test=\"levelThree == -2 \">" +
            " and levelThree > -1" +
            " and levelFour = -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelThree >= 0 \">" +
            " and levelThree = #{levelThree}" +
            "<if test=\"levelFour == -2 \">" +
            " and levelFour > -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelFour >= 0 \">" +
            " and levelFour = #{levelFour}" +
            "<if test=\"levelFive == -2 \">" +
            " and levelFive > -1" +
            "</if>" +
            "<if test=\"levelFive >= 0 \">" +
            " and levelFive = #{levelFive}" +
            "</if>" +
            "</if>" +
            "</if>" +
            "</if>" +
            "</if>" +
            "</script>")
    List<GoodsCategory> findList(@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree, @Param("levelFour") int levelFour, @Param("levelFive") int levelFive);

    /***
     * 新增分类
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into GoodsCategory(name,levelOne,levelTwo,levelThree,levelFour,levelFive,letter,logo,goodCategoryId)" +
            "values (#{name},#{levelOne},#{levelTwo},#{levelThree},#{levelFour},#{levelFive},#{letter},#{logo},#{goodCategoryId})")
    @Options(useGeneratedKeys = true)
    int addYHSort(GoodsCategory homeShopCenter);

    /***
     * 更新分类
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update GoodsCategory set" +
            "<if test=\"name != null and name != ''\">" +
            " name=#{name}," +
            "</if>" +
            " levelOne=#{levelOne}," +
            " levelTwo=#{levelTwo}," +
            " levelThree=#{levelThree}," +
            " levelFour=#{levelFour}," +
            " levelFive=#{levelFive}," +
            "<if test=\"letter != null and letter != ''\">" +
            " letter=#{letter}," +
            "</if>" +
            "<if test=\"logo != null and logo != ''\">" +
            " logo=#{logo}," +
            "</if>" +
            " goodCategoryId=#{goodCategoryId}" +
            " where id=#{id}" +
            "</script>")
    int changeYHSort(GoodsCategory homeShopCenter);


    /***
     * 删除分类
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from GoodsCategory" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delYHSort(@Param("ids") String[] ids);

    /***
     * 查询商品分类
     * @param levelOne 商品1级分类  默认为0, -2为不限:0图书、音像、电子书刊  1手机、数码  2家用电器  3家居家装  4电脑、办公  5厨具  6个护化妆  7服饰内衣  8钟表  9鞋靴  10母婴  11礼品箱包  12食品饮料、保健食品  13珠宝  14汽车用品  15运动健康  16玩具乐器  17彩票、旅行、充值、票务
     * @param levelTwo 商品2级分类  默认为0, -2为不限
     * @param levelThree 商品3级分类  默认为0, -2为不限
     * @param levelFour 商品4级分类  默认为0, -2为不限
     * @param levelFive 商品5级分类  默认为0, -2为不限
     * @return
     */
    @Select("<script>" +
            "select * from GoodsCategory" +
            " where 1=1" +
            "<if test=\"levelOne == -2 and levelTwo == -1\">" +
            " and levelOne > -1" +
            " and levelTwo = -1" +
            " and levelThree = -1" +
            " and levelFour = -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelOne >= 0 \">" +
            " and levelOne = #{levelOne}" +
            "<if test=\"levelTwo == -2 \">" +
            " and levelTwo > -1" +
            " and levelThree = -1" +
            " and levelFour = -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelTwo >= 0 \">" +
            " and levelTwo = #{levelTwo}" +
            "<if test=\"levelThree == -2 \">" +
            " and levelThree > -1" +
            " and levelFour = -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelThree >= -1 \">" +
            " and levelThree = #{levelThree}" +
            "<if test=\"levelFour == -2 \">" +
            " and levelFour > -1" +
            " and levelFive = -1" +
            "</if>" +
            "<if test=\"levelFour >= -1 \">" +
            " and levelFour = #{levelFour}" +
            "<if test=\"levelFive == -2 \">" +
            " and levelFive > -1" +
            "</if>" +
            "<if test=\"levelFive >= -1 \">" +
            " and levelFive = #{levelFive}" +
            "</if>" +
            "</if>" +
            "</if>" +
            "</if>" +
            "</if>" +
            "</script>")
    List<GoodsCategory> findList5(@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree, @Param("levelFour") int levelFour, @Param("levelFive") int levelFive);

    @Select("<script>" +
            "select * from GoodsCategory" +
            " where 1=1" +
            " and levelOne = #{levelOne}" +
            " and levelTwo = #{levelTwo}" +
            " and levelThree = #{levelThree}" +
            " and levelFour = #{levelFour}" +
            " and levelFive = #{levelFive}" +
            "</script>")
    GoodsCategory findList3(@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree, @Param("levelFour") int levelFour, @Param("levelFive") int levelFive);

    @Select("<script>" +
            "select * from GoodsCategory" +
            " where 1=1" +
            " and levelOne = #{levelOne}" +
            "<if test=\"levelTwo != -2 \">" +
            " and levelTwo = #{levelTwo}" +
            "</if>" +
            "<if test=\"levelTwo == -2 \">" +
            " and levelTwo > -1" +
            "</if>" +
            "<if test=\"levelThree != -2 \">" +
            " and levelThree = #{levelThree}" +
            "</if>" +
            "<if test=\"levelThree == -2 \">" +
            " and levelThree > -1" +
            "</if>" +
            " and levelFour = #{levelFour}" +
            " and levelFive = #{levelFive}" +
            "</script>")
    List<GoodsCategory> findList4(@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree, @Param("levelFour") int levelFour, @Param("levelFive") int levelFive);


    /***
     * 模糊查询商品分类
     * @param letter 商品分类首字母
     * @return
     */
    @Select("<script>" +
            "select * from GoodsCategory" +
            " where 1=1" +
            " and (name LIKE CONCAT('%',#{letter},'%')" +
            " or letter = #{letter})" +
            " and levelThree > -1" +
            " and levelFour = -1" +
            " or (name LIKE CONCAT('%',#{letter},'%')" +
            " or letter = #{letter})" +
            " and levelFour > -1" +
            " and levelFive = -1" +
            " or (name LIKE CONCAT('%',#{letter},'%')" +
            " or letter = #{letter})" +
            " and levelFive > -1" +
            "</script>")
    List<GoodsCategory> findList2(@Param("letter") String letter);


    /***
     * 查询商品品牌
     * @param sortId 商品分类ID
     * @return
     */
    @Select("<script>" +
            "select * from GoodsBrandCategoryValue where " +
            "categoryId in" +
            "<foreach collection='sortId' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<GoodsBrandCategoryValue> findCategoryValue(@Param("sortId") String[] sortId);

    /***
     * 查询商品品牌
     * @param ids 商品分类ID
     * @param letter 商品品牌首字母
     * @return
     */
    @Select("<script>" +
            "select * from GoodsBrands " +
            "where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "<if test=\"letter != null and letter != ''\">" +
            " and letter=#{letter}" +
            " and name LIKE CONCAT('%',#{letter},'%')" +
            " and realname LIKE CONCAT('%',#{letter},'%')" +
            "</if>" +
            "</script>")
    List<GoodsBrands> findBrands(@Param("ids") String[] ids, @Param("letter") String letter);

    /***
     * 查询商品属性名称
     * @param goodCategoryId 商品分类id
     * @param goodsBrandId 品牌id
     * @return
     */
    @Select("<script>" +
            "select * from GoodsBrandProperty " +
            "where goodCategoryId=#{goodCategoryId}" +
            " and goodsBrandId=#{goodsBrandId}" +
            "</script>")
    List<GoodsBrandProperty> findBrandProperty(@Param("goodCategoryId") long goodCategoryId, @Param("goodsBrandId") long goodsBrandId);

    /***
     * 查询商品属性值
     * @param goodsBrandPropertyId 品牌商品属性值id
     * @return
     */
    @Select("<script>" +
            "select * from GoodsBrandPropertyValue where goodsBrandPropertyId=#{goodsBrandPropertyId}" +
            "</script>")
    List<GoodsBrandPropertyValue> findBrandPropertyValue(@Param("goodsBrandPropertyId") long goodsBrandPropertyId);

    /***
     * 根据分类&品牌ID查询分类&品牌关联ID
     * @param goodCategoryId
     * @return
     */
    @Select("select * from GoodsBrandCategoryValue where categoryId=#{goodCategoryId} and brandId=#{goodsBrandId}")
    GoodsBrandCategoryValue findRelation(@Param("goodCategoryId") long goodCategoryId, @Param("goodsBrandId") long goodsBrandId);

    /***
     * 根据分类&品牌ID查询分类&品牌关联ID
     * @return
     */
    @Select("<script>" +
            "select * from GoodsBrandCategoryValue" +
            " where categoryId in" +
            "<foreach collection='goodCategoryId' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and brandId in" +
            "<foreach collection='goodsBrandId' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<GoodsBrandCategoryValue> findBrandPropertys(@Param("goodCategoryId") String[] goodCategoryId, @Param("goodsBrandId") String[] goodsBrandId);

    /***
     * 查询商品属性名称
     * @param goodCategoryId 商品分类id
     * @param goodsBrandId 品牌id
     * @return
     */
    @Select("<script>" +
            "select * from GoodsBrandProperty " +
            " where goodCategoryId in" +
            "<foreach collection='goodCategoryId' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and goodsBrandId in" +
            "<foreach collection='goodsBrandId' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<GoodsBrandProperty> findBrandPropertyss(@Param("goodCategoryId") String[] goodCategoryId, @Param("goodsBrandId") String[] goodsBrandId);

    /***
     * 更新分类logo
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update GoodsCategory set" +
            " logo=#{logo}" +
//            " where name LIKE CONCAT('%',#{name},'%')" +
            " where name =#{name}" +
            " and levelFour = -1" +
            " and levelFive = -1" +
            " and goodCategoryId > 0" +
            " and logo = ''" +
            "</script>")
    int updGoodsCategoryLog(GoodsCategory homeShopCenter);
}

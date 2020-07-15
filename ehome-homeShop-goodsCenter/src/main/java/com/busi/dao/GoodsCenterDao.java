package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品信息相关DAO
 * author：ZJJ
 * create time：2019-4-17 15:33:37
 */
@Mapper
@Repository
public interface GoodsCenterDao {

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Insert("insert into HomeShopGoods(shopId,userId,imgUrl,goodsType,goodsTitle,usedSort,levelOne,levelTwo,levelThree,levelFour,levelFive,brandId,videoCoverUrl,videoUrl," +
            "brand,specs,price,stock,details,detailsId,barCode,code,sort,sortId,propertyName," +
            "province,city,district,pinkageType,expressMode,invoice,guarantee,refunds,returnPolicy,stockCount,startTime," +
            "spike,galleryFeatured,releaseTime,refreshTime,sellType,auditType,frontPlaceType,specialProperty,usedSortId) " +
            "values (#{shopId},#{userId},#{imgUrl},#{goodsType},#{goodsTitle},#{usedSort},#{levelOne},#{levelTwo},#{levelThree},#{levelFour},#{levelFive},#{brandId},#{videoCoverUrl},#{videoUrl}," +
            "#{brand},#{specs},#{price},#{stock},#{details},#{detailsId},#{barCode},#{code},#{sort},#{sortId},#{propertyName}," +
            "#{province},#{city},#{district},#{pinkageType},#{expressMode},#{invoice},#{guarantee},#{refunds},#{returnPolicy},#{stockCount},#{startTime}," +
            "#{spike},#{galleryFeatured},#{releaseTime},#{refreshTime},#{sellType},#{auditType},#{frontPlaceType},#{specialProperty},#{usedSortId})")
    @Options(useGeneratedKeys = true)
    int add(HomeShopGoods homeShopGoods);

    /***
     * 新增商品属性
     * @param dishes
     * @return
     */
    @Insert("insert into GoodsProperty(goodsId,name) " +
            "values (#{goodsId},#{name})")
    @Options(useGeneratedKeys = true)
    int addProperty(GoodsProperty dishes);

    /***
     * 新增商品特殊属性
     * @param dishes
     * @return
     */
    @Insert("insert into GoodsOfSpecialProperty(goodsId,name) " +
            "values (#{goodsId},#{name})")
    @Options(useGeneratedKeys = true)
    int addSpecialProperty(GoodsOfSpecialProperty dishes);

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Delete(("delete from HomeShopGoods where id=#{id} and userId=#{userId}"))
    int del(@Param("id") long id, @Param("userId") long userId);

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Update("<script>" +
            "update HomeShopGoods set" +
            "<if test=\"goodsTitle != null and goodsTitle != ''\">" +
            " goodsTitle=#{goodsTitle}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            " province=#{province}," +
            " city=#{city}," +
            " district=#{district}," +
            " videoUrl=#{videoUrl}," +
            " levelOne=#{levelOne}," +
            " levelTwo=#{levelTwo}," +
            " levelThree=#{levelThree}," +
            " levelFour=#{levelFour}," +
            " levelFive=#{levelFive}," +
            " usedSort=#{usedSort}," +
            " usedSortId=#{usedSortId}," +
            " brand=#{brand}," +
            " brandId=#{brandId}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " goodsType=#{goodsType}," +
            " specs=#{specs}," +
            " price=#{price}," +
            " stock=#{stock}," +
            " detailsId=#{detailsId}," +
            " barCode=#{barCode}," +
            " code=#{code}," +
            " sort=#{sort}," +
            " sortId=#{sortId}," +
            " sellType=#{sellType}," +
            " pinkageType=#{pinkageType}," +
            " expressMode=#{expressMode}," +
            " guarantee=#{guarantee}," +
            " refunds=#{refunds}," +
            " returnPolicy=#{returnPolicy}," +
            " stockCount=#{stockCount}," +
            " startTime=#{startTime}," +
            " spike=#{spike}," +
            " refreshTime=#{refreshTime}," +
            " specialProperty=#{specialProperty}," +
            " propertyName=#{propertyName}," +
            " galleryFeatured=#{galleryFeatured}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(HomeShopGoods homeShopGoods);

    /***
     * 更新商品属性
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update GoodsProperty set" +
            " name=#{name}" +
            " where goodsId=#{goodsId}" +
            "</script>")
    int updateProperty(GoodsProperty kitchenDishes);

    /***
     * 更新商品特殊属性
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update GoodsOfSpecialProperty set" +
            " name=#{name}" +
            " where goodsId=#{goodsId}" +
            "</script>")
    int updateSpecialProperty(GoodsOfSpecialProperty kitchenDishes);

    /***
     * 更新删除状态
     * @param homeShopGoods
     * @return
     */
    @Update("<script>" +
            "update HomeShopGoods set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(HomeShopGoods homeShopGoods);

    /***
     * 批量删除商品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update HomeShopGoods set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateDels(@Param("ids") String[] ids);

    /***
     * 批量上下架商品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update HomeShopGoods set" +
            " sellType=#{sellType}" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int changeShopGoods(@Param("ids") String[] ids, @Param("sellType") int sellType);

    /***
     * 根据Id查询
     * @param id
     */
    @Select("select * from HomeShopGoods where id=#{id} and deleteType=0 and auditType=1")
    HomeShopGoods findUserById(@Param("id") long id);

    /***
     * 根据Id查询属性
     * @param id
     */
    @Select("select * from GoodsProperty where goodsId=#{id}")
    GoodsProperty findProperty(@Param("id") long id);

    /***
     * 根据Id查询特殊属性
     * @param id
     */
    @Select("select * from GoodsOfSpecialProperty where goodsId=#{id}")
    GoodsOfSpecialProperty findSpecialProperty(@Param("id") long id);

    /***
     * 统计已上架,已卖出已下架,我的订单数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from HomeShopGoods" +
            " where userId=#{userId}" +
            " and auditType = 1 and deleteType = 0" +
            "<if test=\"type == 1\">" +
            " and sellType=0" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and sellType=1" +
            "</if>" +
            "</script>")
    int findNum(@Param("userId") long userId, @Param("type") int type);

    /***
     * 统计该用户分类数量
     * @param shopId
     * @return
     */
    @Select("<script>" +
            "select count(id) from GoodsSort" +
            " where 1=1 " +
            " and shopId = #{shopId}" +
            "</script>")
    int findSortNum(@Param("shopId") long shopId);

    /***
     * 新增分类
     * @param dishes
     * @return
     */
    @Insert("insert into GoodsSort(userId,superiorId,shopId,sortName,superior) " +
            "values (#{userId},#{superiorId},#{shopId},#{sortName},#{superior})")
    @Options(useGeneratedKeys = true)
    int addGoodsSort(GoodsSort dishes);

    /***
     * 修改分类
     * @param goodsSort
     * @return
     */
    @Update("<script>" +
            "update GoodsSort set" +
            " sortName=#{sortName}," +
            " superior=#{superior}," +
            " superiorId=#{superiorId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeGoodsSort(GoodsSort goodsSort);

    /***
     * 批量修改商品分类
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update HomeShopGoods set" +
            " sortId=#{sortId}," +
            " sort=#{sortName}" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int editGoodsSort(@Param("ids") String[] ids, @Param("sortId") long sortId, @Param("sortName") String sortName);

    /***
     * 删除商品分类
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from GoodsSort" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delGoodsSort(@Param("ids") String[] ids);

    /***
     * 查询分类列表
     * @param id  店铺
     * @param find  0默认所有 1一级分类 2子级分类
     * @param sortId  分类ID(仅查询子级分类有效)
     * @return
     */
    @Select("<script>" +
            "select * from GoodsSort" +
            " where shopId = #{id}" +
            "<if test=\"find == 1\">" +
            " and superiorId = 0" +
            "</if>" +
            "<if test=\"find == 2\">" +
            " and superiorId = #{sortId}" +
            "</if>" +
            " order by id desc" +
            "</script>")
    List<GoodsSort> getGoodsSortList(@Param("id") long id, @Param("find") int find, @Param("sortId") int sortId);

    /***
     * 分页查询商品
     * @param shopId  店铺ID
     * @param sort  排序条件: -1全部 0出售中，1仓库中，2已预约
     * @param stock  库存：0倒序 1正序
     * @param goodsSort  分类
     * @return
     */
    @Select("<script>" +
            "select * from HomeShopGoods" +
            " where shopId = #{shopId} and deleteType=0" +
            "<if test=\"sort != -1\">" +
            " and sellType = #{sort}" +
            "</if>" +
            "<if test=\"goodsSort > 0\">" +
            " and sortId = #{goodsSort}" +
            "</if>" +
            "<if test=\"stock == 1\">" +
            " order by stock asc" +
            "</if>" +
            "<if test=\"stock == 0\">" +
            " order by stock desc" +
            "</if>" +
            " ,refreshTime desc" +
            "</script>")
    List<HomeShopGoods> findDishesSortList(@Param("sort") int sort, @Param("shopId") long shopId, @Param("stock") int stock, @Param("goodsSort") long goodsSort);

    /***
     * 分页查询商品
     * @param shopId  店铺ID
     * @param sort  排序条件:0出售中，1仓库中，2已预约
     * @param time  时间：0倒序 1正序
     * @param goodsSort  分类
     * @return
     */
    @Select("<script>" +
            "select * from HomeShopGoods" +
            " where shopId = #{shopId} and deleteType=0" +
            "<if test=\"sort != -1\">" +
            " and sellType = #{sort}" +
            "</if>" +
            "<if test=\"goodsSort > 0\">" +
            " and sortId = #{goodsSort}" +
            "</if>" +
            "<if test=\"time == 0\">" +
            " order by refreshTime desc" +
            "</if>" +
            "<if test=\"time == 1\">" +
            " order by refreshTime asc" +
            "</if>" +
            " ,stock desc" +
            "</script>")
    List<HomeShopGoods> findDishesSortList2(@Param("sort") int sort, @Param("shopId") long shopId, @Param("time") int time, @Param("goodsSort") long goodsSort);

    /***
     * 二货商城首页分类查询
     * @param sort  分类 0精选 1生活 2电器 3母婴 4时尚
     * @return
     */
    @Select("<script>" +
            "select * from HomeShopGoods" +
            " where auditType = 1 and deleteType=0" +
            " and sellType = 0" +
            "<if test=\"sort == 0\">" +
            " order by refreshTime desc" +
            " ,goodsType,stock desc" +
            "</if>" +
            "<if test=\"sort == 1\">" +
            " and levelOne in(3,5)" +
            " order by refreshTime desc" +
            "</if>" +
            "<if test=\"sort == 2\">" +
            " and levelOne in(1,2)" +
            " order by refreshTime desc" +
            "</if>" +
            "<if test=\"sort == 3\">" +
            " and levelOne = 10" +
            " order by refreshTime desc" +
            "</if>" +
            "<if test=\"sort == 4\">" +
            " and levelOne in(6,7,9,11)" +
            " order by refreshTime desc" +
            "</if>" +
            "</script>")
    List<HomeShopGoods> findHomePageList(@Param("sort") int sort);


    /***
     * 新增商品描述
     * @param dishes
     * @return
     */
    @Insert("insert into GoodsDescribe(userId,imgUrl,shopId,content) " +
            "values (#{userId},#{imgUrl},#{shopId},#{content})")
    @Options(useGeneratedKeys = true)
    int addGoodsDescribe(GoodsDescribe dishes);

    /***
     * 更新商品描述
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update GoodsDescribe set" +
            " imgUrl=#{imgUrl}," +
            " content=#{content}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeGoodsDescribe(GoodsDescribe kitchenDishes);

    /***
     * 删除商品描述
     * @param id
     * @return
     */
    @Delete("<script>" +
            "delete from GoodsDescribe" +
            " where id =#{id}" +
            " and userId=#{userId}" +
            "</script>")
    int delGoodsDescribe(@Param("id") long id, @Param("userId") long userId);

    /***
     * 根据ID查询商品描述
     * @param id
     * @return
     */
    @Select("select * from GoodsDescribe where id=#{id}")
    GoodsDescribe disheSdetails(@Param("id") long id);

    /***
     * 更新浏览数
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update HomeShopGoods set" +
            " seeNumber=#{seeNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateSee(HomeShopGoods kitchenDishes);

    /***
     * 分页查询商品(用户调用)
     * @param sort  排序条件:0综合  1销量  2价格最高  3价格最低
     * @param brandId  -1不限 品牌ID
     * @param pinkageType  是否包邮:-1不限 0是  1否
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param province  -1不限 发货地省份
     * @param city  -1不限 发货地城市
     * @param district  -1不限 发货地区域
     * @param propertyName  属性值 多个属性之间","分隔
     * @return
     */
    @Select("<script>" +
            "select * from HomeShopGoods" +
            " where deleteType=0 and sellType=0 and auditType=1" +

            "<if test=\"brandId >= 1\">" +
            " and brandId = #{brandId}" +
            "</if>" +

            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +

            "<if test=\"maxPrice > 0\">" +
            " and price >= #{minPrice} and #{maxPrice} >= price" +
            "</if>" +
            "<if test=\"maxPrice &lt;= 0\">" +
            " and price >= #{minPrice}" +
            "</if>" +

            "<if test=\"pinkageType >= 0\">" +
            " and pinkageType = #{pinkageType}" +
            "</if>" +


            "<if test=\"propertyName != null and propertyName !=''\">" +
            "<foreach collection='propertyName' index='index' item='item' separator='and'>" +
            "propertyName  LIKE CONCAT('%',#{item},'%')" +
            "</foreach>" +
            "</if>" +


            "<if test=\"sort == 0\">" +
            " order by monthSales desc,refreshTime desc" +
            "</if>" +
            "<if test=\"sort == 1\">" +
            " order by monthSales desc" +
            "</if>" +
            "<if test=\"sort == 2\">" +
            " order by price desc" +
            "</if>" +
            "<if test=\"sort == 3\">" +
            " order by price asc" +
            "</if>" +
            "</script>")
    List<HomeShopGoods> findUserGoodsList(@Param("sort") int sort, @Param("brandId") long brandId, @Param("pinkageType") int pinkageType, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("propertyName") String[] propertyName);

}

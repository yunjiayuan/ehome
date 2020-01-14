package com.busi.dao;

import com.busi.entity.ShopFloorGoods;
import com.busi.entity.ShopFloorGoodsDescribe;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品信息相关DAO
 * author：ZJJ
 * create time：2019-11-19 13:43:33
 */
@Mapper
@Repository
public interface ShopFloorGoodsDao {

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Insert("insert into ShopFloorGoods(userId,imgUrl,goodsTitle,basicDescribe,usedSort,levelOne,levelTwo,levelThree,videoCoverUrl,videoUrl," +
            "specs,price,stock,details,detailsId,discountPrice,goodsCoverUrl," +
            "releaseTime,refreshTime,sellType,auditType,extendSort,discount) " +
            "values (#{userId},#{imgUrl},#{goodsTitle},#{basicDescribe},#{usedSort},#{levelOne},#{levelTwo},#{levelThree},#{videoCoverUrl},#{videoUrl}," +
            "#{specs},#{price},#{stock},#{details},#{detailsId},#{discountPrice},#{goodsCoverUrl}," +
            "#{releaseTime},#{refreshTime},#{sellType},#{auditType},#{extendSort},#{discount})")
    @Options(useGeneratedKeys = true)
    int add(ShopFloorGoods homeShopGoods);

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Update("<script>" +
            "update ShopFloorGoods set" +
            "<if test=\"goodsTitle != null and goodsTitle != ''\">" +
            " goodsTitle=#{goodsTitle}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            " levelThree=#{levelThree}," +
            " videoUrl=#{videoUrl}," +
            " levelOne=#{levelOne}," +
            " levelTwo=#{levelTwo}," +
            " usedSort=#{usedSort}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " goodsCoverUrl=#{goodsCoverUrl}," +
            " specs=#{specs}," +
            " sellType=#{sellType}," +
            " price=#{price}," +
            " stock=#{stock}," +
            " detailsId=#{detailsId}," +
            " basicDescribe=#{basicDescribe}," +
            " discount=#{discount}," +
            " discountPrice=#{discountPrice}," +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(ShopFloorGoods homeShopGoods);

    /***
     * 批量删除商品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update ShopFloorGoods set" +
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
            "update ShopFloorGoods set" +
            " sellType=#{sellType}" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int changeShopGoods(@Param("ids") String[] ids, @Param("sellType") int sellType);

    /***
     * 批量查询指定的商品
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoods" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<ShopFloorGoods> findList(@Param("ids") String[] ids);

    /***
     * 根据Id查询
     * @param id
     */
    @Select("select * from ShopFloorGoods where id=#{id} and deleteType=0 and auditType=1")
    ShopFloorGoods findUserById(@Param("id") long id);

    /***
     * 统计已上架,已卖出已下架,我的订单数量
     * @return
     */
    @Select("<script>" +
            "select count(id) from ShopFloorGoods" +
            " where auditType = 1 and deleteType = 0" +
            "<if test=\"type == 0\">" +
            " and sellType=0" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and sellType=1" +
            "</if>" +
            "</script>")
    int findNum(@Param("type") int type);

    /***
     * 分页查询商品(用户调用)
     * @param sort  排序条件:0默认销量倒序，1最新发布
     * @param discount  0全部，1只看折扣
     * @param price  默认-1不限制 0价格升序，1价格倒序
     * @param stock  默认-1所有 0有货 1没货
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param levelOne  一级分类:默认值为0,-2为不限
     * @param levelTwo  二级分类:默认值为0,-2为不限
     * @param levelThree  三级分类:默认值为0,-2为不限
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoods" +
            " where deleteType=0 and sellType=0" +

            "<if test=\"discount == 1\">" +
            " and discountPrice > 0" +
            "</if>" +

            "<if test=\"levelOne == -2 \">" +
                " and levelOne > -1" +
            "</if>" +

            "<if test=\"levelOne >= 0 \">" +
                "<if test=\"levelTwo == -2 \">" +
                    " and levelOne = #{levelOne}" +
                "</if>" +
                "<if test=\"levelTwo > -1 \">" +
                    " and levelOne = #{levelOne}" +
                    " and levelTwo = #{levelTwo}" +
                    "<if test=\"levelThree >= 0\">" +
                        " and levelThree = #{levelThree}" +
                    "</if>" +
                    "<if test=\"levelThree == -2\">" +
                        " and levelThree >= -1" +
                    "</if>" +
                "</if>" +
            "</if>" +

            "<if test=\"maxPrice > 0\">" +
                " and price >= #{minPrice} and #{maxPrice} >= price" +
            "</if>" +
            "<if test=\"maxPrice &lt;= 0\">" +
                " and price >= #{minPrice}" +
            "</if>" +
            "<if test=\"stock == 0\">" +
                " and stock > 0" +
            "</if>" +
            "<if test=\"stock == 1\">" +
                " and stock = 0" +
            "</if>" +
            "<if test=\"sort == 0\">" +
                "<if test=\"price == -1\">" +
                    " order by sales desc" +
                "</if>" +
                "<if test=\"price == 0\">" +
                    " order by price asc,sales desc" +
                "</if>" +
                "<if test=\"price == 1\">" +
                    " order by price desc,sales desc" +
                "</if>" +
            "</if>" +
            "<if test=\"sort == 1\">" +
                "<if test=\"price == -1\">" +
                    " order by refreshTime desc" +
                "</if>" +
                "<if test=\"price == 0\">" +
                    " order by price asc,refreshTime desc" +
                "</if>" +
                    "<if test=\"price == 1\">" +
                " order by price desc,refreshTime desc" +
                "</if>" +
            "</if>" +
            "</script>")
    List<ShopFloorGoods> findDishesSortList(@Param("discount") int discount,@Param("sort") int sort,@Param("price") int price,@Param("stock") int stock, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice, @Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree);

    /***
     * 分页查询商品
     * @param sort  排序条件: -1全部 0出售中，1仓库中，2已预约
     * @param stock  库存：0倒序 1正序
     * @param levelOne  一级分类:-2为不限
     * @param levelTwo  二级分类:-2为不限
     * @param levelThree  三级分类:-2为不限
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoods" +
            " where deleteType=0" +
            "<if test=\"sort != -1\">" +
                " and sellType = #{sort}" +
            "</if>" +

            "<if test=\"levelOne == -2 \">" +
                " and levelOne > -1" +
                " and levelTwo > -1" +
                " and levelThree > -1" +
            "</if>" +

            "<if test=\"levelOne >= 0 \">" +
                "<if test=\"levelTwo == -2 \">" +
                    " and levelOne = #{levelOne}" +
                    " and levelTwo > -1" +
                    " and levelThree > -1" +
                "</if>" +
                "<if test=\"levelTwo > -1 \">" +
                    " and levelOne = #{levelOne}" +
                    " and levelTwo = #{levelTwo}" +
                        "<if test=\"levelThree >= 0\">" +
                            " and levelThree = #{levelThree}" +
                        "</if>" +
                        "<if test=\"levelThree == -2\">" +
                            " and levelThree > -1" +
                        "</if>" +
                "</if>" +
            "</if>" +

            "<if test=\"stock == 1\">" +
                " order by stock asc" +
            "</if>" +
            "<if test=\"stock == 0\">" +
                " order by stock desc" +
            "</if>" +
                " ,refreshTime desc" +
            "</script>")
    List<ShopFloorGoods> findFGoodsList(@Param("sort") int sort,  @Param("stock") int stock,@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree);

    /***
     * 分页查询商品
     * @param sort  排序条件:0出售中，1仓库中，2已预约
     * @param time  时间：0倒序 1正序
     * @param levelOne  一级分类:-2为不限
     * @param levelTwo  二级分类:-2为不限
     * @param levelThree  三级分类:-2为不限
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoods" +
            " where deleteType=0" +
            "<if test=\"sort != -1\">" +
            " and sellType = #{sort}" +
            "</if>" +

            "<if test=\"levelOne == -2 \">" +
                " and levelOne > -1" +
                " and levelTwo > -1" +
                " and levelThree > -1" +
            "</if>" +

            "<if test=\"levelOne >= 0 \">" +
                "<if test=\"levelTwo == -2 \">" +
                    " and levelOne = #{levelOne}" +
                    " and levelTwo > -1" +
                    " and levelThree > -1" +
                "</if>" +
                "<if test=\"levelTwo > -1 \">" +
                    " and levelOne = #{levelOne}" +
                    " and levelTwo = #{levelTwo}" +
                        "<if test=\"levelThree >= 0\">" +
                            " and levelThree = #{levelThree}" +
                        "</if>" +
                        "<if test=\"levelThree == -2\">" +
                            " and levelThree > -1" +
                        "</if>" +
                "</if>" +
            "</if>" +

            "<if test=\"time == 0\">" +
                " order by refreshTime desc" +
            "</if>" +
                "<if test=\"time == 1\">" +
            " order by refreshTime asc" +
            "</if>" +
                " ,stock desc" +
            "</script>")
    List<ShopFloorGoods> findFGoodsList2(@Param("sort") int sort,  @Param("time") int time,@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree);


    /***
     * 新增商品描述
     * @param dishes
     * @return
     */
    @Insert("insert into ShopFloorGoodsDescribe(userId,imgUrl,content) " +
            "values (#{userId},#{imgUrl},#{content})")
    @Options(useGeneratedKeys = true)
    int addGoodsDescribe(ShopFloorGoodsDescribe dishes);

    /***
     * 更新商品描述
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update ShopFloorGoodsDescribe set" +
            " imgUrl=#{imgUrl}," +
            " content=#{content}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeGoodsDescribe(ShopFloorGoodsDescribe kitchenDishes);

    /***
     * 删除商品描述
     * @param id
     * @return
     */
    @Delete("<script>" +
            "delete from ShopFloorGoodsDescribe" +
            " where id =#{id}" +
            " and userId=#{userId}" +
            "</script>")
    int delGoodsDescribe(@Param("id") long id, @Param("userId") long userId);

    /***
     * 根据ID查询商品描述
     * @param id
     * @return
     */
    @Select("select * from ShopFloorGoodsDescribe where id=#{id}")
    ShopFloorGoodsDescribe disheSdetails(@Param("id") long id);
}

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
    @Insert("insert into ShopFloorGoods(shopId,userId,imgUrl,goodsTitle,basicDescribe,usedSort,levelOne,levelTwo,levelThree,videoCoverUrl,videoUrl," +
            "specs,price,stock,details,detailsId," +
            "releaseTime,refreshTime,sellType,auditType,usedSortId,extendSort,discount) " +
            "values (#{shopId},#{userId},#{imgUrl},#{goodsTitle},#{basicDescribe},#{usedSort},#{levelOne},#{levelTwo},#{levelThree},#{videoCoverUrl},#{videoUrl}," +
            "#{specs},#{price},#{stock},#{details},#{detailsId}," +
            "#{releaseTime},#{refreshTime},#{sellType},#{auditType},#{usedSortId},#{extendSort},#{discount})")
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
            " usedSortId=#{usedSortId}," +
            " extendSort=#{extendSort}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " specs=#{specs}," +
            " price=#{price}," +
            " stock=#{stock}," +
            " detailsId=#{detailsId}," +
            " basicDescribe=#{basicDescribe}," +
            " discount=#{discount}," +
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
     * 根据Id查询
     * @param id
     */
    @Select("select * from ShopFloorGoods where id=#{id} and deleteType=0 and auditType=1")
    ShopFloorGoods findUserById(@Param("id") long id);

    /***
     * 统计已上架,已卖出已下架,我的订单数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from ShopFloorGoods" +
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
     * 分页查询商品
     * @param shopId  店铺ID
     * @param sort  排序条件: -1全部 0出售中，1仓库中，2已预约
     * @param stock  库存：0倒序 1正序
     * @param goodsSort  分类
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoods" +
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
    List<ShopFloorGoods> findDishesSortList(@Param("sort") int sort, @Param("shopId") long shopId, @Param("stock") int stock, @Param("goodsSort") long goodsSort);

    /***
     * 分页查询商品
     * @param shopId  店铺ID
     * @param sort  排序条件:0出售中，1仓库中，2已预约
     * @param time  时间：0倒序 1正序
     * @param goodsSort  分类
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoods" +
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
    List<ShopFloorGoods> findDishesSortList2(@Param("sort") int sort, @Param("shopId") long shopId, @Param("time") int time, @Param("goodsSort") long goodsSort);

    /***
     * 新增商品描述
     * @param dishes
     * @return
     */
    @Insert("insert into ShopFloorGoodsDescribe(userId,imgUrl,shopId,content) " +
            "values (#{userId},#{imgUrl},#{shopId},#{content})")
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
